package connection;

//File Name GreetingClient.java

import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.*;

import utilities.Encrypt;

@SuppressWarnings("serial")
public class UserT implements Serializable  {
	String username,password;
	ArrayList<Group> groups;
	public UserT(String username, String password) throws NoSuchAlgorithmException {
		this.username = username;
		this.password = password;
		groups = new ArrayList<Group>();
	}

//GET REGISTER?plate=value&ResponseType=type
//PUT REGISTER?plate=value&ResponseType=type
//DELETE REGISTER?plate=value&ResponseType=type
	
	public static String register(String username, String password) throws NoSuchAlgorithmException  {
		// TODO Auto-generated method stub
		String pw = utilities.Encrypt.SHA256(password);
		String message = "PUT REGISTER?username="+username+"&password="+pw+"&ResponseType=JSON";
		String response = connect(message);
		return response;

	}
	
	public static String login(String username, String password) throws NoSuchAlgorithmException {
		String pw = utilities.Encrypt.SHA256(password);
		String message = "GET LOGIN?username="+username+"&password="+pw+"&ResponseType=JSON";
		String response = connect(message);
		return response;
	}
	public static String creategroup(String username, String groupname) {
		String message = "PUT CREATEGROUP?username="+username+"&groupname="+groupname+"&ResponseType=JSON";
		String response = connect(message);
		return response;
	}
	
	public static String joingroup(String username, String accesstoken) {
		String message = "POST JOINGROUP?username="+username+"&accesstoken="+accesstoken+"&ResponseType=JSON";	
		String response = connect(message);
		return response;
	}

	public static String joinAdmin(String loggeduser, String admintoken) {
		// TODO Auto-generated method stub
		String message = "POST JOINADMIN?username="+loggeduser+"&accesstoken="+admintoken+"&ResponseType=JSON";	
		String response = connect(message);
		return response;
	}
	public static String connect(String something) {
		String serverName = "localhost",response="";
		int port = 6060;
		try {
			System.out.println("Connecting to " + serverName+ " on port " + port);
			Socket client = new Socket(serverName, port);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(something);
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			response = in.readUTF();
			client.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	public void add(Group group) {
		// TODO Auto-generated method stub
		
		
	}

	public ArrayList<Group> getGroups() {
		// TODO Auto-generated method stub
		return groups;
	}







}