package game.platform;

public abstract class Item {

	boolean collectable = true;
	
	public Item() {
		
	}
	
	public abstract void use(int mouseButton, int key, Player player);
	
	public abstract void pickup(Player player);
	
	public abstract void touch(Player player);

	public abstract void render(ItemState state);
	
	public void update(Player player) {}
	
	public enum ItemState {
		HELD, DROPPED, ICON;
	}
}
