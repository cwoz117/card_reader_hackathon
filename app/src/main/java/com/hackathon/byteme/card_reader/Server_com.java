package com.hackathon.byteme.card_reader;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedWriter;
import java.net.SocketAddress;

/**
 * Handles the connection with the Server database for user authentication.
 */
public class Server_com implements Runnable{
	private String data;
	private String ip;
	private int port;
	Socket s;

	public Server_com(String ipAddr, int  port) {
		ip = ipAddr;
		this.port = port;
	}
	public void setData(String d){
		data = d;
	}
	public void run(){
		Logger.print("SERVER - In Send");
		Logger.print("IP:PORT " + ip + ":" + port);
		try {
			s = new Socket(ip, port);
			Logger.print("Socket Created");
			Logger.print("SERVER - Connected");
			BufferedWriter o = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			Logger.print("SERVER - Writing...");
			o.write(data);
			o.flush();
			Logger.print("SERVER - Wrote Data to Server");
			s.close();
			Logger.print("Received Payload");
		} catch (IOException e) {
			Logger.print("hello");
			e.printStackTrace();
			if (s != null){
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
	}



}
