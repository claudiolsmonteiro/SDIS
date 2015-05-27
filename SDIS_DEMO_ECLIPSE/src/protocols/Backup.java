package protocols;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import utilities.CryptoException;
import utilities.CryptoUtils;
import utilities.Encrypt;
import utilities.MessageFormat;

public class Backup {
	protected static DatagramPacket packet;
	protected static InetAddress adressMC;
	protected static InetAddress adressMDB;

	//Fun��o que recebe um chunk e guarda-o, envia uma mensagem STORED para o MC (STORED)
	//Recebe um PUTCHUNK version filename chunkNo replicDeg <CRLF> <CRLF> Body

	public static int receiveChunk(String[] chunkHeader, byte[] chunkBody) throws IOException{

		String version = chunkHeader[1];
		String fileID = chunkHeader[2];
		String chunkNo = chunkHeader[3];
		Random rand = new Random(); 
		int waitTime = rand.nextInt(401); // 0 a 400 milisegundos	

		File backups = new File("backups");
		if(!backups.exists()){
			System.out.println("There is no backups on this peer!\nCreating backups folder...");
			backups.mkdir();
		}
		if(chunkBody.length == 0){
			System.out.println("Last message of file, multiple of 64KB.");
			File lastChunk = new File("backups/" + fileID + "/" + chunkNo + ".chunk");
			lastChunk.createNewFile();
		}
		else{ //Verificar se j� tenho o chunk
			File filename = new File("backups/" + fileID);
			if(filename.exists()){
				File[] chunks = filename.listFiles();
				File tmp = new File(chunkNo + ".chunk");

				File checkChunkExist = new File("backups/" + fileID + "/" + chunkNo + ".chunk");
				if(checkChunkExist.exists()){
					System.out.println("Chunk already stored on this peer.");
					return 2;
				}
				else{
					FileOutputStream saveChunk = new FileOutputStream("backups/" + fileID + "/" + chunkNo + ".chunk");
					saveChunk.write(chunkBody);
					saveChunk.close();
				}
			}
			else{ //Guardar o chunk
				filename.mkdir();
				FileOutputStream saveChunk = new FileOutputStream("backups/" + fileID + "/" + chunkNo + ".chunk");
				saveChunk.write(chunkBody);
				saveChunk.close();
			}
		}

		//Enviar mensagem para o MC a confirmar

		String response = MessageFormat.createMessageHeader("STORED", version, fileID, chunkNo, "");

		if(Main.chunkCache.containsKey(fileID + chunkNo)){
			int storedTimes = Main.chunkCache.get(fileID + chunkNo);
			storedTimes++;
			Main.chunkCache.put(fileID + chunkNo, storedTimes);
		}
		else{
			Main.chunkCache.put(fileID + chunkNo, 1);
		}

		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		adressMC = Main.mc.getAddress();
		byte[] responseData =  response.getBytes();
		packet = new DatagramPacket(responseData, responseData.length, adressMC, Main.mc.getMCPort());
		Main.mc.getMCsocket().send(packet);

		return 0;	
	}


	//Fun��o que pede para guardar um ficheiro, divide-o e envia um request PUTCHUNK para o MCB (PUTCHUNK) 
	public static int sendChunk() throws NoSuchAlgorithmException, IOException, InterruptedException{
		
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		SecretKey secretKey = keyGen.generateKey();
		System.out.println(secretKey);
		
		
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
		String key ="OIOIOIOIOIOIOIOI";
        File encryptedFile = new File(filepath + "/" + "document.encrypted");
        //File decryptedFile = new File("document.decrypted");
         
        try {
            CryptoUtils.encrypt(key, fileName, encryptedFile);
            //CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
		System.out.print("Insert the replication degree (1-9): ");

		int replicDegree = scanner.nextInt();

		while(replicDegree < 1 || replicDegree > 9){
			System.out.println("The replication degree is out of bounds...\nInsert the replication degree (1-9): ");
			replicDegree = scanner.nextInt();
		}
		
		System.out.println("## Filepath: " + fileDir.getAbsolutePath());
		System.out.println("## Filename: " + fileName.getName());
		byte[] fileData = MessageFormat.getFileData(filepath, "document.encrypted");

		System.out.println("## Filesize: " + fileData.length + " Bytes");

		byte[][] fileSplitted = MessageFormat.getDataArray(fileData);

		
		String fileID = Encrypt.SHA256(filename);



		String[] requests = MessageFormat.createMessageArray("PUTCHUNK", "1.0", fileID, Integer.toString(replicDegree), fileSplitted);
		System.out.println("## Going to send " + requests.length + " chunks to the network.\n");

		String backupFile = new String(filename + "/" + requests.length + "/" + fileData.length + ":");
		
		File logs = new File("info");
		if(!logs.exists())
			logs.mkdir();
		FileOutputStream backupInfo = new FileOutputStream("info/backup.info", true);
		
		backupInfo.write(backupFile.getBytes());
		
		//scanner.close();


		//Envio de requests
		int count = 0;
		for(int i = 0; i < requests.length; i++){
			int timeout = 500; //500milis
			while(count < 5){
				System.out.println("Sending chunk #" + i + "... Try #" + (count+1));
				if(Main.chunkCache.containsKey(fileID + Integer.toString(i))){
					if(Main.chunkCache.get(fileID + Integer.toString(i)) >= replicDegree){
						System.out.println("Reached replication degree on chunk #" + i);
						break;
					}
				}

				adressMDB = Main.mdb.getAddress();
				byte[] requestData =  requests[i].getBytes();
				packet = new DatagramPacket(requestData, requestData.length, adressMDB, Main.mdb.getMDBPort());
				Main.mdb.getMDBsocket().send(packet);

				Thread.sleep(timeout);
				timeout *= 2;
				count++;
			}
			if(count == 5){
				System.out.println("TIMEOUT CHUNK #" + i);
			}
			count = 0;
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//MessageFormat.mergeChunks(filename, fileID);
		return 0;
	}
}
