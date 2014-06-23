package game.bird;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Random;

import lib.game.AbstractEntity3d;
import lib.game.AbstractGame;
import lib.game.RenderUtils;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

public class BirdMain extends AbstractGame {

	static final int KEY_FLAP = Keyboard.KEY_SPACE;
	static final int KEY_EXIT = Keyboard.KEY_ESCAPE;
	
	float fov = 70;
	
	Random rand = new Random();
	
	float horizon = -100.0f;
	float groundHeight = -50.0f;
	float ceilingHeight = 200.0f;
	
	ArrayList<Pipe> pipes = new ArrayList<Pipe>();
	
	float flapVelocity = 0.6f;
	float playerHeight = 10.0f;
	float velocity = 0.0f;
	float gravity = 0.025f;
	
	int texPipeTop;
	int texPipeMiddle;
	
	public BirdMain() throws LWJGLException {
		super("Birdy Game", 600, 600, false);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glClearColor(0.529f, 0.808f, 0.922f, 0.0f);
		texPipeMiddle = RenderUtils.loadTexture("res/flappy-pipemiddle.jpg", true);
		texPipeTop = RenderUtils.loadTexture("res/flappy-pipetop.jpg", true);
		
//		Mouse.setGrabbed(true);
		pipes.add(new Pipe(0.0f, rand.nextFloat()*75.0f - 25.0f, horizon+80.0f));
		pipes.add(new Pipe(0.0f, rand.nextFloat()*75.0f - 25.0f, horizon+60.0f));
		pipes.add(new Pipe(0.0f, rand.nextFloat()*75.0f - 25.0f, horizon+40.0f));
        pipes.add(new Pipe(0.0f, rand.nextFloat()*75.0f - 25.0f, horizon+20.0f));
        pipes.add(new Pipe(0.0f, rand.nextFloat()*75.0f - 25.0f, horizon));
	}

	@Override
	public void update() {
		velocity -= gravity;
		playerHeight += velocity;
//		if(velocity<=0) velocity = 0;
		if(playerHeight <= groundHeight+3) playerHeight = groundHeight+3;
		if(playerHeight >= ceilingHeight) playerHeight = ceilingHeight;
		for(Pipe p : pipes) {
			p.update();
		}
		if(pipes.get(0).getz() >= -3.0f) {
			if(Math.abs(playerHeight-pipes.get(0).gety()) >= pipes.get(0).gapHeight/2) {
				System.out.println("OW");
			}
			pipes.remove(0);
			pipes.add(new Pipe(0.0f, rand.nextFloat()*50.0f - 25.0f, horizon));
		}
	}
	
	@Override
	public void render() {
		RenderUtils.clearScreen();
		glPushMatrix();
		glTranslatef(0.0f, -playerHeight, 0.0f);
//		glRotatef(1.0f*velocity, 1.0f, 0.0f, 0.0f);
		glColor3f(0.0f, 1.0f, 0.0f);
		RenderUtils.drawLineCube(5);
		glColor3f(0.0f, 0.5f, 0.0f);
		glBegin(GL_QUADS); {
			glVertex3f( horizon*2.0f, groundHeight, 0.0f);
			glVertex3f(-horizon*2.0f, groundHeight, 0.0f);
			glVertex3f(-horizon*2.0f, groundHeight, horizon*2);
			glVertex3f( horizon*2.0f, groundHeight, horizon*2);
		} glEnd();
		
		for(Pipe p : pipes) {
			p.draw();
		}
//		glTranslatef(0.0f, 0.0f, -10.0f);
//		RenderUtils.drawLineCube(5);
		glPopMatrix();
		
	}
	
	@Override
	public void processInput() {
		while(Keyboard.next()) {
			switch(Keyboard.getEventKey()) {
			case KEY_FLAP:
				if(Keyboard.getEventKeyState()) {
					System.out.println("Flap");
					velocity = flapVelocity;
				}
				break;
			case KEY_EXIT:
				stop();
				break;
			}
		}
	}
	
	private class Pipe extends AbstractEntity3d {

		float size = 5;
		float gapHeight = 30;
		
		public Pipe(float x, float y, float z) {
			super(x, y, z, 0.1f);
		}

		@Override
		public void draw() {
			glPushMatrix();
			glTranslatef(x, 0, z);
			glColor3f(1.0f, 1.0f, 1.0f);
//			RenderUtils.drawLineCube(5);
			glEnable(GL_TEXTURE_2D);
//			RenderUtils.bindTexture(texPipeMiddle);
			glBegin(GL_QUADS); {
				glTexCoord2f(0.0f, 0.0f); glVertex3f(x-size/2, ceilingHeight, z-size/2);
				glTexCoord2f(0.0f, 1.0f); glVertex3f(x-size/2, y+gapHeight/2+3.0f, z-size/2);
				glTexCoord2f(1.0f, 1.0f); glVertex3f(x+size/2, y+gapHeight/2+3.0f, z-size/2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(x+size/2, ceilingHeight, z-size/2);
			} glEnd();
			RenderUtils.bindTexture(texPipeTop);
			glBegin(GL_QUADS); {
//				glColor3f(0.0f, 0.0f, 1.0f);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(x-size/2, y+gapHeight/2, z-size/2);
				glTexCoord2f(0.0f, 1.0f); glVertex3f(x-size/2, y+gapHeight/2+3, z-size/2);
				glTexCoord2f(1.0f, 1.0f); glVertex3f(x+size/2, y+gapHeight/2+3, z-size/2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(x+size/2, y+gapHeight/2, z-size/2);
			} glEnd();
				RenderUtils.bindTexture(texPipeMiddle);
			glBegin(GL_QUADS); {
				glColor3f(0.0f, 1.0f, 0.0f);
				glVertex3f(x+size/2, y+gapHeight/2, z-size/2);
				glVertex3f(x-size/2, y+gapHeight/2, z-size/2);
				glVertex3f(x-size/2, y+gapHeight/2, z-size);
				glVertex3f(x+size/2, y+gapHeight/2, z-size); 
//				glColor3f(1.0f, 0.0f, 0.0f);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(x-size/2, groundHeight, z-size/2);
				glTexCoord2f(0.0f, 1.0f); glVertex3f(x-size/2, y-gapHeight/2, z-size/2);
				glTexCoord2f(1.0f, 1.0f); glVertex3f(x+size/2, y-gapHeight/2, z-size/2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(x+size/2, groundHeight, z-size/2);
				glVertex3f(x+size/2, y-gapHeight/2, z-size/2);
				glVertex3f(x-size/2, y-gapHeight/2, z-size/2);
				glVertex3f(x-size/2, y-gapHeight/2, z-size);
				glVertex3f(x+size/2, y-gapHeight/2, z-size);  
			} glEnd();
			glDisable(GL_TEXTURE_2D);
			glPopMatrix();
		}

		@Override
		public void update() {
			z += this.getSpeed();
		}
		
	}

	public static void main(String args[]) {
		try {
			new BirdMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
}
