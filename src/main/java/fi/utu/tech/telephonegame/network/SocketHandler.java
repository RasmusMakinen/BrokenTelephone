package fi.utu.tech.telephonegame.network;

import java.io.*;
import java.net.*;

import fi.utu.tech.telephonegame.Message;


public class SocketHandler extends Thread {
	private Socket socket;
	private ObjectOutputStream ulosTulo;
	private ObjectInputStream sisaanTulo;
	private NetworkService networkService;

	public SocketHandler(Socket socket, NetworkService networkService) {
		this.socket = socket;
		this.networkService = networkService;
		networkService.lisaaSocketListiin(this);
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
		while (true) {
		//Kootaan message olio envelope olion sisällöstä
			try {
				//envelopesta message olio
				Message kirjekuori = (Message) (((Envelope)sisaanTulo.readObject()).getPayload());			
				networkService.getOutputQueue().add(kirjekuori);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

		public void send (Envelope env){
			try {
				ulosTulo.writeObject(env);
				ulosTulo.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

