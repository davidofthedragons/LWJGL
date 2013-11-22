package tests.game;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;

import lib.game.AbstractGame;

public class Test3D extends AbstractGame {
	
	float angle = 0.0f;
	
	float fov = 50;

	public Test3D() throws LWJGLException {
		super("Testing 3D", 600, 600);
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
	}

	@Override
	public void update() {
		angle++;
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glPushMatrix();
		glTranslatef(0.0f, 0.0f, -5.0f);
		glRotatef(angle, 1.0f, -1.0f, -1.0f);
		drawCube(.5f);
		glPopMatrix();
		
		glBegin(GL_LINES);
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex3f(-.5f, -.5f, 1.0f);
			glVertex3f(0.0f, 0.0f, 499.0f);
			
			glVertex3f(.5f, .5f, 1.0f);
			glVertex3f(0.0f, 0.0f, 499.0f);
			
			glVertex3f(-.5f, .5f, 1.0f);
			glVertex3f(0.0f, 0.0f, 499.0f);
			
			glVertex3f(.5f, -.5f, 1.0f);
			glVertex3f(0.0f, 0.0f, 499.0f);
		glEnd();
	}
	
	private void drawCube(float s) {
		glBegin(GL_QUADS);
			//Front
			glColor3f(1.0f, 0.0f, 0.0f);
			glVertex3f(-.5f*s, -.5f*s, .5f*s);
			glVertex3f(.5f*s, -.5f*s, .5f*s);
			glVertex3f(.5f*s, .5f*s, .5f*s);
			glVertex3f(-.5f*s, .5f*s, .5f*s);
			//Right
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex3f(.5f*s, -.5f*s, .5f*s);
			glVertex3f(.5f*s, .5f*s, .5f*s);
			glVertex3f(.5f*s, .5f*s, -.5f*s);
			glVertex3f(.5f*s, -.5f*s, -.5f*s);
			//Back
			glColor3f(0.0f, 0.0f, 1.0f);
			glVertex3f(-.5f*s, .5f*s, -.5f*s);
			glVertex3f(.5f*s, .5f*s, -.5f*s);
			glVertex3f(.5f*s, -.5f*s, -.5f*s);
			glVertex3f(-.5f*s, -.5f*s, -.5f*s);
			//Left
			glColor3f(1.0f, 1.0f, 0.0f);
			glVertex3f(-.5f*s, -.5f*s, .5f*s);
			glVertex3f(-.5f*s, .5f*s, .5f*s);
			glVertex3f(-.5f*s, .5f*s, -.5f*s);
			glVertex3f(-.5f*s, -.5f*s, -.5f*s);
			//Top
			glColor3f(1.0f, 0.0f, 1.0f);
			glVertex3f(-.5f*s, .5f*s, .5f*s);
			glVertex3f(.5f*s, .5f*s, .5f*s);
			glVertex3f(.5f*s, .5f*s, -.5f*s);
			glVertex3f(-.5f*s, .5f*s, -.5f*s);
			//Bottom
			glColor3f(0.0f, 1.0f, 1.0f);
			glVertex3f(-.5f*s, -.5f*s, .5f*s);
			glVertex3f(.5f*s, -.5f*s, .5f*s);
			glVertex3f(.5f*s, -.5f*s, -.5f*s);
			glVertex3f(-.5f*s, -.5f*s, -.5f*s);
		glEnd();
		
	}

	@Override
	public void processInput() {
		
	}
	
	public static void main(String args[]) {
		try {
			new Test3D();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}
