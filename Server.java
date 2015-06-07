/**
 * @author DinaSu
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {
	private final static ArrayList<String> members = new ArrayList<String>();	
	private static ServerSocket srv;
	private static String group = "192.168.1.150"; 
	private static int port = 7778;  

	public final static String SENDINGMEMBERS = "sendingMembersList";	


	public static void main(String[] args) throws IOException {

		boolean listening = true;

		try {
			
			srv = new ServerSocket(7777); 

			Socket socket = null; 

			while (listening) { 
				socket = srv.accept();
				new Chat(socket).start();
			}
		} catch (InterruptedIOException e) {
			System.out.println("Connection time out ...");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public static synchronized ArrayList<String> getMembers() {
		return members;
	}

	public static synchronized void addMember(String name) {
		members.add(name);
		Server.sendMessage("\n New Member : " + name); 
		Server.sendMessage(members); 
	}

	public static synchronized void removeMember(String name) {
		members.remove(name);
		Server.sendMessage("\n User just left the chat : " + name); 
		Server.sendMessage(members);

	}

	public static synchronized void sendMessage(String s) { 
		byte[] buf = new byte[256];
		buf = s.getBytes(); 
		try {
			InetAddress address = InetAddress.getByName(group); 
			DatagramPacket packet = new DatagramPacket(buf, buf.length, 
					address, port);
			
		} catch (IOException e) {
		}
	}

	public static synchronized void sendMessage(ArrayList<String> members) { 
		sendMessage(SENDINGMEMBERS); 
		sendMessage("" + members.size()); 
		for (String s : members) {
			sendMessage(s); 
		}
	}
}
