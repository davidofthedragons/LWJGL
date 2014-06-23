package game.maze;

import lib.game.RenderUtils;

import org.lwjgl.opengl.GL11;

public class AmmoBonusItem extends Item {

	public int value = 15;
	
	public AmmoBonusItem(int x, int y) {
		super(x, y);
	}

	@Override
	public void pickedUp(Player player) {
		player.ammo += value;
		if(player.ammo > player.maxAmmo)
			player.ammo = player.maxAmmo;
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		//GL11.glTranslated(x, y, z);
		GL11.glColor3f(0.0f, 1.0f, 1.0f);
		RenderUtils.drawLineCube(2);
		GL11.glPopMatrix();
	}

}
