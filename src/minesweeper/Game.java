package minesweeper;

import java.util.ArrayList;

import javax.swing.Timer;

public class Game {
    private Board board;
    private boolean gameOver;
    private boolean gameWon;
    private Timer timer;

    private boolean firstClickIsSafe;
    private boolean alwaysWin;
   
    public Game(int bombs, int w, int h) {
        firstClickIsSafe = true;
        alwaysWin = false;
        newGame(bombs, w, h);
    }
    
    public void setTimer(Timer t) {
    	timer = t;
    }
    
    public void newGame(Difficulty d) { newGame(d.bombs, d.width, d.height); }
    public void newGame() { newGame(board.getBombs().length, board.getWidth(), board.getHeight()); }
    public void newGame(int bombs, int w, int h) {
    	board = new Board(bombs, w, h);
        gameOver = false;
        gameWon = false;
        
        if (timer != null) {
        	timer.stop();
        	timer.restart();
        }
    }

    public void reveal(Tile tile) {
        if (tile == null || tile.isRevealed() || tile.isFlagged()) return;
        
        if (board.getAmountRevealed() == 0 && firstClickIsSafe && tile.isBomb()) {
            board.moveOrDeleteBomb(tile);
        }

        if (alwaysWin && tile.isBomb()) board.moveOrDeleteBomb(tile);

        board.reveal(tile);

        if (!tile.isBomb() && tile.getValue() == 0) {
            ArrayList<Tile> tilesToCheck = new ArrayList<Tile>();
            addNeighborsToList(tile, tilesToCheck);
            for (int i = 0; i < tilesToCheck.size(); i++) {
                Tile t = tilesToCheck.get(i);
                if (t.isRevealed() || t.isFlagged()) continue;
                board.reveal(t);

                if (!t.isBomb() && t.getValue() == 0) addNeighborsToList(t, tilesToCheck);
            }
        }

        updateGameOver();
        if (timer != null && !timer.isRunning()) timer.start();
    }
    
    public void revealAll() {
    	board.revealAll();
    	updateGameOver();
    }
    
    public void flag(Tile tile, boolean flag) {
        if (tile.isFlagged() == flag) return;

    	board.flag(tile, flag);
    	if (timer != null && !timer.isRunning()) timer.start();
    }

    public void flagNeighbors(Tile tile) {
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor != null && !neighbor.isRevealed() && !neighbor.isFlagged()) flag(neighbor, true);
        }
    }

    public void revealNeighbors(Tile tile) {
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor != null && !neighbor.isRevealed() && !neighbor.isFlagged()) reveal(neighbor);
        }
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }

    private void updateGameOver() {
        for (Tile bomb : board.getBombs()) {
    		if (bomb != null && bomb.isRevealed()) { gameOver = true; return; }
    	}
        
		gameOver = board.getAmountRevealed() == board.getWidth() * board.getHeight() - board.getAmountOfBombs();
		gameWon = gameOver;
    }

    private void addNeighborsToList(Tile tile, ArrayList<Tile> list) {
        for (Tile t : tile.getNeighbors()) {
            if (t != null) list.add(t);
        }
    }

    public Board getBoard() { return board; }
    public int getWidth() { return board.getWidth(); }
    public int getHeight() { return board.getHeight(); }

    public void setFirstClickIsSafe(boolean safe) { firstClickIsSafe = safe; }
    public void setAlwaysWin(boolean win) { alwaysWin = win; }
}
