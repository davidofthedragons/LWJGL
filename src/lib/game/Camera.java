package lib.game;

import static org.lwjgl.opengl.GL11.*;

import math.geom.Point3f;
import math.geom.Vector3f;

public class Camera {

	private Vector3f pos = new Vector3f();
	private float yaxisrot = 0.0f;
	
	private float xaxisrot = 0.0f;
	private float xlBound;
	private float xuBound;
	private boolean applyxBounds = false;
	private float tilt = 0.0f;
	
	public Camera(Vector3f pos) {
		this.pos = pos;
		
	}
	public Camera(Vector3f pos, float xaxisrot, float yaxisrot, float tilt) {
		this.pos = pos;
		this.yaxisrot = yaxisrot;
		this.xaxisrot = xaxisrot;
		this.tilt = tilt;
	}
	public Camera() {}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	
	public float getYaxisrot() {
		return yaxisrot;
	}
	public void setYaxisrot(float yaxisrot) {
		this.yaxisrot = yaxisrot;
	}
	
	public float getXaxisrot() {
		return xaxisrot;
	}
	public void setXaxisrot(float xaxisrot) {
		this.xaxisrot = xaxisrot;
	}
	
	public void setBoundsx(float lBound, float uBound) {
		applyxBounds = true;
		this.xlBound = lBound;
		this.xuBound = uBound;
	}
	
	public float getTilt() {
		return tilt;
	}
	public void setTilt(float tilt) {
		this.tilt = tilt;
	}
	
	public void rotY(float angle) {
		yaxisrot += angle;
	}
	public void rotX(float angle) {
		if(applyxBounds) {
			if(xaxisrot + angle > xlBound && xaxisrot + angle < xuBound)
				xaxisrot += angle;
		}
		else xaxisrot += angle;
		
	}
	public void tilt(float angle) {
		tilt += angle;
	}
	
	public void move(Vector3f moveVect) {
		pos.seti(pos.geti()+moveVect.geti());
		pos.setj(pos.getj()+moveVect.getj());
		pos.setk(pos.getk()+moveVect.getk());
	}
	
	public void moveForward(float amount) {
		pos.seti((float) (pos.geti()+Math.sin(Math.toRadians(yaxisrot))*amount));
		pos.setk((float) (pos.getk()-Math.cos(Math.toRadians(yaxisrot))*amount));
	}
	public void moveBackward(float amount) {
		pos.seti((float) (pos.geti()+Math.sin(Math.toRadians(yaxisrot))*-amount));
		pos.setk((float) (pos.getk()-Math.cos(Math.toRadians(yaxisrot))*-amount));
	}
	public void moveRight(float amount) {
		pos.seti((float) (pos.geti()+Math.sin(Math.toRadians(yaxisrot-90))*-amount));
		pos.setk((float) (pos.getk()-Math.cos(Math.toRadians(yaxisrot-90))*-amount));
	}
	public void moveLeft(float amount) {
		pos.seti((float) (pos.geti()+Math.sin(Math.toRadians(yaxisrot-90))*amount));
		pos.setk((float) (pos.getk()-Math.cos(Math.toRadians(yaxisrot-90))*amount));
	}
	public void moveUp(float amount) {
		pos.setj(pos.getj()+amount);
	}
	public void moveDown(float amount) {
		pos.setj(pos.getj()-amount);
	}
	
	public void applyTransform() { //TODO: Fix movement when looking in different directions
		glRotatef(xaxisrot, 1.0f, 0.0f, 0.0f);
		glRotatef(yaxisrot, 0.0f, 1.0f, 0.0f);
		glRotatef(tilt, 0.0f, 0.0f, 1.0f);
		glTranslatef(-pos.geti(), -pos.getj(), -pos.getk());
		
	}
	public Vector3f getLookVect() {
		float i = (float) (Math.sin(Math.toRadians(yaxisrot)));
		float j = (float) (-Math.sin(Math.toRadians(xaxisrot)));
		float k = (float) (-Math.cos(Math.toRadians(yaxisrot)));
		return new Vector3f(i, j, k);
	}
	
}
