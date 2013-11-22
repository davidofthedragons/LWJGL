package game.maze;

import org.lwjgl.opengl.GL11;

import math.geom.Point3f;
import math.geom.Vector3f;

public class Bullet {

	public Vector3f v;
	public Point3f origin;
	public int framesActive = 2;
	
	public boolean active = true;
	
	public Bullet() {}
	public Bullet(Vector3f v, Point3f origin) {
		this.v = v.getUnitVector();
		this.origin = origin;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void update() {
		framesActive--;
		if(framesActive <= 0) active = false;
	}
	public void draw() {
		GL11.glPushMatrix();
		GL11.glTranslatef(origin.x, origin.y, origin.z);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glVertex3f(0.0f, -0.1f, 0.0f);
		GL11.glVertex3f((100.0f*v.geti()), -0.1f, (100.0f*v.getk()));
		GL11.glEnd();
		GL11.glPopMatrix();
	}
}
