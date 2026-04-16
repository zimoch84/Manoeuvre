using Manoeuvre.Server.Configuration;
using Manoeuvre.Server.Models;
using Manoeuvre.Server.Services;
using Microsoft.Extensions.Options;

namespace Manoeuvre.Server.Tests;

public sealed class ProtocolServiceTests
{
    [Fact]
    public async Task Login_AssignsUserAndBroadcastsUserLogged()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var session = new FakeClientSession();
        state.AddSession(session);

        await protocol.HandleAsync(
            session,
            MessageDto.Create(ProtocolConstants.Login, "Piotr", "secret", "SERVER"),
            CancellationToken.None);

        Assert.Equal("Piotr", session.Username);
        Assert.NotNull(session.Player);
        Assert.Equal("Piotr", session.Player!.Name);
        Assert.Contains(session.SentMessages, message => message.MessageType == ProtocolConstants.Login && message.ContentP == ProtocolConstants.Ok);
        Assert.Contains(session.SentMessages, message => message.MessageType == ProtocolConstants.UserLogged && message.ContentP == ProtocolConstants.Ok);
    }

    [Fact]
    public async Task Login_DuplicateUsername_ReturnsNotOk()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var sessionOne = new FakeClientSession();
        var sessionTwo = new FakeClientSession();
        state.AddSession(sessionOne);
        state.AddSession(sessionTwo);

        await protocol.HandleAsync(
            sessionOne,
            MessageDto.Create(ProtocolConstants.Login, "Piotr", "secret", "SERVER"),
            CancellationToken.None);

        await protocol.HandleAsync(
            sessionTwo,
            MessageDto.Create(ProtocolConstants.Login, "Piotr", "secret", "SERVER"),
            CancellationToken.None);

        Assert.Contains(sessionTwo.SentMessages, message => message.MessageType == ProtocolConstants.Login && message.ContentP == ProtocolConstants.NotOk);
    }

    [Fact]
    public async Task CreateRoom_AfterLogin_CreatesRoomAndBroadcastsRoomList()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var host = await LoginAsync(state, protocol, "Host");

        var roomMessage = new MessageDto
        {
            MessageType = ProtocolConstants.CreateRoom,
            Sender = "Host",
            Content = "Alpha",
            Recipient = "SERVER",
            Players =
            [
                new PlayerDto { Name = "Host", IsHost = true, Nation = "AU" }
            ],
            ChannelList =
            [
                new GameRoomDto
                {
                    Name = "Alpha",
                    Password = "1234",
                    HostPlayer = new PlayerDto { Name = "Host", IsHost = true, Nation = "AU" }
                }
            ]
        };

        await protocol.HandleAsync(host, roomMessage, CancellationToken.None);

        Assert.Single(state.Rooms);
        var room = state.Rooms.Single();
        Assert.Equal("Alpha", room.Name);
        Assert.Equal("Host", room.HostPlayer.Name);
        Assert.Contains(host.SentMessages, message => message.MessageType == ProtocolConstants.CreateRoom && message.ContentP == ProtocolConstants.Ok);
        Assert.Contains(host.SentMessages, message => message.MessageType == ProtocolConstants.GetRoomList);
    }

    [Fact]
    public async Task JoinRoom_WithOpenRoom_JoinsGuestAndNotifiesHost()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var host = await LoginAsync(state, protocol, "Host");
        var guest = await LoginAsync(state, protocol, "Guest");

        await protocol.HandleAsync(
            host,
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
            },
            CancellationToken.None);

        var room = state.Rooms.Single().ToDto();

        await protocol.HandleAsync(
            guest,
            new MessageDto
            {
                MessageType = ProtocolConstants.JoinRoom,
                Sender = "Guest",
                Content = room.Name,
                Recipient = "SERVER",
                Players = [new PlayerDto { Name = "Guest", Nation = "US", IsHost = false }],
                ChannelList = [room]
            },
            CancellationToken.None);

        var stateRoom = state.Rooms.Single();
        Assert.Equal("Guest", stateRoom.GuestPlayer?.Name);
        Assert.True(stateRoom.IsLocked);
        Assert.Contains(guest.SentMessages, message => message.MessageType == ProtocolConstants.JoinRoom && message.ContentP == ProtocolConstants.Ok);
        Assert.Contains(host.SentMessages, message => message.MessageType == ProtocolConstants.JoinRoom && message.ContentP == ProtocolConstants.UserJoinedInRoom);
    }

    [Fact]
    public async Task GetRoomList_ReturnsCurrentRooms()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var host = await LoginAsync(state, protocol, "Host");

        await protocol.HandleAsync(
            host,
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
            },
            CancellationToken.None);

        var requester = await LoginAsync(state, protocol, "Observer");

        await protocol.HandleAsync(
            requester,
            MessageDto.Create(ProtocolConstants.GetRoomList, "Observer", "clientRequest", "SERVER"),
            CancellationToken.None);

        var roomList = requester.SentMessages.Last(message => message.MessageType == ProtocolConstants.GetRoomList);
        Assert.Single(roomList.ChannelList);
        Assert.Equal("Alpha", roomList.ChannelList[0].Name);
    }

    [Fact]
    public async Task StartGame_RequiresHostAndFullRoom()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var host = await LoginAsync(state, protocol, "Host");

        await protocol.HandleAsync(
            host,
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
            },
            CancellationToken.None);

        await protocol.HandleAsync(
            host,
            MessageDto.Create(ProtocolConstants.StartGame, "Host", "Start Game", "SERVER"),
            CancellationToken.None);

        Assert.Contains(host.SentMessages, message => message.MessageType == ProtocolConstants.StartGame && message.ContentP == ProtocolConstants.NotOk);
    }

    [Fact]
    public async Task StartGame_WithHostAndGuest_ReturnsSnapshotToBothPlayers()
    {
        var (state, protocol) = CreateSystemUnderTest();
        var host = await LoginAsync(state, protocol, "Host");
        var guest = await LoginAsync(state, protocol, "Guest");

        await protocol.HandleAsync(
            host,
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
            },
            CancellationToken.None);

        var room = state.Rooms.Single().ToDto();

        await protocol.HandleAsync(
            guest,
            new MessageDto
            {
                MessageType = ProtocolConstants.JoinRoom,
                Sender = "Guest",
                Content = room.Name,
                Recipient = "SERVER",
                Players = [new PlayerDto { Name = "Guest", Nation = "US", IsHost = false }],
                ChannelList = [room]
            },
            CancellationToken.None);

        await protocol.HandleAsync(
            host,
            MessageDto.Create(ProtocolConstants.StartGame, "Host", "Start Game", "SERVER"),
            CancellationToken.None);

        var hostStart = host.SentMessages.Last(message => message.MessageType == ProtocolConstants.StartGame);
        var guestStart = guest.SentMessages.Last(message => message.MessageType == ProtocolConstants.StartGame);

        Assert.Equal(ProtocolConstants.Ok, hostStart.ContentP);
        Assert.Equal(ProtocolConstants.Ok, guestStart.ContentP);
        Assert.NotNull(hostStart.Game);
        Assert.NotNull(guestStart.Game);
        Assert.Equal("Alpha", hostStart.Game!.RoomName);
        Assert.Equal("Alpha", guestStart.Game!.RoomName);
    }

    private static async Task<FakeClientSession> LoginAsync(ServerState state, ProtocolService protocol, string username)
    {
        var session = new FakeClientSession();
        state.AddSession(session);
        await protocol.HandleAsync(
            session,
            MessageDto.Create(ProtocolConstants.Login, username, "secret", "SERVER"),
            CancellationToken.None);
        return session;
    }

    private static (ServerState State, ProtocolService Protocol) CreateSystemUnderTest()
    {
        var options = Options.Create(new TcpServerOptions());
        var state = new ServerState(options);
        var protocol = new ProtocolService(state);
        return (state, protocol);
    }
}

sealed class FakeClientSession : IClientSession
{
    public Guid Id { get; } = Guid.NewGuid();

    public DateTime ConnectedAtUtc { get; } = DateTime.UtcNow;

    public string RemoteEndPoint { get; } = "test";

    public int SocketPortId { get; } = Random.Shared.Next(10000, 60000);

    public string Transport { get; } = "test";

    public string? Username { get; set; }

    public PlayerDto? Player { get; set; }

    public bool IsAuthenticated => !string.IsNullOrWhiteSpace(Username);

    public List<MessageDto> SentMessages { get; } = [];

    public Task SendAsync(MessageDto message, CancellationToken cancellationToken)
    {
        SentMessages.Add(message);
        return Task.CompletedTask;
    }

    public SessionSummaryDto ToSummary() =>
        new()
        {
            SessionId = Id,
            Username = Username,
            RemoteEndPoint = RemoteEndPoint,
            IsAuthenticated = IsAuthenticated,
            SocketPortId = SocketPortId,
            ConnectedAtUtc = ConnectedAtUtc,
            Transport = Transport
        };
}
