package channels;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import utilities.MessageFormat;

public class MulticastControl extends Thread{

	protected InetAddress address;
	protected SocketAddress socketAddress;
	protected MulticastSocket MCsocket;
	protected DatagramPacket packet;
	
	protected volatile boolean running = true;
	
	
	public MulticastControl(String IPaddress, int port) throws IOException{
		address = InetAddress.getByName(IPaddress);
		socketAddress = new InetSocketAddress(port);
		MCsocket = new MulticastSocket(null);
		MCsocket.bind(socketAddress);
		MCsocket.joinGroup(address);
		this.start();
	}
	
	public void run() {
		
		while(running){
			
			String dataReceived;
			
			//Waiting for packets
			try {
				MCsocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			dataReceived = new String(packet.getData(), StandardCharsets.ISO_8859_1);
			
			processData(dataReceived);
			
			
		}
	}
	
	public void terminate(){
		running = false;
	}
	
	public void processData(String data){
		String[] messageValues = new String[5];
		byte[] bodyData = null;
		MessageFormat.processMessage(data, messageValues, bodyData);
		
		String type = messageValues[0];
		
		switch(type){
			case "STORED":{
				//FAZER ALGO
				break;
			}
			case "GETCHUNK":{
				//FAZER ALGO
				break;
			}
			case "DELETE":{
				//FAZER ALGO
				break;
			}
			case "REMOVED":{
				//FAZER ALGO
				break;
			}
		}
		
		
	}
}
