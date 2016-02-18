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
import java.awt.image.BufferedImage;

public class DoorServer extends JFrame
{	
    public static int BUFFERSIZE = 256;
    UserCred[] users;     
    
    public static void main(String args[]) throws Exception 
    {
        if (args.length != 1)
        {
            System.out.println("Usage: DoorServer <Listening Port>");
            System.exit(1);
        }

    	EventQueue.invokeLater(new Runnable() 
		{
	        @Override
	        public void run() {
	            BasicEx ex = new BasicEx();
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

        // Declare a UDP server socket and a datagram packet
        DatagramChannel udp_channel = null;
        
        udp_channel = DatagramChannel.open();
        InetSocketAddress udp_isa = new InetSocketAddress(Integer.parseInt(args[0]));
        udp_channel.socket().bind(udp_isa);
        udp_channel.configureBlocking(false);
        udp_channel.register(selector, SelectionKey.OP_READ);
        
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
	                            System.out.print("TCP Client: " + line);

                            	String[] strSplit = line.split(" ");
                            	
	                            if (line.equals("list\n"))
	                            {   
	                            	String outputStr = getFileList(".");
	                            	int strLen = outputStr.length();
	                            	
	                            	ByteBuffer bufferSize = ByteBuffer.allocate(4);
		                            bufferSize.putInt(strLen);
		                            bufferSize.rewind();
		                            cchannel.write(bufferSize);
	                            	
	                            	CharBuffer newcb = CharBuffer.allocate(strLen);
	                            	ByteBuffer outBuf = ByteBuffer.allocate(strLen);

	                            	newcb.put(outputStr);
	                            	newcb.rewind();
	                            	encoder.encode(newcb, outBuf, false);
	                            	outBuf.flip();
		                            bytesSent = cchannel.write(outBuf); 

		                            if (bytesSent != outputStr.length())
		                            {
		                                System.out.println("write() error, or connection closed");
		                                key.cancel();  // deregister the socket
		                                continue;
		                            }
	                            }
	                            else if (strSplit[0].equals("get"))
	                            {
		                            String filename = strSplit[1];
		                            filename = filename.replaceAll("\\s+", "");			// trim whitespace
		                            System.out.print(String.format("Open file: %s\n", filename));

		                            byte[] data = getFile(filename);
		                            if (data == null)
		                            {
		                                System.out.println(filename + " not found.");		                            
		                            }
		                            else
		                            {       
		                            	ByteBuffer bufferSize = ByteBuffer.allocate(8);
			                            bufferSize.putLong(data.length);
			                            bufferSize.rewind();
			                            cchannel.write(bufferSize);
		                            
			                            ByteBuffer outBuf = ByteBuffer.allocate(data.length);
			                            outBuf.put(data);
			                            outBuf.flip();
			                            cchannel.write(outBuf);
		                            }
	                            }
	                            else if (line.equals("terminate\n"))
	                            {
	                                terminated = true;
	                            }
	                            else
	                            {
	                            	line = line.replaceAll("\\s+", "");			// trim whitespace
	                            	String outStr = String.format("Unknown command: %s\n", line);
	                            	int outLen = outStr.length();
	                            	CharBuffer newcb = CharBuffer.allocate(outLen);
	                            	ByteBuffer outBuf = ByteBuffer.allocate(outLen);
	                            	
	                            	newcb.put(outStr);
	                            	newcb.rewind();
	                            	encoder.encode(newcb, outBuf, false);
	                            	outBuf.flip();
		                            bytesSent = cchannel.write(outBuf); 
		                            
		                            if (bytesSent != outLen)
		                            {
		                                System.out.println("write() error, or connection closed");
		                                key.cancel();  // deregister the socket
		                                continue;
		                            }
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

    static byte[] getFile(String filename)
    {
    	byte[] result = null;
	    
    	try
	    {	
  		    File f = new File(filename);
  		    FileInputStream input = new FileInputStream(f);

	    	int size = (int)f.length();
		    result = new byte[size];

		    input.read(result);
	    }
	    catch(IOException e) {
            System.out.println("open() failed");
        }
	    return result;
    }
    
    static String getFileList(String dir)
    {
	    String result = "";
		try
		{
		    String current = new File(dir).getCanonicalPath();
		    File directory = new File(current);
		    File[] files = directory.listFiles();
			
		    for (int i = 0; i < files.length; i++) 
			{
				if (files[i].isFile()) 
					result += files[i].getName() + '\n';
		    }
		}
        catch (IOException e) {
            System.out.println(e);
        }
		return result;
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
