package fi.utu.tech.telephonegame.network;


import java.net.NetworkInterface;

import java.io.*;
import java.net.*;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import java.net.Inet6Address;
import java.net.InetAddress;

public class NetworkService extends Thread implements Network {
	/*
	 * Do not change the existing class variables
	 * New variables can be added
	 */
	private TransferQueue<Object> outQueue = new LinkedTransferQueue<Object>();
	private TransferQueue<Object> inQueue = new LinkedTransferQueue<Object>();
	private Resolver resolver;
	
	private CopyOnWriteArrayList<SocketHandler> socketList = new CopyOnWriteArrayList<SocketHandler>();
	
	

	/*
	 * No need to change the construtor
	 */
	public NetworkService() {
		this.start();
	}
	
	public void lisaaSocketListiin(SocketHandler s) {
		
		System.out.println(s);
		socketList.add(s);
		System.out.println("Lisätty: " + socketList);
		//TODO miettiä miten lisätään socketlistiin
	}


	/*
	 * In this method a server instance has to be created.
	 * The port used to listen incoming connections is provided by the templete
	 */

	public void initialize(int serverport) {
		//ServerSocket palvelinSocket;
		System.out.println("initialize kutsuttu");
		
			
			
				
				Palvelin palvelin;
				try {
					
					palvelin = new Palvelin(serverport, this);
					palvelin.start();
					//System.out.println("Yhdistetty ");
					//ServerSocket palvelinSocket = new ServerSocket(serverport);
					//System.out.println("Yhdistetty pt2");
					//Socket asiakasSocket = palvelinSocket.accept();
					//System.out.println("Yhdistetty pt3");
					//SocketHandler socketHandler = new SocketHandler(asiakasSocket);
					//socketList.add(socketHandler);
					//socketHandler.start();
					
					
				} catch (IOException e) {
					System.out.println("servercatchi");
					e.printStackTrace();
				}
//				System.out.println("ei mitään initializessa pt2");
				
				
			}
	

	
	/*
	 * In this method you need to connect to a server node.
	 * The IP address and port are provided by the template.
	 */
	
	public void connect(String clientIP, int clientPort) {
		try {
			System.out.println("haloo??");
			Socket asiakasSocket = new Socket(clientIP, clientPort);
			SocketHandler socketHandler = new SocketHandler(asiakasSocket, this);
			System.out.println("toimiiko");
			socketHandler.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This method is used to send the message to all connected nodes
	 * 
	 * The object to send is given to the method as an argument
	 * and is then put into an envelope object
	 */
	private void send(Object out) {
		//Send the env to all nodes.
		Envelope env = new Envelope(out);
		
		for (SocketHandler socketHandler : socketList) {
			socketHandler.send(env);
		}
	}

	
	
	
	/*
	 * Don't edit any methods below this comment
	 */

	@Override
	public void postMessage(Object outMessage) {
		try {
			inQueue.offer(outMessage, 1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public TransferQueue<Object> getOutputQueue() {
		return this.outQueue;
	}

	public void run() {
		while (true) {
			try {
				send(inQueue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startResolver(boolean serverMode, int netType, int rootServerPort, String rootIPAddress) {
		this.resolver = new Resolver(4445, serverMode, netType, rootServerPort, rootIPAddress);
	}

	public String[] resolve() throws UnknownHostException {
		return resolver.resolve();
	}
	
	public ArrayList<String> getIPs() {
		ArrayList<String> result = new ArrayList<String>();
		Enumeration interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) interfaces.nextElement();
				Enumeration inetAddresses = n.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inet = (InetAddress) inetAddresses.nextElement();
					if (!(inet instanceof Inet6Address) && !inet.isLoopbackAddress() && !inet.isLinkLocalAddress()) {
						result.add(inet.getHostAddress());
					}

				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return result;
	}

}
