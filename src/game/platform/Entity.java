package game.platform;

import static org.lwjgl.opengl.GL11.*;
import lib.game.Sprite;
import math.geom.Point2i;
import math.geom.Rectangle;
import math.geom.Vector2f;

public class Entity {

	Rectangle bounds;
	int texture;
	Sprite sprite = null;
	boolean hasTexture = false;
	
	Vector2f velocity = new Vector2f(0.0f, 0.0f);
	float gravacc = -0.1f;
	boolean gravity = true;
	boolean solid = true;
	
	public Entity(Point2i pos, int width, int height) {
		bounds = new Rectangle(pos, width, height);
	}
	
	public Entity(Point2i pos, int width, int height, int texture) {
		this(pos, width, height);
		setTexture(texture);
	}
	
	public void setTexture(int texture) {
		this.texture = texture;
		hasTexture = true;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public boolean collides(Rectangle cbounds) {
		return bounds.intersects(cbounds);
	}
	
	public void update() {
		
	}
	
	public void move() {
		velocity.add(new Vector2f(0.0f, gravacc));
		bounds.translate((int)velocity.geti(), (int)velocity.getj());
	}
	
	public void render() {
		glBegin(GL_QUADS); {
			if(sprite != null) {
				sprite.doCoord(0); glVertex2i(bounds.getp1().x, bounds.getp1().y);
				sprite.doCoord(1); glVertex2i(bounds.getp2().x, bounds.getp1().y);
				sprite.doCoord(2); glVertex2i(bounds.getp2().x, bounds.getp2().y);
				sprite.doCoord(3); glVertex2i(bounds.getp1().x, bounds.getp2().y);
				sprite.nextFrame();
			} else if(hasTexture) {
				glTexCoord2f(0.0f, 0.0f); glVertex2i(bounds.getp1().x, bounds.getp1().y);
				glTexCoord2f(1.0f, 0.0f); glVertex2i(bounds.getp2().x, bounds.getp1().y);
				glTexCoord2f(1.0f, 1.0f); glVertex2i(bounds.getp2().x, bounds.getp2().y);
				glTexCoord2f(0.0f, 1.0f); glVertex2i(bounds.getp1().x, bounds.getp2().y);
			} else {
				glVertex2i(bounds.getp1().x, bounds.getp1().y);
				glVertex2i(bounds.getp2().x, bounds.getp1().y);
				glVertex2i(bounds.getp2().x, bounds.getp2().y);
				glVertex2i(bounds.getp1().x, bounds.getp2().y);
			}
		} glEnd();
		
	}

}
