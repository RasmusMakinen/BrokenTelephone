package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Palvelin extends Thread{
	
	private ServerSocket palvelinSocket = null;
	private NetworkService networkService;
	private Socket soketti = null;
	private int portti;
	

	
	public Palvelin(int portti, NetworkService networkService) throws IOException {
    	this.networkService = networkService;
    	this.portti = portti;
    	this.palvelinSocket = new ServerSocket(portti);
    	
	}
    public void run()  {
    	
    	while(true) {
    		
    		try {
				soketti=palvelinSocket.accept();
				System.out.println("asiakas yhdistetty");
				
				SocketHandler s = new SocketHandler(soketti, networkService);
				s.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
    	}
    	
    	
    }
    

		
		
		
		
		
	}



