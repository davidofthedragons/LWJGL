package lib.game.gui;

import java.util.ArrayList;

import math.geom.Point2i;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Menu {

	private int width, height;
	
	ArrayList<Component> components = new ArrayList<Component>();
	
	public Menu(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public Menu(int width, int height, Component...components) {
		this(width, height);
		for(int i=0; i<components.length; i++) {
			add(components[i]);
		}
	}
	
	
	public void add(Component c) {
		components.add(c);
	}
	
	public void render() {
		for(Component c : components) {
			if(c.isVisible()) c.render();
		}
	}
	
	public void processInput() {
		while(Mouse.next()) {
			Point2i mouse = new Point2i(Mouse.getEventX(), Display.getHeight() - Mouse.getEventY());
//			System.out.println(mouse);
			for(Component c : components) {
//				System.out.println(c.bounds);
				if(!c.isEnabled()) continue;
				if(c.bounds.contains(mouse)) {
					if(Mouse.getEventButtonState()) {
						c.onClick(mouse, Mouse.getEventButton());
						grantFocus(c);
					}
					else c.onMouseMove(mouse);
					break;
				}
			}
		}
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) getFocusedComponent().onKeyDown(new Key(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getKeyName(Keyboard.getEventKey())));
			else getFocusedComponent().onKeyUp(new Key(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getKeyName(Keyboard.getEventKey())));
		}
	}
	
	public Component getComponent(String label) {
		for(Component c : components) {
			if(c.getLabel().equals(label)) return c;
		}
		return null;
	}
	
	protected Component getFocusedComponent() {
		int i = getFocusedComponentIndex();
		return (i==-1)? null : components.get(i);
	}
	protected int getFocusedComponentIndex() {
		for(int i=0; i<components.size(); i++) {
			if(components.get(i).hasFocus())  {
				return i;
			}
		}
		return -1;
	}
	public void grantFocus(Component c) {
		for(Component comp : components) {
			comp.revokeFocus();
			if(comp.equals(c)) comp.grantFocus();
		}
	}
	public void grantFocus(int c) {
		for(int i=0; i<components.size(); i++) {
			components.get(i).revokeFocus();
			if(i == c) components.get(i).grantFocus();
		}
	}
	protected void revokeFocus(Component c) {
		c.revokeFocus();
	}
	protected void revokeFocus(int c) {
		components.get(c).revokeFocus();
	}

}
