using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using Manoeuvre.Server.Models;

namespace Manoeuvre.Server.Services;

public sealed class TcpClientSession : IClientSession, IAsyncDisposable
{
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web);
    private readonly SemaphoreSlim writeLock = new(1, 1);
    private readonly StreamReader reader;
    private readonly StreamWriter writer;

    public TcpClientSession(TcpClient tcpClient)
    {
        Id = Guid.NewGuid();
        TcpClient = tcpClient;
        Stream = tcpClient.GetStream();
        reader = new StreamReader(Stream, Encoding.UTF8, leaveOpen: true);
        writer = new StreamWriter(Stream, new UTF8Encoding(false), leaveOpen: true)
        {
            AutoFlush = true
        };

        ConnectedAtUtc = DateTime.UtcNow;
        RemoteEndPoint = tcpClient.Client.RemoteEndPoint?.ToString() ?? "unknown";
        SocketPortId = ((IPEndPoint?)tcpClient.Client.RemoteEndPoint)?.Port ?? 0;
    }

    public Guid Id { get; }

    public TcpClient TcpClient { get; }

    public NetworkStream Stream { get; }

    public DateTime ConnectedAtUtc { get; }

    public string RemoteEndPoint { get; }

    public int SocketPortId { get; }

    public string Transport => "tcp";

    public string? Username { get; set; }

    public PlayerDto? Player { get; set; }

    public bool IsAuthenticated => !string.IsNullOrWhiteSpace(Username);

    public async Task<string?> ReadLineAsync(CancellationToken cancellationToken)
    {
        using var registration = cancellationToken.Register(() => TcpClient.Dispose());
        return await reader.ReadLineAsync(cancellationToken);
    }

    public async Task SendAsync(MessageDto message, CancellationToken cancellationToken)
    {
        var json = JsonSerializer.Serialize(message, JsonOptions);
        await writeLock.WaitAsync(cancellationToken);
        try
        {
            await writer.WriteLineAsync(json);
            await writer.FlushAsync();
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

    public async ValueTask DisposeAsync()
    {
        await writer.DisposeAsync();
        reader.Dispose();
        Stream.Dispose();
        TcpClient.Dispose();
        writeLock.Dispose();
    }
}
