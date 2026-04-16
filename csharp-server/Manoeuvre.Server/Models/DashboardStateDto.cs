namespace Manoeuvre.Server.Models;

public sealed class DashboardStateDto
{
    public DateTime GeneratedAtUtc { get; set; }

    public IReadOnlyCollection<SessionSummaryDto> Sessions { get; set; } = [];

    public IReadOnlyCollection<GameRoomDto> Rooms { get; set; } = [];

    public IReadOnlyCollection<string> Logs { get; set; } = [];
}
