package lib.game;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Basic framework for a LWJGL game;
 * Extend this class 
 * and implement methods init(), update(),
 * processInput(), and render();
 * Override the kill() method for 
 * custom end-game code;
 *  
 * Call start() to initialize display
 * and start game loop;
 *  
 * Every frame methods are called in 
 * this order:
 * processInput();
 * update();
 * render();
 * @author David Gardner
 *
 */
public abstract class AbstractGame {
	
	protected int width, height;
	private int fps = 60;
	protected boolean closeRequested = false;
	
	private Thread renderer = new Thread(new Runnable() {
			public void run() {
				try {
					Display.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				init();
				while(!closeRequested) {
					closeRequested = Display.isCloseRequested();
					processInput();
					update();
					render();
					Display.update();
					Display.sync(fps);
				}
				kill();
			}
		});
	
	/**
	 * @param title of the frame
	 * @param width of the frame
	 * @param height of the frame
	 * @throws LWJGLException
	 */
	public AbstractGame(String title, int width, int height) throws LWJGLException {
		Display.setTitle(title);
		this.width=width; this.height=height;
		Display.setDisplayMode(new DisplayMode(width, height));
	}
	/**
	 * @param title of the frame
	 * @param width of the frame when not fullscreen
	 * @param height of the frame when not fullscreen
	 * @param fullScreen if false, dimensions will be width and height
	 * @throws LWJGLException
	 */
	public AbstractGame(String title, int width, int height, boolean fullScreen) throws LWJGLException {
		Display.setTitle(title);
		if(fullScreen) {
			Display.setFullscreen(fullScreen);
			Display.setVSyncEnabled(true);
			this.width = Display.getWidth();
			this.height = Display.getHeight();
		}
		else {
			this.width=width; this.height=height;
			Display.setDisplayMode(new DisplayMode(width, height));
		}
	}
	/**
	 * @param title of the frame
	 * @param fullScreen intended to be true
	 * @throws LWJGLException
	 */
	public AbstractGame(String title, boolean fullScreen) throws LWJGLException {
		Display.setTitle(title);
		Display.setFullscreen(fullScreen);
		Display.setVSyncEnabled(true);
		this.width = Display.getWidth();
		this.height = Display.getHeight();
		
	}
	
	/**
	 * OpenGL initialization code
	 */
	public abstract void init();
	/**
	 * Entity update code
	 */
	public abstract void update();
	/**
	 * OpenGL render code
	 */
	public abstract void render();
	/**
	 * Input processing code
	 */
	public abstract void processInput();
	
	/**
	 * Starts the update loop
	 * @throws LWJGLException
	 */
	public void start() throws LWJGLException {
		renderer.start();
	}
	
	/**
	 * @return the current max frames per second
	 */
	public int getfps() {return fps;}
	/**
	 * Sets the max fps
	 * @param fps
	 */
	public void setfps(int fps) {this.fps=fps;}
	
	/**
	 * Ends the update loop and destroys the display.
	 * Override to add more actions on close.
	 */
	public void kill() {
		Display.destroy();
	}
	
	/**
	 * Tells the Display to close. The window will close at the beginning of the next frame.
	 */
	public void stop() {
		closeRequested = true;
	}
	
	
	
}
