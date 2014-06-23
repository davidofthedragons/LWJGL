package game.maze;

import static org.lwjgl.opengl.GL11.*;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lib.game.AbstractEntity3d;
import lib.game.Model;
import math.geom.Point3f;
import math.geom.Vector3d;

public class Player extends AbstractEntity3d {
	
	String name;
	Model model;
	int modelDisplayList;
	ArrayList<String> modelData = null;
	
	public int startHealth;
	public int health;
	public int ammo;
	public int maxAmmo = 100;
	public Point3f pos;
	
	public float size = 2.0f;
	
	public Player(ArrayList<String> modelData, Point3f pos) {
		super(1.0f);
		this.pos = pos;
		this.modelData = modelData;
	}
	public Player(ArrayList<String> modelData) {
		super(1.0f);
		this.modelData = modelData;
	}
	public Player(Model model, Point3f pos) {
		super(1.0f);
		this.model = model;
		modelDisplayList = model.createDisplayList(0);
		this.pos = pos;
	}
	public Player(Point3f pos, int health, int ammo) {
		super(1.0f);
		this.pos = pos;
		this.health = startHealth = health;
		this.ammo = ammo;
	}
	public Player() {
		super(1.0f);
		this.pos = new Point3f();
		this.health = 0;
		this.health = startHealth;
		this.ammo = 0;
		this.moveVect = new Vector3d();
	}

	public void createModel() {
		if(modelData != null)
			model = Model.loadModel(modelData);
	}

	@Override
	public void draw() {
		glPushMatrix();
		glEnable(GL_TEXTURE_2D);
		glTranslatef(pos.x, pos.y, pos.z);
		if(model.hasTexture());
		model.bindTexture();
		glCallList(modelDisplayList);
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();
	}


	@Override
	public void update() {
		
	}

	public String getEncoding() {
		String s = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element elem = doc.createElement("player");
			elem.appendChild(doc.createElement("name")).setTextContent(name);
			elem.appendChild(doc.createElement("health")).setTextContent(Integer.toString(health));
			elem.appendChild(doc.createElement("ammo")).setTextContent(Integer.toString(ammo));
			Element posElem = doc.createElement("pos");
			posElem.appendChild(doc.createElement("x")).setTextContent(Float.toString(pos.x));
			posElem.appendChild(doc.createElement("y")).setTextContent(Float.toString(pos.y));
			posElem.appendChild(doc.createElement("z")).setTextContent(Float.toString(pos.z));
			elem.appendChild(posElem);
			doc.appendChild(elem);
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
//			System.out.println(writer.toString());
			s = doc.getTextContent();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static Player constructPlayer(Node playerNode) {
		NodeList nodes = playerNode.getChildNodes();
		String name;
		int health, ammo;
		Point3f pos;
		for(int i=0; i<nodes.getLength(); i++) {
			switch(nodes.item(i).getNodeName()) {
			case "name": name = nodes.item(i).getTextContent(); break;
			case "health": health = Integer.parseInt(nodes.item(i).getTextContent()); break;
			case "ammo": ammo = Integer.parseInt(nodes.item(i).getTextContent()); break;
			case "pos": 
				
			}
		}
		
		
		return null;
	}
	
}
