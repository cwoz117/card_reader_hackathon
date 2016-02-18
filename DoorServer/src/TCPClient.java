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
    	user = new UserCred("12345678", "butts");
    	
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

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        // Get user input and send to the server
        // Display the echo meesage from the server
        System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
        line = inFromUser.readLine(); 
        
        while (!line.equals("logout"))
        {           
            String[] split = line.split(" ");
            
            // Send to the server
            outBuffer.writeBytes(line + "\n"); 

        	if (split[0].equals("list"))
        	{
        		receiveFileList(inData);
        	}
        	else if (split[0].equals("get"))
        	{
        		receiveFile(inData);    			
        	}
        	else
        	{
	            // Getting response from the server
	            line = inBuffer.readLine();
	            System.out.println("Server: " + line);
        	}   
        	
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine(); 
        }

        // Close the socket
        clientSocket.close();           
    } 
    
    static void receiveFile(DataInputStream inBuffer)
    {
		int bytesRead = 0;
		String outFile = "downloadedfile";
		try
		{
			long filesize = inBuffer.readLong();
		
			byte[] data = new byte[(int)filesize];
			File content = new File(outFile);
			
			FileOutputStream fos = new FileOutputStream(content);
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	
			bytesRead = inBuffer.read(data, 0, data.length);
		    bos.write(data, 0, bytesRead);
		    
		    bos.flush();
			
		    fos.close();
			bos.close();
			System.out.println(String.format("File saved in %s (%d bytes)", outFile, filesize));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
    }
    
    static void receiveFileList(DataInputStream inBuffer)
    {
	    int bytesRead = 0;
		try
		{
			int filesize = inBuffer.readInt();
			
			byte[] data = new byte[filesize];
			bytesRead = inBuffer.read(data, 0, data.length);
			if (bytesRead != filesize)
			{
				System.out.println(String.format("receiveFileList: expected %d bytes, read %d bytes", filesize, bytesRead));
				return;
			}
			String text = new String(data, "UTF-8");
		 	
			System.out.print(text);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
    }
} 
