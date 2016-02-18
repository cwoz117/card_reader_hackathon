package com.hackathon.byteme.card_reader;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.BufferedWriter;

/**
 * Handles the connection with the Server database for user authentication.
 */
public class Server_com {

	private String ip;
	private int port;
	Socket s;

	public Server_com(String ipAddr, int  port) {
		ip = ipAddr;
		this.port = port;
	}

	void send(String user_data){
		System.out.println("SERVER - In Send");
		try {

			s = new Socket(ip, port);
			System.out.println("SERVER - Connected");
			BufferedWriter o = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			System.out.println("SERVER - Writing...");
			o.write(user_data);
			o.flush();
			System.out.println("SERVER - Wrote Data to Server");
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
