package lib.game.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import graphics.Color3f;

import org.lwjgl.input.Keyboard;

import lib.text.TextRenderer;
import math.geom.Point2i;
import math.geom.Rectangle;

public abstract class Button extends TextComponent {

	public Button(String text, Point2i pos) {
		super(text, pos);
	}

	@Override
	public abstract void onClick(Point2i pos, int button);

	@Override
	public void onMouseMove(Point2i pos) {}

	@Override
	public void onKeyDown(Key key) {}

	@Override
	public void onKeyUp(Key key) {}
	
}
