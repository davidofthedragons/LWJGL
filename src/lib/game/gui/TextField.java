package lib.game.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import lib.game.RenderUtils;
import math.geom.Point2i;
import math.geom.Rectangle;

public class TextField extends TextComponent {
	
	private boolean expanding = true;
	
	public TextField(int chars, Point2i pos) {
		super(chars, pos);
	}
	public TextField(String text, Point2i pos) {
		super(text, pos);
	}

	public void append(String textToAppend) {
		if (!expanding) {
			for (int i = 0; i < textToAppend.length(); i++) {
				if (text.length() + i > textLength)
					return;
				text += textToAppend.charAt(i);
			}
		}
		else {
			text += textToAppend;
			if(textLength <text.length()) setTextLength(text.length());
		}
	}
	
	public void backspace() {
		if(text.length() == 0) return;
		text = text.substring(0, text.length()-1);
	}
	
	public boolean isExpanding() {
		return expanding;
	}
	public void setExpanding(boolean expand) {
		this.expanding = expand;
	}
	@Override
	public void render() {
		super.render();
		if(super.hasFocus()) {
			//Draw cursor
			int cursorx = bounds.getp1().x + borderWidth + text.length()*fontSize + 2;
			RenderUtils.applyColor(textColor);
			glBegin(GL_LINES); {
				glVertex2i(cursorx, bounds.getp1().y + borderWidth-2);
				glVertex2i(cursorx, bounds.getp1().y + (int) ((double)fontSize * fontRatio) + borderWidth+2);
			} glEnd();
		}
	}

	@Override
	public void onKeyDown(Key key) {
		switch(key.getKeyInt()) {
		case Keyboard.KEY_BACK:
			backspace();
			return;
		case Keyboard.KEY_RETURN:
			onSubmit();
			return;
		case Keyboard.KEY_LSHIFT:
			return;
		case Keyboard.KEY_RSHIFT: return;
		case Keyboard.KEY_LCONTROL: return;
		case Keyboard.KEY_RCONTROL: return;
		}
//		System.out.println(key);
		
		append(key.getKeyChar());
	}

	@Override
	public void onKeyUp(Key key) {
//		System.out.println(key);
	}
	
	public void onSubmit() {
		//Override if desired (called when enter pressed)
	}

	@Override
	public void onClick(Point2i pos, int button) {
		//Place cursor
	}

	@Override
	public void onMouseMove(Point2i pos) {}

}
