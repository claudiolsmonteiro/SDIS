package protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

		Scanner scan = new Scanner(System.in);

		File backupInfoExists = new File("info/backup.info");
		if(!backupInfoExists.exists()){
			System.out.println("There is no backups done by this peer.");
			return 0;
		}

		FileInputStream backupInfo = new FileInputStream("info/backup.info");
		byte[] backupInfoBytes = new byte[(int) backupInfoExists.length()];
		backupInfo.read(backupInfoBytes);
		backupInfo.close();

		String backupInfoString = new String(backupInfoBytes);

		String[] filesBackedUp = backupInfoString.split(":");

		String[] filesNames = new String[filesBackedUp.length];
		String[] noChunks = new String[filesBackedUp.length];
		String[] filesSize = new String[filesBackedUp.length];

		for(int i = 0; i < filesBackedUp.length; i++){
			String[] fileInfo = filesBackedUp[i].split("/");
			filesNames[i] = fileInfo[0];
			noChunks[i] = fileInfo[1];
			filesSize[i] = fileInfo[2];
		}

		for(int i = 0 ; i < filesNames.length; i++){
			System.out.println("Filename: " + filesNames[i] + " | Number of chunks: " + noChunks[i] + " | Filesize: " + filesSize[i]);
		}

		System.out.print("Insert the name of a file above: ");
		filename = scan.nextLine();

		while(!Arrays.asList(filesNames).contains(filename)){
			System.out.print("Invalid filename! Try again: ");
			filename = scan.nextLine();
		}

		String fileID = Encrypt.SHA256(filename);
		adressMC = Main.mc.getAddress();
		System.out.println("Requesting file deletion of: " + filename);

		String newmessage = MessageFormat.createMessageHeader("DELETE", "1.0", fileID, "", "");

		adressMC = Main.mc.getAddress();
		byte[] responseData =  newmessage.getBytes();
		packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
		Main.mc.getMCsocket().send(packet);

		File backupFileSize1 = new File("info/backup.info");
		FileInputStream backupInfo1 = new FileInputStream("info/backup.info");
		byte[] backupFile1 = new byte[(int) backupFileSize1.length()];
		backupInfo1.read(backupFile1);
		backupInfo1.close();
		String backupString1 = new String(backupFile1);

		String[] filesBackedUp1 = backupString1.split(":");
		int i = 0;
		for(i = 0; i < filesBackedUp1.length; i++){
			if(filesBackedUp1[i].contains(filename)){
				break;
			}
		}

		ArrayList<String> tempArray = new ArrayList<String>(Arrays.asList(filesBackedUp1));
		tempArray.remove(filesBackedUp1[i]);

		tempArray.toArray(filesBackedUp1);
		String backupSaveString = "";

		for(int j = 0; j < filesBackedUp1.length-1; j++){
			System.out.println(filesBackedUp1[j]);
			String[] fileBackupInfo = filesBackedUp1[j].split("/");
			String backupSaveString1 = new String(fileBackupInfo[0] + "/" + fileBackupInfo[1] + "/" + fileBackupInfo[2] + ":");
			backupSaveString += backupSaveString1;
		}

		if(filesBackedUp1.length-1 == 0){
			File backupDelete = new File("info");
			File[] deleteFiles = backupDelete.listFiles();
			for(File deleteFile : deleteFiles){
				deleteFile.delete();
			}
		}
		else{
			FileOutputStream backupSave = new FileOutputStream("info/backup.info");
			backupSave.write(backupSaveString.getBytes());
			backupSave.close();		
		}
		return 0;
	}

	public static int deleteChunk(String fileID) throws NoSuchAlgorithmException, IOException {
		// TODO Auto-generated method stub
		File delete = new File("backups/" + fileID);
		if(delete.exists()){
			File[] filesDelete = delete.listFiles();
			for(File fileDelete : filesDelete){
				fileDelete.delete();
			}
			delete.delete();
			System.out.println("File deleted successfully!");
		}
		else {
			System.out.println("There's no file with that fileID");
			return -1;
		}
		return 0;
	}

}