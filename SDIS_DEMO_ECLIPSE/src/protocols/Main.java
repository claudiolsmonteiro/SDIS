package protocols;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.*;

import utilities.*;
import channels.*;
import connection.*;
public class Main {

	public static MulticastControl mc;
	public static MulticastRestore mdr;
	public static MulticastBackup mdb;
	protected static String mc_ip,mdb_ip,mdr_ip, loggeduser="";
	protected static ArrayList<String> loggeduser_groups;
	protected static int mc_port,mdb_port,mdr_port;

	public static HashMap<String, Integer> chunkCache = new HashMap<String, Integer>();
	public static boolean wh = true;

	public static void startMenu() throws NoSuchAlgorithmException, NumberFormatException, IOException, InterruptedException{
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
				String username, password,response;

				System.out.println("Username? ");
				username = sc.nextLine();

				System.out.println("Password? ");
				password = sc.nextLine();
				response = UserT.login(username,password);
				JSONObject jsonObj = new JSONObject(response);
				response = jsonObj.toString();
				System.out.println(response);
				response = jsonObj.getString("Login");
				System.out.println(response);
				if(response.matches("Success")) {
					System.out.println("Logged in successfully!");
					loggeduser = username;
					loggeduser_groups = new ArrayList<String>();
					if(response.matches("Success")) {
						JSONArray group = jsonObj.getJSONArray("Group List");
						System.out.println("Group name: ");
						for(int i = 0; i < group.length();i++) {
							String nome[] = group.getString(i).split("\\:");
							loggeduser_groups.add(nome[1].trim());
							System.out.println("Nome: "+loggeduser_groups.get(i));
						}
					}
					loggedMenu();
				}
				else	
					System.out.println("Login failed.");
			}
			else if(op.equals("2")){
				String username ="", password="",response;

				System.out.println("Username? ");
				username = sc.nextLine();

				System.out.println("Password? ");
				password = sc.nextLine();

				response =UserT.register(username, password);
				JSONObject jsonObj = new JSONObject(response);
				response = jsonObj.getString("Register");
				System.out.println(response);
				if(response.matches("Success"))
					System.out.println("Registered successfully!");
				else	
					System.out.println("Register failed.");
			}			
			else if(op.equals("9"))
				wh = false;

		} while(ret < 5 && wh);
	}
	public static void loggedMenu() throws NumberFormatException, NoSuchAlgorithmException, IOException, InterruptedException {
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
				if(loggeduser.length() != 0) {
					String groupname,response,accesstoken,admintoken;
					JSONArray group;

					System.out.println("Group Name? (It cannot have whitespaces)");
					groupname = sc.nextLine();
					while(groupname.contains(" ")){
						System.out.println("The name you entered has whitespaces. Enter another one:");
						groupname = sc.nextLine();
					}
					response = UserT.creategroup(loggeduser, groupname);
					JSONObject jsonObj = new JSONObject(response);
					response = jsonObj.getString("CreateGroup");
					if(response.matches("Success")) {
						System.out.println("Logged in successfully!");
						group = jsonObj.getJSONArray("Group details");
						accesstoken =group.getString(0);
						admintoken = group.getString(1);
						System.out.println("Group details \n" + accesstoken + "\n"+ admintoken );
					}
					else	
						System.out.println("Error creating group.");
				}
				else
					System.out.println("You need to login 1st");
			}
			else if(op.equals("2")) {
				if(loggeduser.length() != 0) {
					String accesstoken, response;

					System.out.println("Wanna enter as admin? (y/n)");
					String opt = sc.nextLine();
					opt = opt.trim();

					while(!opt.equals("Y") && !opt.equals("y") && !opt.equals("N") && !opt.equals("n")){
						System.out.println("Wrong otion! Enter (y/n)...");
						opt = sc.nextLine();
						opt = opt.trim();
					}

					if(opt.equals("Y") || opt.equals("y")){
						String admintoken;
						System.out.println("Admin Token?");
						admintoken = sc.nextLine();
						response = UserT.joinAdmin(loggeduser, admintoken);
						JSONObject jsonObj = new JSONObject(response);
						response = jsonObj.getString("JoinAdmin");
						if(response.matches("Success")) {
							System.out.println("Admin powers given");
						}
						else	
							System.out.println("Failed Admin.");
					}
					else{
						System.out.println("Acess Token?");
						accesstoken = sc.nextLine();
						response = UserT.joingroup(loggeduser, accesstoken);
						JSONObject jsonObj = new JSONObject(response);
						response = jsonObj.getString("JoinGroup");
						if(response.matches("Success")) {
							System.out.println("Joined Group successfully!");
						}
						else	
							System.out.println("Failed Joining group.");
					}

				}
				else
					System.out.println("You need to login 1st");
			}
			else if(op.equals("3")) {
				//Listar grupos para entrar e poder sincronizar ficheiros
				if(loggeduser_groups.size()==0)
					System.out.println("No groups to show. Join a group 1st");
				else {
					System.out.println("Groups:");
					for(int i = 0; i < loggeduser_groups.size();i++)
						System.out.println((i+1) + " - " + loggeduser_groups.get(i));

					System.out.print("Select the group you want to enter: ");
					String opt = sc.nextLine();
					opt = opt.trim();

					while(true){
						try{
							if(Integer.parseInt(opt) < 1 || Integer.parseInt(opt) > loggeduser_groups.size()){
								System.out.println("Group doesn't exist! Enter one that exists...");
								opt = sc.nextLine();
								opt = opt.trim();
							}
							else
								break;
						}catch(NumberFormatException n){
							System.out.println("You need to enter the number of the group... Enter again");
							opt = sc.nextLine();
							opt = opt.trim();
						}
					}
					
					groupMenu(Integer.parseInt(opt) - 1);
				}
			}
			else if(op.equals("9")){
				loggeduser = "";
				wh = false;
			}

		} while(ret < 5 && wh);		
	}

	public static void groupMenu(int groupName) throws NoSuchAlgorithmException, IOException, InterruptedException{
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;

		do {
			System.out.println("_________ Group Menu : " + loggeduser_groups.get(groupName) + " _________");
			System.out.println("1 - Chunk backup");
			System.out.println("2 - Chunk restore");
			System.out.println("3 - File deletion");
			System.out.println("4 - Show all files on the group");
			System.out.println("9 - Leave");
			op = sc.nextLine();

			if(op.equals("1")){
				if(loggeduser.length()!=0)
					ret = Backup.sendChunk(loggeduser_groups);
				else
					System.out.println("You need to login 1st");
			}
			else if(op.equals("2")){
				if(loggeduser.length()!=0)
					Restore.sendGetChunk();
				else
					System.out.println("You need to login 1st");
			}
			else if(op.equals("3")){
				if(loggeduser.length()!=0)	
					Delete.sendDeleteChunk();
				else
					System.out.println("You need to login 1st");
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
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;
		// private static Backup backup;
		// private static Restore restore;
		//  private static Control control;
		if (args.length != 6) {
			mc_ip = "230.0.0.1";
			mc_port = 4446;
			mdb_ip = "230.0.0.2";
			mdb_port = 4446;
			mdr_ip = "230.0.0.3";
			mdr_port = 4446;
		}
		else {
			mc_ip = args[0];
			mc_port = Integer.parseInt(args[1]);
			mdb_ip = args[2];
			mdb_port = Integer.parseInt(args[3]);
			mdr_ip = args[4];
			mdr_port = Integer.parseInt(args[5]);
		}
		mc = new MulticastControl(mc_ip,mc_port);
		mc.start();
		mdr = new MulticastRestore(mdr_ip,mdr_port);
		mdr.start();
		mdb = new MulticastBackup(mdb_ip,mdb_port);
		mdb.start();

		startMenu();
	}
}
