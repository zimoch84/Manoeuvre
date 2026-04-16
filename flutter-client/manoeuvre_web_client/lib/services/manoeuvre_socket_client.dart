import 'dart:async';
import 'dart:convert';

import 'package:web_socket_channel/web_socket_channel.dart';

import '../models/protocol_models.dart';

enum ConnectionStateStatus { disconnected, connecting, connected }

class ManoeuvreSocketClient {
  WebSocketChannel? _channel;
  StreamSubscription<dynamic>? _subscription;

  final _messages = StreamController<MessageDto>.broadcast();
  final _states = StreamController<ConnectionStateStatus>.broadcast();

  Stream<MessageDto> get messages => _messages.stream;
  Stream<ConnectionStateStatus> get states => _states.stream;

  ConnectionStateStatus _currentState = ConnectionStateStatus.disconnected;
  ConnectionStateStatus get currentState => _currentState;

  Future<void> connect(String url) async {
    await disconnect();
    _setState(ConnectionStateStatus.connecting);

    final channel = WebSocketChannel.connect(Uri.parse(url));
    _channel = channel;

    _subscription = channel.stream.listen(
      (dynamic event) {
        final jsonMap = jsonDecode(event as String) as Map<String, dynamic>;
        _messages.add(MessageDto.fromJson(jsonMap));
      },
      onDone: () => _setState(ConnectionStateStatus.disconnected),
      onError: (_) => _setState(ConnectionStateStatus.disconnected),
    );

    _setState(ConnectionStateStatus.connected);
  }

  Future<void> disconnect() async {
    await _subscription?.cancel();
    await _channel?.sink.close();
    _subscription = null;
    _channel = null;
    _setState(ConnectionStateStatus.disconnected);
  }

  void send(MessageDto message) {
    final json = jsonEncode(message.toJson());
    _channel?.sink.add(json);
  }

  void dispose() {
    _subscription?.cancel();
    _channel?.sink.close();
    _messages.close();
    _states.close();
  }

  void _setState(ConnectionStateStatus state) {
    _currentState = state;
    _states.add(state);
  }
}
