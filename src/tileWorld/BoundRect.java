package tileWorld;

import java.io.Serializable;

import math.geom.Point3f;

public class BoundRect implements Serializable {

	//Rectangular prism - ta,tb,tc,td is top rectangle, ba,bb,bc,bd is bottom.
	
	Point3f ta, tb, tc, td, ba, bb, bc, bd;
	
	/*
	 * ta----tb
	 * |      |
	 * |      |
	 * ba----bb
	 * 
	 * td----tc
	 * |      | Top
	 * |      |
	 * ta----tb
	 */
	
	public BoundRect() {}
	
	public BoundRect(Point3f ta, Point3f bc) {
		this.ta = ta;
		this.tb = new Point3f(bc.x, ta.y, ta.z);
		this.tc = new Point3f(bc.x, ta.y, bc.z);
		this.td = new Point3f(ta.x, ta.y, bc.z);
		this.ba = new Point3f(ta.x, bc.y, ta.z);
		this.bb = new Point3f(bc.x, bc.y, ta.z);
		this.bc = bc;
		this.bd = new Point3f(ta.x, bc.y, bc.z);
	}
	
	public BoundRect(Point3f pos, float xlength, float ylength, float zlength) {
		this.ta = new Point3f(pos.x, pos.y+ylength, pos.z);
		this.tb = new Point3f(pos.x+xlength, pos.y+ylength, pos.z);
		this.tc = new Point3f(pos.x+xlength, pos.y+ylength, pos.z+zlength);
		this.td = new Point3f(pos.x, pos.y+ylength, pos.z+zlength);
		this.ba = pos;
		this.bb = new Point3f(pos.x+xlength, pos.y, pos.z);
		this.bc = new Point3f(pos.x+xlength, pos.y, pos.z+zlength);
		this.bd = new Point3f(pos.x, pos.y, pos.z+zlength);
//		System.out.println(pos);
	}
	
	public boolean contains(Point3f p) {
		if(p.x>=ta.x && p.x<=tb.x && p.y>=ba.y && p.y<=ta.y && p.z>=ta.z && p.z<=td.z) return true;
		return false;
	}
	
	public boolean collides(BoundRect r) {
		if(contains(r.ba) || contains(r.bb) || contains(r.bc)|| contains(r.bd)
				|| contains(r.ta) || contains(r.tb) || contains(r.tc)|| contains(r.td))
			return true;
		return false;
	}
	
	public Point3f getta() {
		return ta;
	}
	
	public Point3f getbc() {
		return bc;
	}
	
	public Point3f getCenter() {
		return new Point3f((ta.x+tb.x)/2, (ta.y+ba.y)/2, (ta.z+td.z)/2);
	}

}
