using Manoeuvre.Server.Models;

namespace Manoeuvre.Server.Services;

public sealed class ProtocolService
{
    private readonly ServerState state;

    public ProtocolService(ServerState state)
    {
        this.state = state;
    }

    public async Task HandleAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        state.Log($"RECV type={message.MessageType} sender={message.Sender ?? "?"} session={session.Id}");

        switch (message.MessageType)
        {
            case ProtocolConstants.Login:
                await HandleLoginAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.GetRoomList:
                await HandleGetRoomListAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.CreateRoom:
                await HandleCreateRoomAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.JoinRoom:
                await HandleJoinRoomAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.SetNation:
                await HandleSetNationAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.StartGame:
                await HandleStartGameAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.Bye:
                await HandleByeAsync(session, message, cancellationToken);
                break;
            case ProtocolConstants.Chat:
            case ProtocolConstants.ChatInRoom:
            case ProtocolConstants.Command:
                state.Log($"IGNORED type={message.MessageType} reason=not_implemented_in_v1");
                break;
            default:
                state.Log($"UNKNOWN type={message.MessageType}");
                break;
        }
    }

    private async Task HandleLoginAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        var username = message.Sender?.Trim();
        if (string.IsNullOrWhiteSpace(username) || state.UsernameExists(username))
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.Login, "SERVER", ProtocolConstants.NotOk, username ?? "SERVER"),
                cancellationToken);
            return;
        }

        session.Username = username;
        session.Player = new PlayerDto
        {
            Name = username,
            IsHost = false
        };

        await session.SendAsync(
            MessageDto.Create(ProtocolConstants.Login, "SERVER", ProtocolConstants.Ok, username),
            cancellationToken);

        await BroadcastAsync(
            MessageDto.Create(ProtocolConstants.UserLogged, "SERVER", ProtocolConstants.Ok, username),
            cancellationToken);

        state.Log($"LOGIN_OK user={username}");
    }

    private async Task HandleGetRoomListAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        var response = MessageDto.Create(
            ProtocolConstants.GetRoomList,
            "SERVER",
            $"Channels size {state.Rooms.Count()}",
            message.Sender ?? session.Username ?? "SERVER");

        response.ChannelList = state.Rooms.Select(room => room.ToDto()).ToList();
        await session.SendAsync(response, cancellationToken);
    }

    private async Task HandleCreateRoomAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        if (!session.IsAuthenticated || session.Player is null)
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.CreateRoom, "SERVER", ProtocolConstants.NotOk, message.Sender ?? "SERVER"),
                cancellationToken);
            return;
        }

        var inboundRoom = message.ChannelList.FirstOrDefault();
        var roomName = inboundRoom?.Name?.Trim() ?? message.Content?.Trim();
        if (string.IsNullOrWhiteSpace(roomName))
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.CreateRoom, "SERVER", ProtocolConstants.BadChannelName, session.Username!),
                cancellationToken);
            return;
        }

        var room = new RoomState
        {
            Name = roomName,
            Password = inboundRoom?.Password,
            HostSessionId = session.Id,
            HostSocketPortId = session.SocketPortId,
            HostPlayer = new PlayerDto
            {
                Name = session.Player.Name,
                IsHost = true,
                Nation = inboundRoom?.HostPlayer?.Nation ?? "AU",
                Army = session.Player.Army
            }
        };

        if (!state.TryAddRoom(room))
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.CreateRoom, "SERVER", ProtocolConstants.BadChannelName, session.Username!),
                cancellationToken);
            return;
        }

        session.Player.IsHost = true;
        session.Player.Nation = room.HostPlayer.Nation;

        await session.SendAsync(
            MessageDto.Create(ProtocolConstants.CreateRoom, "SERVER", ProtocolConstants.Ok, session.Username!),
            cancellationToken);

        await BroadcastRoomListAsync(cancellationToken);
        state.Log($"ROOM_CREATED name={room.Name} host={session.Username}");
    }

    private async Task HandleJoinRoomAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        if (!session.IsAuthenticated || session.Player is null)
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.JoinRoom, "SERVER", ProtocolConstants.NotOk, message.Sender ?? "SERVER"),
                cancellationToken);
            return;
        }

        var requestedRoom = message.ChannelList.FirstOrDefault();
        RoomState? room = null;

        if (requestedRoom is not null)
        {
            room = state.FindRoomByHostSocketPort(requestedRoom.HostSocketPortId)
                ?? state.Rooms.FirstOrDefault(candidate => string.Equals(candidate.Name, requestedRoom.Name, StringComparison.OrdinalIgnoreCase));
        }

        if (room is null && !string.IsNullOrWhiteSpace(message.Content))
        {
            room = state.Rooms.FirstOrDefault(candidate =>
                message.Content.Contains(candidate.Name, StringComparison.OrdinalIgnoreCase));
        }

        if (room is null)
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.JoinRoom, "SERVER", ProtocolConstants.RoomNotFound, session.Username!),
                cancellationToken);
            return;
        }

        if (room.IsLocked)
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.JoinRoom, "SERVER", ProtocolConstants.IsRoomLocked, session.Username!),
                cancellationToken);
            return;
        }

        var guestPlayer = message.Players.FirstOrDefault() ?? new PlayerDto
        {
            Name = session.Player.Name,
            IsHost = false
        };

        guestPlayer.Nation ??= "US";
        room.GuestSessionId = session.Id;
        room.GuestSocketPortId = session.SocketPortId;
        room.GuestPlayer = guestPlayer;
        session.Player.IsHost = false;
        session.Player.Nation = guestPlayer.Nation;

        var guestResponse = MessageDto.Create(ProtocolConstants.JoinRoom, "SERVER", ProtocolConstants.Ok, session.Username!);
        guestResponse.Players.Add(ClonePlayer(room.HostPlayer));
        await session.SendAsync(guestResponse, cancellationToken);

        var hostSession = state.GetSession(room.HostSessionId);
        if (hostSession is not null)
        {
            var hostResponse = MessageDto.Create(
                ProtocolConstants.JoinRoom,
                "SERVER",
                ProtocolConstants.UserJoinedInRoom,
                room.HostPlayer.Name);
            hostResponse.Players.Add(ClonePlayer(guestPlayer));
            await hostSession.SendAsync(hostResponse, cancellationToken);
        }

        await BroadcastRoomListAsync(cancellationToken);
        state.Log($"ROOM_JOINED name={room.Name} guest={guestPlayer.Name}");
    }

    private async Task HandleSetNationAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        var room = state.FindRoomBySession(session.Id);
        if (room is null)
        {
            state.Log($"SET_NATION_IGNORED session={session.Id} reason=no_room");
            return;
        }

        var nation = !string.IsNullOrWhiteSpace(message.Content)
            ? message.Content
            : message.ContentP.ToString();

        if (room.HostSessionId == session.Id)
        {
            room.HostPlayer.Nation = nation;
            if (session.Player is not null)
            {
                session.Player.Nation = nation;
            }
        }
        else if (room.GuestSessionId == session.Id && room.GuestPlayer is not null)
        {
            room.GuestPlayer.Nation = nation;
            if (session.Player is not null)
            {
                session.Player.Nation = nation;
            }
        }

        if (room.IsLocked)
        {
            var opponentSessionId = room.HostSessionId == session.Id ? room.GuestSessionId : room.HostSessionId;
            var opponentSession = opponentSessionId.HasValue
                ? state.GetSession(opponentSessionId.Value)
                : state.GetSession(room.HostSessionId);
            if (opponentSession is not null)
            {
                var response = MessageDto.Create(ProtocolConstants.SetNation, "SERVER", nation ?? string.Empty, $"ROOM: {room.Name}");
                response.ContentP = message.ContentP;
                await opponentSession.SendAsync(response, cancellationToken);
            }
        }

        state.Log($"SET_NATION room={room.Name} user={session.Username} nation={nation}");
    }

    private async Task HandleStartGameAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        var room = state.FindRoomBySession(session.Id);
        if (room is null || room.HostSessionId != session.Id || room.GuestPlayer is null || room.GuestSessionId is null)
        {
            await session.SendAsync(
                MessageDto.Create(ProtocolConstants.StartGame, "SERVER", ProtocolConstants.NotOk, message.Sender ?? session.Username ?? "SERVER"),
                cancellationToken);
            return;
        }

        room.Game = new GameSnapshotDto
        {
            RoomName = room.Name,
            StartedAtUtc = DateTime.UtcNow,
            HostPlayer = ClonePlayer(room.HostPlayer),
            GuestPlayer = ClonePlayer(room.GuestPlayer),
            Status = "STARTED"
        };

        var startMessage = MessageDto.Create(ProtocolConstants.StartGame, "SERVER", ProtocolConstants.Ok, $"ROOM: {room.Name}");
        startMessage.Game = room.Game;
        startMessage.HostPlayer = ClonePlayer(room.HostPlayer);
        startMessage.GuestPlayer = ClonePlayer(room.GuestPlayer);

        await session.SendAsync(startMessage, cancellationToken);

        var guestSession = state.GetSession(room.GuestSessionId.Value);
        if (guestSession is not null)
        {
            await guestSession.SendAsync(startMessage, cancellationToken);
        }

        state.Log($"GAME_STARTED room={room.Name} host={room.HostPlayer.Name} guest={room.GuestPlayer.Name}");
    }

    private async Task HandleByeAsync(IClientSession session, MessageDto message, CancellationToken cancellationToken)
    {
        var signout = MessageDto.Create(ProtocolConstants.Signout, "SERVER", message.Sender ?? session.Username ?? "SERVER", "All");
        await BroadcastAsync(signout, cancellationToken);
    }

    private async Task BroadcastRoomListAsync(CancellationToken cancellationToken)
    {
        var roomList = MessageDto.Create(ProtocolConstants.GetRoomList, "SERVER", $"Channels size {state.Rooms.Count()}", "All");
        roomList.ChannelList = state.Rooms.Select(room => room.ToDto()).ToList();
        await BroadcastAsync(roomList, cancellationToken);
    }

    private async Task BroadcastAsync(MessageDto message, CancellationToken cancellationToken)
    {
        foreach (var target in state.Sessions.ToArray())
        {
            try
            {
                await target.SendAsync(message, cancellationToken);
            }
            catch (IOException)
            {
                state.Log($"SEND_FAILED session={target.Id}");
            }
        }
    }

    private static PlayerDto ClonePlayer(PlayerDto player) =>
        new()
        {
            Name = player.Name,
            Nation = player.Nation,
            IsHost = player.IsHost,
            Army = player.Army
        };
}
