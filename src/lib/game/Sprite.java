package lib.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import math.geom.Point2f;
import math.geom.Rectangle;

public class Sprite {

	int texture;
	
	ArrayList<Textangle> texCoords = new ArrayList<Textangle>();
	
	int index;
	
	boolean repeat;
	
	public Sprite(boolean repeat) {
		this.repeat = repeat;
	}
	
	public void nextFrame() {
		index++;
		if(index == texCoords.size()) {
			if(repeat)
				index = 0;
			else index--;
		}
		
	}
	
	public void doCoord(int coord) {
		Point2f p = new Point2f();
		switch(coord) {
		case 0: p = texCoords.get(index).p1; break;
		case 1: p = texCoords.get(index).p2; break;
		case 2: p = texCoords.get(index).p3; break;
		case 3: p = texCoords.get(index).p4; break; 
			
		}
		glTexCoord2f(p.x, p.y);
	}
	
	private class Textangle {
		public Point2f p1, p2, p3, p4;
		
	}

}
