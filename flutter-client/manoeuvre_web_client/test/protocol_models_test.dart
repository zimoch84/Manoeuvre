import 'package:flutter_test/flutter_test.dart';
import 'package:manoeuvre_web_client/models/protocol_models.dart';

void main() {
  test('MessageDto.fromJson parses nested players rooms and game snapshot', () {
    final message = MessageDto.fromJson({
      'sender': 'SERVER',
      'recipient': 'Piotr',
      'messageType': ProtocolConstants.startGame,
      'contentP': ProtocolConstants.ok,
      'players': [
        {'name': 'Host', 'nation': 'AU', 'isHost': true}
      ],
      'channelList': [
        {
          'name': 'Alpha',
          'locked': true,
          'hostPlayer': {'name': 'Host', 'nation': 'AU', 'isHost': true},
          'guestPlayer': {'name': 'Guest', 'nation': 'US', 'isHost': false}
        }
      ],
      'hostPlayer': {'name': 'Host', 'nation': 'AU', 'isHost': true, 'army': 'Red'},
      'guestPlayer': {'name': 'Guest', 'nation': 'US', 'isHost': false, 'army': 'Blue'},
      'game': {
        'roomName': 'Alpha',
        'startedAtUtc': '2026-04-17T10:00:00Z',
        'status': 'STARTED',
        'hostPlayer': {'name': 'Host', 'nation': 'AU', 'isHost': true},
        'guestPlayer': {'name': 'Guest', 'nation': 'US', 'isHost': false}
      }
    });

    expect(message.messageType, ProtocolConstants.startGame);
    expect(message.contentP, ProtocolConstants.ok);
    expect(message.players.single.name, 'Host');
    expect(message.channelList.single.name, 'Alpha');
    expect(message.channelList.single.locked, isTrue);
    expect(message.game?.roomName, 'Alpha');
    expect(message.game?.status, 'STARTED');
    expect(message.hostPlayer?.army, 'Red');
    expect(message.guestPlayer?.army, 'Blue');
  });

  test('GameRoomDto.toJson preserves room and nested snapshot fields', () {
    final room = GameRoomDto(
      name: 'Bravo',
      password: '1234',
      hostSocketPortId: 12,
      guestSocketPortId: 13,
      locked: true,
      hostPlayer: PlayerDto(name: 'Host', nation: 'AU', isHost: true),
      guestPlayer: PlayerDto(name: 'Guest', nation: 'US'),
      game: GameSnapshotDto(
        roomName: 'Bravo',
        startedAtUtc: DateTime.parse('2026-04-17T12:30:00Z'),
        status: 'STARTED',
        hostPlayer: PlayerDto(name: 'Host', nation: 'AU', isHost: true),
        guestPlayer: PlayerDto(name: 'Guest', nation: 'US'),
      ),
    );

    final json = room.toJson();

    expect(json['name'], 'Bravo');
    expect(json['password'], '1234');
    expect(json['locked'], isTrue);
    expect((json['hostPlayer'] as Map<String, dynamic>)['name'], 'Host');
    expect((json['guestPlayer'] as Map<String, dynamic>)['name'], 'Guest');
    expect((json['game'] as Map<String, dynamic>)['roomName'], 'Bravo');
    expect((json['game'] as Map<String, dynamic>)['status'], 'STARTED');
  });
}
