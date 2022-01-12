package fi.utu.tech.telephonegame.network;

import java.io.*;
import java.net.*;


public class SocketHandler extends Thread {
	private Socket socket;
	private ObjectOutputStream ulosTulo;
	private ObjectInputStream sisaanTulo;
	private NetworkService networkService;
	
	public SocketHandler(Socket socket, NetworkService networkService ) {
		this.socket = socket;
		this.networkService = networkService;
		networkService.lisääSocketListiin(this);
		
		//TODO tähän pitäs lisätä 
		
	}

	public void run() {
		try {
			InputStream iS = socket.getInputStream();
			OutputStream oS = socket.getOutputStream(); 
			ulosTulo = new ObjectOutputStream(oS); 
			sisaanTulo = new ObjectInputStream(iS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Envelope env) {
		try {
			ulosTulo.writeObject(env);
			ulosTulo.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
