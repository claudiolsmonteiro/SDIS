package protocols;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import utilities.*;
import channels.*;
import connection.*;

public class Main {

	public static MulticastControl mc;
	public static MulticastRestore mdr;
	public static MulticastBackup mdb;
	//public static UserT usertcp;
	protected static String mc_ip,mdb_ip,mdr_ip, loggeduser;
	protected static int mc_port,mdb_port,mdr_port;
	
	public static HashMap<String, Integer> chunkCache = new HashMap<String, Integer>();
	public static boolean wh = true;

	public static void startMenu() throws NoSuchAlgorithmException{
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;
		
		do {
			System.out.println("_________Start Menu_________");
			System.out.println("1 - Login");
			System.out.println("2 - Register user");
			System.out.println("9 - Exit");
			op = sc.nextLine();
			
			if(op.equals("1")){
				String username, password;

				System.out.println("Username? ");
				username = sc.nextLine();

				System.out.println("Password? ");
				password = sc.nextLine();
				UserT.login(username,password);
				loggeduser = username;
				loggedMenu();
			}
			else if(op.equals("2")){
				String username ="", password="";

				System.out.println("Username? ");
				username = sc.nextLine();

				System.out.println("Password? ");
				password = sc.nextLine();

				if(UserT.register(username, password) == true){
					System.out.println("Registered successfully!");
				}
				else
					System.out.println("User already registered.");
			}			
			else if(op.equals("9"))
				wh = false;
			
		} while(ret < 5 && wh);
	}
	public static void loggedMenu() {
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;
		
		do {
			System.out.println("_________Logged Menu_________");
			System.out.println("1 - Create Group");
			System.out.println("2 - Join Group");
			System.out.println("3 - Enter Group");
			System.out.println("9 - Logout");
			op = sc.nextLine();
			
			
			if(op.equals("1")) {
				String groupname;

				System.out.println("Group Name? ");
				groupname = sc.nextLine();
				UserT.creategroup(loggeduser, groupname);
			}
			else if(op.equals("2")) {
				
				System.out.println("Wanna enter as admin? (y/n)");
				String opt = sc.nextLine();
				
				while(opt != "Y" || opt != "y" || opt != "N" || opt != "n"){
					System.out.println("Wrong otion! Enter (y/n)...");
					opt = sc.nextLine();
				}
				
				if(opt == "Y" || opt == "y"){
					System.out.println("Admin Token?");
					String admintoken = sc.nextLine();
					UserT.joinAdmin(loggeduser, admintoken);
				}
				else{
					System.out.println("Acess Token?");
					String accesstoken = sc.nextLine();
					UserT.joingroup(loggeduser, accesstoken);
				}
			}
			else if(op.equals("3")) {
				//Listar grupos para entrar e poder sincronizar ficheiros
				
			}
			else if(op.equals("9")){
				loggeduser = "";
				wh = false;
			}
			
		} while(ret < 5 && wh);		
	}
	
	public static void groupMenu() throws NoSuchAlgorithmException, IOException, InterruptedException{
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;

		do {
			System.out.println("_________Group Menu_________");
			System.out.println("1 - Chunk backup");
			System.out.println("2 - Chunk restore");
			System.out.println("3 - File deletion");
			System.out.println("4 - Show all files on the group");
			System.out.println("9 - Leave");
			op = sc.nextLine();
			
			if(op.equals("1")){
				ret = Backup.sendChunk();
			}
			else if(op.equals("2")){
				Restore.sendGetChunk();
			}
			else if(op.equals("3")){
				Delete.sendDeleteChunk();
			}
			else if(op.equals("4")) {
				//list all files of a group
			}
			else if(op.equals("9")){
				wh = false;
				loggedMenu();
			}
		} while(ret < 5 && wh);
	}
	
	public static void main(String []args) throws IOException, NoSuchAlgorithmException, InterruptedException {
		
		startMenu();
	}
}
