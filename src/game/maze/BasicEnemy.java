package game.maze;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import lib.game.AbstractEntity3d;
import lib.game.Model;
import lib.game.RenderUtils;
import math.geom.Point2f;
import math.geom.Point2i;
import math.geom.Point3f;
import math.geom.Vector3d;
import math.geom.Vector3f;
import math.geom.Vector2f;

public class BasicEnemy extends AbstractEntity3d {

	private int health;
	public float size = 2.0f;
	private boolean shooting = false;
	boolean hit = false;
	
//	private float theta;
//	private float dtheta = 0.1f;
//	private float ptheta;
	private Vector3d idealVect = new Vector3d();
	private Point3f aimPoint = new Point3f();
	private float delta = 0.05f;
	
	private int frame;
	private int shootInterval = 60;
	
	Model model;
	int displayList;
	
	public BasicEnemy(float x, float y, float z, float speed, int health) {
		super(x, y, z, speed);
		this.health = health;
		try {
			model = Model.loadModel(new File("res/sphere.obj"), new File("res/enemy0.png"));
			displayList = model.createDisplayList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw() {
		glPushMatrix();
		glEnable(GL_TEXTURE_2D);
		glColor3f(1.0f, 1.0f, 1.0f);
		if(hit) {
			glColor3f(1.0f, 0.0f, 0.0f);
			hit = false;
		}
		glTranslated(x+size/2, y, z+size/2);
//		glRotated(Math.toDegrees(theta), 0.0, 1.0, 0.0);
		//RenderUtils.drawLineCube(size);
		model.bindTexture();
		glCallList(displayList);
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();
	}

	@Override
	public void update() {
		/*if(ptheta > theta) theta += dtheta;
		if(ptheta < theta) theta -= dtheta;
		theta = ptheta;
		System.out.println(theta);*/
		if(moveVect.geti()<idealVect.geti()) moveVect.seti(moveVect.geti() + delta);
		if(moveVect.getj()<idealVect.getj()) moveVect.setj(moveVect.getj() + delta);
		if(moveVect.getk()<idealVect.getk()) moveVect.setk(moveVect.getk() + delta);
		if(moveVect.geti()>idealVect.geti()) moveVect.seti(moveVect.geti() - delta);
		if(moveVect.getj()>idealVect.getj()) moveVect.setj(moveVect.getj() - delta);
		if(moveVect.getk()>idealVect.getk()) moveVect.setk(moveVect.getk() - delta);
		
		if (aimPoint.dist(new Point3f(x, y, z)) >= 2.0f) {
			x += getdx();
			//y += getdy();
			z += getdz();
		}
		frame++;
		shooting = false;
		if(frame==shootInterval) {
			shooting = true;
			frame = 0;
		}
	}
	
	public void aimTo(Point3f p) {
//		ptheta = new Vector2f(new Point2f((float)x, (float)z), new Point2f(p.x, p.z)).getTheta();
		aimPoint = p;
		idealVect = new Vector3d(p.x - x, p.y - y, p.z - z);
	}
	
	public void hurt(int damage) {
		health -= damage;
		hit = true;
	}
	
//	@Override
//	public double getdx() {
//		return Math.sin(theta)*getSpeed();
//	}
//	@Override 
//	public double getdz() {
//		return Math.cos(theta)*getSpeed();
//	}
	
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
	
	

	public boolean isShooting() {
		return shooting;
	}

}
