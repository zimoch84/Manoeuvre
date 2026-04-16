using System.Text.Json.Serialization;

namespace Manoeuvre.Server.Models;

public sealed class MessageDto
{
    [JsonPropertyName("type")]
    public string? Type { get; set; }

    [JsonPropertyName("sender")]
    public string? Sender { get; set; }

    [JsonPropertyName("content")]
    public string? Content { get; set; }

    [JsonPropertyName("recipient")]
    public string? Recipient { get; set; }

    [JsonPropertyName("messageType")]
    public int MessageType { get; set; }

    [JsonPropertyName("contentP")]
    public int ContentP { get; set; }

    [JsonPropertyName("players")]
    public List<PlayerDto> Players { get; set; } = [];

    [JsonPropertyName("channelList")]
    public List<GameRoomDto> ChannelList { get; set; } = [];

    [JsonPropertyName("hostPlayer")]
    public PlayerDto? HostPlayer { get; set; }

    [JsonPropertyName("guestPlayer")]
    public PlayerDto? GuestPlayer { get; set; }

    [JsonPropertyName("command")]
    public CommandDto? Command { get; set; }

    [JsonPropertyName("game")]
    public GameSnapshotDto? Game { get; set; }

    public static MessageDto Create(int messageType, string sender, string? content, string recipient) =>
        new()
        {
            MessageType = messageType,
            Sender = sender,
            Content = content,
            Recipient = recipient
        };

    public static MessageDto Create(int messageType, string sender, int contentP, string recipient) =>
        new()
        {
            MessageType = messageType,
            Sender = sender,
            ContentP = contentP,
            Recipient = recipient
        };
}
