package tileWorld.entity;

import math.geom.Point3f;
import tileWorld.BoundRect;

public class Player extends Entity {

	public float speed = 0.06f;
	public float angle = 0.0f;
	public float yVelocity = 0.0f;
	public float eyeHeight = 1.7f;
	
	public BoundRect bounds;
	public float xlength = 0.7f, zlength = 0.7f, height = 1.9f;
	
	public Player(Point3f pos) {
		super(pos);
		bounds = new BoundRect(pos, xlength, height, zlength);
	}
	
	public void move(float dx, float dy, float dz) {
		pos.x += dx;
		pos.y += dy;
		pos.z += dz;
		bounds = new BoundRect(pos, xlength, height, zlength);
	}
	public void setPos(Point3f pos) {
		this.pos = pos;
		bounds = new BoundRect(pos, xlength, height, zlength);
	}

	@Override
	public void render() {
		
	}

	@Override
	public void update() {
		
	}

}
