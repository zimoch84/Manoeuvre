using System.Collections.Concurrent;
using Manoeuvre.Server.Configuration;
using Manoeuvre.Server.Models;
using Microsoft.Extensions.Options;

namespace Manoeuvre.Server.Services;

public sealed class ServerState
{
    private readonly ConcurrentDictionary<Guid, IClientSession> sessions = new();
    private readonly ConcurrentDictionary<string, RoomState> rooms = new(StringComparer.OrdinalIgnoreCase);
    private readonly ConcurrentQueue<string> logs = new();
    private readonly int logCapacity;

    public ServerState(IOptions<TcpServerOptions> options)
    {
        logCapacity = Math.Max(10, options.Value.LogCapacity);
    }

    public IEnumerable<IClientSession> Sessions => sessions.Values;

    public IEnumerable<RoomState> Rooms => rooms.Values;

    public void AddSession(IClientSession session)
    {
        sessions[session.Id] = session;
        Log($"CONNECTED {session.RemoteEndPoint} session={session.Id}");
    }

    public void RemoveSession(Guid sessionId)
    {
        if (!sessions.TryRemove(sessionId, out var session))
        {
            return;
        }

        foreach (var room in rooms.Values.Where(room => room.HostSessionId == sessionId || room.GuestSessionId == sessionId).ToList())
        {
            rooms.TryRemove(room.Name, out _);
            Log($"ROOM_REMOVED name={room.Name} reason=disconnect");
        }

        Log($"DISCONNECTED {session.RemoteEndPoint} user={session.Username ?? "anonymous"}");
    }

    public bool UsernameExists(string username) =>
        sessions.Values.Any(session => string.Equals(session.Username, username, StringComparison.OrdinalIgnoreCase));

    public RoomState? FindRoomBySession(Guid sessionId) =>
        rooms.Values.FirstOrDefault(room => room.HostSessionId == sessionId || room.GuestSessionId == sessionId);

    public RoomState? FindRoomByHostSocketPort(int hostSocketPortId) =>
        rooms.Values.FirstOrDefault(room => room.HostSocketPortId == hostSocketPortId);

    public bool TryAddRoom(RoomState room) => rooms.TryAdd(room.Name, room);

    public bool TryGetRoom(string roomName, out RoomState room) => rooms.TryGetValue(roomName, out room!);

    public IClientSession? GetSession(Guid sessionId) => sessions.GetValueOrDefault(sessionId);

    public IClientSession? GetSessionByUsername(string username) =>
        sessions.Values.FirstOrDefault(session => string.Equals(session.Username, username, StringComparison.OrdinalIgnoreCase));

    public DashboardStateDto BuildDashboardState() =>
        new()
        {
            GeneratedAtUtc = DateTime.UtcNow,
            Sessions = sessions.Values
                .OrderBy(session => session.ConnectedAtUtc)
                .Select(session => session.ToSummary())
                .ToArray(),
            Rooms = rooms.Values
                .OrderBy(room => room.Name)
                .Select(room => room.ToDto())
                .ToArray(),
            Logs = GetLogs()
        };

    public IReadOnlyCollection<string> GetLogs() => logs.ToArray();

    public void Log(string message)
    {
        var line = $"{DateTime.UtcNow:O} {message}";
        logs.Enqueue(line);

        while (logs.Count > logCapacity && logs.TryDequeue(out _))
        {
        }
    }
}
