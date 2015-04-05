package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import utilities.Encrypt;
import utilities.MessageFormat;

public class Delete {
	Scanner sc = new Scanner(System.in);
	static String message = "";
	File directory;
	boolean exists= false;
	static int nchunks;
	protected static DatagramPacket packet;
	protected static InetAddress adressMC;
	protected static InetAddress adressMDR;
	public static String filename;
	
	public static int sendDeleteChunk() throws NoSuchAlgorithmException, IOException {

		Scanner scanner = new Scanner(System.in);

		System.out.print("Insert file's name: ");
		filename = scanner.nextLine();

		File fileName = new File("backups/" + filename);

		while(!fileName.exists()){
			System.out.println("The name specified doesn't exist...\nInsert file's name: ");
			filename = scanner.nextLine();
			fileName = new File("backups/" + filename);
		}
		scanner.close();

		System.out.println("## Filepath: " + fileName.getAbsolutePath());
		System.out.println("## Filename: " + fileName.getName());
		String fileID = Encrypt.SHA256(filename);

		message = MessageFormat.createMessageHeader("DELETE", "1.3", fileID, "", "");

		adressMC = Main.mc.getAddress();
		byte[] responseData =  message.getBytes();
		packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
		Main.mc.getMCsocket().send(packet);

		return 0;
	}
	public static int deleteChunk(String fileID) {
		// TODO Auto-generated method stub
		File delete = new File("backups/" + fileID);
		if(delete.exists())
			delete.delete();
		else {
			System.out.println("There's no file with that fileID");
			return -1;
		}
		return 0;
	}

}
