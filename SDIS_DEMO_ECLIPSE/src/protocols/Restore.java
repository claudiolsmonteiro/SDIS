package protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import utilities.*;

public class Restore {
	Scanner sc = new Scanner(System.in);
	static String message = "";
	File directory;
	boolean exists= false;
	static int nchunks;
	protected static DatagramPacket packet;
	protected static InetAddress adressMC;
	protected static InetAddress adressMDR;
	public static String filename;

	//Funcao que envia um pedido de restauro do ficheiro, GETCHUNK para o MC (GETCHUNK)
	public static int sendGetChunk() throws IOException, NoSuchAlgorithmException {
		Scanner scan = new Scanner(System.in);

		File backupInfoExists = new File("info/backup.info");
		if(!backupInfoExists.exists()){
			System.out.println("There is no backups done by this peer.");
			return 0;
		}

		FileInputStream backupInfo = new FileInputStream("info/backup.info");
		byte[] backupInfoBytes = new byte[(int) backupInfoExists.length()];
		backupInfo.read(backupInfoBytes);

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
		int position = 0;

		for(int i = 0; i < filesNames.length; i++){
			if(filesNames[i].equals(filename)){
				position = i;
				break;
			}
		}
		String fileID = Encrypt.SHA256(filename);
		adressMC = Main.mc.getAddress();
		System.out.println("Requesting file restore of: " + filename);
		for(int i = 0; i < Integer.parseInt(noChunks[position]); i++){
			String message = MessageFormat.createMessageHeader("GETCHUNK", "1.0", "",fileID, Integer.toString(i), "");

			byte[] responseData =  message.getBytes();
			packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
			Main.mc.getMCsocket().send(packet);
			System.out.println("Requested chunk #" + i + "!");
		}
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageFormat.mergeChunks(filename, fileID);

		//scan.close();

		/*byte[][] fileSplitted = MessageFormat.getDataArray(fileData);
		String fileID = Encrypt.SHA256(filename);
		nchunks = fileSplitted.length;

		for(int i = 0;i < nchunks;i++) {
			message = MessageFormat.createMessageHeader("GETCHUNK", "1.0", fileID, Integer.toString(i), "");
		}

		adressMC = Main.mc.getAddress();
		byte[] responseData =  message.getBytes();
		packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
		Main.mc.getMCsocket().send(packet);
		 */
		return 0;

	}



	//Funcao que quando recebe um GETCHUNK, verifica se tem chunks do ficheiro e envia-os para o MDR (CHUNK)
	public static int checkChunk(String fileID, String chunkNo) throws IOException {
		adressMDR = Main.mdr.getAddress();
		byte[] responseData, fileData;
		DatagramPacket newPacket;
		Random rand = new Random(); 
		int waitTime = rand.nextInt(401); // 0 a 400 milisegundos

		File backups = new File("backups");
		if(!backups.exists()){
			System.out.println("There is no backups on this peer!");
			return 1;
		}
		else{ //Verificar se j� tenho o chunk
			File filename = new File("backups/" + fileID);
			if(filename.exists()){
				File[] chunks = filename.listFiles();
				File tmp = new File(chunkNo + ".chunk");

				if(Arrays.asList(chunks).contains(tmp)){
					System.out.println("Chunk is not stored on this peer.");
					return 2;
				}
				else{
					File chunkFileSize = new File("backups/" + fileID + "/" + chunkNo + ".chunk");
					FileInputStream chunkFile = new FileInputStream("backups/" + fileID + "/" + chunkNo + ".chunk");
					fileData = new byte[(int) chunkFileSize.length()];
					chunkFile.read(fileData);
					chunkFile.close();
				}
			}
			else{ //Guardar o chunk
				System.out.println("There is no backups for this file.");
				return 3;
			}
		}

		String responseMessage = MessageFormat.createMessage("CHUNK", "1.0", "",fileID, chunkNo, "", fileData);

		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//SEND CHUNK MESSAGE MESSAGE
		adressMDR = Main.mdr.getAddress();
		responseData = responseMessage.getBytes();
		newPacket = new DatagramPacket(responseData, responseData.length, adressMDR, Main.mdr.getMDRPort());
		Main.mdr.getMDRsocket().send(newPacket);

		return 0;
	}

	public static int saveChunk(String[] messageHeader, byte[] bodyData) throws IOException{

		File restored = new File("restored");
		if(!restored.exists()){
			restored.mkdir();
		}
		File filename = new File("restored/" + messageHeader[2]);
		if(filename.exists()){
			File[] chunks = filename.listFiles();
			File tmp = new File(messageHeader[3] + ".chunk");
			File checkChunkExist = new File("restored/" + messageHeader[2] + "/" + messageHeader[3] + ".chunk");
			if(checkChunkExist.exists()){
				System.out.println("Chunk already restored on this peer.");
				return 2;
			}
			else{
				FileOutputStream saveChunk = new FileOutputStream("restored/" + messageHeader[2] + "/" + messageHeader[3] + ".chunk");
				saveChunk.write(bodyData);
				saveChunk.close();
			}
		}
		else{ 
			filename.mkdir();
			FileOutputStream saveChunk = new FileOutputStream("restored/" + messageHeader[2] + "/" + messageHeader[3] + ".chunk");
			saveChunk.write(bodyData);
			saveChunk.close();
		}
		return 0;		
	}
}
