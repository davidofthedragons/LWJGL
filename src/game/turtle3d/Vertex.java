package game.turtle3d;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glVertex3f;
import graphics.Color3f;
import math.geom.Point3f;

public class Vertex {

	public Point3f vertex;
	public Color3f color;
	public Vertex(Point3f p, Color3f c) {
		vertex = p;
		color = c;
//		System.out.println(this);
	}
	public void vertex() {
		glColor3f(color.r, color.g, color.b);
		glVertex3f(vertex.x, vertex.y, vertex.z);
	}
	
	public String toString() {
		return "Vertex: " + vertex.toString() + ", " + color.toString();
	}

}
