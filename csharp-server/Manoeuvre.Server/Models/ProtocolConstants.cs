namespace Manoeuvre.Server.Models;

public static class ProtocolConstants
{
    public const int NotOk = 0;
    public const int Ok = 1;
    public const int BadChannelName = 2;
    public const int BadPassword = 3;
    public const int IsRoomLocked = 4;
    public const int RoomNotFound = 5;
    public const int UserJoinedInRoom = 6;

    public const int CreateRoom = 10;
    public const int JoinRoom = 11;
    public const int InRoomChat = 12;
    public const int GetRoomList = 13;
    public const int Login = 14;
    public const int Bye = 15;
    public const int Signout = 16;
    public const int UserLogged = 17;
    public const int ChatInRoom = 18;
    public const int StartGame = 19;
    public const int SetNation = 20;
    public const int Command = 21;
    public const int Chat = 99;
}
