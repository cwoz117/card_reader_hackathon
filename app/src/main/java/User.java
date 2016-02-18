import java.io.Serializable;

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

	public User(String received){
		String[] a = received.split(" ");
		ucid = a[0];
		password = a[1];
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
		return ucid + " " + password;
	}
}