package lib.game.gui;

public class Key {

	private int keyInt;
	private String keyChar;
	private String keyString;
	
	public Key(int keyInt, String keyChar, String keyString) {
		this.keyInt = keyInt;
		this.keyChar = keyChar;
		this.keyString = keyString;
	}

	public Key(int keyInt, char keyChar, String keyString) {
		this(keyInt, new String(new char[] {keyChar}), keyString);
	}

	public int getKeyInt() {
		return keyInt;
	}
	
	public String getKeyChar() {
		return keyChar;
	}
	
	public String getKeyString() {
		return keyString;
	}
	
	public String toString() {
		return keyString + " " + keyChar + " " + keyInt;
	}

}
