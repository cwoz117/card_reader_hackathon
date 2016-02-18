/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */


import java.awt.image.DataBuffer;
import java.io.*; 
import java.net.*;
import java.nio.CharBuffer; 

class TCPClient { 
	
	static UserCred user;

    public static void main(String args[]) throws Exception 
    { 
    	user = new UserCred("123456718", "butts");
    	
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
            System.exit(1);
        }

        // Initialize a client socket connection to the server
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1])); 

        // Initialize input and an output stream for the connection(s)
        DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
        BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));         
        DataInputStream inData = new DataInputStream(clientSocket.getInputStream());

        outBuffer.writeBytes(user.getUCID() + " " + user.getPassword()); 
        outBuffer.flush();
        clientSocket.close();     
    } 
} 
