using System.Net.WebSockets;
using System.Text;
using System.Text.Json;
using Manoeuvre.Server.Models;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;

namespace Manoeuvre.Server.Tests;

public sealed class WebSocketIntegrationTests : IClassFixture<WebApplicationFactory<Program>>
{
    private readonly WebApplicationFactory<Program> factory;
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web)
    {
        PropertyNameCaseInsensitive = true
    };

    public WebSocketIntegrationTests(WebApplicationFactory<Program> factory)
    {
        this.factory = factory.WithWebHostBuilder(builder => builder.UseEnvironment("Testing"));
    }

    [Fact]
    public async Task WebSocket_Login_ReturnsOk()
    {
        var client = factory.Server.CreateWebSocketClient();
        using var socket = await client.ConnectAsync(new Uri("ws://localhost/ws"), CancellationToken.None);

        await SendAsync(socket, MessageDto.Create(ProtocolConstants.Login, "Piotr", "secret", "SERVER"));
        var response = await ReceiveAsync(socket);

        Assert.Equal(ProtocolConstants.Login, response.MessageType);
        Assert.Equal(ProtocolConstants.Ok, response.ContentP);
    }

    [Fact]
    public async Task WebSocket_CreateRoom_AndJoinRoom_WorksAcrossTwoClients()
    {
        var client = factory.Server.CreateWebSocketClient();
        using var hostSocket = await client.ConnectAsync(new Uri("ws://localhost/ws"), CancellationToken.None);
        using var guestSocket = await client.ConnectAsync(new Uri("ws://localhost/ws"), CancellationToken.None);

        await SendAsync(hostSocket, MessageDto.Create(ProtocolConstants.Login, "Host", "secret", "SERVER"));
        await ReceiveUntilAsync(hostSocket, message => message.MessageType == ProtocolConstants.Login);

        await SendAsync(guestSocket, MessageDto.Create(ProtocolConstants.Login, "Guest", "secret", "SERVER"));
        await ReceiveUntilAsync(guestSocket, message => message.MessageType == ProtocolConstants.Login);

        await SendAsync(
            hostSocket,
            new MessageDto
            {
                MessageType = ProtocolConstants.CreateRoom,
                Sender = "Host",
                Content = "Alpha",
                Recipient = "SERVER",
                Players = [new PlayerDto { Name = "Host", IsHost = true, Nation = "AU" }],
                ChannelList =
                [
                    new GameRoomDto
                    {
                        Name = "Alpha",
                        HostPlayer = new PlayerDto { Name = "Host", IsHost = true, Nation = "AU" }
                    }
                ]
            });

        var hostCreate = await ReceiveUntilAsync(hostSocket, message => message.MessageType == ProtocolConstants.CreateRoom);
        Assert.Equal(ProtocolConstants.Ok, hostCreate.ContentP);

        await SendAsync(guestSocket, MessageDto.Create(ProtocolConstants.GetRoomList, "Guest", "clientRequest", "SERVER"));
        var roomList = await ReceiveUntilAsync(guestSocket, message => message.MessageType == ProtocolConstants.GetRoomList && message.ChannelList.Count > 0);
        var room = roomList.ChannelList.Single();

        await SendAsync(
            guestSocket,
            new MessageDto
            {
                MessageType = ProtocolConstants.JoinRoom,
                Sender = "Guest",
                Content = room.Name,
                Recipient = "SERVER",
                Players = [new PlayerDto { Name = "Guest", IsHost = false, Nation = "US" }],
                ChannelList = [room]
            });

        var guestJoin = await ReceiveUntilAsync(guestSocket, message => message.MessageType == ProtocolConstants.JoinRoom);
        var hostNotice = await ReceiveUntilAsync(hostSocket, message => message.MessageType == ProtocolConstants.JoinRoom && message.ContentP == ProtocolConstants.UserJoinedInRoom);

        Assert.Equal(ProtocolConstants.Ok, guestJoin.ContentP);
        Assert.Equal(ProtocolConstants.UserJoinedInRoom, hostNotice.ContentP);
        Assert.Equal("Guest", hostNotice.Players.Single().Name);
    }

    private static async Task SendAsync(WebSocket socket, MessageDto message)
    {
        var json = JsonSerializer.Serialize(message, JsonOptions);
        var bytes = Encoding.UTF8.GetBytes(json);
        await socket.SendAsync(bytes, WebSocketMessageType.Text, true, CancellationToken.None);
    }

    private static async Task<MessageDto> ReceiveUntilAsync(WebSocket socket, Func<MessageDto, bool> predicate)
    {
        while (true)
        {
            var message = await ReceiveAsync(socket);
            if (predicate(message))
            {
                return message;
            }
        }
    }

    private static async Task<MessageDto> ReceiveAsync(WebSocket socket)
    {
        var buffer = new byte[16 * 1024];
        var builder = new StringBuilder();
        WebSocketReceiveResult result;
        do
        {
            result = await socket.ReceiveAsync(buffer, CancellationToken.None);
            builder.Append(Encoding.UTF8.GetString(buffer, 0, result.Count));
        }
        while (!result.EndOfMessage);

        return JsonSerializer.Deserialize<MessageDto>(builder.ToString(), JsonOptions)!;
    }
}
