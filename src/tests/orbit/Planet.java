package tests.orbit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import lib.game.AbstractEntity3d;
import lib.game.Model;
import lib.game.path.ParametricPath;
import lib.game.path.OvalPath;
import math.geom.Point3f;

public class Planet extends AbstractEntity3d {
	
	float t = 0.0f;
	float dt = 0.5f;
	
	float angle = 0.0f;
	float da = 1.0f;
	
	Model model;
	int displayList;
	
	ParametricPath path;

	public Planet() {
		this("earth");
	}
	public Planet(String name) {
		super(1.0f);
		try {
			model = Model.loadModel(new File("res/orbit/" + name + ".obj"), new File("res/orbit/" + name + ".png"));
			displayList = model.createDisplayList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.path = new OvalPath("xz", 0.0f, 0.0f, new Point3f((float)x, (float)y, (float)z));
	}
	public Planet(String name, ParametricPath path) {
		this(name);
		this.path = path;
	}


	@Override
	public void draw() {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(27.5f, 1.0f, 0.0f, 1.0f);
		model.bindTexture();
		GL11.glCallList(displayList);
		GL11.glPopMatrix();
	}

	@Override
	public void update() {
		Point3f p = path.par(t);
		x = p.x;
		y = p.y;
		z = p.z;
		t += dt;
		angle += da;
	}

}
