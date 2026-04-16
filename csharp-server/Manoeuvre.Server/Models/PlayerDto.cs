using System.Text.Json.Serialization;

namespace Manoeuvre.Server.Models;

public sealed class PlayerDto
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("nation")]
    public string? Nation { get; set; }

    [JsonPropertyName("isHost")]
    public bool IsHost { get; set; }

    [JsonPropertyName("army")]
    public string? Army { get; set; }
}
