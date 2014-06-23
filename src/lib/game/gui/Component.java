package lib.game.gui;

import graphics.Color3f;
import math.geom.Point2i;
import math.geom.Rectangle;

public abstract class Component {

	public Rectangle bounds;
	private boolean hasFocus = false;
	private boolean enabled = true;
	private boolean visible  = true;
	private String label;
	protected Color3f borderColor = new Color3f(0.3f, 0.3f, 0.3f),
			bgColor = new Color3f(0.5f, 0.5f, 0.5f),
			textColor = Color3f.white;
	protected int texture;
	protected boolean hasTexture = false;
	
	public Component(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public Component setBorderColor(Color3f c) {
		this.borderColor = c;
		return this;
	}
	public Component setBgColor(Color3f c) {
		this.bgColor = c;
		return this;
	}
	public Component setTexture(int texture) {
		this.texture = texture;
		this.hasTexture = true;
		return this;
	}
	public Component setBounds(Rectangle r) {
		this.bounds = r;
		return this;
	}
	
	public void grantFocus() {
		hasFocus = true;
	}
	public void revokeFocus() {
		hasFocus = false;
	}
	public boolean hasFocus() {
		return hasFocus;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public abstract void render();
	public abstract void onClick(Point2i pos, int button);
//	public abstract void onMouseUp(Point2i pos, int button);
	public abstract void onMouseMove(Point2i pos);
	public abstract void onKeyDown(Key key);
	public abstract void onKeyUp(Key key);
	
}
