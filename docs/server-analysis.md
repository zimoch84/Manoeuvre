# Server Analysis

## Summary

This document analyzes the current Java server as preparation for a later C# headless port.
The scope is limited to `LOGIN`, `GET_ROOM_LIST`, `CREATE_ROOM`, `JOIN_ROOM`, `SET_NATION`, and `START_GAME`.
No behavior is changed here. This is an implementation-facing analysis of current flows, state mutations, and refactor priorities.

## Current roles and dependencies

- `ServerFrame` is both the GUI entrypoint and an operational dependency of the server. It owns startup, restart, database path resolution, and the text log sink. See [ServerFrame.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerFrame.java:10).
- `ManouvreServer` owns the listening socket, the in-memory client array, room list, and client removal/broadcast logic. See [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:140).
- `ClientServerThread` is one thread per connected socket. It reads serialized `Message` objects and forwards them to `ServerMessageHandler`. See [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:21).
- `ServerMessageHandler` is the protocol dispatcher. It mixes validation, room/session state transitions, response creation, and UI logging in one large `switch`. See [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:19).
- `GameRoom` is the in-memory room/session model. It stores room metadata, two players, socket port identifiers, and an optional `Game`. See [GameRoom.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/GameRoom.java:17).
- `Database` is an XML-backed user store in name only. `checkLogin()` currently returns `true` unconditionally, so the active login system is effectively "username is unique among connected clients." See [Database.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/Database.java:48).
- `Message` is the transport DTO shared by client and server. It mixes string and int type systems, optional payload fields, serialized domain objects, and commands. See [Message.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/client/Message.java:13).

## Message flow analysis

### LOGIN

Client entry:
- `LoginWindow` creates `SocketClient`, then sends `new Message(Message.LOGIN, username, password, "SERVER")`. See [LoginWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/LoginWindow.java:221) and [LoginWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/LoginWindow.java:225).

Server handling:
- `ServerMessageHandler.handle()` checks whether the username is already associated with an active thread via `findUserThread(msg.sender)`.
- If not, it calls `db.checkLogin(msg.sender, msg.content)`.
- On success, it sets `username` and `player` on the matching `ClientServerThread`, sends `LOGIN/OK` to that client, and broadcasts `USER_LOGGED/OK` to everyone.
- On failure or duplicate username, it sends `LOGIN/NOT_OK` to the requesting client.

Memory state changes:
- `clients[index].username = msg.sender`
- `clients[index].setPlayer(new Player(msg.sender))`

Responses:
- Requesting client: `LOGIN/OK` or `LOGIN/NOT_OK`
- All connected clients on success: `USER_LOGGED/OK`

Null/inconsistency risks:
- `db.checkLogin()` always returns `true`, so password validation is disabled in practice. See [Database.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/Database.java:48).
- Identity is bound to the thread located by `findClient(portID)`. If the client lookup fails, later field access can throw.

### GET_ROOM_LIST

Client entry:
- `MainChatWindow` periodically sends `new Message(Message.GET_ROOM_LIST, player.getName(), "clientRequest", "SERVER")`. See [MainChatWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/MainChatWindow.java:635).

Server handling:
- The handler constructs a response message, attaches `server.getRooms()`, sets a textual count in `content`, and sends it back only to the requesting client.

Memory state changes:
- None, aside from sharing the current `channels` list reference in the message payload.

Responses:
- Requesting client: `GET_ROOM_LIST` with `channelList`

Null/inconsistency risks:
- The room list contains mutable `GameRoom` objects, not a read-only projection.
- There is no snapshotting; a client can receive room state while another thread is mutating it.

### CREATE_ROOM

Client entry:
- `CreateRoomWindow` constructs a local `GameRoom` using `socketClient.getSocket().getLocalPort()` as the host identifier.
- It sends `Message.CREATE_ROOM`, adds the current player, and adds the `GameRoom` object into `channelList`. See [CreateRoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/CreateRoomWindow.java:162) through [CreateRoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/CreateRoomWindow.java:169).

Server handling:
- The handler tries to parse `msg.content` with `split("|")`, assigns `name` and `password`, but never uses those variables.
- It takes the first room from `msg.getChannelList()`.
- It mutates the embedded host player to a default nation `AU`.
- It adds the room to `channels` only if `portID == newRoom.getHostSocketPortId()`.
- It replies with `CREATE_ROOM/OK` to the creator.
- It broadcasts a fresh `GET_ROOM_LIST` to all clients.

Memory state changes:
- `channels.add(newRoom)`
- `newRoom.hostPlayer.nation = AU`

Responses:
- Creator: `CREATE_ROOM/OK`
- All clients: `GET_ROOM_LIST` with updated room list

Null/inconsistency risks:
- `split("|")` is a regex bug; it splits by every character, not by a literal pipe. See [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:72).
- The parsed `name/password` are dead code; the authoritative room object comes from the client payload.
- The server trusts a client-sent `GameRoom` object, including `hostSocketPortId`.
- Room creation correctness depends on matching the server-observed remote port to the client-observed local port. This is fragile and transport-specific.
- There is no uniqueness check for room name, host, or duplicate room creation.

### JOIN_ROOM

Client entry:
- `MainChatWindow` sends `Message.JOIN_ROOM`, but it fills only `content` with `selected.toString()` in the shown code path. See [MainChatWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/MainChatWindow.java:580).

Server handling:
- The handler expects `msg.getChannelList().get(0)` as the requested room.
- It checks whether a room exists by comparing host socket port ids.
- If the room is not locked, it stores `guestSocketPortId = portID`, extracts the first player from `msg.players`, defaults nation to `US`, stores the guest player, replies `JOIN_ROOM/OK` to guest with host player attached, and sends `JOIN_ROOM/USER_JOINED_IN_ROOM` to host with guest player attached.
- If locked, it returns `IS_ROOM_LOCKED`.
- If not found, it returns `ROOM_NOT_FOUND`.

Memory state changes:
- `room.guestSocketPortId = portID`
- `room.guestPlayer = msg.getPlayer()`
- `room.locked = true`
- `guestPlayer.nation = US`

Responses:
- Guest: `JOIN_ROOM/OK` + host player, or `IS_ROOM_LOCKED`, or `ROOM_NOT_FOUND`
- Host on success: `JOIN_ROOM/USER_JOINED_IN_ROOM` + guest player

Null/inconsistency risks:
- There is an observed mismatch between client send code and server expectations:
  - server expects `channelList[0]` and `players[0]`
  - the visible join send path populates only `content`
- If the actual runtime path does not populate `channelList` and `players`, this branch relies on the broad `NullPointerException` catch at the bottom of the handler. See [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:235).
- Room lookup is keyed by host socket port id instead of a stable room id.

### SET_NATION

Client entry:
- `RoomWindow` creates `new Message(Message.SET_NATION, currentPlayer.getName(), currentPlayer.getNation().toString(), "IN_ROOM")`. See [RoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/RoomWindow.java:548).

Server handling:
- The handler resolves the room by caller port.
- It updates `gameRoom.getCurrentPlayer(portID).setNation(Player.Nation.fromValue(msg.getContentP()))`.
- If the room is locked, it forwards a `SET_NATION` message to the opponent with `contentP`.

Memory state changes:
- The current player's nation in the room object

Responses:
- Opponent only, and only when the room is full: `SET_NATION`

Null/inconsistency risks:
- Client sends nation as string content, but server reads `msg.getContentP()`. This means the active branch depends on `contentP`, which is never set in the shown send code. See [RoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/RoomWindow.java:548) and [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:199).
- If `contentP` stays default `0`, nation decoding may be wrong or invalid depending on `Player.Nation.fromValue`.
- There is no validation that only room members can update the room state, beyond "room found by port."

### START_GAME

Client entry:
- `RoomWindow` sends `new Message(Message.START_GAME, currentPlayer.getName(), "Start Game", "SERVER")`. See [RoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/RoomWindow.java:674).

Server handling:
- The handler resolves the room by caller port.
- It gets `players = gameRoom.getPlayers()`.
- It deep-copies the players list into `players2`, creates a new `Game(players2)`, deep-copies that `Game` again into `game2`, stores `game` in the room, and sends the same `START_GAME/OK` message with `game2`, `hostPlayer`, and `guestPlayer` to both host and guest.

Memory state changes:
- `gameRoom.game = game`
- player army references are reassigned from generated game state

Responses:
- Host: `START_GAME/OK` + serialized `Game` + host/guest player objects
- Guest: same message object and payload

Null/inconsistency risks:
- There is no explicit validation that only the host may start the game, although the comment says that is intended.
- There is no guard that the room is full before `getPlayers()` is used; `guestPlayer` can still be null.
- The server sends the same serialized `Game` snapshot to both sides instead of authoritative incremental state.
- `CommandQueue.storeAndExecuteAndSend()` on the client sends serialized command objects, and the server currently just relays them to the opponent without applying them to `gameRoom.getGame()`. See [CommandQueue.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/commands/CommandQueue.java:49) and [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:210).

## Prioritized problems

### P0: functional bugs and crash risks

- `Database.checkLogin()` returns `true` unconditionally, so authentication is disabled. [Database.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/Database.java:48)
- `CREATE_ROOM` uses `split("|")`, which is incorrect regex usage and produces broken parsing. [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:72)
- `JOIN_ROOM` appears to expect payload fields that the visible client code path does not populate. This can turn normal join attempts into null-driven behavior. [MainChatWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/MainChatWindow.java:580), [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:102)
- `SET_NATION` expects `contentP`, while the client sends string `content`. The flow is internally inconsistent. [RoomWindow.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/gui/RoomWindow.java:548), [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:199)
- `GameRoom.equals()` compares room names with `==` instead of `.equals()`, which makes containment and lookup behavior unreliable. [GameRoom.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/GameRoom.java:107)
- `ServerMessageHandler` catches `NullPointerException` at the top level and only logs it, which hides contract mismatches and leaves state recovery undefined. [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:235)
- `START_GAME` does not explicitly verify "caller is host" and "room has guest" before creating the game.
- Disconnect handling calls `server.destroyRoom(server.getRoomByPort(socketClientServerPortID))` without checking for a null room first. [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:98)

### P1: architectural blockers for a C# headless port

- Server logic is tightly coupled to Swing UI through `ServerFrame` for lifecycle, logging, and database path resolution. [ServerFrame.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerFrame.java:10), [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:151)
- Session identity depends on socket port numbers, not stable server-generated session or room identifiers. [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:235), [GameRoom.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/GameRoom.java:154)
- `Message` is an ambiguous transport model:
  - both `type` and `messageType`
  - both `content` and `contentP`
  - optional serialized `Game`, `Player`, `GameRoom`, `Command`
  - no strict schema per message type
  This will make a C# contract brittle and hard to validate. [Message.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/client/Message.java:60)
- The protocol trusts client-sent domain objects instead of server-owned requests identified by ids.
- The gameplay path is not server-authoritative. Commands are relayed rather than applied to room state. [ServerMessageHandler.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ServerMessageHandler.java:221)
- One-thread-per-client plus shared mutable arrays/lists and ad hoc synchronization will not translate cleanly into a headless service architecture.

### P2: technical debt and cleanup after stabilization

- `Thread.stop()` is used for server and client thread shutdown. [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:223)
- Client management uses a fixed-size array plus manual compaction instead of a keyed collection. [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:145), [ManouvreServer.java](/c:/Users/Piotr/source/repos/Manoeuvre/src/manouvre/network/server/ManouvreServer.java:235)
- Logging is split between `System.out`, Swing text area writes, and Java util logging.
- The server ships whole mutable room lists and full game objects instead of projections or DTOs.
- Dead code and commented legacy branches in `ServerMessageHandler` make the current behavior harder to reason about.
- `ManouvreServer` contains unused imports and mixed responsibilities unrelated to transport.

## Candidate future contracts

The analysis points to these future contracts for the C# server:

- Transport contract:
  - typed request/response messages
  - explicit message schema per operation
  - no embedded UI-only or Java-specific objects
- Session model:
  - server-generated client session id
  - authenticated user identity
  - connection state independent of socket port numbers
- Room model:
  - stable room id
  - host session id
  - optional guest session id
  - player setup state including selected nation
  - status enum such as `Open`, `Full`, `Starting`, `InGame`
- Response contract:
  - `login result`
  - `room list snapshot`
  - `create room result`
  - `join room result`
  - `nation updated`
  - `start game result`

## Scenario-based validation notes

- Correct login:
  - expected current behavior: succeeds for any password if username is not already connected
  - expected server state: client thread gets `username` and a new `Player`
- Duplicate username login:
  - expected current behavior: `LOGIN/NOT_OK`
  - risk: duplicate detection only checks active connected threads
- Room creation:
  - expected current behavior: server trusts client-sent room object and broadcasts room list
  - risk: no uniqueness checks and fragile host-port matching
- Room list fetch:
  - expected current behavior: requesting client receives current mutable room list
- Join existing room:
  - intended behavior: guest joins room, host and guest receive asymmetric responses
  - risk: visible client send path may not provide the required payload structure
- Join locked or missing room:
  - intended behavior: `IS_ROOM_LOCKED` or `ROOM_NOT_FOUND`
  - risk: lookup keyed by host port id only
- Set nation before/after room fills:
  - intended behavior: room state updated locally, opponent notified only when room is full
- Start game:
  - intended behavior: both players receive a full `Game` snapshot
  - risk: missing guards for host-only and full-room preconditions
- Disconnect inside a room:
  - current behavior: socket exception triggers room destruction and client removal
  - risk: null room path and unsafe thread termination

## Recommended work order

1. Extract a headless server core from `ServerFrame`.
   Move lifecycle, logging, configuration, room registry, and message handling behind non-UI interfaces.
2. Replace `Message` with a typed transport contract.
   Use one payload shape per operation and remove mixed string/int typing and serialized Java objects from setup flows.
3. Stabilize sessions and room identity.
   Use server-generated session ids and room ids instead of socket port numbers.
4. Fix and lock the current setup flows.
   Validate payloads explicitly, enforce host/guest permissions, and remove broad `NullPointerException` handling.
5. Only then prepare the concrete C# server port plan.
   Port against a stable protocol and headless architecture instead of reproducing Java-specific quirks.
