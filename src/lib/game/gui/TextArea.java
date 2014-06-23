package lib.game.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import utils.Utils;
import lib.game.RenderUtils;
import math.geom.Point2i;
import math.geom.Rectangle;

public class TextArea extends TextComponent {

	int cursorPos = 0;
	boolean lineWrapping = true;
	int charsPerLine = 10;
	
	public TextArea(int charsx, int charsy, Point2i pos) {
		super(new Rectangle(pos.x, pos.y, charsx*fontSize, (int)((double)(charsy*(fontSize+paddingy))*fontRatio)));
		charsPerLine = charsx;
	}
	
	public TextArea(Rectangle bounds) {
		super(bounds);
	}

	public TextArea(String text, Point2i pos) {
		super(text, pos);
	}
	
	private void addText(String text) {
		this.text = this.text.substring(0, cursorPos) + text + this.text.substring(cursorPos);
		cursorPos += text.length();
	}
	public void clearText() {
		text = "";
		cursorPos = 0;
	}
	@Override
	public TextComponent setText(String text) {
		super.setText(text);
		cursorPos = 0;
		return this;
	}
	private void backspace() {
		if(cursorPos == 0) return;
		this.text = this.text.substring(0, cursorPos-1) + this.text.substring(cursorPos);
		cursorPos--;
	}
	
	@Override
	public void onClick(Point2i pos, int button) {
		//Set cursor
		pos.x -= borderWidth;
		pos.y -= borderWidth;
		pos.x = (int) Utils.round(pos.x, fontSize);
		pos.y = (int) Utils.round(pos.y, (double)fontSize*fontRatio);
	}
	
	private Point2i getCharPos(int c) {
		int x = 0;
		int y = 0;
		for(int i=0; i<c; i++) {
			x++;
			if(text.charAt(i) == '\n' || (lineWrapping && x%charsPerLine == 0)) {
				x=0;
				y++;
			}
		}
		return new Point2i(x*fontSize, y*(int)((double)(fontSize)*fontRatio));
		
	}

	@Override
	public void onMouseMove(Point2i pos) {
		
	}
	
	@Override
	public void render() {
		super.render();
		if(super.hasFocus()) {
			//Draw cursor
			int cursorx = bounds.getp1().x + borderWidth + cursorPos*fontSize + 2;
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
		case Keyboard.KEY_RETURN:
			addText("\n");
			return;
		case Keyboard.KEY_RIGHT:
			cursorPos++;
			if(cursorPos >= text.length()) cursorPos = text.length()-1;
			return;
		case Keyboard.KEY_LEFT:
			cursorPos--;
			if(cursorPos < 0) cursorPos = 0;
			return;
		case Keyboard.KEY_BACK:
			backspace();
			return;
		case Keyboard.KEY_LSHIFT:return;
		case Keyboard.KEY_RSHIFT:return;
		case Keyboard.KEY_LCONTROL: return;
		case Keyboard.KEY_RCONTROL: return;
		}
		addText(key.getKeyChar());
	}

	@Override
	public void onKeyUp(Key key) {
		
	}

}
