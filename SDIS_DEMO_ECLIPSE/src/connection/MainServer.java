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
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				//System.out.println(in.readUTF());
				
				String message = getMessage(userbase,in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + message + "\nGoodbye!");
				server.close();
			}catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e) {
				e.printStackTrace();
				break;
			}
			FileOutputStream f_out = new FileOutputStream("database.ser");

			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

			// Write object out to disk
			obj_out.writeObject (userbase);
			obj_out.close();
		}
	}
	
	//register
	//login
	//create group
	//join group
	//add permissions
	//delete permissions
	//delete file
	//backup file
	//restore file
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
					response = "LOGIN COM SUCESSUH";
				}
				else {
					response = "LOGIN FALHOU";
				}
			}
			break;

		case "PUT":
			if(components[0].trim().matches("REGISTER") == true) {
				String[] password = arguments[1].split("\\=");
				//System.out.println("variable2: " + variable2[1]);
				UserT teste = new UserT(variable1[1].trim(), password[1].trim());
				if(userbase.usercheck(variable1[1].trim()) == true){
					response = "ESSE USER JA EXISTE FILHO";
				}
				else {
					response = "USER AINDA NAO EXISTE BOY. VOU ADICIONAR XOXO";
					userbase.addUser(teste);
				}

			}
			else if(components[0].trim().matches("CREATEGROUP") == true) {
				String[] groupname = arguments[1].split("\\=");
				int id = UserDB.generateGroupID();
				if(userbase.checkgroupname(groupname[1].trim()) == true) {
					response= "ESSE GRUPO JA EXISTE";
				}
				else {
					Group newgroup = new Group (groupname[1].trim(),id,variable1[1].trim());
					response = "VOU CRIAR NOVO GRUPO";
					userbase.addGroup(newgroup);
				}
			}
			break;
		case "POST":
			if(components[0].trim().matches("JOINGROUP") == true) {
				userbase.printGroup();
				String[] accesstoken = arguments[1].split("\\=");
				if(userbase.checkAccesstoken(accesstoken[1].trim()) == false) {
					response = "TOKEN ERRADO/ GRUPO NAO EXISTE";
				}
				else {
					userbase.joinUserGroup(variable1[1].trim(),accesstoken[1].trim());
					response = "ja ca estas mano";
				}
				System.out.println("ENTREI AKI 4");
			}
			if(components[0].trim().matches("JOINADMIN") == true) {
				userbase.printGroup();
				String[] admintoken = arguments[1].split("\\=");
				if(userbase.checkAdmintoken(admintoken[1].trim()) == false) {
					response = "ADMIN TOKEN ERRADO/ GRUPO NAO EXISTE";
				}
				else {
					userbase.joinUserAdmin(variable1[1].trim(),admintoken[1].trim());
					response = "boa filho, es admin";
				}
			}
			break;
		}
		//DELETE
		return response;

	}
}