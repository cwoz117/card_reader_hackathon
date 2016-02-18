/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import javax.swing.JFrame;
import java.awt.EventQueue;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;

public class DoorServer extends JFrame
{	
    static BasicEx ex;
    static UserCred[] users;     
    static int LOCK_DELAY = 2;
    public static int BUFFERSIZE = 256;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static void openDoor()
    {
    	ex.getSurface().setLocked(false);
    	ex.getContentPane().repaint();

    	scheduler.schedule(new Runnable() {
	        @Override
	        public void run() {	        	
	        	ex.getSurface().setLocked(true);
	        	ex.getContentPane().repaint();
	            System.out.println("Locking door.");
	        }	        
        }, LOCK_DELAY, SECONDS);
    	
        System.out.println("Opening door.");
    }

    static void loadUsers()
    {
    	users = new UserCred[]{new UserCred("12345678", "butts")};
    }
    
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: DoorServer <Listening Port>");
            System.exit(1);
        }

        loadUsers();

        EventQueue.invokeLater(new Runnable() 
		{
	        @Override
	        public void run() {
	            ex = new BasicEx();
	            ex.setVisible(true);
	        }
    	});
    	
        // Initialize buffers and coders for channel receive and send
        String line = "";
        Charset charset = Charset.forName( "us-ascii" );  
        CharsetDecoder decoder = charset.newDecoder();  
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer inBuffer = null;
        CharBuffer cBuffer = null;
        int bytesSent, bytesRecv;     // number of bytes sent or received
        
        // Initialize the selector
        Selector selector = Selector.open();

        // Create a server channel and make it non-blocking
        ServerSocketChannel tcp_channel = ServerSocketChannel.open();
        tcp_channel.configureBlocking(false);
       
        // Get the port number and bind the socket
        InetSocketAddress isa = new InetSocketAddress(Integer.parseInt(args[0]));
        tcp_channel.socket().bind(isa);

        // Register that the server selector is interested in connection requests
        tcp_channel.register(selector, SelectionKey.OP_ACCEPT);

        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            while (!terminated) 
            {
                if (selector.select(500) < 0)
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }
                
                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();

                // Walk through the ready set
                while (readyItor.hasNext()) 
                {
                    // Get key from set
                    SelectionKey key = (SelectionKey)readyItor.next();

                    // Remove current entry
                    readyItor.remove();

                    // Accept new connections, if any
                    if (key.isAcceptable())
                    {
                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accept conncection from " + cchannel.socket().toString());
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    } 
                    else 
                    {
                    	SelectableChannel sc = key.channel();

                    	{
	                        SocketChannel cchannel = (SocketChannel)sc;
	                        if (key.isReadable())
	                        {
	                            Socket socket = cchannel.socket();
	                        
	                            // Open input and output streams
	                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
	                            cBuffer = CharBuffer.allocate(BUFFERSIZE);
	                             
	                            // Read from socket
	                            Thread.sleep(100);
	                            
	                            bytesRecv = cchannel.read(inBuffer);
	                            if (bytesRecv <= 0)
	                            {
	                                System.out.println("read() error, or connection closed");
	                                key.cancel();  // deregister the socket
	                                continue;
	                            }
	                             
	                            inBuffer.flip();      // make buffer available  
	                            decoder.decode(inBuffer, cBuffer, false);
	                            cBuffer.flip();
	                            line = cBuffer.toString();
	                            System.out.println("TCP Client: " + line);

	                            String[] split = line.split(" ");
	                            UserCred testUser = new UserCred(split[0], split[1]);
	                            
	                            for (int i = 0; i < users.length; i++){	                            	
	                            	if (testUser.equals(users[i]));
	                            		openDoor();
	                            }
                        	}
                    	}
                    }
                } // end of while (readyItor.hasNext()) 
            } // end of while (!terminated)
        }
        catch (IOException e) {
            System.out.println(e);
        }
 
        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if (key.isAcceptable())
            {
            	closeChannel(key.channel());
            }
            else if (key.isValid())
            {
            	closeChannel(key.channel());                
            }
        }
    }

    static void closeChannel(SelectableChannel channel)
    {
    	try
    	{
    		if (channel instanceof ServerSocketChannel)
    		{
    			ServerSocketChannel tcpChannel = (ServerSocketChannel)channel;
	        	tcpChannel.socket().close();
    		}
    		else if (channel instanceof DatagramChannel)
    		{
	        	DatagramChannel udpChannel = (DatagramChannel)channel;
            	udpChannel.socket().close();
	        }
    	}
        catch (IOException e) {
            System.out.println(e);
    	}
    }
}
