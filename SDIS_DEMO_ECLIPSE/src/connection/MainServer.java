package connection;

import java.security.NoSuchAlgorithmException;
import java.util.List;
/*
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;
 */
import java.net.*;
import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainServer extends Thread{
	static ServerSocket serverSocket;

	public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		serverSocket = new ServerSocket(6060);

		UserDB userbase = new UserDB();

		File dbase = new File("database.ser");
		if(dbase.exists()){
			System.out.println("Database file exists, loading.");

			FileInputStream f_in = new FileInputStream("database.ser");


			ObjectInputStream obj_in = new ObjectInputStream (f_in);


			Object obj = obj_in.readObject();

			if (obj instanceof UserDB)
				userbase = (UserDB) obj;

			obj_in.close();
		}

		else{
			System.out.println("Creating Database");

			FileOutputStream f_out = new FileOutputStream("database.ser");


			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);


			obj_out.writeObject (userbase);
			obj_out.close();
		}

		while(true) {
			try {
				userbase.listBD();
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				//System.out.println(in.readUTF());
				
				String message = getMessage(userbase,in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF(message);
				FileOutputStream f_out = new FileOutputStream("database.ser");

				// Write object with ObjectOutputStream
				ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

				// Write object out to disk
				obj_out.writeObject (userbase);
				obj_out.close();
				
				server.close();

			}catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	//register XX
	//login XX
	//create group XX
	//join group XX
	//add permissions XX
	//delete permissions -- da maneira que esta feito depois um novo admin podia apagar o criador
	//delete file
	//backup file
	//restore file
	@SuppressWarnings("unchecked")
	public static String getMessage(UserDB userbase, String message ) throws NoSuchAlgorithmException {
		String response= "";
		System.out.println(message);
		String[] msg = message.split("\\s");
		String[] components = msg[1].split("\\?");
		String[] arguments = components[1].split("\\&");
		String[] variable1 = arguments[0].split("\\=");
		//GET
		switch(msg[0].trim()) {
		case "GET":
			if(components[0].trim().matches("LOGIN") == true) {
				String[] password = arguments[1].split("\\=");
				//System.out.println("variable2: " + variable2[1]);
				if(userbase.passwordcheck(variable1[1].trim(),password[1].trim()) == true){
			        JSONObject obj = new JSONObject();
			        obj.put("Login", "Success");
			        response = obj.toString();
				}
				else {
			        JSONObject obj = new JSONObject();
			        obj.put("Login", "Failed");
			        response = obj.toString();
				}
			}
			break;

		case "PUT":
			if(components[0].trim().matches("REGISTER") == true) {
				String[] password = arguments[1].split("\\=");
				//System.out.println("variable2: " + variable2[1]);
				UserT teste = new UserT(variable1[1].trim(), password[1].trim());
				if(userbase.usercheck(variable1[1].trim()) == true){
			        JSONObject obj = new JSONObject();
			        obj.put("Register", "Failed");
			        response = obj.toString();
				}
				else {
			        JSONObject obj = new JSONObject();
			        obj.put("Register", "Success");
			        response = obj.toString();
					userbase.addUser(teste);
				}

			}
			else if(components[0].trim().matches("CREATEGROUP") == true) {
				String[] groupname = arguments[1].split("\\=");
				int id = UserDB.generateGroupID();
				if(userbase.checkgroupname(groupname[1].trim()) == true) {
			        JSONObject obj = new JSONObject();
			        obj.put("CreateGroup", "Failed: Already exists");
			        response = obj.toString();
				}
				else {
					Group newgroup = new Group (groupname[1].trim(),id,variable1[1].trim());
			        JSONObject obj = new JSONObject();
			        obj.put("CreateGroup", "Success");
			 
					userbase.addGroup(newgroup);
					JSONArray group = new JSONArray();
			        group.put("Accesstoken: "+ newgroup.getAccesstoken());
			        group.put("Admintoken: "+ newgroup.getAdmintoken());
			        obj.put("Group details", group);
			        response = obj.toString();
				}
			}
			break;
		case "POST":
			if(components[0].trim().matches("JOINGROUP") == true) {
				userbase.printGroup();
				String[] accesstoken = arguments[1].split("\\=");
				if(userbase.checkAccesstoken(accesstoken[1].trim()) == false) {
			        JSONObject obj = new JSONObject();
			        obj.put("JoinGroup", "Failed");
			        response = obj.toString();
				}
				else {
					userbase.joinUserGroup(variable1[1].trim(),accesstoken[1].trim());
			        JSONObject obj = new JSONObject();
			        obj.put("JoinGroup", "Success");
			        response = obj.toString();
				}
			}
			if(components[0].trim().matches("JOINADMIN") == true) {
				userbase.printGroup();
				String[] admintoken = arguments[1].split("\\=");
				if(userbase.checkAdmintoken(admintoken[1].trim()) == false) {
			        JSONObject obj = new JSONObject();
			        obj.put("JoinAdmin", "Failed");
			        response = obj.toString();
				}
				else {
					userbase.joinUserAdmin(variable1[1].trim(),admintoken[1].trim());
			        JSONObject obj = new JSONObject();
			        obj.put("JoinAdmin", "Success");
			        response = obj.toString();
				}
			}
			break;
		case "DELETE":
			break;
		}
			
		//DELETE
		return response;

	}
}