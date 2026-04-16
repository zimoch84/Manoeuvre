# Manoeuvre C# Server

This project is a new C# server for Manoeuvre.

What it keeps from the Java server:
- socket-based transport
- `Message` contract semantics
- message type ids such as `LOGIN`, `CREATE_ROOM`, `JOIN_ROOM`, `GET_ROOM_LIST`, `SET_NATION`, `START_GAME`
- room/session flow close to the Java code

What it changes:
- transport format is newline-delimited JSON instead of Java object serialization
- server admin UI is web-based instead of Swing
- state is kept in a headless ASP.NET Core process with a background TCP listener

## Run

```bash
dotnet run --project csharp-server/Manoeuvre.Server
```

Web admin:
- `http://localhost:5000` or the URL shown by ASP.NET Core

TCP socket server:
- `0.0.0.0:5002` by default

## Transport notes

Each TCP message is one JSON line.

Example login request:

```json
{"messageType":14,"sender":"Piotr","content":"secret","recipient":"SERVER"}
```

Example room list request:

```json
{"messageType":13,"sender":"Piotr","content":"clientRequest","recipient":"SERVER"}
```
