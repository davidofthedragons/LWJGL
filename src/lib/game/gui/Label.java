package lib.game.gui;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2i;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import graphics.Color3f;
import lib.text.TextRenderer;
import math.geom.Point2i;
import math.geom.Rectangle;

public class Label extends TextComponent {

	
	public Label(Rectangle bounds, String text) {
		super(bounds);
		super.text = text;
		try {
			tr = new TextRenderer(new File("res/font.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Label(String text, Point2i pos) {
		super(text, pos);
	}
	
	public Label setBorderColor(Color3f c) {
		this.borderColor = c;
		return this;
	}
	public Label setBgColor(Color3f c) {
		this.bgColor = c;
		return this;
	}
	public Label setBorderWidth(int width) {
		this.borderWidth = width;
		return this;
	}
	public Label setText(String text) {
		this.text = text;
		return this;
	}
	public Label setBounds(Rectangle r) {
		this.bounds = r;
		return this;
	}
	public Label setTexture(int texture) {
		this.texture = texture;
		hasTexture = true;
		return this;
	}

	@Override
	public void onClick(Point2i pos, int button) {}
	@Override
	public void onMouseMove(Point2i pos) {}
	@Override
	public void onKeyDown(Key key) {}
	@Override
	public void onKeyUp(Key key) {}
	
}
