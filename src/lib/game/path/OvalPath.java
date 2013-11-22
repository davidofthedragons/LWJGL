package lib.game.path;

import math.geom.Point3f;

public class OvalPath implements ParametricPath {

	String plane;
	float d1, d2;
	Point3f origin;
	
	public OvalPath(String plane, float d1, float d2, Point3f origin) {
		this.plane = plane;
		this.d1 = d1;
		this.d2 = d2;
		this.origin = origin;
	}
	
	@Override
	public Point3f par(float t) {
		float x=0, y=0, z=0;
		if(plane.equals("xy")) {
			x = d1 * (float) Math.sin(Math.toRadians(t)) - d1/2;
			y = d2 * (float) Math.cos(Math.toRadians(t)) - d2/2;
		}
		else if(plane.equals("yz")) {
			y = d1 * (float) Math.sin(Math.toRadians(t)) - d1/2;
			z = d2 * (float) Math.cos(Math.toRadians(t)) - d2/2;
		}
		else if(plane.equals("xz")) {
			x = d1 * (float) Math.sin(Math.toRadians(t)) - d1/2;
			z = d2 * (float) Math.cos(Math.toRadians(t)) - d2/2;
		}
		return new Point3f(x + origin.x, y + origin.y, z + origin.z);
	}

}
