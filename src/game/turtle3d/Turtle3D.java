package game.turtle3d;

import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import lib.game.AbstractGame;
import lib.game.Camera;
import lib.game.RenderUtils;
import math.geom.Point3f;
import math.geom.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Turtle3D extends AbstractGame {

	static final int KEY_OPEN_FILE = Keyboard.KEY_O;
	static final int KEY_GO = Keyboard.KEY_RETURN;
	static final int KEY_FORWARD = Keyboard.KEY_W;
	static final int KEY_BACKWARD = Keyboard.KEY_S;
	
	float fov = 70;
	
	HashMap<String, Float> vars = new HashMap<String, Float>();
	Point3f pos = new Point3f();
	float yaw, pitch;
//	Vector3f vect = new Vector3f();
	boolean pen = false; //false = up, true = down;
	Color3f color = new Color3f(1.0f, 1.0f, 1.0f);
	ArrayList<String> commands = new ArrayList<String>();
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	
	Camera cam = new Camera();
	boolean movingForward, movingBackward;
	float moveSpeed = 0.1f, rotSpeed = 0.1f;
	
	public Turtle3D() throws LWJGLException {
		super("Turtle 3D", 600, 600, false);
		start();
	}
	public Turtle3D(String fileName) throws LWJGLException {
		this();
		readFile(fileName);
	}

	@Override
	public void init() { //TODO init
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		def("pi", (float) Math.PI);
		
		readFile("res/test1.ts");
	}

	@Override
	public void update() { //TODO update
		
	}

	@Override
	public void render() { //TODO render
		RenderUtils.clearScreen();
		
		glBegin(GL_LINES); {
			for(int i=0; i<vertices.size(); i++) {
				if(i>0 && vertices.get(i-1)!=null && vertices.get(i)!=null) {
					vertices.get(i-1).vertex();
					vertices.get(i).vertex();
				}
			}
		} glEnd();
		
		glBegin(GL_QUADS); {
			for(int i=0; i<vertices.size(); i++) {
				if(vertices.get(i)!=null) {
					vertices.get(i).vertex();
				}
			}
		} glEnd();
	}

	@Override
	public void processInput() { //TODO processInput
		cam.rotX(-Mouse.getEventDY()*rotSpeed);
		cam.rotY(Mouse.getEventDX()*rotSpeed);
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				switch(Keyboard.getEventKey()) {
				case KEY_OPEN_FILE:
					String fileName = JOptionPane.showInputDialog(null, "Input File Name");
					readFile(fileName);
					break;
				case KEY_GO:
					for(String s : commands) {
						execCmd(s);
					}
					/*for(int i=0; i<vertices.size(); i++) {
						if(vertices.get(i)!=null)
							echo(vertices.get(i).toString());
					}*/
					break;
				case KEY_FORWARD:
					movingForward = true;
					break;
				}
			}
		}
	}
	
	/*
	 * loop:
	 * FOR [var] [init] [final] [delta] => for(int var=init; var<final; var++)
	 *  ...
	 * END
	 * 
	 * vars:
	 * DEF [name] [value]
	 * refer to by $[name]
	 */
	
	private boolean containsSpecialChars(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, " */+-=/<>()[]{}\\.,#%^?");
		return tokenizer.hasMoreElements();
	}
	private int nextSpecialChar(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, " */+-=/<>()[]{}\\.,#%^?");
		return 0;
	}
	
	public void parseCommands(ArrayList<String> cmds) {
		for(int i=0; i<cmds.size(); i++) {
			String s = cmds.get(i);
			if(s.startsWith("#")) continue;
			if(s.contains("$")) {
				String varPart = s.split("$")[0];
				echo("varPart = " + varPart);
				String var = containsSpecialChars(varPart)? varPart.split(" ")[0]:varPart;
				echo(var);
				s.replace("$" + var, vars.get(var).toString());
			}
			echo(s);
			String[] parts = s.split(" ");
			switch(parts[0]) {
			case "DEF":
				def(parts[1], new Float(parts[2]));
				break;
			case "FOR":
				String n = parts[1];
				float in = Float.parseFloat(parts[2]);
				float fin = Float.parseFloat(parts[3]);
				float d = Float.parseFloat(parts[4]);
				int end = cmds.size();
				for(int j=i; j<cmds.size(); j++) {
					if(cmds.get(j).equalsIgnoreCase("END")) {
						end = j;
						break;
					}
				}
				ArrayList<String> loopCmds = (ArrayList<String>) cmds.subList(i+1, end);
				loopCmds.add(" ");
				for(float j=in; j<fin; j+=d) {
					loopCmds.remove(0);
					loopCmds.add("DEF: " + n + " " + j);
					parseCommands(loopCmds);
				}
				break;
			default:
				commands.add(s);
				break;
			}
		}
	}
	
	public void readFile(String fileName) {
		ArrayList<String> cmds = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while(scanner.hasNextLine()) {
				cmds.add(scanner.nextLine());
			}
			scanner.close();
			parseCommands(cmds);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void execCmd(String cmd) {
		String[] parts = cmd.split(" ");
		switch(parts[0]) {
		case "MOVE":
			move(Float.parseFloat(parts[1]));
			break;
		case "ROT":
			rot(parts[1], Float.parseFloat(parts[2]));
			break;
		case "PEN":
			pen(parts[1].equalsIgnoreCase("DOWN"));
			break;
		case "COLOR":
			color(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
			break;
		case "ECHO":
			echo(cmd.replace("ECHO: ", ""));
			break;
		default:
			echo("Command not found");
			break;
		}
	}
	
	public void def(String name, float value) {
		vars.put(name, value);
	}
	public void move(float r) {
		float xzplane = (float) Math.cos(pitch);
		pos.x += r*(xzplane*Math.cos(yaw));
		pos.y += r*Math.sin(pitch);
		pos.z += r*(xzplane*Math.sin(-yaw));
		//If pen is down, add new point as a vertex. 
		if(pen) vertices.add(new Vertex(pos, color));
		//Else just move pos
		echo(pos.toString());
	}
	public void rot(String type, float amount) {
		amount = (float) Math.toRadians(amount);
		if(type.equalsIgnoreCase("yaw")) {
			yaw += amount;
			System.out.println(Math.toDegrees(yaw));
//			echo("yaw = " + yaw);
		} else if (type.equalsIgnoreCase("pitch")) {
			pitch += amount;
//			echo("pitch = " + pitch);
		}
	}
	public void pen(boolean down) {
		if(pen && !down) vertices.add(null);
		else if(!pen && down) vertices.add(new Vertex(pos, color));
		this.pen = down;
	}
	public void color(float r, float g, float b) {
		this.color = new Color3f(r, g, b);
	}
	public void echo(String message) {
		System.out.println(message);
	}

	public static void main(String[] args) {
		if(args.length == 1) {
			try {
				new Turtle3D(args[0]);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			new Turtle3D();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}
