using System.Text.Json.Serialization;

namespace Manoeuvre.Server.Models;

public sealed class CommandDto
{
    [JsonPropertyName("type")]
    public string? Type { get; set; }

    [JsonPropertyName("payload")]
    public Dictionary<string, object?> Payload { get; set; } = [];
}
