
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import utilities.Encrypt;
import utilities.Utilities;
import static java.util.Arrays.copyOfRange;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fábio
 */


public class MessageFormat {
	private static final String CRLF = "\r\n";
	private static final int MAXDATASIZE = 64000;

	protected byte[] buf = new byte[65536];
	protected byte[] headerBuf = new byte[1536];
	protected byte[] dataBuf = new byte[MAXDATASIZE];

	String[] headerString = new String[6];

	//Create Header Message receiving description of the message (type, version, etc)

	public static String createMessageHeader(String type, String version, String fileID, int chunkNo, String replicDeg){
		String headerMSG = new String();
		headerMSG = type + " " + version + " " + fileID + " " + chunkNo + " " + replicDeg + CRLF;

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

	public static String createMessage(String type, String version, String fileID, int chunkNo, String replicDeg, byte[] fileData){
		String messageDone = createMessageHeader(type, version, fileID, chunkNo, replicDeg) + (new String (fileData, StandardCharsets.ISO_8859_1));

		return messageDone;        
	}

	public static String[] createMessageArray(String type, String version, String fileID, String replicDeg, byte[][] fileData){
		String[] messagesArray = new String[fileData.length];

		for(int i = 0; i < fileData.length; i++){
			messagesArray[i] = createMessage(type, version, fileID, i, replicDeg, fileData[i]);
		}

		return messagesArray;
	}

	public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
		System.out.println("Insere o directorio do ficheiro:");
		Scanner scanner = new Scanner(System.in);
		String filepath = scanner.nextLine();
		System.out.println("Insere o nome do ficheiro:");
		String filename = scanner.nextLine();

		File file2 = new File("backups");

		if (!file2.exists()) {
			file2.mkdir(); //make folder backups to store backups
		}

		String filename2 = Encrypt.SHA256(filename);
		File file1 = new File("backups/" + filename2);

		if (!file1.exists()) {
			file1.mkdir(); //make folder with the file name to store chunks
		}


		byte[] file;
		file = getFileData(filepath, filename);
		byte[][] fileSplitted = getDataArray(file);
		String[] messages = createMessageArray("PUTCHUNK", "1.0", "filename", "replicDegree", fileSplitted);

		for(int i = 0; i < fileSplitted.length; i++){

			FileOutputStream fileOuputStream = new FileOutputStream("backups" + "/" + filename2 + "/" + i + ".chunk", true);
			fileOuputStream.write(fileSplitted[i]);
			fileOuputStream.close();
			System.out.println("ACABOU O CHUNK!!!!!!!");
		}
		
		
		//Restauro do ficheiro
		File folder = new File("backups/" + filename2);
		File[] listFiles = folder.listFiles();
		Utilities.sortFilesByIdName(true, listFiles);
		
		File file3 = new File("restored");

		if (!file3.exists()) {
			file3.mkdir(); //make folder backups to store backups
		}
		
		System.out.println("########## A começar o processo de restauro...");
		
		FileOutputStream ficheiroRestaurado = new FileOutputStream("restored/teste.JPG", true);
		for(int i = 0; i < listFiles.length; i++){
			System.out.println("Juntou o chunk: #" + i);
			RandomAccessFile f = new RandomAccessFile(listFiles[i], "r");
			byte[] b = new byte[(int)f.length()];
			f.read(b);
			ficheiroRestaurado.write(b);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ficheiroRestaurado.close();
	}
}
