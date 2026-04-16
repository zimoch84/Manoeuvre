using System.Text.Json.Serialization;

namespace Manoeuvre.Server.Models;

public sealed class GameSnapshotDto
{
    [JsonPropertyName("roomName")]
    public string RoomName { get; set; } = string.Empty;

    [JsonPropertyName("startedAtUtc")]
    public DateTime StartedAtUtc { get; set; }

    [JsonPropertyName("hostPlayer")]
    public PlayerDto? HostPlayer { get; set; }

    [JsonPropertyName("guestPlayer")]
    public PlayerDto? GuestPlayer { get; set; }

    [JsonPropertyName("status")]
    public string Status { get; set; } = "STARTED";
}
