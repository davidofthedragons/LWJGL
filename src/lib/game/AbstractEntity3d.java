package lib.game;

import math.geom.Point3d;
import math.geom.Point3f;
import math.geom.Vector3d;

public abstract class AbstractEntity3d {
	protected float x, y, z;
	protected Vector3d moveVect;
	private float speed;
	
	/**
	 * 
	 * @param x initial x position
	 * @param y initial y position
	 * @param z initial z position
	 * @param speed
	 */
	public AbstractEntity3d(float x, float y, float z, float speed) {
		this.x=x; this.y=y; this.z=z; this.speed=speed;
		moveVect = new Vector3d();
	}
	
	/**
	 * 
	 * @param speed
	 */
	public AbstractEntity3d(float speed) {
		this.speed = speed;
	}
	
	/**
	 * OpenGL render code
	 */
	public abstract void draw();
	/**
	 * Entity update code
	 */
	public abstract void update();
	
	/**
	 * Changes the move vector so the entity is aimed at the given point
	 * @param p the point to point to
	 */
	public void aim(Point3d p) {
		moveVect = new Vector3d(p.x-x, p.y-y, p.z-z);
	}
	
	/**
	 * 
	 * @return the movement vector (heading) of the entity
	 */
	public Vector3d getMoveVect() {
		return moveVect;
	}
	/**
	 * 
	 * @param v the new movement vector
	 */
	public void setMoveVect(Vector3d v) {
		moveVect=v;
	}
	
	public double getx() {return x;}
	public double gety() {return y;}
	public double getz() {return z;}
	public void setx(float x) {this.x=x;}
	public void sety(float y) {this.y=y;}
	public void setz(float z) {this.z=z;}
	public void setPos(Point3f p) {this.x=p.x; this.y=p.y; this.z=p.z;}
	public Point3d getPos() {return new Point3d(x, y, z);}
	public double getSpeed() {return speed;}
	public void setSpeed(float s) {speed=s;}
	
	public double getdx() {
		if(moveVect.getMagnitude()==0) return 0;
		return moveVect.getUnitVector().geti()*speed;
	}
	public double getdy() {
		if(moveVect.getMagnitude()==0) return 0;
		return moveVect.getUnitVector().getj()*speed;
	}
	public double getdz() {
		if(moveVect.getMagnitude()==0) return 0;
		return moveVect.getUnitVector().getk()*speed;
	}
}
