package game.cuberunner;

import graphics.Color3f;
import lib.game.AbstractEntity3d;
import math.geom.Point3d;
import math.geom.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Cube extends AbstractEntity3d {
	
	public static float SIZE = 3.0f;
	boolean solid = false;
	Color3f color = new Color3f(0.0f, 1.0f, 0.0f);

	public Cube(float x, float y, float z, float speed) {
		super(x, y, z, speed);
		this.aim(new Point3d(x, y, 0));
	}

	@Override
	public void draw() {
		glPushMatrix();
		glTranslatef((float)x, (float)y, (float)z);
		if(solid) {
			//TODO: draw solid
		}
		else {
			glColor3f(color.r, color.g, color.b);
			drawLineCube(SIZE);
		}
		glPopMatrix();
	}
	
	public static void drawLineCube(float SIZE) {
		glBegin(GL_LINES); {
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, SIZE, 0.0f);   //Front
			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			
			glVertex3f(0.0f, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, SIZE, SIZE);
			glVertex3f(SIZE, SIZE, SIZE);   //Back
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, 0.0f, SIZE);
			
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, SIZE);
			
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, SIZE);   //Sides
			
			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(SIZE, SIZE, SIZE);
			
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, SIZE);//*/
		} glEnd();
	}

	@Override
	public void update() {
		x += this.getdx();
		y += this.getdy();
		z += this.getdz();
	}
	
	public boolean checkCollision(float camerax) {
		return (camerax-CubeMain.CSIZE/2>=x && camerax-CubeMain.CSIZE/2<=x+CubeMain.CSIZE) || 
				(camerax+CubeMain.CSIZE/2>=x && camerax+CubeMain.CSIZE/2<=x+CubeMain.CSIZE);
		//return (camerax>=x && camerax<=x+SIZE);
	}

}
