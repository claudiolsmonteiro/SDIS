package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import utilities.MessageFormat;

public class MulticastBackup extends Thread {

	protected InetAddress address;
	protected SocketAddress socketAddress;
	protected MulticastSocket MDBsocket;
	protected DatagramPacket packet ;
	protected int MDBport;

	protected byte[] data = new byte[256];
	protected volatile boolean running = true;


	public MulticastBackup(String IPaddress, int port) throws IOException{
		address = InetAddress.getByName(IPaddress);
		socketAddress = new InetSocketAddress(port);
		MDBsocket = new MulticastSocket(null);
		MDBsocket.bind(socketAddress);
		MDBsocket.joinGroup(address);
		MDBport = port;
		this.start();
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
