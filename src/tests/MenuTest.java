package tests;

import graphics.Color3f;

import org.lwjgl.LWJGLException;

import lib.game.AbstractGame;
import lib.game.RenderUtils;
import lib.game.gui.*;
import math.geom.Point2i;

public class MenuTest extends AbstractGame {

	Menu menu;
	
	public MenuTest() throws LWJGLException {
		super("Menu Test", 600, 600);
		start();
	}

	@Override
	public void init() {
		RenderUtils.initStd2d(width, height);
		menu = new Menu(width, height);
		final Label label = new Label("I'm a label!", new Point2i(200, 50));
		menu.add(label);
		Button button = (Button) ((Button) new Button("Push Me!", new Point2i(50, 50)) {
			@Override
			public void onClick(Point2i pos, int button) {
				System.out.println("Click");
				label.setVisible(!label.isVisible());
			}
			@Override
			public void onMouseMove(Point2i pos) {/*System.out.println("Move");*/}
		}.setBgColor(Color3f.blue).setBorderColor(Color3f.red)).setTextColor(Color3f.white);
		menu.add(button);
		Button exitButton = new Button("Exit", new Point2i(50, 200)) {
			@Override
			public void onClick(Point2i pos, int button) {
				stop();
			}
		};
		menu.add(exitButton);
//		Label label = new Label(new Rectangle(200, 50, 100, 50), "I'm a label!");
		TextField textField = new TextField("Hello", new Point2i(200, 200)) {
			@Override
			public void onSubmit() {
				label.setText(super.text);
			}
		};
		menu.add(textField);
		TextArea textArea = new TextArea(15, 3, new Point2i(200, 300));
		menu.add(textArea);
		TextArea textArea1 = new TextArea("Hello\nWorld", new Point2i(100, 300));
		menu.add(textArea1);
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		menu.render();
	}

	@Override
	public void processInput() {
		menu.processInput();
	}
	
	public static void main(String args[]) {
		try {
			new MenuTest();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}
