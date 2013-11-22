package lib.game.path;

import math.geom.Point3f;

public class PiecewisePath implements ParametricPath {

	PathPiece[] paths;
	
	public PiecewisePath(PathPiece[] paths) {
		this.paths = paths;
	}
	
	@Override
	public Point3f par(float t) {
		Point3f p = new Point3f();
		
		for(PathPiece path : paths) {
			if(t>=path.lBound && t<=path.uBound) {
				p = path.par(t);
				break;
			}
		}
		return p;
	}
	
}
