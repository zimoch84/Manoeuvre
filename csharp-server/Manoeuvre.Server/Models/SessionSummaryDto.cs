namespace Manoeuvre.Server.Models;

public sealed class SessionSummaryDto
{
    public Guid SessionId { get; set; }

    public string? Username { get; set; }

    public string RemoteEndPoint { get; set; } = string.Empty;

    public bool IsAuthenticated { get; set; }

    public int SocketPortId { get; set; }

    public DateTime ConnectedAtUtc { get; set; }

    public string Transport { get; set; } = string.Empty;
}
