package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
	protected static InetAddress adressMDB;
	//Fun��o que envia um pedido de restauro do ficheiro, GETCHUNK para o MC (GETCHUNK)
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
	
	
	
	//Fun��o que quando recebe um GETCHUNK, verifica se tem chunks do ficheiro e envia-os para o MDR (CHUNK)
	public int checkChunk(String fileID,int chunkNo) {
		adressMDB = Main.mdb.getAddress();
		byte[] responseData;
		DatagramPacket newPacket;
		
		ArrayList<String> chunksRestored = new ArrayList<String>();
		
		String chunkheader = MessageFormat.createMessageHeader("CHUNK", "1.3", fileID, Integer.toString(chunkNo), "");
		
		chunksRestored.add(chunkheader);
		
		Random rand = new Random(); 
		int waitTime = rand.nextInt(401); // 0 a 400 milisegundos	
		long start = System.currentTimeMillis(), end = start + waitTime; // define tempo de espera inicial antes de responder
		
		while (System.currentTimeMillis() < end)
		{
			// Aguarda o tempo aleatorio entre 0 a 400 milisegundos
		}
		
		//SEND CHUNK MESSAGE MESSAGE
		adressMDB = Main.mdr.getAddress();
		for(int i=0; i<chunksRestored.size(); i++){
			responseData = chunksRestored.get(i).getBytes();
			newPacket = new DatagramPacket(responseData, responseData.length, adressMDB, Main.mdr.getMDRPort());

		}
		
		
		
		return 0;
	}
}
