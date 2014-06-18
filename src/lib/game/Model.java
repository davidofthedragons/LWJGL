package lib.game;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import math.geom.Point2f;
import math.geom.Point3f;
import math.geom.Point3i;

import org.newdawn.slick.opengl.Texture;

/**
 * Representation of a 3D model in OpenGL
 * @author David Gardner
 *
 */
public class Model {
	public List<Point3f> vertices = new ArrayList<Point3f>();
	public List<Point3f> normals = new ArrayList<Point3f>();
	public List<Point2f> texCoords = new ArrayList<Point2f>();
	public List<Face> faces = new ArrayList<Face>();
	
	boolean textured = false;
	Texture tex;
	int texID;
	
	/**
	 * Creates a new model
	 * @param vertices list of vertices
	 * @param normals list of normals
	 * @param faces list of faces
	 */
	public Model(List<Point3f> vertices, List<Point3f> normals, List<Face> faces) {
		this.vertices = vertices;
		this.normals = normals;
		this.faces = faces;
	}
	public Model(List<Point3f> vertices, List<Point3f> normals, List<Face> faces, List<Point2f> texCoords, Texture tex) {
		this(vertices, normals, faces);
		this.texCoords = texCoords;
		this.tex = tex;
	}
	/**
	 * Creates an empty model
	 */
	public Model() {};
	
	/**
	 * Reads the given .obj file and creates a new model from the data
	 * @param f File object of the .obj file
	 * @return a Model object created from the file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Model loadModel(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Model m = new Model();
		
		String line;
		while((line = reader.readLine()) != null) {
			if(line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Point3f(x, y, z));
				//System.out.println("Added vertex " + (m.vertices.size()-1) + " (" + x + " ," + y + ", " + z + ")");
			}
			else if(line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Point3f(x, y, z));
			}
			else if(line.startsWith("vt ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				m.texCoords.add(new Point2f(x, y));
			}
			else if(line.startsWith("f ")) {
				int vx = Integer.valueOf(line.split(" ")[1].split("/")[0]);
				int vy = Integer.valueOf(line.split(" ")[2].split("/")[0]);
				int vz = Integer.valueOf(line.split(" ")[3].split("/")[0]);
				Point3i v = new Point3i(vx, vy, vz);
				
				int nx = Integer.valueOf(line.split(" ")[1].split("/")[2]);
				int ny = Integer.valueOf(line.split(" ")[2].split("/")[2]);
				int nz = Integer.valueOf(line.split(" ")[3].split("/")[2]);
				Point3i n = new Point3i(nx, ny, nz);
				
				if (!line.split(" ")[1].split("/")[1].equals("")) {
					int tx = Integer.valueOf(line.split(" ")[1].split("/")[1]);
					int ty = Integer.valueOf(line.split(" ")[2].split("/")[1]);
					int tz = Integer.valueOf(line.split(" ")[3].split("/")[1]);
					Point3i t = new Point3i(tx, ty, tz);
					m.faces.add(new Face(v, n, t));
				} else m.faces.add(new Face(v, n));
			}
		}
		reader.close();
		return m;
	}
	public static Model loadModel(ArrayList<String> modelData) {
		Model m = new Model();
		
		String line;
		for(int i=0; i<modelData.size(); i++) {
			line = modelData.get(i);
			if(line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Point3f(x, y, z));
				//System.out.println("Added vertex " + (m.vertices.size()-1) + " (" + x + " ," + y + ", " + z + ")");
			}
			else if(line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Point3f(x, y, z));
			}
			else if(line.startsWith("vt ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				m.texCoords.add(new Point2f(x, y));
			}
			else if(line.startsWith("f ")) {
				int vx = Integer.valueOf(line.split(" ")[1].split("/")[0]);
				int vy = Integer.valueOf(line.split(" ")[2].split("/")[0]);
				int vz = Integer.valueOf(line.split(" ")[3].split("/")[0]);
				Point3i v = new Point3i(vx, vy, vz);
				
				int nx = Integer.valueOf(line.split(" ")[1].split("/")[2]);
				int ny = Integer.valueOf(line.split(" ")[2].split("/")[2]);
				int nz = Integer.valueOf(line.split(" ")[3].split("/")[2]);
				Point3i n = new Point3i(nx, ny, nz);
				
				if (!line.split(" ")[1].split("/")[1].equals("")) {
					int tx = Integer.valueOf(line.split(" ")[1].split("/")[1]);
					int ty = Integer.valueOf(line.split(" ")[2].split("/")[1]);
					int tz = Integer.valueOf(line.split(" ")[3].split("/")[1]);
					Point3i t = new Point3i(tx, ty, tz);
					m.faces.add(new Face(v, n, t));
				} else m.faces.add(new Face(v, n));
			}
		}
		return m;
	}
	/**
	 * Constructs a textured model from the given files
	 * @param modelFile the .obj file with vertex, normal, and face data
	 * @param textureFile the png format texture
	 * @return a Model object constructed from the files
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Model loadModel(File modelFile, File textureFile) throws FileNotFoundException, IOException {
		return loadModel(modelFile).loadTexture(textureFile.getAbsolutePath());
	}
	
	/**
	 * Loads the texture from the given png format file.
	 * @param fileName of the png file.
	 * @return the model with the texture added.
	 */
	public Model loadTexture(String fileName) {
		//this.tex = RenderUtils.loadTexture(fileName);
		//this.textured = (tex != null) && (!texCoords.isEmpty());
		this.texID = RenderUtils.loadTexture(fileName, true);
		this.textured = true;
		return this;
	}
	public Model setTexture(Texture tex) {
		this.tex = tex;
		this.textured = (tex != null) && (!texCoords.isEmpty());
		return this;
	}
	/**
	 * Binds the texture to the OpenGL context
	 */
	public void bindTexture() {
		//if(tex!=null) tex.bind();
		if(textured) RenderUtils.bindTexture(texID);
	}
	/**
	 * 
	 * @return true if the object has a texture (texture coords and a texture file)
	 */
	public boolean hasTexture() {
		return textured;
	}
	
	/**
	 * Creates an OpenGL display list for the model
	 * @return the id of the display list
	 */
	public int createDisplayList(int list) {
		int displayList = glGenLists(list);
		glNewList(displayList, GL_COMPILE); {
        	glBegin(GL_TRIANGLES); {
        		int i=0;
        		for(Face face : faces) {
        			try {
        				//glColor3f(1.0f, 0.0f, 0.7f);
						Point3f n1 = normals.get(face.normals.x-1);
						Point3f v1 = vertices.get(face.vertices.x-1);
						if(textured) {
							Point2f t1 = texCoords.get(face.texCoords.x-1);
							glTexCoord2f(t1.x, t1.y);
						}
						glNormal3f(n1.x, n1.y, n1.z);
						glVertex3f(v1.x, v1.y, v1.z);
						
						Point3f n2 = normals.get(face.normals.y-1);
						Point3f v2 = vertices.get(face.vertices.y-1);
						if(textured) {
							Point2f t2 = texCoords.get(face.texCoords.y-1);
							glTexCoord2f(t2.x, t2.y);
						}
						glNormal3f(n2.x, n2.y, n2.z);
						glVertex3f(v2.x, v2.y, v2.z);
						
						Point3f n3 = normals.get(face.normals.z-1);
						Point3f v3 = vertices.get(face.vertices.z-1);
						if(textured) {
							Point2f t3 = texCoords.get(face.texCoords.z-1);
							glTexCoord2f(t3.x, t3.y);
						}
						glNormal3f(n3.x, n3.y, n3.z);
						glVertex3f(v3.x, v3.y, v3.z);
					} catch (IndexOutOfBoundsException e) {
						System.err.println("faces[" + i + "]");
						e.printStackTrace();
					}
        			i++;
        		}
        	} glEnd();
        } glEndList();
		
		return displayList;
	}
	
	public int createDisplayList() {
		return createDisplayList(RenderUtils.nextDisplayListID());
	}
	
}
