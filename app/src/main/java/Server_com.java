import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.ObjectOutputStream;

/**
 * Handles the connection with the Server database for user authentication.
 */
public class Server_com {

	private String ip;
	private int port;
	Socket s;

	public Server_com(String ipAddr, int  port) {
		//TODO String and int validation on user inputs.
	}

	void send(User u){
		try {
			s = new Socket(ip, port);
			ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
			o.writeObject(u);
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
