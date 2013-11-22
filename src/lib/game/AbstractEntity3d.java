package lib.game;

import math.geom.Point3d;
import math.geom.Vector3d;

public abstract class AbstractEntity3d {
	protected double x, y, z;
	private Vector3d moveVect;
	private double speed;
	
	public AbstractEntity3d(double x, double y, double z, double speed) {
		this.x=x; this.y=y; this.z=z; this.speed=speed;
		moveVect = new Vector3d();
	}
	
	public AbstractEntity3d(double speed) {
		this.speed = speed;
	}

	public abstract void draw();
	public abstract void update();
	
	public void aim(Point3d p) {
		moveVect = new Vector3d(p.x-x, p.y-y, p.z-z);
	}
	
	public Vector3d getMoveVect() {
		return moveVect;
	}
	public void setMoveVect(Vector3d v) {
		moveVect=v;
	}
	
	public double getx() {return x;}
	public double gety() {return y;}
	public double getz() {return z;}
	public void setx(double x) {this.x=x;}
	public void sety(double y) {this.y=y;}
	public void setz(double z) {this.z=z;}
	public void setPos(Point3d p) {this.x=p.x; this.y=p.y; this.z=p.z;}
	public Point3d getPos() {return new Point3d(x, y, z);}
	public double getSpeed() {return speed;}
	public void setSpeed(double s) {speed=s;}
	
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
