package game.maze;

public enum MazeType {
	WALL, SPACE, NO_ROOF, GOAL, ENEMY, PLAYER, ITEM_HEALTHBONUS, ITEM_AMMOBONUS;
}

/*
 *  List<Point3f> vertices = new ArrayList<Point3f>();
        vertices.add(new Point3f(0.0f, HALLHEIGHT, 0.0f));
        vertices.add(new Point3f(HALLWIDTH, HALLHEIGHT, 0.0f));
        vertices.add(new Point3f(HALLWIDTH, 0.0f, 0.0f));
        vertices.add(new Point3f(HALLWIDTH, 0.0f, 0.0f));
        vertices.add(new Point3f(0.0f, 0.0f, 0.0f));
        vertices.add(new Point3f(0.0f, HALLHEIGHT, 0.0f));
        List<Point2f> texCoords = new ArrayList<Point2f>();
        texCoords.add(new Point2f(0.0f, 1.0f));
        texCoords.add(new Point2f(1.0f, 1.0f));
        texCoords.add(new Point2f(1.0f, 0.0f));
        texCoords.add(new Point2f(1.0f, 0.0f));
        texCoords.add(new Point2f(0.0f, 0.0f));
        texCoords.add(new Point2f(0.0f, 1.0f));
        List<Point3f> normals = new ArrayList<Point3f>();
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        normals.add(new Point3f(0.0f, 0.0f, -1.0f));
        List<Face> faces = new ArrayList<Face>();
        faces.add(new Face(new Point3i(0, 1, 2), new Point3i(0, 1, 2), new Point3i(0, 1, 2)));
        faces.add(new Face(new Point3i(3, 4, 5), new Point3i(3, 4, 5), new Point3i(3, 4, 5)));
        wallModel = new Model(vertices, normals, faces, texCoords, wall);
//        wallDisplayList = wallModel.createDisplayList();
 */
