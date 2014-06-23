package game.turtle3d;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import graphics.Color3f;
import lib.game.AbstractGame;
import lib.game.RenderUtils;
import lib.game.gui.*;
import math.geom.*;
import math.parser.MathParser;
import math.parser.MathParser.MathSyntaxException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

public class Turtle2D extends AbstractGame {

	Menu menu;
	
	HashMap<String, Float> vars = new HashMap<String, Float>();
	ArrayList<Vertex2D> vertices = new ArrayList<Vertex2D>();
	Point2f pos = new Point2f();
	Color3f color = Color3f.white;
	float angle = 0.0f;
	boolean pen = false; //true = down; false = up;
	
	private enum Angle {RADIANS, DEGREES}
	Angle angleMode = Angle.DEGREES;
	
	public Turtle2D() throws LWJGLException {
		super("Turtle2D", 1000, 650, false);
		start();
	}

	@Override
	public void init() {
		RenderUtils.initStd2d(width, height);
//		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		updateSysVars();
		
		menu = new Menu(width, height);
		final TextArea console = new TextArea(40, 5, new Point2i(50, 525));
		menu.add(console);
		Button drawButton = new Button("DRAW!", new Point2i(525, 525)) {
			@Override
			public void onClick(Point2i pos, int button) {
				ArrayList<String> cmds = new ArrayList<String>();
				int lastReturn = -1;
				for(int i=0; i<console.getText().length(); i++) {
					if(console.getText().charAt(i) == '\n') {
						cmds.add(console.getText().substring(lastReturn+1, i));
						lastReturn = i;
					}
				}
				cmds.add(console.getText().substring(lastReturn+1));
				parseCommands(cmds);
			}
		};
		menu.add(drawButton);
		Button clearButton = new Button("Clear Console", new Point2i(525, 575)) {
			@Override
			public void onClick(Point2i pos, int button) {
				console.clearText();
				menu.grantFocus(console);
			}
		};
		menu.add(clearButton);
		menu.grantFocus(console);
	}

	@Override
	public void update() {
		for(Vertex2D v : vertices) {
//			System.out.println(v);
		}
		updateSysVars();
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		for(int i=0; i<vertices.size()-1; i++) {
			if(vertices.get(i)==null || vertices.get(i+1)==null) continue;
			RenderUtils.applyColor(vertices.get(i).color);
			glBegin(GL_LINES); {
				vertices.get(i).draw();
				vertices.get(i+1).draw();
			} glEnd();
		}
		RenderUtils.applyColor(color);
		glBegin(GL_QUADS); {
			glVertex2f(pos.x-2, pos.y-2);
			glVertex2f(pos.x+2, pos.y-2);
			glVertex2f(pos.x+2, pos.y+2);
			glVertex2f(pos.x-2, pos.y+2);
		} glEnd();
		menu.render();
		
	}

	@Override
	public void processInput() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {stop(); return;}
		menu.processInput();
	}
	
	private float getAngle() { //Angle in radians
		switch(angleMode) {
		case RADIANS: return angle;
		case DEGREES: return (float) Math.toRadians(angle);
		}
		return 0.0f;
	}
	
	private void updateSysVars() {
		vars.put("x", pos.x);
		vars.put("y", pos.y);
		vars.put("angle", angle);
		vars.put("pen", (pen)?1.0f:0.0f);
		vars.put("r", color.r);
		vars.put("g", color.g);
		vars.put("b", color.b);
		vars.put("pi", 3.1415926535f);
	}
	
	private ArrayList<String> subList(ArrayList<String> list, int begin, int end) {
		ArrayList<String> a = new ArrayList<String>();
		for(int i=begin; i<end; i++) {
			a.add(list.get(i));
		}
		return a;
	}
	
	private String parseVars(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, "+-*/%().1234567890", true);
		String p = "";
		while(tokenizer.hasMoreTokens()) {
			String t = tokenizer.nextToken();
			if(t.startsWith("$")) p += Float.toString(vars.get(t.substring(1)));
			else p += t;
		}
		try {
			if(!p.startsWith("\"") && !p.startsWith("'")) p = MathParser.parse(p);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (MathSyntaxException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	private void parseCommands(ArrayList<String> cmds) { //control structure parsing here
		for(int i=0; i<cmds.size(); i++) {
			String[] cmd = cmds.get(i).split(" ");
			if(cmd[0].startsWith("#")) continue;
			else if(cmd[0].equalsIgnoreCase("for")) {
				float in = Float.parseFloat(cmd[2]);
				float fin = Float.parseFloat(cmd[3]);
				float d = Float.parseFloat(cmd[4]);
				int end = cmds.size();
				for(int j=i; j<cmds.size(); j++) {
					if(cmds.get(j).equalsIgnoreCase("end")) {
						end = j;
						break;
					}
				}
				ArrayList<String> loopCmds = (ArrayList<String>) subList(cmds, i+1, end);
				loopCmds.add(0, " ");
				
				if (fin>in) {
					for (float j = in; j < fin; j += d) {
						loopCmds.remove(0);
						loopCmds.add(0, "def " + cmd[1] + " " + j);
						parseCommands(loopCmds);
					}
				}
				else {
					for (float j = in; j > fin; j += d) {
						loopCmds.remove(0);
						loopCmds.add(0, "def " + cmd[1] + " " + j);
						parseCommands(loopCmds);
					}
				}
				i = end;
			}
			else execCmd(cmd);
		}
		
	}
	
	private void execCmd(String[] args) { //var parsing here
//		System.out.println(args[0]);
		for(int i=1; i<args.length; i++) {
			args[i] = parseVars(args[i]);
			/*if(args[i].startsWith("$")) {
				args[i] = Float.toString(vars.get(args[i].substring(1)));
			}*/
			
		}
		switch(args[0]) {
		case "move":
			move(Float.parseFloat(args[1]));
			break;
		case "rot":
			rot(Float.parseFloat(args[1]));
			break;
		case "pen":
			if(args[1].equalsIgnoreCase("down")) pen(true);
			else pen(false);
			break;
		case "def":
			def(args[1], Float.parseFloat(args[2]));
			break;
		case "run":
			run(args[1]);
			break;
		case "angleMode":
			angleMode(args[1]);
			break;
		case "reset":
			reset(args);
			break;
		case "color":
			float r = Float.parseFloat(args[1]);
			float g = Float.parseFloat(args[2]);
			float b = Float.parseFloat(args[3]);
			color((r>=0)?r:0, (g>=0)?g:0, (b>=0)?b:0);
			break;
		case "print":
			print(args[1]);
			break;
		case "save":
			saveImage(toBufferedImage(), args[1]);
		}
		updateSysVars();
	}
	
	//Actual function calls
	private void move(float n) {
		pos.x += n*Math.cos(getAngle());
		pos.y += n*Math.sin(getAngle());
		if(pen) vertices.add(new Vertex2D(new Point2f(pos.x, pos.y), color));
	}
	
	private void rot(float theta) {
		angle += theta;
	}
	
	private void pen(boolean pen) {
		this.pen = pen;
		if(pen) vertices.add(new Vertex2D(new Point2f(pos.x, pos.y), color));
		else vertices.add(null);
	}
	
	private void pen() {
		pen = !pen;
	}
	
	private void def(String name, float value) {
		vars.put(name, value);
	}
	
	private void run(String fileName) {
		readFile(fileName);
	}
	
	private void angleMode(String arg) {
		if(arg.equalsIgnoreCase("radians"))
			angleMode = Angle.RADIANS;
		else angleMode = Angle.DEGREES;
	}
	
	private void reset(String args[]) {
		
		for (int i=1; i<args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("pos"))
				pos = new Point2f();
			else if (arg.equalsIgnoreCase("angle"))
				angle = 0;
			else if (arg.equalsIgnoreCase("screen"))
				vertices.clear();
			else if (arg.equalsIgnoreCase("pen"))
				pen = false;
			else if (arg.equalsIgnoreCase("color"))
				color = Color3f.white;
			else if (arg.equalsIgnoreCase("all")) {
				pos = new Point2f();
				angle = 0;
				vertices.clear();
				pen = false;
				color = Color3f.white;
			}
		}
	}
	
	private void print(String arg) {
		System.out.println(arg);
		if(arg.equalsIgnoreCase("vertices")) {
			for(Vertex2D v : vertices) {
				System.out.println(v);
			}
		}
	}
	
	private void readFile(String fileName) {
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
	
	public void saveImage(BufferedImage img, String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists())
				file.createNewFile();
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
		}
		
	}
	
	private void color(float r, float g, float b) {
		color = new Color3f(r, g, b);
	}
	
	public BufferedImage toBufferedImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.black);
		g.drawRect(0, 0, width, height);
		for(int i=0; i<vertices.size()-1; i++) {
			g.setColor(vertices.get(i).color.toAWT());
			g.drawLine((int)vertices.get(i).vertex.x, (int)vertices.get(i).vertex.y,
					(int)vertices.get(i+1).vertex.x, (int)vertices.get(i+1).vertex.y);
		}
		return img;
	}
	

	private class Vertex2D {
		public Point2f vertex;
		public Color3f color;
		public Vertex2D(Point2f p, Color3f c) {
			vertex = p;
			color = c;
//			System.out.println(this);
		}
		public void draw() {
//			glColor3f(color.r, color.g, color.b);
			glVertex2f(vertex.x, vertex.y);
		}
		
		public String toString() {
			return "Vertex: " + vertex.toString() + ", " + color.toString();
		}

	}
	
	public static void main(String args[]) {
		try {
			new Turtle2D();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
}
