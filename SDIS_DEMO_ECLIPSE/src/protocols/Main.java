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

	public static void main(String []args) throws IOException, NoSuchAlgorithmException, InterruptedException {
		Scanner sc = new Scanner(System.in);
		String op;
		int ret = 0;
		// private static Backup backup;
		// private static Restore restore;
		//  private static Control control;
		/*if (args.length != 6) {
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
*/		
		//UserT.connect("oi");
		do {
			System.out.println("_________Main Menu_________");
			System.out.println("1 - Chunk backup");
			System.out.println("2 - Chunk restore");
			System.out.println("3 - File deletion");
			System.out.println("4 - Create a new User");
			System.out.println("5 - Login");
			System.out.println("6 - Create Group");
			System.out.println("7 - Join Group");
			System.out.println("8 - Use admin Token");
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
			else if(op.equals("5")) {
				String username, password;

				System.out.println("Username? ");
				username = sc.nextLine();

				System.out.println("Password? ");
				password = sc.nextLine();
				UserT.login(username,password);
				loggeduser = username;
			}
			else if(op.equals("6")) {
				String groupname;

				System.out.println("Group Name? ");
				groupname = sc.nextLine();
				UserT.creategroup(loggeduser, groupname);
			}
			else if(op.equals("7")) {
				String accesstoken;
				
				System.out.println("Acess Token?");
				accesstoken = sc.nextLine();
				UserT.joingroup(loggeduser, accesstoken);
			}
			else if(op.equals("8")) {
				String admintoken;
				System.out.println("Admin Token?");
				admintoken = sc.nextLine();
				UserT.joinAdmin(loggeduser, admintoken);
				
			}
			
			else if(op.equals("9"))
				wh = false;
			
		} while(ret < 5 && wh);
	}
}
