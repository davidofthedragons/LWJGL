package game.maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MazeServer {
	
	Random rand = new Random();
	
	Maze maze;
	
	public final int MAX_PLAYERS = 5;
	ArrayList<PlayerHandler> players = new ArrayList<PlayerHandler>();
	
	ServerSocket serverSocket = null;
	
	public MazeServer(final int port) {
		
		maze = Maze.loadMaze("res/map-mult1.png");
		maze.findSpawns();
		
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					printMessage("Creating server on port " + port, MessageType.INFO);
					serverSocket = new ServerSocket(port);
					
					PlayerHandler player;
					Socket clientSocket;
					while(true) {
						if(players.size() >= MAX_PLAYERS) continue;
						clientSocket = serverSocket.accept();
						player = new PlayerHandler(clientSocket, maze);
						player.sendData(readModelData(new File("res/sphere.obj")), "[MD]:", "[/MD]:");
						Thread t = new Thread(player);
						t.start();
						messagePlayers("[M]: " + player.getName() + " has joined the game");
						players.add(player);
					}
				} catch (IOException e) {
					printMessage("Failed to create server, port occupied", MessageType.ERROR);
					e.printStackTrace();
					System.exit(1);
				}
			}
			
		});
		Thread gameThread = new Thread(new Runnable(){
			public void run(){
				while(true) {
					while(players.size() == 0) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					printMessage("Starting game loop", MessageType.INFO);
					while(true) { //game loop
	//					System.out.println("Loop");
						for(int i=0; i<players.size(); i++) {
							if(!players.get(i).connected) {
								printMessage("Lost connection from player " + players.get(i).getName(), MessageType.ERROR);
								players.remove(i);
								i--;
								continue;
							}
							for(String s : players.get(i).getMessages()) {
								printMessage(players.get(i).getName() + ": " + s, MessageType.INFO);
							}
//							ArrayList<String> data = new ArrayList<String>();
							String data = "";
							for(PlayerHandler p : players) {
								//data.clear();
								//data.add(p.getName());
//								printMessage("Player pos is" + p.pos, MessageType.INFO);
								//if(p.player.pos != null) data.add(p.player.pos.x + "," + p.player.pos.y + "," + p.player.pos.z + ",");
								//p.sendData(data, "[PI]", "[/PI]");
								data += p.player.getEncoding();
							}
							players.get(i).sendMessage(data);
						}
					}
				}
			}
		});
		gameThread.start();
		serverThread.start();
	}
	
	public void messagePlayers(String message) {
		printMessage(message, MessageType.INFO);
		for(PlayerHandler p : players) {
			p.sendMessage(message);
		}
	}
	
	public static void printMessage(String message, MessageType type) {
		switch(type) {
		case INFO:
			System.out.println("[INFO]: " + message);
			break;
		case ERROR:
			System.err.println("[ERROR]: " + message);
		}
	}
	
	public static void printInfo(String message) {
		printMessage(message, MessageType.INFO);
	}
	
	public ArrayList<String> readModelData(File modelFile) {
		Scanner scanner;
		try {
			scanner = new Scanner(modelFile);
			ArrayList<String> lines = new ArrayList<String>();
			while(scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Usage: java -jar MazeServer.jar <port>");
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		new MazeServer(port);
	}
	
	private enum MessageType {
		INFO, ERROR;
	}
}
