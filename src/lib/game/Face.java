package lib.game;

import math.geom.Point3i;

public class Face {
	public Point3i vertices;
	public Point3i normals;
	public Point3i texCoords;
	
	public Face(Point3i vertices, Point3i normals) {
		this.vertices = vertices;
		this.normals = normals;
	}
	public Face(Point3i vertices, Point3i normals, Point3i texCoords) {
		this(vertices, normals);
		this.texCoords = texCoords;
	}
}
