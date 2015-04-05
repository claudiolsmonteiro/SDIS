package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Arrays.copyOfRange;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author F�bio
 */


public class MessageFormat {
	private static final String CRLF = "\r\n";
	public static final int MAXDATASIZE = 64000;

	protected byte[] buf = new byte[65536];
	protected byte[] headerBuf = new byte[1536];
	protected byte[] dataBuf = new byte[MAXDATASIZE];

	String[] headerString = new String[6];

	//Create Header Message receiving description of the message (type, version, etc)

	public static String createMessageHeader(String type, String version, String fileID, String chunkNo, String replicDeg){
		String headerMSG = new String();
		headerMSG = type + " " + version + " " + fileID + " " + chunkNo + " " + replicDeg + CRLF + CRLF;

		return headerMSG;
	}

	public static byte[] getFileData(String filepath, String filename) throws IOException{
		Path path = FileSystems.getDefault().getPath(filepath, filename); 
		byte[] data = Files.readAllBytes(path);        
		return data;      
	}

	public static byte[][] getDataArray(byte[] fileData){
		int noChunks = fileData.length/MAXDATASIZE;
		byte[][] dataSplitted = new byte[noChunks + 1][];

		for(int i = 0; i < noChunks + 1; i++){
			if(i == noChunks){
				dataSplitted[i] = copyOfRange(fileData, 0 + i*MAXDATASIZE, fileData.length);
			}
			else{
				dataSplitted[i] = copyOfRange(fileData, 0 + i*MAXDATASIZE, MAXDATASIZE + i*MAXDATASIZE);
			}
		}

		return dataSplitted;
	}

	public static String createMessage(String type, String version, String fileID, String chunkNo, String replicDeg, byte[] fileData){
		String messageDone = createMessageHeader(type, version, fileID, chunkNo, replicDeg) + (new String (fileData, StandardCharsets.ISO_8859_1));

		return messageDone;        
	}

	public static String[] createMessageArray(String type, String version, String fileID, String replicDeg, byte[][] fileData){
		String[] messagesArray = new String[fileData.length];

		for(int i = 0; i < fileData.length; i++){
			messagesArray[i] = createMessage(type, version, fileID, Integer.toString(i), replicDeg, fileData[i]);
		}

		return messagesArray;
	}
	
	public static int processMessage(String message, String[] values, byte[] fileData){
		
		String[] messageData = message.split("\r\n\r\n");
		
		String[] messageHeader = messageData[0].split("\\s+");
		
		if(messageHeader.length == 2){ //DELETE command
			values[0] = messageHeader[0];
			values[1] = messageHeader[1];
		}
		else if(messageHeader.length == 4){ //STORED, GETCHUNK, CHUNK & REMOVED commands
			values[0] = messageHeader[0];
			values[1] = messageHeader[1];
			values[2] = messageHeader[2];
			values[3] = messageHeader[3];
		}
		else if(messageHeader.length == 5){
			values[0] = messageHeader[0];
			values[1] = messageHeader[1];
			values[2] = messageHeader[2];
			values[3] = messageHeader[3];
			values[4] = messageHeader[4];
		}
		else{
			System.out.println(messageHeader.length);
			for(int i = 0; i < messageHeader.length; i++){
				System.out.println(messageHeader[i]);
			}
			return 1; //Return 1, failed message header processing
		}
		
		if(messageData.length == 2){ //Message with data
			fileData = messageData[1].getBytes();
		}
		else if(messageData.length > 2)
			return 2; //Return 2, failed message data processing
	
		return 0; //Message processed successfully
		
	}
	
	public static void mergeChunks(String filename, String fileID) throws IOException{
		
		File file3 = new File("restored");

		if (!file3.exists()) {
			file3.mkdir(); //make folder backups to store backups
		}
		
		System.out.println("########## A comecar o processo de restauro do ficheiro...");
		
		FileOutputStream ficheiroRestaurado = new FileOutputStream("restored/" + filename, true);
		File fileName = new File("restored/" + fileID);
		File[] listFiles = fileName.listFiles();
		Utilities.sortFilesByIdName(true, listFiles);
		
		for(int i = 0; i < listFiles.length; i++){
			System.out.println("Juntou o chunk: #" + i);
			RandomAccessFile f = new RandomAccessFile(listFiles[i], "r");
			byte[] b = new byte[(int)f.length()];
			f.read(b);
			ficheiroRestaurado.write(b);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			f.close();
		}
		ficheiroRestaurado.close();		
	}
}
