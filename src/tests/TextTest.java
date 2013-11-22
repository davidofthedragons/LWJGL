package tests;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import lib.game.AbstractGame;
import lib.game.RenderUtils;
import lib.text.TextRenderer;

public class TextTest extends AbstractGame {

	TrueTypeFont font;
	Color fontColor = Color.green;
	TextRenderer textRenderer;
	
	public TextTest() throws LWJGLException {
		super("Testing Text", 600, 600);
		start();
	}

	@Override
	public void init() {
		RenderUtils.initStd2d(width, height);
		font = new TrueTypeFont(new Font("Arial", Font.BOLD, 24), false);
		try {
			textRenderer = new TextRenderer();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		glBegin(GL_QUADS);
			glVertex2d(100, 100);
			glVertex2d(150, 100);
			glVertex2d(150, 150);
			glVertex2d(100, 150);
		glEnd();
		//Color.white.bind();
		//font.drawString(50, 50, "Hello LWJGL", fontColor);
		glColor3f(0.0f, 1.0f, 0.0f);
		textRenderer.renderText("Hello LWJGL", 50, 50, 20);
		
	}

	@Override
	public void processInput() {
		
	}
	
	public static void main(String args[]) {
		try {
			new TextTest();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}
