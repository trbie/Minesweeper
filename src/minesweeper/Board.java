package minesweeper;

import java.awt.Point;
import java.util.ArrayList;

public class Board {
	private int width;
	private int height;
	private Tile[][] tiles;
	private Tile[] bombs;
	private int revealed;
	private int flagged;
	
	public Board(int amountOfBombs, int w, int h) {
		if (amountOfBombs >= w * h) throw new RuntimeException("Amount of bombs has to be less than total tiles on the board");

		width = w;
		height = h;
		bombs = new Tile[amountOfBombs];
		tiles = new Tile[h][w];
		revealed = 0;
		flagged = 0;
		
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				tiles[y][x] = new Tile();
			}
		}
		
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				Tile[] neighbors = new Tile[8];
				
				// TODO: optimize later
				if (0 <= y - 1 && 0 <= x - 1) neighbors[0] = tiles[y-1][x-1];
				if (0 <= y - 1) neighbors[1] = tiles[y-1][x];
				if (0 <= y - 1 && w > x + 1) neighbors[2] = tiles[y-1][x+1];
				if (0 <= x - 1) neighbors[3] = tiles[y][x-1];
				if (w > x + 1) neighbors[4] = tiles[y][x+1];
				if (h > y + 1 && 0 <= x - 1) neighbors[5] = tiles[y+1][x-1];
				if (h > y + 1) neighbors[6] = tiles[y+1][x];
				if (h > y + 1 && w > x + 1) neighbors[7] = tiles[y+1][x+1];
				
				tiles[y][x].setNeighbors(neighbors);
			}
		}

		for (int i = 0; i < amountOfBombs; i++) {
			int x = (int) (Math.random() * w);
		 	int y = (int) (Math.random() * h);
			
			if (tiles[y][x].isBomb()) {
				i--;
				continue;
			}

			bombs[i] = tiles[y][x];
			makeBomb(bombs[i]);
		}
	}
	
	private void makeBomb(Tile tile) {
		tile.setBomb(true);
		for (Tile neighbor : tile.getNeighbors()) {
			if (neighbor != null) neighbor.addBomb();
		}
	}

	public void moveOrDeleteBomb(Tile tile) {
		if (!tile.isBomb()) return;

		tile.setBomb(false);
		for (Tile neighbor : tile.getNeighbors()) {
			if (neighbor != null) neighbor.removeBomb();
		}

		for (int i = 0; i < bombs.length; i++) {
			if (tile == bombs[i]) {
				ArrayList<Tile> exclude = new ArrayList<Tile>();
				exclude.add(tile);
				Tile newTile = getEmptyTile(exclude);
				bombs[i] = newTile;

				if (newTile == null) return;
				makeBomb(newTile);
			}
		}
	}

	public void reveal(Tile tile) {
		tile.setRevealed(true);
		revealed++;
	}
	
	public void revealAll() {
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				if (!tile.isFlagged() && !tile.isRevealed()) reveal(tile);
			}
		}
	}
	
	public void flag(Tile tile, Boolean f) {
		tile.setFlagged(f);
		if (f) flagged++;
		else flagged--;
	}
	
	public int getAmountRevealed() { return revealed; }
	public int getAmountFlagged() { return flagged; }
	
	public int getAmountOfBombs() {
		int amount = 0;
		for (Tile bomb : bombs) {
			if (bomb != null) amount++;
		}
		
		return amount;
	}
	
	public Tile[][] getTiles() { return tiles; }
	public Tile getTile(int x, int y) { 
		if (x < 0 || width <= x) return null;
		if (y < 0 || height <= y) return null;
		return tiles[y][x];
	}

	public Point getIndex(Tile tile) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (tile == tiles[y][x]) return new Point(x, y);
			}
		}

		return null;
	}

	public Tile[] getBombs() { return bombs; }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }

	private Tile getEmptyTile(ArrayList<Tile> excludedTiles) {
		Tile tile = null;

		int startX = (int)(Math.random() * width);
		int startY = (int)(Math.random() * height);
		int x = startX;
		int y = startY;

		while (!(x == startX && y == startY) || tile == null) {
			x++;

			if (width <= x) {
				y++;
				x = 0;
			};
			if (height <= y) y = 0;

			tile = tiles[y][x];

			boolean excluded = false;
			for (Tile t : excludedTiles) {
				if (tile == t) {
					excluded = true;
					break;
				}
			}

			if (!tile.isBomb() && !tile.isRevealed() && !excluded) return tile;
		}

		return null;
	}
}
