package tests.game;

import lib.game.AbstractEntity;
import math.geom.Vector2d;

import static org.lwjgl.opengl.GL11.*;

public class TestEntity extends AbstractEntity {

	public TestEntity(double x, double y) {
		super(x, y, 5.0);
		//super.setMoveVect(Vector2d.createVector(Math.PI/3, 1.0));
	}

	@Override
	public void draw() {
		glColor3f(0.0f, 1.0f, 0.0f);
		glRotatef((float)Math.toDegrees(getMoveVect().getTheta()), (float) (x+25), (float) (y+25),  -1.0f);
		glBegin(GL_QUADS);
			glVertex2d(x, y);
			glVertex2d(x+50, y);
			glVertex2d(x+50, y+50);
			glVertex2d(x, y+50);
		glEnd();
		//System.out.println("(" + x + ", " + y + ")");
		//System.out.println(getMoveVect().getTheta());
	}

	@Override
	public void update() {
		x += getdx();
		y += getdy();
	}

}
