package channels;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import protocols.Delete;
import protocols.Main;
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

			/*try {
				if(packet.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())){
					continue;
				}
				else{*/
					dataReceived = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.ISO_8859_1);

					processData(dataReceived);
				/*}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}

	public void terminate(){
		running = false;
	}

	public void processData(String data){
		String[] messageValues = new String[5];
		String fileData = null;
		MessageFormat.processMessage(data, messageValues, fileData);
		Thread t1;
		String type = messageValues[0];

		switch(type){
		case "STORED":{
			t1 = new Thread(new Runnable() {
				public void run() {
					if(Main.chunkCache.containsKey(messageValues[2] + messageValues[3])){
						int increment = Main.chunkCache.get(messageValues[2] + messageValues[3]);
						increment++;
						Main.chunkCache.put(messageValues[2] + messageValues[3], increment);
					}
					else{
						Main.chunkCache.put(messageValues[2] + messageValues[3], 1);
					}
				}
			});
			t1.start();
			break;
		}
		case "GETCHUNK":{
			//FAZER ALGO
			t1 = new Thread(new Runnable() {
				public void run() {
					try {
						Restore.checkChunk(messageValues[2], messageValues[3]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t1.start();
			break;
		}
		case "DELETE":{
			//FAZER ALGO
			t1 = new Thread(new Runnable() {
				public void run() {
					try {
						Delete.deleteChunk(messageValues[2]);
					} catch (NoSuchAlgorithmException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t1.start();
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
