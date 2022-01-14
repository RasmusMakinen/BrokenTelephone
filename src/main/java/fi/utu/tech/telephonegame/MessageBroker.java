package fi.utu.tech.telephonegame;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TransferQueue;
import fi.utu.tech.telephonegame.network.Network;
import fi.utu.tech.telephonegame.network.NetworkService;
import fi.utu.tech.telephonegame.util.ConcurrentExpiringHashSet;




public class MessageBroker extends Thread {

	
	/*
	 * No need to change there variables
	 */
	private TransferQueue<Object> procQueue;
	private Network network;
	private Gui_io gui_io;
	private final int rootServerPort = 8050;
	private ConcurrentExpiringHashSet<UUID> prevMessages = new ConcurrentExpiringHashSet<UUID>(1000, 5000);

	/*
	 * No need to edit the constructor
	 */
	
	public MessageBroker(Gui_io gui_io) {
		this.gui_io = gui_io;
		network = new NetworkService();
		procQueue = network.getOutputQueue();
	}

	
	/*
	 * In the the process method you need to:
	 * 1. test the type of the incoming object
	 * 2. keep track which messages are already processed in this node
	 * 3. Show the incoming message in the received message text area
	 * 4. Change the text and the color using the Refiner class
	 * 5. Set the new color to the color are
	 * 6. Show the refined message in the refined message text area
	 * 7. Return the message 
	 */

	private Message process(Object procMessage) {

			Message chekattuViesti = (Message) procMessage;
			
			if (!(procMessage instanceof Message)) { 
				return null;
			}

			System.out.println("tän ID " +chekattuViesti.getId());
			
			if (this.prevMessages.containsKey(chekattuViesti.getId())) { 
			System.out.println("this.prevMessages.containsKey(chekattuViesti.getId())");
				return null;
			}
			
			this.gui_io.setReceivedMessage(chekattuViesti.getMessage()); 
			chekattuViesti.setColor(Refiner.refineColor(chekattuViesti.getColor()));
			chekattuViesti.setMessage(Refiner.refineText(chekattuViesti.getMessage())); 
			this.gui_io.setSignal(chekattuViesti.getColor()); 
			this.gui_io.setRefinedMessage(chekattuViesti.getMessage()); 
			this.prevMessages.put(chekattuViesti.getId()); 
			return chekattuViesti;
	}


	/*
	 * In the run method you need to:
	 * 
	 * 1. Check if there are any incoming objects
	 * 2. When a new object is available it has to been processed using the process method
	 * 3. Send the processed message returned by the process method
	 * 
	 */
	public void run() {
		
		while(true) {
			Object jonosta = null;
			try {
				jonosta = this.procQueue.take();
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
			Message muutettuViesti = this.process(jonosta);
			if(!(muutettuViesti == null)) {
				prevMessages.put(muutettuViesti.getId());this.network.postMessage(muutettuViesti);
			}	
		}
	}
	
	
	
	/*
	 * Do not edit anything below this point.
	 * 
	 */
	
	public void connect(int netType, boolean rootNode, String rootIPAddress) {
		if (rootNode) {
			System.out.println("Root node");
			network.initialize(rootServerPort);
			network.startResolver(true, netType, rootServerPort, rootIPAddress);
		} else {
			System.out.println("Client");
			network.startResolver(false, netType, rootServerPort, rootIPAddress);
			try {
			String[] addresses = network.resolve();
			network.initialize(Integer.parseInt(addresses[0]));
			network.connect(addresses[1], Integer.parseInt(addresses[2]));
			}
			catch (UnknownHostException e) {
				gui_io.enableConnect();
			}
		}
	}

	public void send(Message message) {
		prevMessages.put(message.getId());
		network.postMessage(message);
	}

	public void send(String text) {
		Message message = new Message(text, 0);
		prevMessages.put(message.getId());
		network.postMessage(message);
	}
	
	public ArrayList<String> getIPs() {
		return network.getIPs();
	}

	
}

