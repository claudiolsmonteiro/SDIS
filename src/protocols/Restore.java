package protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import utilities.*;

public class Restore {
	Scanner sc = new Scanner(System.in);
	String message = "";
	File directory;
	boolean exists= false;
	int nchunks;
	protected static DatagramPacket packet;
	protected static InetAddress adressMC;
	protected static InetAddress adressMDR;

	//Funcao que envia um pedido de restauro do ficheiro, GETCHUNK para o MC (GETCHUNK)
	public int sendGetChunk() throws IOException, NoSuchAlgorithmException {
		System.out.print("Insert file's path: ");
		Scanner scanner = new Scanner(System.in);
		String filepath = scanner.nextLine();

		File fileDir = new File(filepath);

		while(!fileDir.exists()){
			System.out.print("The path specified doesn't exist...\nInsert file's path: ");
			filepath = scanner.nextLine();
			fileDir = new File(filepath);
		}

		System.out.print("Insert file's name: ");
		String filename = scanner.nextLine();

		File fileName = new File(filepath + "/" + filename);

		while(!fileName.exists()){
			System.out.println("The name specified doesn't exist...\nInsert file's name: ");
			filename = scanner.nextLine();
			fileName = new File(filepath + "/" + filename);
		}


		System.out.println("## Filepath: " + fileName.getAbsolutePath());
		System.out.println("## Filename: " + fileName.getName());
		byte[] fileData = MessageFormat.getFileData(filepath, filename);		

		System.out.println("## Filesize: " + fileData.length + " Bytes");

		byte[][] fileSplitted = MessageFormat.getDataArray(fileData);
		String fileID = Encrypt.SHA256(filename);
		nchunks = fileData.length/MessageFormat.MAXDATASIZE;

		for(int i = 0;i < nchunks;i++) {
			message = MessageFormat.createMessageHeader("GETCHUNK", "1.0", fileID, Integer.toString(i), "");
		}

		adressMC = Main.mc.getAddress();
		byte[] responseData =  message.getBytes();
		packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
		Main.mc.getMCsocket().send(packet);

		return 0;

	}



	//Funcao que quando recebe um GETCHUNK, verifica se tem chunks do ficheiro e envia-os para o MDR (CHUNK)
	public static int checkChunk(String fileID, String chunkNo) throws IOException {
		adressMDR = Main.mdr.getAddress();
		byte[] responseData, fileData = null;
		DatagramPacket newPacket;
		Random rand = new Random(); 
		int waitTime = rand.nextInt(401); // 0 a 400 milisegundos

		File backups = new File("backups");
		if(!backups.exists()){
			System.out.println("There is no backups on this peer!");
			return 1;
		}
		else{ //Verificar se já tenho o chunk
			File filename = new File("backups/" + fileID);
			if(filename.exists()){
				File[] chunks = filename.listFiles();
				int chunkNumber = Integer.parseInt(chunkNo);
				File tmp = new File(chunkNo + ".chunk");

				if(!Arrays.asList(chunks).contains(tmp)){
					System.out.println("Chunk is not stored on this peer.");
					return 2;
				}
				else{
					FileInputStream chunkFile = new FileInputStream("backups/" + fileID + "/" + chunkNo + ".chunk");
					chunkFile.read(fileData);
					chunkFile.close();
				}
			}
			else{ //Guardar o chunk
				System.out.println("There is no backups for this file.");
				return 3;
			}
		}

		String responseMessage = MessageFormat.createMessage("CHUNK", "1.0", fileID, chunkNo, "", fileData);

		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//SEND CHUNK MESSAGE MESSAGE
		adressMDR = Main.mdr.getAddress();
		responseData = responseMessage.getBytes();
		newPacket = new DatagramPacket(responseData, responseData.length, adressMDR, Main.mdr.getMDRPort());

		return 0;
	}
}
