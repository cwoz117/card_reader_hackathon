import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class User implements Serializable{

	private String ucid;
	private String password;

	public User(){
		ucid = "00000000";
		password = "";
	}

	public User(String u, String p){
		ucid = u;
		password = p;
	}

	public String getUcid(){
		return ucid;
	}

	public String getPassword(){
		return password;
	}

	public void setUcid(String v) {
		ucid = v;
	}

	public void setPassword(String p){
		password = p;
	}

	public String toString(){
		return ucid + "\n" + password;
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
}