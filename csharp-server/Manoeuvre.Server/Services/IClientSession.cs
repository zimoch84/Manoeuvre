using Manoeuvre.Server.Models;

namespace Manoeuvre.Server.Services;

public interface IClientSession
{
    Guid Id { get; }

    DateTime ConnectedAtUtc { get; }

    string RemoteEndPoint { get; }

    int SocketPortId { get; }

    string Transport { get; }

    string? Username { get; set; }

    PlayerDto? Player { get; set; }

    bool IsAuthenticated { get; }

    Task SendAsync(MessageDto message, CancellationToken cancellationToken);

    SessionSummaryDto ToSummary();
}
