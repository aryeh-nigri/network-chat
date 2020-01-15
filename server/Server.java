import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();

	private int port;
	private DatagramSocket socket;
	
	private Thread run, manage, send, receive;
	private boolean running = false;
	
	private final int MAX_ATTEMPTS = 5;

	private boolean raw = false;
	
	public Server(int port) {
		this.port = port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		
		run = new Thread(this, "Server");
		run.start();
	}
	
	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		
		Scanner scanner = new Scanner(System.in);
		while(running) {
			String command = scanner.nextLine();
			if(command.isEmpty()) continue;
			
			if(!command.startsWith("/")) {
				sendToAll("/m/Server: " + command + "/e/");
				continue;
			}
			
			command = command.substring(1);
			
			if(command.equals("raw")) {
				raw = !raw;
				String rawMode = "Raw mode " + (raw? "ON" : "OFF");
				System.out.println(rawMode);
			} else if(command.equals("clients")) {
				System.out.println("Clients:");
				System.out.println("========");
				for (ServerClient client : clients) {
					System.out.println(client);
				}
				System.out.println("========");
			} else if(command.startsWith("kick")) { // /kick 7541
				String name = command.split(" ")[1];
				int id = -1;
				boolean isNumber= true;
				try {
					id = Integer.parseInt(name);
				} catch (NumberFormatException e) {
					isNumber = false;
				}
				if(isNumber) {
					boolean exists = false;
					String clientName = "";
					for (int i = 0; i < clients.size(); i++) {
						ServerClient client = clients.get(i);
						if(client.getID() == id) {
							exists = true;
							clientName = client.name;
							break;
						}
					}
					if (exists) {
						disconnect(id, true);
						sendToAll("/m/Server: User \"" + clientName + "\" was kicked off the chat./e/");
					} else {
						System.out.println("Client " + id + " doesn't exist! Check ID number.");
					}
				}
			} else if(command.equals("quit")) {
				quit();
			} else if(command.equals("help")) {
				printHelp();				
			} else {
				System.out.println("Invalid command. Type /help for list of available commands.");
			}
			
		}
		scanner.close();
	}
	
	private void printHelp() {
		System.out.println("Here is a list of all available commands: ");
		System.out.println("==========================================");
		System.out.println("/raw - enable/disable raw mode");
		System.out.println("/clients - shows all connected clients.");
		System.out.println("/kick [user ID] - kicks a user by his ID.");
		System.out.println("/help - shows this help message.");
		System.out.println("/quit - shuts down the server.");
		System.out.println("==========================================");
		System.out.println("*** Text without the beggining slash (/) will be broadcasted as a message from Server. ***");
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient client = clients.get(i);
						if(!clientResponse.contains(client.getID())) {
							if(client.attempt >= MAX_ATTEMPTS) {
								disconnect(client.getID(), false);
							}else {
								client.attempt++;
							}
						}
						else {
							clientResponse.remove(new Integer(client.getID()));
							client.attempt = 0;
						}
					}
				}
			}
		};
		
		manage.start();
	}
	
	private void sendStatus() {
		if(clients.size() <= 0) return;
		
		String users = "/u/";
		
		for (int i = 0; i < clients.size(); i++) {
			users += clients.get(i).name + "/n/";
		}
		
		sendToAll(users);
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while(running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					
					try {
						socket.receive(packet);
					} catch (SocketException e) {
						// do nothing
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					process(packet);
				}
			}
		};
		
		receive.start();
	}
	
	private void send(final byte[] data, InetAddress ip, int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		send.start();
	}
	
	private void send(String message, InetAddress ip, int port) {
		message += "/e/";
		send(message.getBytes(), ip, port);
	}

	private void process(DatagramPacket packet) {
		String message = new String(packet.getData());
		
		if(raw) {
			String address = packet.getAddress().toString();
			int port = packet.getPort();
			System.out.println(address + ":" + port + " => " + message);
		}
		
		if(message.startsWith("/c/")) {
			String name = message.split("/c/|/e/")[1];
			int id = UniqueIdentifier.getIdentifier();
			InetAddress packetAddress = packet.getAddress();
			int packetPort = packet.getPort();
			clients.add(new ServerClient(name, packetAddress, packetPort, id));
			String ID = "/c/" + id;
			send(ID, packetAddress, packetPort);
		} else if(message.startsWith("/m/")) {
			sendToAll(message);
		} else if(message.startsWith("/d/")) {
			String id = message.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		}
		else if(message.startsWith("/i/")){
			clientResponse.add(Integer.parseInt(message.split("/i/|/e/")[1]));
		}
		else {
			System.out.println(message);			
		}
	}
	
	private void quit() {
		System.out.println("Quitting server...");
		
		sendToAll("/u/");
		
		for (int i = 0; i < clients.size(); i++) {
			System.out.println("Client " + clients.get(i) + " removed.");
		}
		
		clients.clear();
		
		socket.close();
		running = false;
		System.out.println("Server quitted");
	}
	
	private void disconnect(int id, boolean status) {
		
		for (int i = 0; i < clients.size(); i++) {
			
			ServerClient client = clients.get(i);
			
			if(client.getID() == id) {
				String message = "Client " + client.toString();
				message += status? " disconnected." : " timed out.";
				System.out.println(message);
				clients.remove(i);
				break;
			}
			
		}
		
	}

	private void sendToAll(String message) {
		if(message.startsWith("/m/")) {			
			String text = message.substring(3).split("/e/")[0];
			System.out.println(text);
		}
		
		byte[] data = message.getBytes();
		
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(data, client.ip, client.port);
		}

	}
	
}
