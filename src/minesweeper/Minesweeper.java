package minesweeper;

public class Minesweeper {

	public static void main(String[] args) {
		Game game = new Game(10, 9, 9);

		MinesweeperGUI window = new MinesweeperGUI(game, "xp", 16);
		window.setLocationRelativeTo(null);
	}
}
