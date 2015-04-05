package channels;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import protocols.Restore;
import utilities.MessageFormat;

public class MulticastControl extends Thread{

	protected InetAddress address;
	protected SocketAddress socketAddress;
	protected MulticastSocket MCsocket;
	protected DatagramPacket packet ;
	protected int MCport;

	protected byte[] data = new byte[256];
	protected volatile boolean running = true;


	public MulticastControl(String IPaddress, int port) throws IOException{
		address = InetAddress.getByName(IPaddress);
		socketAddress = new InetSocketAddress(port);
		MCsocket = new MulticastSocket(null);
		MCsocket.bind(socketAddress);
		MCsocket.joinGroup(address);
		MCport = port;
		this.start();
	}

	public void run() {

		while(running){

			packet = new DatagramPacket(data, data.length);
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
		Thread t1;
		String type = messageValues[0];

		switch(type){
		case "STORED":{
			t1 = new Thread(new Runnable() {
				public void run() {
					System.out.println("STORED CHUNK: " + messageValues[3]);
				}
			});
			t1.start();
			break;
		}
		case "GETCHUNK":{
			//FAZER ALGO
			t1 = new Thread(new Runnable() {
				public void run() {
					int ret = 1;
					try {
						ret = Restore.checkChunk(messageValues[2], messageValues[3]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(ret == 0){
						System.out.println("Chunk #" + messageValues[3] + " restored successfully!");
					}
				}
			});
			t1.start();
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

	public InetAddress getAddress(){
		return address;
	}

	public int getMCPort() {
		return MCport;
	}

	public MulticastSocket getMCsocket() {
		return MCsocket;
	}
}
