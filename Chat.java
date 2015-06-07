/**
 * @author DinaSu
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

public class Chat extends Thread {
	private Socket socket = null;
	private ObjectOutputStream out; 
	private ObjectInputStream in; 
	private String name; 
	public Chat(Socket s) throws IOException {
		socket = s;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			System.out.printf("\n Got I/O streams \n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.printf("Connection successful \n");
		String input;
		try {
			
			input = (String) in.readObject();
			name = input;
			System.out.printf("name is : %s", input);
			Server.addMember(input);

			out.writeObject(Server.getMembers());
			out.flush();
			
			input = (String) in.readObject();
			while (input != null) {
				Server.sendMessage(name +" says : "+ input);
					input = (String) in.readObject();	 
			}
			in.close();
			out.close();
			socket.close();
		}catch (EOFException s) {
		} 
		catch(SocketException s){
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		}finally{
			Server.removeMember(name);
		}

	}

}
