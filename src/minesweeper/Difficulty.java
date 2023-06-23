package minesweeper;

public class Difficulty {
	public String name;
	public int bombs;
	public int width;
	public int height;

	public Difficulty() { this("Easy", 10, 9, 9); }
	public Difficulty(String name, int bombs, int width, int height) {
		this.name = name;
		this.bombs = bombs;
		this.width = width;
		this.height = height;
	}
}
