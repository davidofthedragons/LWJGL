package game.maze;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import lib.game.AbstractEntity3d;
import lib.game.Model;
import lib.game.RenderUtils;
import math.geom.Point2f;
import math.geom.Point2i;
import math.geom.Point3f;
import math.geom.Vector3f;
import math.geom.Vector2f;

public class BasicEnemy extends AbstractEntity3d {

	private int health;
	private float size = 2.0f;
	private boolean shooting = false;
	
	private float theta;
	private float dtheta = 0.1f;
	private float ptheta;
	
	private int frame;
	private int shootInterval = 60;
	
	Model model;
	int displayList;
	
	public BasicEnemy(double x, double y, double z, double speed, int health) {
		super(x, y, z, speed);
		this.health = health;
		try {
			model = Model.loadModel(new File("res/sphere.obj"), new File("res/orbit/earth.png"));
			model.createDisplayList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw() {
		glPushMatrix();
		glColor3f(1.0f, 0.0f, 0.0f);
		glTranslated(x+size/2, y, z+size/2);
		glRotated(Math.toDegrees(theta), 0.0, 1.0, 0.0);
		RenderUtils.drawLineCube(size);
		model.bindTexture();
		glCallList(displayList);
		glPopMatrix();
	}

	@Override
	public void update() {
		if(ptheta > theta) theta += dtheta;
		if(ptheta < theta) theta -= dtheta;
		System.out.println(theta);
		x += getdx();
		//y += getdy();
		z += getdz();
		frame++;
		shooting = false;
		if(frame==shootInterval) {
			shooting = true;
			frame = 0;
		}
	}
	
	public void aimTo(Point3f p) {
		ptheta = new Vector2f(new Point2f(p.x, p.z), new Point2f((float)x, (float)z)).getTheta();
	}
	
	@Override
	public double getdx() {
		return Math.sin(theta)*getSpeed();
	}
	@Override 
	public double getdz() {
		return Math.cos(theta)*getSpeed();
	}
	
	public boolean isAlive() {
		return (health>0);
	}
	
	public boolean hasLineOfSight(Point3f playerPos, Maze m) {
		Point3f pos = new Point3f((float)x, (float)y, (float)z);
		Vector3f v = new Vector3f(pos, playerPos).getUnitVector();
		while(Math.abs(playerPos.x-pos.x)>1 || Math.abs(playerPos.y-pos.y)>1 || Math.abs(playerPos.z-pos.z)-1>1) {
			pos.x += v.geti(); pos.y += v.getj(); pos.z += v.getk();
			Point2i p = MazeMain.findPoint(pos);
			if(m.get(p.x, p.y) == MazeType.WALL) return false;
		}
		return true;
	}
	
	public boolean hits(Bullet b, Maze m) {
		Point3f pos = new Point3f(b.origin.x, b.origin.y, b.origin.z);
		while(Math.abs(x-pos.x)>1 || Math.abs(y-pos.y)>1 || Math.abs(z-pos.z)-1>1) {
			pos.x += b.v.geti(); pos.y += b.v.getj(); pos.z += b.v.getk();
			Point2i p = MazeMain.findPoint(pos);
			Point2i epos = MazeMain.findPoint(new Point3f((float)x, (float)y, (float)z));
			if(p.x == epos.x && p.y == epos.y) {
				health--;
				System.out.println("OW!");
				return true;
			}//*/
			if(pos.x>x && pos.x<x+size && pos.y>z && pos.y<z+size) {
				health--;
				System.out.println("OW!");
				return true;
			}
			if(m.get(p.x, p.y) == MazeType.WALL) {
				//System.out.println("Wall.");
				return false;
			}
			if(p.x<0 || p.x>m.MAZEX || p.y<0 || p.y>m.MAZEZ) return false;
		}
		return false;
	}

	public boolean isShooting() {
		return shooting;
	}

}
