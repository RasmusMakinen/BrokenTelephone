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
//		while (true) {
		//Kootaan message olio envelope olion sisällöstä
			try {
				//envelopesta message olio
				Message kirjekuori = (Message) (((Envelope)sisaanTulo.readObject()).getPayload());
				
				//messagesta sisältö
				String kirjekuoriSisalto = kirjekuori.getMessage();
				
				//messagesta väri
				Integer kirjekuoriVari = kirjekuori.getColor();
				
				//luodaan lopullinen lähetettävä message
				Message viesti = new Message(kirjekuoriSisalto, kirjekuoriVari);
				
				networkService.getOutputQueue().add(viesti);
				System.out.println("Vastaanotettu " + viesti.toString() + " " + viesti.getMessage().toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

//		}
	}

		public void send (Envelope env){
			try {
				ulosTulo.writeObject(env);
				System.out.println("Lähetetty: " + env.toString());
				ulosTulo.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

