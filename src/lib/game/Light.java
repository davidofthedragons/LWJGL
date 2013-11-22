package lib.game;

import static org.lwjgl.opengl.GL11.*;

import graphics.Color3f;

import java.nio.FloatBuffer;

import math.geom.Point3f;

public class Light {

	LightModel model;
	FloatBuffer pos;
	int lightNum;
	Color3f shine, diffuse;
	
	public Light() {}
	public Light(LightModel model) {
		this.setLightModel(model);
	}
	public Light(LightModel model, Point3f pos, Color3f shine, Color3f diffuse) {
		this.setLightModel(model).setPosition(pos).setShineColor(shine).setDiffuseColor(diffuse);
	}
	
	public Light setShineColor(Color3f color) {
		this.shine = color;
		return this;
	}
	public Light setDiffuseColor(Color3f color) {
		this.diffuse = color;
		return this;
	}
	
	public Color3f getShineColor() {
		return shine;
	}
	public Color3f getDiffuseColor() {
		return diffuse;
	}
	
	public Light setSpotProperties(float cutOff, float exponent) {//TODO other pars
		return this;
	}
	
	public Light setPosition(Point3f pos) {
		this.pos = RenderUtils.asFloatBuffer(new float[]{pos.x, pos.y, pos.z});
		return this;
	}
	public FloatBuffer getPosition() {
		return pos;
	}
	
	public LightModel getLightModel() {
		return model;
	}
	public Light setLightModel(LightModel model) {
		this.model = model;
		return this;
	}
	
	public Light bind(int light) {
		lightNum = light;
		int[] lights = {GL_LIGHT0, GL_LIGHT1, GL_LIGHT2, GL_LIGHT3, GL_LIGHT4, GL_LIGHT5, GL_LIGHT6, GL_LIGHT7};
		glEnable(lights[light]);
		return this;
	}
	
	public void renderLight() {
		
	}
	
	public enum LightModel {
		AMBIENT, SPOT;
	}
}
