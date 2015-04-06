package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import protocols.Backup;
import utilities.MessageFormat;

public class MulticastBackup extends Thread {

	protected InetAddress address;
	protected SocketAddress socketAddress;
	protected MulticastSocket MDBsocket;
	protected DatagramPacket packet ;
	protected int MDBport;

	protected byte[] data = new byte[65536];
	protected volatile boolean running = true;

	public MulticastBackup(String IPaddress, int port) throws IOException{
		address = InetAddress.getByName(IPaddress);
		socketAddress = new InetSocketAddress(port);
		MDBsocket = new MulticastSocket(null);
		MDBsocket.bind(socketAddress);
		MDBsocket.joinGroup(address);
		MDBport = port;
	}

	public void run() {

		while(running){

			packet = new DatagramPacket(data, data.length);
			String dataReceived;

			//Waiting for packets
			try {
				MDBsocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if(packet.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())){
					continue;
				}
				else{
					dataReceived = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.ISO_8859_1);

					try {
						processData(dataReceived);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	public void terminate(){
		running = false;
	}

	public void processData(String data) throws IOException{
		String[] messageValues = new String[5];
		String fileData = null;
		int size = MessageFormat.processMessage(data, messageValues, fileData);
		byte[] bodyData = new byte[size];

		Thread t1;
		t1 = new Thread(new Runnable() {
			public void run() {
				int ret = 2;
				try {
					ret = Backup.receiveChunk(messageValues, bodyData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(ret == 0){
					System.out.println("Chunk #" + messageValues[3] + " stored successfully!");
				}
			}
		});
		t1.start();
	}


	public InetAddress getAddress() {
		return address;
	}

	public MulticastSocket getMDBsocket() {
		return MDBsocket;
	}

	public int getMDBPort() {
		return MDBport;
	}

}
