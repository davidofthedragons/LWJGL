package game.tiles;

import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lib.game.AbstractGame;
import lib.game.RenderUtils;
import lib.game.gui.Button;
import lib.game.gui.Menu;
import lib.text.TextRenderer;
import math.geom.Point2f;
import math.geom.Point2i;
import math.geom.Rectangle;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;


public class TilesMain extends AbstractGame {

	ArrayList<Tile> tiles = new ArrayList<Tile>();
	GameState state = GameState.MENU;
	float speed = 2.0f;
	int tilesx = 4;
	int tilesy = 6;
	int tilew, tileh;
	Random rand = new Random(718);
	int score = 0;
	
	Menu mainMenu;
	Menu pauseMenu;
	Menu gameOverMenu;
	TextRenderer tr;
	
	public TilesMain() throws LWJGLException {
		super("Tiles", 600, 600);
		start();
	}
	
	private class Tile {
		Rectangle bounds;
		Color color;
		
		public Tile(Rectangle bounds, Color color) {
			this.bounds = bounds;
			this.color = color;
		}
		
		public void render() {
//			System.out.println("rendering tile");
			RenderUtils.applyColor((color == Color.WHITE)? Color3f.white : Color3f.black);
			glBegin(GL_QUADS); {
				glVertex2i(bounds.getp1().x, bounds.getp1().y);
				glVertex2i(bounds.getp2().x, bounds.getp1().y);
				glVertex2i(bounds.getp2().x, bounds.getp2().y);
				glVertex2i(bounds.getp1().x, bounds.getp2().y);
			} glEnd();
			RenderUtils.applyColor(Color3f.black);
			glBegin(GL_LINES); {
				glVertex2i(bounds.getp1().x, bounds.getp1().y);
				glVertex2i(bounds.getp2().x, bounds.getp1().y);
			} glEnd();
		}
		
	}
	
	private enum Color {
		WHITE, BLACK;
	}
	private enum GameState {
		MENU, CONTINUE, PAUSE, GAME_OVER;
	}
	
	public void spawnRow(int y) {
		int black = rand.nextInt(tilesx);
		int tilew = width/tilesx;
		int tileh = height/tilesy;
		for(int i=0; i<tilesx; i++) {
			tiles.add(new Tile(new Rectangle(new Point2i(i*tilew, y), tilew, tileh), (i==black)?Color.BLACK:Color.WHITE));
		}
	}

	@Override
	public void init() {
		RenderUtils.initStd2d(width, height);
		tilew = width/tilesx;
		tileh = height/tilesy;
		for(int i=0; i<=tilesy; i++) {
			spawnRow(height+tileh*i);
		}
		int fontSize = 25;
		int x = 150, y = 300;
		mainMenu = new Menu(width, height);
		mainMenu.add(new Button("Start Game", new Point2i(x, y)) {
			@Override
			public void onClick(Point2i pos, int button) {
				state = GameState.CONTINUE;
			}
		}.setFontSize(fontSize));
		pauseMenu = new Menu(width, height);
		pauseMenu.add(new Button("Resume Game", new Point2i(x, y)) {
			@Override
			public void onClick(Point2i pos, int button) {
				state = GameState.CONTINUE;
			}
		}.setFontSize(fontSize));
		pauseMenu.add(new Button("Exit Game", new Point2i(x, y+100)) {
			@Override
			public void onClick(Point2i pos, int button) {
				stop();
			}
		}.setFontSize(fontSize));
		gameOverMenu = new Menu(width, height);
		gameOverMenu.add(new Button("Play Again", new Point2i(x, y)) {
			@Override
			public void onClick(Point2i pos, int button) {
				tiles.clear();
				init();
				state = GameState.CONTINUE;
			}
		}.setFontSize(fontSize));
		gameOverMenu.add(new Button("Exit Game", new Point2i(x, y+100)) {
			@Override
			public void onClick(Point2i pos, int button) {
				stop();
			}
		}.setFontSize(fontSize));
		try {
			tr = new TextRenderer(new File("res/font1.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		switch(state) {
		case CONTINUE:
			boolean newRowFlag = false;
			for(int i=0; i<tiles.size(); i++) {
				Tile t = tiles.get(i);
				t.bounds = new Rectangle(new Point2i(t.bounds.getp1().x, (int) (t.bounds.getp1().y-speed)),
						new Point2i(t.bounds.getp2().x, (int) (t.bounds.getp2().y-speed)));
				if(t.bounds.getp2().y <= 0) {
					if(t.color == Color.BLACK) {
						state = GameState.GAME_OVER;
						break;
					}
					newRowFlag = true;
					tiles.remove(t);
					i--;
				}
			}
			if(newRowFlag) {
				spawnRow(600);
			}
			speed += .001;
			break;
		case GAME_OVER:
			break;
		case MENU:
			break;
		case PAUSE:
			break;
		}
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		switch(state) {
		case CONTINUE:
			glDisable(GL_TEXTURE_2D);
			for(Tile t : tiles) {
				t.render();
			}
			RenderUtils.applyColor(Color3f.black);
//			int start = tiles.get(0).bounds.getp1().y;
			for(int i=0; i<tilesx+1; i++) {
				glBegin(GL_LINES); {
					glVertex2i(i*tilew, 0);
					glVertex2i(i*tilew, height);
				} glEnd();
			}
			/*for(int i=0; i<tilesy; i++) {
				glBegin(GL_LINES); {
					glVertex2i(0, start+i*tileh);
					glVertex2i(width, start+i*tileh);
				} glEnd();
			}*/
			glEnable(GL_TEXTURE_2D);
			RenderUtils.applyColor(Color3f.green);
			tr.renderText("Score:" + score, new Point2f(), 20.0f, 30.f);
			glDisable(GL_TEXTURE_2D);
			break;
		case GAME_OVER:
			gameOverMenu.render();
			break;
		case MENU:
			mainMenu.render();
			break;
		case PAUSE:
			pauseMenu.render();
			break;
		}
	}

	@Override
	public void processInput() {
		switch(state) {
		case CONTINUE:
			while(Mouse.next()) {
				Point2i pos = new Point2i(Mouse.getEventX(), Mouse.getEventY());
				pos.y = height-pos.y;
				if(Mouse.getEventButtonState()) {
					System.out.println(pos);
					for(Tile t : tiles) {
						if(t.bounds.contains(pos)) {
							switch(t.color) {
							case WHITE:
								state = GameState.GAME_OVER;
								System.out.println("Game Over");
								break;
							case BLACK:
								t.color = Color.WHITE;
								score++;
								System.out.println(score);
								break;
							
							}
						}
					}
				}
			}
			break;
		case GAME_OVER:
			gameOverMenu.processInput();
			break;
		case MENU:
			mainMenu.processInput();
			break;
		case PAUSE:
			pauseMenu.processInput();
			break;
		}
	}

	public static void main(String args[]) {
		try {
			new TilesMain();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
