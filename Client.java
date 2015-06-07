/**
 * @author DinaSu
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
import javax.swing.JOptionPane;

public class Client extends JFrame {
	// GUI
	private JButton connectBut;
	private JButton disconnectBut;
	// private ButtonGroup buttonGroup;
	private JTextArea membersList;
	private JTextArea chatArea;
	private JTextField writeMessage;
	private JPanel panel;
	// name of client instance
	private String name;
	// TCP
	private Socket socket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private String host; 
	private int TCPport; 
	
	
	
	
	private static final String SENDINGMEMBERS = "sendingMembersList";

	public Client() {
		super("Cinent");


		connectBut = new JButton("Connect");
		disconnectBut = new JButton("Disconnect");
		disconnectBut.setEnabled(false);
		panel = new JPanel();
		panel.add(connectBut);
		panel.add(disconnectBut);
		add(panel, BorderLayout.NORTH);

		membersList = new JTextArea();
		membersList.setEditable(false);
		membersList.setBackground(Color.WHITE);
		membersList.append("\t\t");
		add(new JScrollPane(membersList), BorderLayout.WEST);


		chatArea = new JTextArea();
		add(new JScrollPane(chatArea), BorderLayout.CENTER);


		writeMessage = new JTextField();
		writeMessage.setEditable(false);
		add(writeMessage, BorderLayout.SOUTH);


		Listener l = new Listener();
		connectBut.addActionListener(l);
		disconnectBut.addActionListener(l);
		writeMessage.addActionListener(l);

		setSize(600, 400);
		setVisible(true);
	}

	public static void main(String[] args) throws IOException {

		Client client = new Client();
		client.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void connectTCP() {
		try {
			
			host = JOptionPane.showInputDialog(null,
							"Please enter server TCP connection name  \n Ip is localhost");
			TCPport = Integer.parseInt(JOptionPane.showInputDialog(null,
									"Please enter TCP connection port\n port is 7777"));
			socket = new Socket(host, TCPport);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Server not found "
					+ "press disconnect " + "and please try again");

		} catch (NumberFormatException d) {
			JOptionPane.showMessageDialog(null, "Server not found "
					+ "press disconnect " + "and please try again");
		}
	}

	
	private void getStreamsTCP() throws IOException {
		//gets input and output streams
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	private void manageConnectionTCP() throws IOException,
			ClassNotFoundException {
		//start connction, send name and gets members list
		name = JOptionPane.showInputDialog(null, "Please enter your name");
		if (name != null) {
			out.writeObject(name);
			out.flush();
			refreshMembers();
		} else {
			JOptionPane.showMessageDialog(null, "Bad input program exits",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void disconnectTCP() throws IOException {
		out.close();
		in.close();
		socket.close();
	}


	private void sendMessage(String s) throws IOException {
		out.writeObject(s);
		out.flush();
	}

	private void refreshMembers() throws ClassNotFoundException, IOException {
		
		ArrayList<String> members = (ArrayList<String>) in.readObject();
		String membersString = "";
		for (String s : members) {
			membersString += s + "\n";
		}
		membersList.setText("");
		membersList.append(membersString);

	}

	private class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {

				if (e.getSource() == connectBut) {
					
					connectBut.setEnabled(false);
					disconnectBut.setEnabled(true);
					writeMessage.setEditable(true);

					
					connectTCP();
					getStreamsTCP();
					manageConnectionTCP();

				} else if (e.getSource() == disconnectBut) {
					
					connectBut.setEnabled(true);
					disconnectBut.setEnabled(false);
					writeMessage.setEditable(false);

					disconnectTCP();
				} else if (e.getSource() == writeMessage) {
					sendMessage(writeMessage.getText());
					writeMessage.setText("");
				}
			} catch (IOException ex) {
				;
			} catch (ClassNotFoundException ex) {
				;
			} catch (NullPointerException ex) {
			}

			repaint();

		}
	}

}

