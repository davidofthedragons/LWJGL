/***davidofthedragons LWJGL game template***
 * 
 * Extend this class 
 *  and implement methods init(), update(),
 *  processInput(), and render();
 * Override the kill() method for 
 *  custom end-game code;
 *  
 * Call start() to initialize display
 *  and start game loop;
 *  
 * Every frame methods are called in 
 *  this order:
 *  processInput();
 *  update();
 *  render();
 * 
 * 
 */

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
	
	public AbstractGame(String title, int width, int height) throws LWJGLException {
		Display.setTitle(title);
		this.width=width; this.height=height;
		Display.setDisplayMode(new DisplayMode(width, height));
	}
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
	public AbstractGame(String title, boolean fullScreen) throws LWJGLException {
		Display.setTitle(title);
		Display.setFullscreen(fullScreen);
		Display.setVSyncEnabled(true);
		this.width = Display.getWidth();
		this.height = Display.getHeight();
		
	}

	public abstract void init(); //OpenGL initialization code
	public abstract void update(); //Entity update code
	public abstract void render(); //OpenGL render code
	public abstract void processInput(); //Input code
	
	public void start() throws LWJGLException {
		renderer.start();
	}
	
	public int getfps() {return fps;}
	public void setfps(int fps) {this.fps=fps;}
	
	public void kill() {
		Display.destroy();
	}
	
	public void stop() {
		closeRequested = true;
	}
	
	
	
}
