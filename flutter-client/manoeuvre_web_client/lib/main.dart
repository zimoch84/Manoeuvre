import 'dart:async';

import 'package:flutter/material.dart';

import 'models/protocol_models.dart';
import 'services/manoeuvre_socket_client.dart';

void main() {
  runApp(const ManoeuvreApp());
}

class ManoeuvreApp extends StatelessWidget {
  const ManoeuvreApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Manoeuvre Web Client',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF7A2C1D),
          brightness: Brightness.light,
        ),
        scaffoldBackgroundColor: const Color(0xFFF3EBDD),
        useMaterial3: true,
      ),
      home: const ManoeuvreHomePage(),
    );
  }
}

class ManoeuvreHomePage extends StatefulWidget {
  const ManoeuvreHomePage({super.key});

  @override
  State<ManoeuvreHomePage> createState() => _ManoeuvreHomePageState();
}

class _ManoeuvreHomePageState extends State<ManoeuvreHomePage> {
  final client = ManoeuvreSocketClient();

  final wsUrlController = TextEditingController(text: _defaultWebSocketUrl());
  final usernameController = TextEditingController();
  final passwordController = TextEditingController();
  final roomNameController = TextEditingController();
  final roomPasswordController = TextEditingController();

  StreamSubscription<MessageDto>? messageSubscription;
  StreamSubscription<ConnectionStateStatus>? stateSubscription;

  ConnectionStateStatus connectionState = ConnectionStateStatus.disconnected;
  final List<String> logs = <String>[];
  List<GameRoomDto> rooms = <GameRoomDto>[];
  GameRoomDto? selectedRoom;
  GameRoomDto? currentRoom;
  String? pendingJoinRoomName;
  PlayerDto? selfPlayer;
  PlayerDto? opponentPlayer;
  GameSnapshotDto? gameSnapshot;

  String? username;
  String selectedNation = 'AU';

  @override
  void initState() {
    super.initState();

    messageSubscription = client.messages.listen(_handleMessage);
    stateSubscription = client.states.listen((state) {
      if (!mounted) {
        return;
      }
      setState(() {
        connectionState = state;
      });
      _addLog('STATE ${state.name}');
    });

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _connect();
    });
  }

  @override
  void dispose() {
    messageSubscription?.cancel();
    stateSubscription?.cancel();
    client.dispose();
    wsUrlController.dispose();
    usernameController.dispose();
    passwordController.dispose();
    roomNameController.dispose();
    roomPasswordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: DecoratedBox(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFFF5E8D1), Color(0xFFE1D2BA)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: LayoutBuilder(
              builder: (context, constraints) {
                final panelWidth = constraints.maxWidth > 1500
                    ? (constraints.maxWidth - 32) / 3
                    : constraints.maxWidth > 980
                        ? (constraints.maxWidth - 16) / 2
                        : constraints.maxWidth;

                return SingleChildScrollView(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Wrap(
                        spacing: 16,
                        runSpacing: 16,
                        children: [
                          SizedBox(
                            width: panelWidth,
                            child: _buildRoomsColumnCompact(),
                          ),
                          SizedBox(
                            width: panelWidth,
                            child: _buildSessionColumnCompact(),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildRoomsColumnCompact() {
    return Column(
      children: [
        _Panel(
          title: 'Create Room',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _LabeledField(
                label: 'Room name',
                child: TextField(controller: roomNameController),
              ),
              const SizedBox(height: 12),
              _LabeledField(
                label: 'Password',
                child: TextField(controller: roomPasswordController),
              ),
              const SizedBox(height: 12),
              FilledButton(
                onPressed: username == null ? null : _createRoom,
                child: const Text('Create Room'),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        _Panel(
          title: 'Available Rooms',
          trailing: IconButton(
            onPressed: username == null ? null : _requestRoomList,
            icon: const Icon(Icons.refresh),
          ),
          child: SizedBox(
            height: 320,
            child: rooms.isEmpty
                ? const Center(child: Text('No rooms available.'))
                : ListView.separated(
                    itemCount: rooms.length,
                    separatorBuilder: (_, _) => const SizedBox(height: 10),
                    itemBuilder: (context, index) {
                      final room = rooms[index];
                      final isSelected = selectedRoom?.name == room.name;
                      return InkWell(
                        onTap: () => setState(() => selectedRoom = room),
                        borderRadius: BorderRadius.circular(14),
                        child: Ink(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(14),
                            border: Border.all(
                              color: isSelected
                                  ? const Color(0xFF7A2C1D)
                                  : const Color(0xFFC8B59C),
                              width: isSelected ? 2 : 1,
                            ),
                            color: Colors.white.withValues(alpha: 0.85),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(room.name, style: Theme.of(context).textTheme.titleMedium),
                              const SizedBox(height: 4),
                              Text('Host: ${room.hostPlayer?.name ?? 'unknown'}'),
                              Text('Guest: ${room.guestPlayer?.name ?? 'open'}'),
                              Text('Locked: ${room.locked ? 'yes' : 'no'}'),
                              const SizedBox(height: 8),
                              FilledButton.tonal(
                                onPressed: username == null || room.locked ? null : () => _joinRoom(room),
                                child: const Text('Join'),
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ),
      ],
    );
  }

  Widget _buildSessionColumnCompact() {
    final isHost = selfPlayer?.isHost ?? false;
    final roomTitle = currentRoom?.name ?? 'No active room';

    return Column(
      children: [
        _Panel(
          title: 'Login',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _LabeledField(
                label: 'Username',
                child: TextField(
                  controller: usernameController,
                  enabled: username == null && connectionState == ConnectionStateStatus.connected,
                  onChanged: (_) => setState(() {}),
                ),
              ),
              const SizedBox(height: 12),
              _LabeledField(
                label: 'Password',
                child: TextField(
                  controller: passwordController,
                  obscureText: true,
                  enabled: username == null && connectionState == ConnectionStateStatus.connected,
                ),
              ),
              const SizedBox(height: 12),
              FilledButton(
                onPressed: username == null &&
                        connectionState == ConnectionStateStatus.connected &&
                        usernameController.text.trim().isNotEmpty
                    ? _login
                    : null,
                child: const Text('Login'),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        _Panel(
          title: 'Current Session',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _StatusChip(state: connectionState),
              const SizedBox(height: 12),
              Text('User: ${username ?? '-'}'),
              const SizedBox(height: 6),
              Text('Room: $roomTitle'),
              const SizedBox(height: 6),
              Text('Role: ${isHost ? 'Host' : 'Guest'}'),
              const SizedBox(height: 6),
              Text('Opponent: ${opponentPlayer?.name ?? 'waiting'}'),
              const SizedBox(height: 6),
              Text('Server: ${wsUrlController.text}'),
            ],
          ),
        ),
        const SizedBox(height: 16),
        _Panel(
          title: 'Nation',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              DropdownButtonFormField<String>(
                initialValue: selectedNation,
                items: const ['AU', 'US', 'FR', 'BR', 'PR', 'RU', 'OT', 'SP']
                    .map((nation) => DropdownMenuItem(value: nation, child: Text(nation)))
                    .toList(),
                onChanged: username == null ? null : (value) {
                  if (value == null) {
                    return;
                  }
                  setState(() => selectedNation = value);
                },
              ),
              const SizedBox(height: 12),
              FilledButton(
                onPressed: currentRoom == null || username == null ? null : _setNation,
                child: const Text('Set Nation'),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        _Panel(
          title: 'Event Log',
          child: SizedBox(
            height: 220,
            child: ListView.builder(
              itemCount: logs.length,
              itemBuilder: (context, index) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 4),
                child: Text(
                  logs[index],
                  style: const TextStyle(
                    fontFamily: 'monospace',
                    fontSize: 12,
                  ),
                ),
              ),
            ),
          ),
        ),
        const SizedBox(height: 16),
        _Panel(
          title: 'Game',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              if (gameSnapshot == null)
                const Text('Game not started yet.')
              else ...[
                Text('Status: ${gameSnapshot!.status}'),
                const SizedBox(height: 6),
                Text('Started: ${gameSnapshot!.startedAtUtc.toLocal()}'),
                const SizedBox(height: 6),
                Text('Host army: ${gameSnapshot!.hostPlayer?.army ?? '-'}'),
                Text('Guest army: ${gameSnapshot!.guestPlayer?.army ?? '-'}'),
              ],
              const SizedBox(height: 12),
              FilledButton(
                onPressed: currentRoom == null || !isHost || opponentPlayer == null ? null : _startGame,
                child: const Text('Start Game'),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Future<void> _connect() async {
    if (connectionState != ConnectionStateStatus.disconnected) {
      return;
    }

    try {
      await client.connect(wsUrlController.text.trim());
      _addLog('Connected to ${wsUrlController.text.trim()}');
    } catch (error) {
      _addLog('Connect error: $error');
    }
  }

  Future<void> _disconnect() async {
    if (username != null) {
      client.send(MessageDto(
        messageType: ProtocolConstants.bye,
        sender: username,
        content: '.bye',
        recipient: 'SERVER',
      ));
    }
    await client.disconnect();
    if (mounted) {
      setState(() {
        username = null;
        selfPlayer = null;
        opponentPlayer = null;
        currentRoom = null;
        selectedRoom = null;
        pendingJoinRoomName = null;
        gameSnapshot = null;
        rooms = <GameRoomDto>[];
      });
    }
  }

  void _login() {
    final user = usernameController.text.trim();
    client.send(MessageDto(
      messageType: ProtocolConstants.login,
      sender: user,
      content: passwordController.text,
      recipient: 'SERVER',
    ));
  }

  void _requestRoomList() {
    if (username == null) {
      return;
    }

    client.send(MessageDto(
      messageType: ProtocolConstants.getRoomList,
      sender: username,
      content: 'clientRequest',
      recipient: 'SERVER',
    ));
  }

  void _createRoom() {
    if (username == null) {
      return;
    }

    final roomName = roomNameController.text.trim();
    if (roomName.isEmpty) {
      _addLog('Room name is required.');
      return;
    }

    final player = PlayerDto(
      name: username!,
      nation: selectedNation,
      isHost: true,
    );

    final room = GameRoomDto(
      name: roomName,
      password: roomPasswordController.text,
      hostPlayer: player,
      hostSocketPortId: 0,
    );

    client.send(MessageDto(
      messageType: ProtocolConstants.createRoom,
      sender: username,
      content: roomName,
      recipient: 'SERVER',
      players: [player],
      channelList: [room],
    ));
  }

  void _joinRoom(GameRoomDto room) {
    if (username == null) {
      return;
    }

    setState(() {
      selectedRoom = room;
      pendingJoinRoomName = room.name;
    });

    final player = PlayerDto(
      name: username!,
      nation: selectedNation,
      isHost: false,
    );

    client.send(MessageDto(
      messageType: ProtocolConstants.joinRoom,
      sender: username,
      content: room.name,
      recipient: 'SERVER',
      players: [player],
      channelList: [room],
    ));
  }

  void _setNation() {
    if (username == null) {
      return;
    }

    client.send(MessageDto(
      messageType: ProtocolConstants.setNation,
      sender: username,
      content: selectedNation,
      recipient: 'IN_ROOM',
    ));

    setState(() {
      if (selfPlayer != null) {
        selfPlayer = PlayerDto(
          name: selfPlayer!.name,
          nation: selectedNation,
          isHost: selfPlayer!.isHost,
          army: selfPlayer!.army,
        );
      }
    });
  }

  void _startGame() {
    if (username == null) {
      return;
    }

    client.send(MessageDto(
      messageType: ProtocolConstants.startGame,
      sender: username,
      content: 'Start Game',
      recipient: 'SERVER',
    ));
  }

  void _handleMessage(MessageDto message) {
    _addLog('IN ${message.messageType} from ${message.sender ?? 'SERVER'}');

    if (!mounted) {
      return;
    }

    setState(() {
      switch (message.messageType) {
        case ProtocolConstants.login:
          if (message.contentP == ProtocolConstants.ok) {
            username = usernameController.text.trim();
            selfPlayer = PlayerDto(
              name: username!,
              nation: selectedNation,
              isHost: false,
            );
            _requestRoomList();
            _addLog('LOGIN OK $username');
          } else {
            _addLog('LOGIN FAILED ${usernameController.text.trim()}');
          }
          break;
        case ProtocolConstants.getRoomList:
          rooms = message.channelList;
          if (currentRoom != null) {
            currentRoom = rooms.cast<GameRoomDto?>().firstWhere(
                  (room) => room?.name == currentRoom!.name,
                  orElse: () => currentRoom,
                );
          }
          if (pendingJoinRoomName != null) {
            selectedRoom = rooms.cast<GameRoomDto?>().firstWhere(
              (room) => room?.name == pendingJoinRoomName,
              orElse: () => selectedRoom,
            );
          }
          break;
        case ProtocolConstants.createRoom:
          if (message.contentP == ProtocolConstants.ok) {
            final room = GameRoomDto(
              name: roomNameController.text.trim(),
              password: roomPasswordController.text,
              hostPlayer: PlayerDto(
                name: username!,
                nation: selectedNation,
                isHost: true,
              ),
            );
            currentRoom = room;
            selfPlayer = room.hostPlayer;
            opponentPlayer = null;
            _requestRoomList();
          }
          break;
        case ProtocolConstants.joinRoom:
          if (message.contentP == ProtocolConstants.ok) {
            opponentPlayer = message.players.isNotEmpty ? message.players.first : opponentPlayer;
            selfPlayer = PlayerDto(
              name: username!,
              nation: selectedNation,
              isHost: false,
            );
            currentRoom = rooms.cast<GameRoomDto?>().firstWhere(
              (room) => room?.name == pendingJoinRoomName || room?.name == selectedRoom?.name,
              orElse: () => selectedRoom,
            );
            pendingJoinRoomName = null;
            _addLog('JOIN OK ${currentRoom?.name ?? selectedRoom?.name ?? ''}');
          } else if (message.contentP == ProtocolConstants.userJoinedInRoom) {
            opponentPlayer = message.players.isNotEmpty ? message.players.first : opponentPlayer;
            _addLog('HOST NOTICE ${opponentPlayer?.name ?? 'guest'} joined room');
          } else if (message.contentP == ProtocolConstants.isRoomLocked) {
            pendingJoinRoomName = null;
            _addLog('JOIN FAILED room is locked');
          } else if (message.contentP == ProtocolConstants.roomNotFound) {
            pendingJoinRoomName = null;
            _addLog('JOIN FAILED room not found');
          } else {
            pendingJoinRoomName = null;
            _addLog('JOIN FAILED code ${message.contentP}');
          }
          break;
        case ProtocolConstants.setNation:
          if (opponentPlayer != null) {
            opponentPlayer = PlayerDto(
              name: opponentPlayer!.name,
              nation: message.content?.isNotEmpty == true ? message.content : opponentPlayer!.nation,
              isHost: opponentPlayer!.isHost,
              army: opponentPlayer!.army,
            );
          }
          break;
        case ProtocolConstants.startGame:
          if (message.contentP == ProtocolConstants.ok) {
            gameSnapshot = message.game;
            selfPlayer = selfPlayer == null
                ? selfPlayer
                : PlayerDto(
                    name: selfPlayer!.name,
                    nation: selfPlayer!.nation,
                    isHost: selfPlayer!.isHost,
                    army: selfPlayer!.isHost
                        ? message.hostPlayer?.army
                        : message.guestPlayer?.army,
                  );
            opponentPlayer = selfPlayer?.isHost == true ? message.guestPlayer : message.hostPlayer;
          }
          break;
        case ProtocolConstants.userLogged:
          _requestRoomList();
          break;
        case ProtocolConstants.signout:
          _requestRoomList();
          break;
      }
    });
  }

  void _addLog(String message) {
    final timestamp = DateTime.now().toIso8601String();
    setState(() {
      logs.insert(0, '$timestamp $message');
      if (logs.length > 100) {
        logs.removeLast();
      }
    });
  }

  static String _defaultWebSocketUrl() {
    final base = Uri.base;
    final scheme = base.scheme == 'https' ? 'wss' : 'ws';
    final host = base.host.isEmpty ? 'localhost' : base.host;

    if (base.port == 5062 || base.port == 7230) {
      final portSegment = base.hasPort ? ':${base.port}' : '';
      return '$scheme://$host$portSegment/ws';
    }

    return '$scheme://$host:5062/ws';
  }
}

class _Panel extends StatelessWidget {
  const _Panel({
    required this.title,
    required this.child,
    this.trailing,
  });

  final String title;
  final Widget child;
  final Widget? trailing;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.9),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFFD2C0A4)),
        boxShadow: const [
          BoxShadow(
            blurRadius: 18,
            color: Color(0x1438281F),
            offset: Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  title,
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.w700,
                      ),
                ),
              ),
              trailing ?? const SizedBox.shrink(),
            ],
          ),
          const SizedBox(height: 14),
          child,
        ],
      ),
    );
  }
}

class _LabeledField extends StatelessWidget {
  const _LabeledField({required this.label, required this.child});

  final String label;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: Theme.of(context).textTheme.labelLarge),
        const SizedBox(height: 6),
        child,
      ],
    );
  }
}

class _StatusChip extends StatelessWidget {
  const _StatusChip({required this.state});

  final ConnectionStateStatus state;

  @override
  Widget build(BuildContext context) {
    final (label, color) = switch (state) {
      ConnectionStateStatus.connected => ('Connected', const Color(0xFF2F6B45)),
      ConnectionStateStatus.connecting => ('Connecting', const Color(0xFFAA6A17)),
      ConnectionStateStatus.disconnected => ('Disconnected', const Color(0xFF983B2B)),
    };

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(Icons.circle, size: 10, color: color),
          const SizedBox(width: 8),
          Text(label, style: TextStyle(color: color, fontWeight: FontWeight.w700)),
        ],
      ),
    );
  }
}
