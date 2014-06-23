package lib.game.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import graphics.Color3f;
import lib.game.RenderUtils;
import lib.text.TextRenderer;
import math.geom.Point2i;
import math.geom.Rectangle;

public abstract class TextComponent extends Component {

	
	
	protected static int borderWidth = 10;
	protected static int paddingy = 3;
	protected String text = "";
	protected int textLength;
	protected static int fontSize = 10;
	protected static double fontRatio = 1.5;
	
	protected TextRenderer tr;
	
	public TextComponent(Rectangle bounds) {
		super(bounds);
		try {
			tr = new TextRenderer(new File("res/font.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public TextComponent(String text, Point2i pos) {
		this(new Rectangle());
		this.text = text;
		this.textLength = text.length(); //chars
		int textLength = fontSize * text.length(); //pixels
		int textHeight = (int) ((double) fontSize * fontRatio);
		this.setBounds(new Rectangle(pos, textLength + 2*borderWidth, textHeight + 2*borderWidth));
	}
	public TextComponent(int chars, Point2i pos) {
		this(new Rectangle());
		this.textLength = chars; //in characters
		int textLength = fontSize * chars; //in pixels
		int textHeight = (int) ((double) fontSize * fontRatio);
		this.setBounds(new Rectangle(pos, textLength + 2*borderWidth, textHeight + 2*borderWidth));
	}
	
	
	public TextComponent setText(String text) {
		this.text = text;
		if(text.length() > textLength) textLength = text.length();
		return this;
	}
	public TextComponent setTextColor(Color3f c) {
		this.textColor = c;
		return this;
	}
	public TextComponent setFontSize(int fontSize) {
		TextComponent.fontSize = fontSize;
		int textLength = fontSize * text.length();
		int textHeight = (int) ((double) fontSize * fontRatio);
		return (TextComponent) this.setBounds(new Rectangle(bounds.getp1(), textLength + 2*TextComponent.borderWidth, textHeight + 2*borderWidth));
	}
	public TextComponent setTextLength(int chars) {
		this.textLength = chars;
		int textLength = fontSize * chars;
		int textHeight = (int) ((double) fontSize * fontRatio);
		return (TextComponent) this.setBounds(new Rectangle(bounds.getp1(), textLength + 2*borderWidth, textHeight + 2*borderWidth));
	}
	
	public String getText() {
		return text;
	}

	@Override
	public void render() {
		RenderUtils.applyColor(bgColor);
		glBegin(GL_QUADS); {
			if(hasTexture) glTexCoord2f(0.0f, 0.0f); glVertex2i(super.bounds.getp1().x, super.bounds.getp1().y);
			if(hasTexture) glTexCoord2f(1.0f, 0.0f); glVertex2i(super.bounds.getp2().x, super.bounds.getp1().y);
			if(hasTexture) glTexCoord2f(1.0f, 1.0f); glVertex2i(super.bounds.getp2().x, super.bounds.getp2().y);
			if(hasTexture) glTexCoord2f(0.0f, 1.0f); glVertex2i(super.bounds.getp1().x, super.bounds.getp2().y);
		} glEnd();
		RenderUtils.applyColor(borderColor);
		glBegin(GL_LINES); {
			glVertex2i(super.bounds.getp1().x, super.bounds.getp1().y);
			glVertex2i(super.bounds.getp2().x, super.bounds.getp1().y);
			glVertex2i(super.bounds.getp2().x, super.bounds.getp1().y);
			glVertex2i(super.bounds.getp2().x, super.bounds.getp2().y);
			glVertex2i(super.bounds.getp2().x, super.bounds.getp2().y);
			glVertex2i(super.bounds.getp1().x, super.bounds.getp2().y);
			glVertex2i(super.bounds.getp1().x, super.bounds.getp2().y);
			glVertex2i(super.bounds.getp1().x, super.bounds.getp1().y);
		} glEnd();
		RenderUtils.applyColor(textColor);
		Point2i textStart = new Point2i(bounds.getp1().x + borderWidth, bounds.getp1().y + borderWidth);
		glPushMatrix();
		tr.renderText(text, textStart, fontSize, (int) ((double)fontSize * fontRatio));
		glPopMatrix();
	}

	@Override
	public abstract void onClick(Point2i pos, int button);

	@Override
	public abstract void onMouseMove(Point2i pos);

	@Override
	public abstract void onKeyDown(Key key);

	@Override
	public abstract void onKeyUp(Key key);

}
