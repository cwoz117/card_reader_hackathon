import java.io.*;

public class UserCred implements java.io.Serializable
{
	static final long serialVersionUID = 1;
	
	private String ucid;
	private String password;

	public UserCred(String id, String pwd)
	{
		setUCID(id);
		setPassword(pwd);
	}
	
	public String getUCID()
	{
		return new String(ucid);
	}

	public String getPassword()
	{
		return new String(password);
	}
	
	public void setUCID(String id)
	{
		ucid = id;
	}

	public void setPassword(String pwd)
	{
		password = pwd;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException
	{
		out.writeChars(ucid + "\n");
		out.writeChars(password + "\n");
	}
	 
	private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
	{
		ucid = "";
		password = "";
		
		char ch = in.readChar();
		while (ch != '\n')
		{
			ucid += ch;
		}
		
		ch = in.readChar();
		while (ch != '\n')
		{
			password += ch;
		}
	}
		 
	private void readObjectNoData()
	     throws ObjectStreamException
	{
		 
	}
}
