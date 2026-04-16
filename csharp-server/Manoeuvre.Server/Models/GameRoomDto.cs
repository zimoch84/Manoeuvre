using System.Text.Json.Serialization;

namespace Manoeuvre.Server.Models;

public sealed class GameRoomDto
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("password")]
    public string? Password { get; set; }

    [JsonPropertyName("hostSocketPortId")]
    public int HostSocketPortId { get; set; }

    [JsonPropertyName("guestSocketPortId")]
    public int GuestSocketPortId { get; set; }

    [JsonPropertyName("locked")]
    public bool Locked { get; set; }

    [JsonPropertyName("hostPlayer")]
    public PlayerDto? HostPlayer { get; set; }

    [JsonPropertyName("guestPlayer")]
    public PlayerDto? GuestPlayer { get; set; }

    [JsonPropertyName("game")]
    public GameSnapshotDto? Game { get; set; }
}
