namespace Manoeuvre.Server.Configuration;

public sealed class TcpServerOptions
{
    public const string SectionName = "TcpServer";

    public int Port { get; set; } = 5002;

    public string Host { get; set; } = "0.0.0.0";

    public int LogCapacity { get; set; } = 200;
}
