class ProtocolConstants {
  static const int notOk = 0;
  static const int ok = 1;
  static const int badChannelName = 2;
  static const int badPassword = 3;
  static const int isRoomLocked = 4;
  static const int roomNotFound = 5;
  static const int userJoinedInRoom = 6;

  static const int createRoom = 10;
  static const int joinRoom = 11;
  static const int inRoomChat = 12;
  static const int getRoomList = 13;
  static const int login = 14;
  static const int bye = 15;
  static const int signout = 16;
  static const int userLogged = 17;
  static const int chatInRoom = 18;
  static const int startGame = 19;
  static const int setNation = 20;
  static const int command = 21;
  static const int chat = 99;
}

class PlayerDto {
  PlayerDto({
    required this.name,
    this.nation,
    this.isHost = false,
    this.army,
  });

  final String name;
  final String? nation;
  final bool isHost;
  final String? army;

  factory PlayerDto.fromJson(Map<String, dynamic> json) => PlayerDto(
        name: json['name'] as String? ?? '',
        nation: json['nation'] as String?,
        isHost: json['isHost'] as bool? ?? false,
        army: json['army'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'name': name,
        'nation': nation,
        'isHost': isHost,
        'army': army,
      };
}

class GameSnapshotDto {
  GameSnapshotDto({
    required this.roomName,
    required this.startedAtUtc,
    required this.status,
    this.hostPlayer,
    this.guestPlayer,
  });

  final String roomName;
  final DateTime startedAtUtc;
  final String status;
  final PlayerDto? hostPlayer;
  final PlayerDto? guestPlayer;

  factory GameSnapshotDto.fromJson(Map<String, dynamic> json) => GameSnapshotDto(
        roomName: json['roomName'] as String? ?? '',
        startedAtUtc: DateTime.tryParse(json['startedAtUtc'] as String? ?? '') ?? DateTime.now(),
        status: json['status'] as String? ?? 'UNKNOWN',
        hostPlayer: json['hostPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['hostPlayer'] as Map<String, dynamic>)
            : null,
        guestPlayer: json['guestPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['guestPlayer'] as Map<String, dynamic>)
            : null,
      );
}

class GameRoomDto {
  GameRoomDto({
    required this.name,
    this.password,
    this.hostSocketPortId = 0,
    this.guestSocketPortId = 0,
    this.locked = false,
    this.hostPlayer,
    this.guestPlayer,
    this.game,
  });

  final String name;
  final String? password;
  final int hostSocketPortId;
  final int guestSocketPortId;
  final bool locked;
  final PlayerDto? hostPlayer;
  final PlayerDto? guestPlayer;
  final GameSnapshotDto? game;

  factory GameRoomDto.fromJson(Map<String, dynamic> json) => GameRoomDto(
        name: json['name'] as String? ?? '',
        password: json['password'] as String?,
        hostSocketPortId: json['hostSocketPortId'] as int? ?? 0,
        guestSocketPortId: json['guestSocketPortId'] as int? ?? 0,
        locked: json['locked'] as bool? ?? false,
        hostPlayer: json['hostPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['hostPlayer'] as Map<String, dynamic>)
            : null,
        guestPlayer: json['guestPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['guestPlayer'] as Map<String, dynamic>)
            : null,
        game: json['game'] is Map<String, dynamic>
            ? GameSnapshotDto.fromJson(json['game'] as Map<String, dynamic>)
            : null,
      );

  Map<String, dynamic> toJson() => {
        'name': name,
        'password': password,
        'hostSocketPortId': hostSocketPortId,
        'guestSocketPortId': guestSocketPortId,
        'locked': locked,
        'hostPlayer': hostPlayer?.toJson(),
        'guestPlayer': guestPlayer?.toJson(),
        'game': game == null
            ? null
            : {
                'roomName': game!.roomName,
                'startedAtUtc': game!.startedAtUtc.toIso8601String(),
                'status': game!.status,
                'hostPlayer': game!.hostPlayer?.toJson(),
                'guestPlayer': game!.guestPlayer?.toJson(),
              },
      };
}

class MessageDto {
  MessageDto({
    this.type,
    this.sender,
    this.content,
    this.recipient,
    this.messageType = 0,
    this.contentP = 0,
    this.players = const [],
    this.channelList = const [],
    this.hostPlayer,
    this.guestPlayer,
    this.game,
    this.command,
  });

  final String? type;
  final String? sender;
  final String? content;
  final String? recipient;
  final int messageType;
  final int contentP;
  final List<PlayerDto> players;
  final List<GameRoomDto> channelList;
  final PlayerDto? hostPlayer;
  final PlayerDto? guestPlayer;
  final GameSnapshotDto? game;
  final Map<String, dynamic>? command;

  factory MessageDto.fromJson(Map<String, dynamic> json) => MessageDto(
        type: json['type'] as String?,
        sender: json['sender'] as String?,
        content: json['content'] as String?,
        recipient: json['recipient'] as String?,
        messageType: json['messageType'] as int? ?? 0,
        contentP: json['contentP'] as int? ?? 0,
        players: (json['players'] as List<dynamic>? ?? const [])
            .map((item) => PlayerDto.fromJson(Map<String, dynamic>.from(item as Map)))
            .toList(),
        channelList: (json['channelList'] as List<dynamic>? ?? const [])
            .map((item) => GameRoomDto.fromJson(Map<String, dynamic>.from(item as Map)))
            .toList(),
        hostPlayer: json['hostPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['hostPlayer'] as Map<String, dynamic>)
            : json['hostPlayer'] is Map
                ? PlayerDto.fromJson(Map<String, dynamic>.from(json['hostPlayer'] as Map))
            : null,
        guestPlayer: json['guestPlayer'] is Map<String, dynamic>
            ? PlayerDto.fromJson(json['guestPlayer'] as Map<String, dynamic>)
            : json['guestPlayer'] is Map
                ? PlayerDto.fromJson(Map<String, dynamic>.from(json['guestPlayer'] as Map))
            : null,
        game: json['game'] is Map<String, dynamic>
            ? GameSnapshotDto.fromJson(json['game'] as Map<String, dynamic>)
            : json['game'] is Map
                ? GameSnapshotDto.fromJson(Map<String, dynamic>.from(json['game'] as Map))
            : null,
        command: json['command'] is Map
            ? Map<String, dynamic>.from(json['command'] as Map)
            : null,
      );

  Map<String, dynamic> toJson() => {
        'type': type,
        'sender': sender,
        'content': content,
        'recipient': recipient,
        'messageType': messageType,
        'contentP': contentP,
        'players': players.map((player) => player.toJson()).toList(),
        'channelList': channelList.map((room) => room.toJson()).toList(),
        'hostPlayer': hostPlayer?.toJson(),
        'guestPlayer': guestPlayer?.toJson(),
        'game': game == null
            ? null
            : {
                'roomName': game!.roomName,
                'startedAtUtc': game!.startedAtUtc.toIso8601String(),
                'status': game!.status,
                'hostPlayer': game!.hostPlayer?.toJson(),
                'guestPlayer': game!.guestPlayer?.toJson(),
              },
        'command': command,
      };
}
