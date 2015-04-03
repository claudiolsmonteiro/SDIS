
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
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
        String messageDone = createMessageHeader(type, version, fileID, chunkNo, replicDeg) + fileData.toString();
        
        return messageDone;        
    }
    
    public static String[] createMessageArray(String type, String version, String fileID, String replicDeg, byte[][] fileData){
        String[] messagesArray = new String[fileData.length];
        
        for(int i = 0; i < fileData.length; i++){
            messagesArray[i] = createMessage(type, version, fileID, i, replicDeg, fileData[i]);
        }
        
        return messagesArray;
    }
    
    public static void main(String args[]) throws IOException {
        System.out.println("Insere a merda do directorio do ficheiro:");
        Scanner scanner = new Scanner(System.in);
        String filepath = scanner.nextLine();
        System.out.println("Insere a merda do nome do ficheiro:");
        String filename = scanner.nextLine();
        
        byte[] file;
        file = getFileData(filepath, filename);
        byte[][] fileSplitted = getDataArray(file);
        String[] messages = createMessageArray("yolo", "1.0", "file1", "1", fileSplitted);
        
        for(int i = 0; i < messages.length; i++){
            System.out.println(messages[i]);
        }        
    }
    
    
    
    
    
    
}
