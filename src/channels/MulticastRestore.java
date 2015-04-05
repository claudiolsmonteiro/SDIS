package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import protocols.Restore;
import utilities.MessageFormat;

public class MulticastRestore extends Thread{
	protected InetAddress address;
	protected SocketAddress socketAddress;
	protected MulticastSocket MDRsocket;
	protected DatagramPacket packet ;
	protected int MDRport;

	protected byte[] data = new byte[65536];
	protected volatile boolean running = true;
	public MulticastRestore(String IPaddress, int port) throws IOException{
		address = InetAddress.getByName(IPaddress);
		socketAddress = new InetSocketAddress(port);
		MDRsocket = new MulticastSocket(null);
		MDRsocket.bind(socketAddress);
		MDRsocket.joinGroup(address);
		MDRport = port;
	}
	
	public void run() {

		while(running){

			packet = new DatagramPacket(data, data.length);
			String dataReceived;

			//Waiting for packets
			try {
				MDRsocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			dataReceived = new String(packet.getData(), StandardCharsets.ISO_8859_1);

			try {
				processData(dataReceived);
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
	}
	public void processData(String data) throws IOException{
		String[] messageValues = new String[5];
		byte[] bodyData = new byte[64000];
		MessageFormat.processMessage(data, messageValues, bodyData);
		Thread t1;
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
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getMDRPort() {
		return MDRport;
	}

	public MulticastSocket getMDRsocket() {
		return MDRsocket;
	}
}
