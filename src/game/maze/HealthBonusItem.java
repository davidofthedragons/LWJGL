package game.maze;

import org.lwjgl.opengl.GL11;

import lib.game.RenderUtils;

public class HealthBonusItem extends Item {

	public int value = 3;
	
	public HealthBonusItem(int x, int y) {
		super(x, y);
	}

	@Override
	public void pickedUp(Player player) {
		player.health += value;
		if(player.health > player.startHealth) player.health = player.startHealth;
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		//GL11.glTranslated(x, y, z);
		GL11.glColor3f(1.0f, 0.0f, 1.0f);
		RenderUtils.drawLineCube(2);
		GL11.glPopMatrix();
	}

}
