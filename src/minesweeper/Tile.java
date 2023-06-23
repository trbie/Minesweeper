package minesweeper;

public class Tile {
	private boolean revealed;
	private boolean flagged;
	private boolean bomb;
	private int value;
	private Tile[] neighbors;
	
	public Tile() {
		revealed = false;
		flagged = false;
		bomb = false;
		value = 0;
		neighbors = new Tile[8];
	}
	
	public boolean isRevealed() { return revealed; }
	public boolean isFlagged() { return flagged; }
	public boolean isBomb() { return bomb; }
	public int getValue() { return value; }
	public Tile[] getNeighbors() { return neighbors; }
	
	public void setRevealed(boolean revealed) { this.revealed = revealed; }
	public void setFlagged(boolean flagged) { this.flagged = flagged; }
	public void setBomb(boolean bomb) { this.bomb = bomb; }
	public void addBomb() { value++; }
	public void removeBomb() { value--; }
	
	public void setNeighbors(Tile[] neighbors) { 
		this.neighbors = neighbors; 
	}
}
