package tileWorld.entity;

import math.geom.Point3f;
import math.geom.Vector3f;

public abstract class Entity {

	public Vector3f velocity = new Vector3f();
	Point3f pos = new Point3f();
	
	public Entity(Point3f pos, Vector3f velocity) {
		this.pos = pos; this.velocity = velocity;
	}
	
	public Entity(Point3f pos) {
		this.pos = pos;
	}
	
	public Entity() {
		
	}
	
	public abstract void render();
	public abstract void update();
	
}
