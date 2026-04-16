using Manoeuvre.Server.Configuration;
using Manoeuvre.Server.Models;
using Manoeuvre.Server.Services;
using System.Net.WebSockets;
using System.Text;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);

builder.Services.Configure<TcpServerOptions>(
    builder.Configuration.GetSection(TcpServerOptions.SectionName));

builder.Services.AddSingleton<ServerState>();
builder.Services.AddSingleton<ProtocolService>();
if (!builder.Environment.IsEnvironment("Testing"))
{
    builder.Services.AddHostedService<TcpSocketServer>();
}

var app = builder.Build();

app.UseWebSockets();
app.UseDefaultFiles();
app.UseStaticFiles();

app.MapGet("/api/status", (ServerState state) => Results.Ok(state.BuildDashboardState()));
app.MapGet("/api/logs", (ServerState state) => Results.Ok(state.GetLogs()));
app.MapGet("/api/rooms", (ServerState state) => Results.Ok(state.BuildDashboardState().Rooms));
app.MapGet("/api/sessions", (ServerState state) => Results.Ok(state.BuildDashboardState().Sessions));
app.Map("/ws", async (HttpContext context, ServerState state, ProtocolService protocol) =>
{
    if (!context.WebSockets.IsWebSocketRequest)
    {
        context.Response.StatusCode = StatusCodes.Status400BadRequest;
        return;
    }

    using var webSocket = await context.WebSockets.AcceptWebSocketAsync();
    var remote = context.Connection.RemoteIpAddress is null
        ? "web"
        : $"{context.Connection.RemoteIpAddress}:{context.Connection.RemotePort}";
    var session = new WebSocketClientSession(webSocket, remote);
    state.AddSession(session);

    var buffer = new byte[16 * 1024];
    var jsonOptions = new JsonSerializerOptions(JsonSerializerDefaults.Web)
    {
        PropertyNameCaseInsensitive = true
    };

    try
    {
        while (webSocket.State == WebSocketState.Open)
        {
            var builder = new StringBuilder();
            WebSocketReceiveResult result;

            do
            {
                result = await webSocket.ReceiveAsync(buffer, context.RequestAborted);
                if (result.MessageType == WebSocketMessageType.Close)
                {
                    await webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "bye", context.RequestAborted);
                    state.Log($"WS_CLOSED session={session.Id}");
                    state.RemoveSession(session.Id);
                    return;
                }

                builder.Append(Encoding.UTF8.GetString(buffer, 0, result.Count));
            }
            while (!result.EndOfMessage);

            var payload = builder.ToString();
            if (string.IsNullOrWhiteSpace(payload))
            {
                continue;
            }

            MessageDto? message;
            try
            {
                message = JsonSerializer.Deserialize<MessageDto>(payload, jsonOptions);
            }
            catch (JsonException ex)
            {
                state.Log($"INVALID_WS_JSON session={session.Id} error={ex.Message}");
                continue;
            }

            if (message is null)
            {
                continue;
            }

            await protocol.HandleAsync(session, message, context.RequestAborted);
        }
    }
    finally
    {
        state.RemoveSession(session.Id);
    }
});

app.Run();

public partial class Program;
