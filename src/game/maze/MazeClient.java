package game.maze;

import static lib.game.RenderUtils.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lib.game.*;
import lib.game.gui.Button;
import lib.game.gui.Label;
import lib.game.gui.Menu;
import lib.game.gui.TextField;
import lib.text.TextRenderer;
import math.geom.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MazeClient extends AbstractGame {

	private static boolean fullscreen = false;
	private static boolean mouseGrabbed = true;
	
	private boolean running = false;
	
	private Camera camera = new Camera();
	private float camSpeed = 0.2f;
	private float rotSpeed = 0.1f;
	private boolean movingForward = false;
	private boolean movingBackward = false;
	private boolean movingRight = false;
	private boolean movingLeft = false;
	private boolean movingUp = false;
	private boolean movingDown = false;
	
	private final int KEY_FORWARD = Keyboard.KEY_W;
	private final int KEY_BACKWARD = Keyboard.KEY_S;
	private final int KEY_LEFT = Keyboard.KEY_A;
	private final int KEY_RIGHT = Keyboard.KEY_D;
	private final int KEY_UP = Keyboard.KEY_SPACE;
	private final int KEY_DOWN = Keyboard.KEY_LSHIFT;
	private final int KEY_CLOSE = Keyboard.KEY_ESCAPE;
	private final int KEY_RELEASE_MOUSE = Keyboard.KEY_SLASH;
	
	private final int BUTTON_SHOOT = 0;
	
	private boolean shooting = false;
	
	public final static float HALLWIDTH = 4.0f;
	public final static float HALLHEIGHT = 5.0f;
	
	private enum State {
		CONTINUE, PAUSE, MENU, DEAD;
	}
	State gameState = State.MENU;
	Menu mainMenu;
	Menu pauseMenu;
	Menu deadMenu;
	
	TextRenderer textRenderer;
//	Texture wall;
	int wallTexture;
	
	Player thisPlayer;
	ArrayList<Player> otherPlayers = new ArrayList<Player>();
	Player somePlayer;
	
	String ip;
	int port;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	ArrayList<String> incomingMessages = new ArrayList<String>();
	ArrayList<String> renderMessages = new ArrayList<String>();
	
	String userName;
	
	Maze maze;
	Point3f spawnPoint;
	
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db = null;
	
	public MazeClient(String serverIP, int port, String userName) throws LWJGLException { //TODO constructor
		super("Maze Multiplayer", 700, 400, fullscreen);
		this.userName = userName;
		this.ip = serverIP;
		this.port = port;
		connect(ip, port);
		gameState = State.CONTINUE;
		start();
	}
	public MazeClient() throws LWJGLException {
		super("Maze Multiplayer", 700, 400, fullscreen);
		gameState = State.MENU;
		start();
	}
	
	public void connect(String ip, int port) {
		try {
			printMessage("Connecting to server: " + ip + ":" + port, MessageType.INFO);
			socket = new Socket(ip, port);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			sendToServer(userName);
			printMessage("Waiting for map...", MessageType.INFO);
			String mapLine = in.readLine();
			printMessage("Map recieved", MessageType.INFO);
			parseMap(mapLine);
			printMessage("Waiting for spawn point...", MessageType.INFO);
			String spawnLine = in.readLine();
			printMessage("Spawn Point Recieved.", MessageType.INFO);
			Point2i p = new Point2i(Integer.parseInt(spawnLine.split(" ")[0]),
					Integer.parseInt(spawnLine.split(" ")[1]));
			spawnPoint = new Point3f(HALLWIDTH*p.x+HALLWIDTH/2, 2.0f, HALLWIDTH*p.y+HALLWIDTH/2);
			camera.setPos(new Vector3f(spawnPoint.x, spawnPoint.y, spawnPoint.z));
			printMessage("Waiting for model data", MessageType.INFO);
			if(in.readLine().equals("[MD]:")) {
				ArrayList<String> lines = new ArrayList<String>();
				String line;
				while(!(line = in.readLine()).equals("[/MD]:")) {
					lines.add(line);
				}
				thisPlayer = new Player(lines);
				thisPlayer.pos = spawnPoint;
				thisPlayer.createModel();
			} else printMessage("Did not recieve model data", MessageType.INFO);
			gameState = State.CONTINUE;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			somePlayer = new Player(Model.loadModel(new File("res/sphere.obj"), new File("res/orbit/earth.png")),
					new Point3f(5.0f, 4.0f, 5.0f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        camera.setBoundsx(-90.0f, 90.0f);
        Mouse.setGrabbed(mouseGrabbed);
        try {
			textRenderer = new TextRenderer(new File("res/font1.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Thread networkThread = new Thread(new Runnable() {
			public void run() {
				try {
					while(running) {
						String line = in.readLine();
						System.out.println(line);
						incomingMessages.add(line);
					}
				} catch(IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
			}
		});
		printMessage("Starting game", MessageType.INFO);
		networkThread.start();
	}
	
	public ArrayList<String> getMessages() {
		ArrayList<String> messages = new ArrayList<String>(incomingMessages);
		incomingMessages.clear();
		return messages;
	}
	public String nextMessage() {
		if(incomingMessages.size() == 0) return null;
		String s = incomingMessages.get(0);
		incomingMessages.remove(0);
		return s;
	}
	
	public void sendToServer(String message) {
		printMessage("Sending message to server:" + message, MessageType.INFO);
		out.println(message);
		out.flush();
	}
	
	public void parseMap(String mapLine) {
		int mapWidth = Integer.parseInt(mapLine.split(" ")[0]);
		int mapHeight = Integer.parseInt(mapLine.split(" ")[1]);
		String mapData = mapLine.split(" ")[2];
		MazeType[][] map = new MazeType[mapWidth][mapHeight];
		for(int i=0; i<mapData.length(); i++) {
			switch(mapData.charAt(i)) {
			case '0':
				map[i/mapWidth][i%mapWidth] = MazeType.SPACE;
				break;
			case '1':
				map[i/mapWidth][i%mapWidth] = MazeType.WALL;
				break;
//			default:
//				map[i/mapWidth][i%mapWidth] = MazeType.SPACE;
//				break;
			}
		}
		maze = new Maze(map, mapWidth, mapHeight);
	}
	
	public synchronized void modifyPlayers(ArrayList<String> data) {
		
	}
	public synchronized Player getPlayer(int index) {
		return otherPlayers.get(index);
	}

	@Override
	public void init() { //TODO: init
		running = true;
		glClearColor(0.4f, 0.5f, 1.0f, 0.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glShadeModel(GL_SMOOTH);
        glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        createGUI();
        wallTexture = loadTexture("res/wall.png", true);
        printMessage("Exiting init", MessageType.INFO);
        
	}
	
	private void createGUI() {
		mainMenu = new Menu(width, height);
		mainMenu.add(new Label("Maze Multiplayer", new Point2i(25, 15)).setFontSize(25));
		mainMenu.add(new Label("Username:", new Point2i(100, 200)));
		final TextField nameField = new TextField(15, new Point2i(250, 200));
		mainMenu.add(nameField);
		mainMenu.add(new Label("Server IP:", new Point2i(100, 250)));
		final TextField ipField = new TextField(15, new Point2i(250, 250));
		mainMenu.add(ipField);
		mainMenu.add(new Label("Port:", new Point2i(100, 300)));
		final TextField portField = new TextField(15, new Point2i(250, 300));
		mainMenu.add(portField);
		Button connectButton = new Button("Connect!", new Point2i(250, 350)) {
			@Override
			public void onClick(Point2i pos, int button) {
				userName = nameField.getText();
				connect(ipField.getText(), Integer.parseInt(portField.getText()));
			}
		};
		mainMenu.add(connectButton);
		Button exitButton = new Button("Exit", new Point2i(500, 350)) {
			@Override
			public void onClick(Point2i pos, int button) {
				kill();
			}
		};
		mainMenu.add(exitButton);
		
		printMessage("Exiting createGUI", MessageType.INFO);
	}
	
	private void init3d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(75, (float)Display.getWidth()/(float)Display.getHeight(), 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        enableLighting();
	}
	
	private void enableLighting() {
		glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glShadeModel(GL_SMOOTH);
        glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
	}
	private void init2d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDisable(GL_CULL_FACE);
		glDisable(GL_COLOR_MATERIAL);
	}

	@Override
	public void update() { //TODO: update
		printMessage("update", MessageType.INFO);
		switch(gameState) {
		case CONTINUE:
			break;
		case DEAD:
			return;
		case MENU:
			return;
		case PAUSE:
			return;
		}
		
		//printMessage("Shooting = " + shooting, MessageType.INFO);
		if(movingForward) {
			movePlayerForward();
		}
		if(movingBackward) {
			movePlayerBackward();
		}
		if(movingLeft) {
			movePlayerLeft();
		}
		if(movingRight) {
			movePlayerRight();
		}
		if(movingUp) {
			camera.moveUp(camSpeed);
		}
		if(movingDown) {
			camera.moveDown(camSpeed);
		}
		//thisPlayer.pos = getPlayerPos();
		/*String sendToServer = "[PI]:" + this.getPlayerPos().x + "," + this.getPlayerPos().y + "," + this.getPlayerPos().z 
				+ "," + camera.getLookVect().geti() + "," + camera.getLookVect().getj() + "," + camera.getLookVect().getk()
				+ "," + shooting;
		sendToServer(sendToServer);*/
		sendToServer(thisPlayer.getEncoding());
		String line;
		while((line = nextMessage()) != null) {
			try {
				db = dbf.newDocumentBuilder();
				InputSource source = new InputSource();
				source.setCharacterStream(new StringReader(line));
				Document doc = db.parse(source);
				otherPlayers.clear();
				for(int i=0; i<doc.getChildNodes().getLength(); i++) {
					if(doc.getChildNodes().item(i).getNodeName().equals("player")) {
							otherPlayers.add(Player.constructPlayer(doc.getChildNodes().item(i)));
					}
					else if(doc.getChildNodes().item(i).getNodeName().equals("entity"));
				}
				/*if(line.startsWith("[PI]")) {
					String name = nextMessage();
					line = nextMessage();
					System.out.println(line);
					if (!line.equals("[/PI]")) {
						Point3f pos = new Point3f(Float.parseFloat(line.split(",")[0]),
												  Float.parseFloat(line.split(",")[1]),
												  Float.parseFloat(line.split(",")[2])); //TODO fix communication lineups
						for (Player p : otherPlayers)
							if (p.name.equals(name))
								p.pos = pos;
					}
				}
				else if(line.startsWith("[M]:")) showMessage(line.split(":")[1]);*/
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
//				System.exit(1);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void showMessage(String message) {
		renderMessages.add(message);
		System.out.println("[M]: " + message);
	}

	public void movePlayerLeft() {
		camera.moveLeft(camSpeed);
		if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveRight(camSpeed);
		thisPlayer.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerRight() {
		camera.moveRight(camSpeed);
		if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveLeft(camSpeed);
		thisPlayer.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerForward() {
		camera.moveForward(camSpeed);
		if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveBackward(camSpeed);
		thisPlayer.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerBackward(){
		camera.moveBackward(camSpeed);
		if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveForward(camSpeed);
		thisPlayer.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	
	public Point2i findPlayer() {
		return findPoint(getPlayerPos());
	}
	
	public Point3f getPlayerPos() {
		return new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	
	public static Point2i findPoint(Point3f p) {
		Point2i p1 = new Point2i();
		p1.x = (int) Math.round((p.x-HALLWIDTH/2)/HALLWIDTH);
		p1.y = (int) Math.round((p.z-HALLWIDTH/2)/HALLWIDTH);
		return p1;
	}
	
	@Override
	public void render() { //TODO: Render
		clearScreen();
		switch(gameState) {
		case MENU:
			init2d();
			mainMenu.render();
			return;
		case PAUSE:
			
			return;
		case DEAD:
			
			return;
		case CONTINUE:
			break;
		default:
			break;
		}
		
		init3d();
		
		glPushMatrix();
		camera.applyTransform();
		
		glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(new float[]{
				camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk(), 1.0f}));
		glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0f);
		glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 10f);
		
		glColor3f(0.7f, 0.7f, 0.7f);
		glBegin(GL_QUADS); {
			//Floor
			glNormal3f(0.0f, -1.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, 0.0f, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			//Ceiling
			glNormal3f(0.0f, 1.0f, 0.0f);
			glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, Maze.HALLHEIGHT, 0.0f);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, Maze.HALLHEIGHT, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH*maze.MAZEZ);
		} glEnd();
		for(Bullet b : maze.bullets) {
			b.draw();
		}
		
		for(int i=0; i<maze.MAZEX; i++) {
			for(int j=0; j<maze.MAZEZ; j++) {
				
				glPushMatrix();
				glTranslatef(Maze.HALLWIDTH*i, 0.0f, Maze.HALLWIDTH*j);
				if(maze.get(i, j) == MazeType.WALL) {
					glEnable(GL_TEXTURE_2D);
					RenderUtils.bindTexture(wallTexture);
					glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
					
					glBegin(GL_QUADS); {
						glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						if(maze.get(i, j-1) != MazeType.WALL) { //FRONT
							glNormal3f(0.0f, 0.0f, -1.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							
						}
						
						if(maze.get(i, j+1) != MazeType.WALL) { //BACK
							glNormal3f(0.0f, 0.0f, 1.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							
						}
						
						if(maze.get(i-1, j) != MazeType.WALL) { //LEFT
							glNormal3f(-1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
						}
						if(maze.get(i+1, j) != MazeType.WALL) { //RIGHT
							glNormal3f(1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, 0.0f);
							
						}
					} glEnd();
					glDisable(GL_TEXTURE_2D);
				}
				else if(maze.get(i, j)==MazeType.GOAL) {
					glColor3f(0.0f, 1.0f, 0.0f);
					drawLineCube(2.0f);
				}
				else {
					if(maze.getItem(i, j) != null) maze.getItem(i, j).draw();
				}
				glPopMatrix();
			}
		}
		
		
		glPopMatrix();
		init2d(); //TODO render 2d
//		textRenderer.renderText("Health " + maze.playerHealth, new Point2f(0.0f, 0.0f), 20.0f, 15.0f);
		glColor3f(1.0f, 0.0f, 0.0f);
		glBegin(GL_LINES); { //crosshairs
			glVertex2i(width/2-5, height/2);
			glVertex2i(width/2+4, height/2);
			glVertex2i(width/2, height/2-5);
			glVertex2i(width/2, height/2+4);
		} glEnd();
		glBegin(GL_QUADS); { //Minimap
			int step = 25;
			Point2i pPos = findPlayer();
			int width = 7, height = 7;
			int startx = this.width - 50 - step*width - 10;
			int starty = 4*this.height/6;
			
			glColor3f(0.4f, 0.4f, 0.4f);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + height*step + 5);
			glVertex2f(startx + width*step + 5, starty + height*step + 5);
			glVertex2f(startx + width*step + 5, starty - 5);
			
			for(int i=0; i<width; i++) {
				for(int j=0; j<height; j++) {
					switch(maze.get(pPos.x + (i-width/2), pPos.y + (j-height/2))) {
					case SPACE:
						glColor3f(0.3f, 0.3f, 0.3f);
						break;
					case WALL:
						glColor3f(0.5f, 0.5f, 0.5f);
						break;
					case GOAL:
						glColor3f(0.0f, 0.0f, 1.0f);
						break;
					case ENEMY:
						glColor3f(1.0f, 0.0f, 0.0f);
						break;
					case ITEM_HEALTHBONUS:
						if(maze.getItem(pPos.x + (i-width/2), pPos.y + (j-height/2)) != null)
							glColor3f(1.0f, 0.0f, 1.0f);
						else glColor3f(0.3f, 0.3f, 0.3f);
						break;
					case ITEM_AMMOBONUS:
						if(maze.getItem(pPos.x + (i-width/2), pPos.y + (j-height/2)) != null)
							glColor3f(0.0f, 1.0f, 1.0f);
						else glColor3f(0.3f, 0.3f, 0.3f);
						break;
					default:
						glColor3f(0.3f, 0.3f, 0.3f);
						break;
					}
					if(pPos.x + (i-width/2) == pPos.x && pPos.y + (j-height/2) == pPos.y) glColor3f(0.0f, 1.0f, 0.0f);
					glVertex2i(startx + step*i, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j + step);
					glVertex2i(startx + step*i, starty + step*j + step);
				}
			}
			
		} glEnd();
		
		glBegin(GL_QUADS); { //Health bar
			glColor3f(0.4f, 0.4f, 0.4f);
			float startx = 50;
			float starty = 50;
			float healthBarTotalSize = 150;
			float healthBarHeight = 25;
			float healthFraction = (float)(thisPlayer.health) / (float)(thisPlayer.startHealth);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + healthBarHeight + 5);
			glVertex2f(startx + healthBarTotalSize + 5, starty + healthBarHeight + 5);
			glVertex2f(startx + healthBarTotalSize + 5, starty - 5);
			
			glColor3f(0.2f, 0.2f, 0.2f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize, starty);
			
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize*(healthFraction), starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize*(healthFraction), starty);
		} glEnd();
		
		glBegin(GL_QUADS); { //Ammo Bar
			float ammoBarWidth = 25;
			float ammoBarHeight = 150;
			float startx = width - 50 - ammoBarWidth - 10;
			float starty = 50;
			float ammoFraction = (float)(thisPlayer.ammo) / (float)(thisPlayer.maxAmmo);
			glColor3f(0.4f, 0.4f, 0.4f);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + ammoBarHeight + 5);
			glVertex2f(startx + ammoBarWidth + 5, starty + ammoBarHeight + 5);
			glVertex2f(startx + ammoBarWidth + 5, starty - 5);
			
			glColor3f(0.2f, 0.2f, 0.2f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty);
			
			glColor3f(1.0f, 0.0f, 0.0f);
			glVertex2f(startx, starty + ammoBarHeight - ammoBarHeight*ammoFraction);
			glVertex2f(startx, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight - ammoBarHeight*ammoFraction);
			
		} glEnd();
	}

	@Override
	public void processInput() { //TODO Process Input
		switch(gameState) {
		case MENU:
			mainMenu.processInput();
			return;
		case PAUSE:
			pauseMenu.processInput();
			return;
		case DEAD:
			deadMenu.processInput();
			return;
		case CONTINUE:
			break;
		}
		shooting = false;
		while(Mouse.next()) {
			if(Mouse.getEventButton() == BUTTON_SHOOT) {
				if(Mouse.getEventButtonState()) {
					shooting = true;
				}
			}
			camera.rotX(-Mouse.getEventDY()*rotSpeed);
			camera.rotY(Mouse.getEventDX()*rotSpeed);
		}
		
		while(Keyboard.next()) {
			switch (Keyboard.getEventKey()) {
			case KEY_CLOSE:
				stop();
				break;
			case KEY_FORWARD:
				if(Keyboard.getEventKeyState()) {
					movingForward = true;
					movingBackward = false;
				}
				else movingForward = false;
				break;
			case KEY_BACKWARD:
				if(Keyboard.getEventKeyState()) {
					movingBackward = true;
					movingForward = false;
				}
				else movingBackward = false;
				break;
			case KEY_LEFT:
				if(Keyboard.getEventKeyState()) {
					movingLeft = true;
					movingRight = false;
				}
				else movingLeft = false;
				break;
			case KEY_RIGHT:
				if(Keyboard.getEventKeyState()) {
					movingRight = true;
					movingLeft = false;
				}
				else movingRight = false;
				break;
			case KEY_UP:
				if(Keyboard.getEventKeyState()) {
					movingUp = true;
					movingDown = false;
				}
				else movingUp = false;
				break;
			case KEY_DOWN:
				if(Keyboard.getEventKeyState()) {
					movingDown = true;
					movingUp = false;
				}
				else movingDown = false;
				break;
			case KEY_RELEASE_MOUSE:
				if(Keyboard.getEventKeyState()) {
					Mouse.setGrabbed(!Mouse.isGrabbed());
				}
			}
		}
	}
	
	public void kill() {
		printMessage("Ending game", MessageType.INFO);
		running = false;
		try {
			sendToServer("Disconnecting");
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.kill();
	}
	
	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Usage: java -jar MazeClient.jar <server ip> <port> <username>");
			System.exit(1);
		}
		try {
			new MazeClient();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		/*String name = JOptionPane.showInputDialog("Enter your username");
		try {
			new MazeClient(args[0], Integer.parseInt(args[1]), name);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}*/
	}
	
	public void printMessage(String message, MessageType type) {
		switch(type) {
		case INFO:
			System.out.println("[INFO]: " + message);
			break;
		case ERROR:
			System.err.println("[ERROR]: " + message);
		}
	}
	private enum MessageType {
		INFO, ERROR;
	}

}
