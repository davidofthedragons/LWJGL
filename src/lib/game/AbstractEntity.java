package lib.game;

import java.awt.Rectangle;

import math.geom.*;

public abstract class AbstractEntity {

	
	protected double x, y;
	private Vector2d moveVect;
	private double speed;
	protected int w, h;
	
	public AbstractEntity(double x, double y, double speed) {
		this.x=x; this.y=y; this.speed=speed;
		moveVect = new Vector2d();
	}
	
	public AbstractEntity(double speed) {
		this.speed = speed;
	}

	public abstract void draw();
	public abstract void update();
	
	public void aim(Point2d p) {
		moveVect = new Vector2d(p.x-x, p.y-y);
	}
	
	public void setAngle(double theta) {
		moveVect.setTheta(theta);
	}
	public Vector2d getMoveVect() {
		return moveVect;
	}
	public void setMoveVect(Vector2d v) {
		moveVect=v;
	}
	
	public double getx() {return x;}
	public double gety() {return y;}
	public void setx(double x) {this.x=x;}
	public void sety(double y) {this.y=y;}
	public void setPos(Point2d p) {this.x=p.x; this.y=p.y;}
	public Point2d getPos() {return new Point2d(x, y);}
	public double getSpeed() {return speed;}
	public void setSpeed(double s) {speed=s;}
	
	public Rectangle getBounds() {return new Rectangle((int)x, (int)y, w, h);}
	public int getWidth() {return w;}
	public void setWidth(int w) {this.w=w;}
	public int getHeight() {return h;}
	public void setHeight(int h) {this.h=h;}
	
	public double getdx() {
		if(moveVect.getMagnitude()==0) return 0;
		return moveVect.getUnitVector().geti()*speed;
	}
	public double getdy() {
		if(moveVect.getMagnitude()==0) return 0;
		return moveVect.getUnitVector().getj()*speed;
	}
}
