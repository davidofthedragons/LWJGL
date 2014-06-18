package tests;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;

public class HelloLWJGL {

	public HelloLWJGL() {
		try {
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.setTitle("Hello LWJGL!");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0d, 640d, 480d, 0d, 1d, -1d);
		glMatrixMode(GL_MODELVIEW);
		
		int angle = 0;
		
		while(!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glPushMatrix();
			glRotatef(angle, 0, 0, 1);
			angle = (angle+1)%360;
			
			glBegin(GL_QUADS);
			glVertex3i(100, 100, 0);
			glVertex3i(200, 100, 0);
			glVertex3i(200, 200, 0);
			glVertex3i(100, 200, 0);
			/*glVertex3i(100, 100, 0);
			glVertex3i(100, 100, -100);
			glVertex3i(100, 200, -100);
			glVertex3i(100, 200, 0);*/
			glEnd();
			glPopMatrix();
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
	}
	
	public static void main(String[] args) {
		new HelloLWJGL();
	}
}
