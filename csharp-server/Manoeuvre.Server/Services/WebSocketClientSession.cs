using System.Net.WebSockets;
using System.Text;
using System.Text.Json;
using Manoeuvre.Server.Models;

namespace Manoeuvre.Server.Services;

public sealed class WebSocketClientSession : IClientSession
{
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web);
    private readonly SemaphoreSlim writeLock = new(1, 1);

    public WebSocketClientSession(WebSocket socket, string remoteEndPoint)
    {
        Id = Guid.NewGuid();
        Socket = socket;
        ConnectedAtUtc = DateTime.UtcNow;
        RemoteEndPoint = remoteEndPoint;
    }

    public Guid Id { get; }

    public WebSocket Socket { get; }

    public DateTime ConnectedAtUtc { get; }

    public string RemoteEndPoint { get; }

    public int SocketPortId => 0;

    public string Transport => "websocket";

    public string? Username { get; set; }

    public PlayerDto? Player { get; set; }

    public bool IsAuthenticated => !string.IsNullOrWhiteSpace(Username);

    public async Task SendAsync(MessageDto message, CancellationToken cancellationToken)
    {
        var json = JsonSerializer.Serialize(message, JsonOptions);
        var bytes = Encoding.UTF8.GetBytes(json);

        await writeLock.WaitAsync(cancellationToken);
        try
        {
            await Socket.SendAsync(bytes, WebSocketMessageType.Text, true, cancellationToken);
        }
        finally
        {
            writeLock.Release();
        }
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
