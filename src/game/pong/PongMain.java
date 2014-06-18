package game.pong;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import lib.game.AbstractEntity;
import lib.game.AbstractGame;
import lib.text.TextRenderer;
import math.geom.Point2d;
import math.geom.Point2f;
import math.geom.Vector2d;

public class PongMain extends AbstractGame {
	
	boolean twoPlayer = true;
	GameState state = GameState.MENU;
	int bottomScore = 0;
	int topScore = 0; 
	int scoreCap = 1;
	
	Paddle userPaddle = new Paddle(0, 550, 5.0);
	Paddle paddle2 = new Paddle(0, 50, 5.0);
	//Ball ball = new Ball(0, 250, 2.0);
	ArrayList<Ball> balls = new ArrayList<Ball>();
	TextRenderer textRenderer;
	
	public PongMain() throws LWJGLException {
		super("Pong", 600, 600);
		start();
	}
	
	public PongMain(boolean twoPlayer) throws LWJGLException {
		super("Pong", 600, 600);
		this.twoPlayer = twoPlayer;
		start();
	}
	
	public static void main(String[] args) {
		try {
			if(args.length>0) {
				new PongMain(Boolean.parseBoolean(args[0]));
			}
			else new PongMain();
		} catch (LWJGLException e) {
			System.out.println("Something went screwy");
		}
	}

	@Override
	public void init() {
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
		glLoadIdentity();
		balls.add(new Ball(0, 250, 2.6));
		try {
			textRenderer = new TextRenderer(new File("res/font1.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		textRenderer = new TextRenderer();
	}

	@Override
	public void update() { //TODO update
//		System.out.println("loop");
		switch(state) {
		case MENU: break;
		case CONTINUE:
			userPaddle.update();
			if(!twoPlayer) {
				int closestIndex = 0;
				for(int i=0; i<balls.size(); i++) {
					if(balls.get(i).getx() < balls.get(closestIndex).getx()) closestIndex = i;
				}
				paddle2.aim(balls.get(closestIndex).getPos());
				paddle2.getMoveVect().setj(0.0);
			}
			paddle2.update();
			for(int i=0; i<balls.size(); i++) balls.get(i).update();
			if(topScore==scoreCap) {
				state = GameState.END;
				System.out.println("Top wins");
			}
			if(bottomScore==scoreCap) {
				state = GameState.END;
				System.out.println("Bottom wins");
			}
			break;
		case PAUSE:
			
			break;
		case END:
			break;
		}
	}

	@Override
	public void render() { //TODO render
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		
		glEnable(GL_TEXTURE_2D);
		textRenderer.renderText("Player 2: " + topScore, new Point2f(0.0f, 0.0f), 22.0f, 32.0f);
		textRenderer.renderText("Player 1: " + bottomScore, new Point2f(0.0f, height-32.0f), 22.0f, 32.0f);
		glDisable(GL_TEXTURE_2D);
		userPaddle.draw();
		paddle2.draw();
		for(int i=0; i<width; i+=60) {
			glBegin(GL_QUADS);
				glVertex2d(i+15, 300);
				glVertex2d(i+15+30, 300);
				glVertex2d(i+15+30, 300+5);
				glVertex2d(i+15, 300+5);
			glEnd();
		}
		
		
		switch(state) {
		case MENU: 
			glEnable(GL_TEXTURE_2D);
			textRenderer.renderText("PONG", new Point2f(width/4.0f, height/4.0f), 75.0f, 60.0f);
			textRenderer.renderText("1: Start singleplayer game", new Point2f(width/5, 3*height/4.5f), 15.0f, 22.0f);
			textRenderer.renderText("2: Start two player game", new Point2f(width/5, 3*height/4.5f+32.0f), 15.0f, 22.0f);
			glDisable(GL_TEXTURE_2D);
			break;
		case CONTINUE:
			for(int i=0; i<balls.size(); i++) balls.get(i).draw();
			break;
		case PAUSE: 
			glEnable(GL_TEXTURE_2D);
			textRenderer.renderText("Paused", new Point2f(width/7.5f, height/4.0f), 75.0f, 60.0f);
			textRenderer.renderText("Space: Resume Game", new Point2f(width/3.75f, 3*height/4.5f), 15.0f, 22.0f);
			textRenderer.renderText("Escape: Exit Game", new Point2f(width/3.75f, 3*height/4.5f+32.0f), 15.0f, 22.0f);
			glDisable(GL_TEXTURE_2D);
			break;
		case END:
			textRenderer.renderText("Player " + ((bottomScore == scoreCap)?"1":"2") + " won", new Point2f(width/7.5f, height/4.0f), 37.0f, 60.0f);
			textRenderer.renderText("Escape: Exit Game", new Point2f(width/3.75f, 3*height/4.5f+32.0f), 15.0f, 22.0f);
			glDisable(GL_TEXTURE_2D);
			break;
		}
	}

	@Override
	public void processInput() { //TODO processInput
		
		while (Keyboard.next()) {
			switch (state) {
			case MENU:
				//			if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) state = GameState.CONTINUE;
				if (Keyboard.getEventKey()==Keyboard.KEY_1 && Keyboard.getEventKeyState()) {
					twoPlayer = false;
					state = GameState.CONTINUE;
				} else if (Keyboard.getEventKey()==Keyboard.KEY_2 && Keyboard.getEventKeyState()) {
					twoPlayer = true;
					state = GameState.CONTINUE;
				}
				break;
			case CONTINUE:
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
					userPaddle.getMoveVect().seti(1.0);
				else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
					userPaddle.getMoveVect().seti(-1.0);
				else
					userPaddle.getMoveVect().seti(0.0);
				if (twoPlayer) {
					if (Keyboard.isKeyDown(Keyboard.KEY_A))
						paddle2.getMoveVect().seti(-1.0);
					else if (Keyboard.isKeyDown(Keyboard.KEY_D))
						paddle2.getMoveVect().seti(1.0);
					else
						paddle2.getMoveVect().seti(0.0);
				}
				if (Keyboard.getEventKey()==Keyboard.KEY_SPACE && Keyboard.getEventKeyState()) {
					state = GameState.PAUSE;
					System.out.println("Score: Top-" + topScore + " Bottom-"
							+ bottomScore);
				}
				break;
			case PAUSE:
				if (Keyboard.getEventKey()==Keyboard.KEY_SPACE && Keyboard.getEventKeyState())
					state = GameState.CONTINUE;
				else if (Keyboard.getEventKey()==Keyboard.KEY_ESCAPE && Keyboard.getEventKeyState())
					stop();
				break;
			case END:
				if (Keyboard.getEventKey()==Keyboard.KEY_ESCAPE && Keyboard.getEventKeyState())
					stop();
				break;
			}
		}
	}
	
	private class Ball extends AbstractEntity {
		
		boolean bouncedLast = false;
		
		public Ball(double x, double y, double speed) {
			super(x, y, speed);
			setWidth(20);
			setHeight(20);
			randomAngle();
		}

		@Override
		public void draw() {
			glBegin(GL_QUADS);
				glVertex2d(x, y);
				glVertex2d(x+w, y);
				glVertex2d(x+w, y+h);
				glVertex2d(x, y+h);
			glEnd();
		}

		@Override
		public void update() {
			x += getdx();
			y += getdy();
			if(x<0 || x+w>width) bouncex();
			if(y<0) {
				setPos(new Point2d(0, 250));
				randomAngle();
				getMoveVect().setTheta(getMoveVect().getTheta()-(Math.PI/2));
				bottomScore++;
				System.out.println("Score: Top-" + topScore + " Bottom-" + bottomScore);
			}
			if(y>height) {
				setPos(new Point2d(0, 250));
				randomAngle();
				topScore++;
				System.out.println("Score: Top-" + topScore + " Bottom-" + bottomScore);
			}
			if(getBounds().intersects(userPaddle.getBounds()) || getBounds().intersects(paddle2.getBounds())) {
				if(!bouncedLast) {
					bouncey();
					setSpeed(getSpeed()+0.1);
					System.out.println("Speed: " + getSpeed());
					bouncedLast = true;
				}
				else bouncedLast = false;
				if(getSpeed()>5.0) {
					balls.add(new Ball(0, 250, 2.6));
					setSpeed(2.0);
				}
			} else bouncedLast = false;
		}
		
		public void bouncex() {
			getMoveVect().seti(-getMoveVect().geti());
		}
		public void bouncey() {
			getMoveVect().setj(-getMoveVect().getj());
		}
		
		public void randomAngle() {
			Random rand = new Random();
			double angle = Math.PI/(rand.nextDouble()*4+2.5);
			//System.out.println(angle);
			setMoveVect(Vector2d.createVector(angle, 1.0));
		}
	}
	
	private class Paddle extends AbstractEntity {
		
		public Paddle(double x, double y, double speed) {
			super(x, y, speed);
			setWidth(75);
			setHeight(15);
		}

		@Override
		public void draw() {
			glBegin(GL_QUADS);
				glVertex2d(x, y);
				glVertex2d(x+w, y);
				glVertex2d(x+w, y+h);
				glVertex2d(x, y+h);
			glEnd();
		}

		@Override
		public void update() {
			x += getdx();
			if(x<0) x=0;
			if(x>width-w) x=width-w;
			//y+=getdy();
		}
	}
	
	private enum GameState {
		CONTINUE, PAUSE, MENU, END;
	}
}
