package game.maze;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import math.geom.*;

public class PlayerHandler implements Runnable {
	
	/* Notification from Player:
	 * posx,posy,posz,movei,movej,movek,shooting, 
	 * 
	 * Send to player:
	 * Start of game: map,otherPlayerInfo,
	 * During game: otherPlayers,otherPlayerInfo,damage,
	 * 
	 */
	
	Player player = new Player();
	
//	public Point3f pos;
//	public Vector3f moveVect;
//	public boolean shooting;
	
	Random rand = new Random();

//	String name;
	boolean connected = false;
	
	Socket clientSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	
	ArrayList<String> incomingMessages = new ArrayList<String>();
	
	public PlayerHandler(Socket socket, Maze maze) {
		clientSocket = socket;
		connected = true;
		try {
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			player.name = in.readLine();
//			MazeServer.printInfo("Sending map...");
			sendMessage(maze.createDataPacket());
//			MazeServer.printInfo("Map Sent");
//			MazeServer.printInfo("Sending Spawn...");
			Point2i p = maze.spawnPoints.get(rand.nextInt(maze.spawnPoints.size()));
			sendMessage(p.x + " " + p.y);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getMessages() {
		ArrayList<String> list = new ArrayList<String>(incomingMessages);
		incomingMessages.clear();
		return list;
	}
	
	public String getName() {
		return player.name;
	}

	@Override
	public void run() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		while(connected) {
//			System.out.println("loop");
			try {
				if(!clientSocket.isConnected()) {connected = false; continue;}
				String text = in.readLine();
				db = dbf.newDocumentBuilder();
				InputSource source = new InputSource();
				source.setCharacterStream(new StringReader(text));
				Document doc = db.parse(source);
				for(int i=0; i<doc.getChildNodes().getLength(); i++) {
					switch(doc.getChildNodes().item(i).getNodeName()){
					case "player": player = Player.constructPlayer(doc.getChildNodes().item(i)); break;
					case "entity": break;
					}
				}
				
			} catch(IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			/*try {
				if(!clientSocket.isConnected()) {connected = false; continue;}
				String line = null;
				try{line = in.readLine();}catch(SocketException e) {}
				if(line == null) {connected = false; continue;}
				if(line.split(":")[0].equals("[PI]")) { //Player Info
					line = line.split(":")[1];
//					incomingMessages.add(line);
//					System.out.println(line);
					String[] lines = line.split(",");
					pos = new Point3f(Float.parseFloat(lines[0]), Float.parseFloat(lines[1]), Float.parseFloat(lines[2]));
					moveVect = new Vector3f(Float.parseFloat(lines[3]), Float.parseFloat(lines[4]), Float.parseFloat(lines[5]));
					shooting = Boolean.parseBoolean(lines[6]);
					System.out.println(pos);
				}
				else incomingMessages.add(line);
			} catch (IOException e) {
				e.printStackTrace();
			}/**/ 
		}
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void sendMessage(String message) {
		out.println(message);
		out.flush();
	}
	
	public void sendData(ArrayList<String> data, String open, String close) {
		sendMessage(open);
		for(int i=0; i<data.size(); i++) {
			sendMessage(data.get(i));
			System.out.print(data.get(i) + "; ");
		}
		System.out.println();
		sendMessage(close);
	}
	
}
