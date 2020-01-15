public class ServerMain {
	
	public ServerMain(int port) {
		new Server(port);
	}
	
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("Usage: java -jar NetworkChatServer.jar [port]");
			return;
		}
		
		try {
			int port = Integer.parseInt(args[0]);
			new ServerMain(port);			
		} catch (Exception e) {
			System.out.println("Usage: java -jar NetworkChatServer.jar [port]");
		}
	}
	
}