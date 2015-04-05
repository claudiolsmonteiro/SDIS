package protocols;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import channels.*;

public class Main {

	public static MulticastControl mc;
	public static MulticastRestore mdr;
	public static MulticastBackup mdb;
	protected static Scanner sc = new Scanner(System.in);
	protected static String mc_ip,mdb_ip,mdr_ip;
	protected static int mc_port,mdb_port,mdr_port,op = 0;
	public static HashMap<String, Integer> chunkCache = new HashMap<String, Integer>();


	public static void main(String []args) throws IOException, NoSuchAlgorithmException, InterruptedException {

		// private static Backup backup;
		// private static Restore restore;
		//  private static Control control;
		if (args.length == 0 || args.length != 6) {
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

		do {
			System.out.println("_________Main Menu_________");
			System.out.println("1 - Chunk backup");
			System.out.println("2 - Chunk restore");
			System.out.println("3 - File deletion");
			System.out.println("4 - Space reclaiming");
			System.out.println("5 - Leave");
			op = sc.nextInt();

			Thread t1;

			if(op == 1){
				Backup.sendChunk();
			}
		} while(op < 1 || op > 5);
	}
}
