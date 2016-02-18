/*
 * A simple UDP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */


import java.io.*; 
import java.net.*; 

class UDPClient { 
    public static int BUFFERSIZE = 32;
    public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: UDPClient <Server IP> <Server Port>");
            System.exit(1);
        }

        // Initialize a UDP server socket and declare a datagram packet
        DatagramSocket clientSocket = new DatagramSocket();
        DatagramPacket myPacket = null;

        // Initialize input and an output stream for the connection(s)
        byte[] outBuffer = null;
        byte[] inBuffer = null;

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        // Get user input and send to the server
        // Display the echo meesage from the server
        try {
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine(); 
            while (!line.equals("logout"))
            {
                // Send to the server
                outBuffer = line.getBytes();
                myPacket = new DatagramPacket(outBuffer, outBuffer.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                clientSocket.send(myPacket); 
            
                // Getting response from the server
                inBuffer = new byte[BUFFERSIZE];
                myPacket = new DatagramPacket(inBuffer, inBuffer.length);
            
                // Receive from the UDP socket
                clientSocket.receive(myPacket);
            
                // Convert the packet to a string
                line = new String(myPacket.getData());
            
                // Trim the buffer data and get the actual received data
                line = line.substring(0, myPacket.getLength());
                System.out.println("Server: " + line);
                             
                System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
                line = inFromUser.readLine(); 
            }
        }
        catch (UnknownHostException ex) { 
            System.err.println(ex);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
        
        // Close the socket
        clientSocket.close();           
    } 
} 
