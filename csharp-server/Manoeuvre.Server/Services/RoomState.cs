using Manoeuvre.Server.Models;

namespace Manoeuvre.Server.Services;

public sealed class RoomState
{
    public required string Name { get; init; }

    public string? Password { get; set; }

    public required Guid HostSessionId { get; init; }

    public Guid? GuestSessionId { get; set; }

    public required int HostSocketPortId { get; init; }

    public int GuestSocketPortId { get; set; }

    public required PlayerDto HostPlayer { get; init; }

    public PlayerDto? GuestPlayer { get; set; }

    public GameSnapshotDto? Game { get; set; }

    public bool IsLocked => GuestSessionId.HasValue;

    public GameRoomDto ToDto() =>
        new()
        {
            Name = Name,
            Password = Password,
            HostSocketPortId = HostSocketPortId,
            GuestSocketPortId = GuestSocketPortId,
            Locked = IsLocked,
            HostPlayer = ClonePlayer(HostPlayer),
            GuestPlayer = ClonePlayer(GuestPlayer),
            Game = Game is null
                ? null
                : new GameSnapshotDto
                {
                    RoomName = Game.RoomName,
                    StartedAtUtc = Game.StartedAtUtc,
                    HostPlayer = ClonePlayer(Game.HostPlayer),
                    GuestPlayer = ClonePlayer(Game.GuestPlayer),
                    Status = Game.Status
                }
        };

    private static PlayerDto? ClonePlayer(PlayerDto? player) =>
        player is null
            ? null
            : new PlayerDto
            {
                Name = player.Name,
                Nation = player.Nation,
                IsHost = player.IsHost,
                Army = player.Army
            };
}
