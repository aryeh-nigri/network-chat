import java.net.InetAddress;

public class ServerClient {

	public String name;
	public InetAddress ip;
	public int port;
	public final int ID;
	public int attempt = 0;

	public ServerClient(String name, InetAddress ip, int port, final int ID) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
	@Override
	public String toString() {
		return name + "(" + ID + "): @ " + ip.toString() + ":" + port;
	}
	
}
