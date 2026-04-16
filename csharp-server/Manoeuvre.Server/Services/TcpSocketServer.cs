using System.Net;
using System.Net.Sockets;
using System.Text.Json;
using Manoeuvre.Server.Configuration;
using Manoeuvre.Server.Models;
using Microsoft.Extensions.Options;

namespace Manoeuvre.Server.Services;

public sealed class TcpSocketServer : BackgroundService
{
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web)
    {
        PropertyNameCaseInsensitive = true
    };

    private readonly ServerState state;
    private readonly ProtocolService protocol;
    private readonly TcpServerOptions options;
    private readonly ILogger<TcpSocketServer> logger;
    private TcpListener? listener;

    public TcpSocketServer(
        ServerState state,
        ProtocolService protocol,
        IOptions<TcpServerOptions> options,
        ILogger<TcpSocketServer> logger)
    {
        this.state = state;
        this.protocol = protocol;
        this.options = options.Value;
        this.logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var ipAddress = options.Host == "0.0.0.0"
            ? IPAddress.Any
            : IPAddress.Parse(options.Host);

        listener = new TcpListener(ipAddress, options.Port);
        listener.Start();

        state.Log($"TCP_SERVER_STARTED endpoint={options.Host}:{options.Port}");
        logger.LogInformation("TCP server listening on {Host}:{Port}", options.Host, options.Port);

        while (!stoppingToken.IsCancellationRequested)
        {
            TcpClient tcpClient;
            try
            {
                tcpClient = await listener.AcceptTcpClientAsync(stoppingToken);
            }
            catch (OperationCanceledException)
            {
                break;
            }

            _ = Task.Run(() => HandleClientAsync(tcpClient, stoppingToken), stoppingToken);
        }
    }

    public override async Task StopAsync(CancellationToken cancellationToken)
    {
        listener?.Stop();
        await base.StopAsync(cancellationToken);
    }

    private async Task HandleClientAsync(TcpClient tcpClient, CancellationToken cancellationToken)
    {
        await using var session = new TcpClientSession(tcpClient);
        state.AddSession(session);

        try
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                var line = await session.ReadLineAsync(cancellationToken);
                if (string.IsNullOrWhiteSpace(line))
                {
                    break;
                }

                MessageDto? message;
                try
                {
                    message = JsonSerializer.Deserialize<MessageDto>(line, JsonOptions);
                }
                catch (JsonException ex)
                {
                    state.Log($"INVALID_JSON session={session.Id} error={ex.Message}");
                    continue;
                }

                if (message is null)
                {
                    continue;
                }

                await protocol.HandleAsync(session, message, cancellationToken);
            }
        }
        catch (IOException ex)
        {
            state.Log($"CLIENT_IO_ERROR session={session.Id} error={ex.Message}");
        }
        catch (SocketException ex)
        {
            state.Log($"CLIENT_SOCKET_ERROR session={session.Id} error={ex.Message}");
        }
        finally
        {
            state.RemoveSession(session.Id);
        }
    }
}
