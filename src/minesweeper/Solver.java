package minesweeper;

// Based on https://whereitisvc.github.io/minesweeper-reactjs/
public class Solver {
    private static Tile lastMove = null;

    public static Tile doBestMove(Game game) {
        if (game.isGameOver()) return null;

        if (game.getBoard().getAmountRevealed() == 0 && revealRandom(game)) return lastMove;

        if (flagKnownBombs(game)) return lastMove;
        if (revealKnownSafe(game)) return lastMove;
        if (revealRandom(game)) return lastMove;

        System.out.println("No best move");
        return null;
    }

    private static boolean revealRandom(Game game) {
        Tile tile = null;

        while (tile == null || tile.isRevealed() || tile.isFlagged()) {
            int x = (int)(Math.random() * game.getWidth());
            int y = (int)(Math.random() * game.getHeight());
            tile = game.getBoard().getTile(x, y);
        }
        
        game.reveal(tile);

        return sr(tile);
    }

    // Flag all neighbors if the amount of unrevealed neighbors equal tile value
    private static boolean flagKnownBombs(Game game) {
        Tile[][] tiles = game.getBoard().getTiles();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (!tile.isRevealed() || tile.isFlagged() || tile.getValue() == 0) continue;

                int unrevealed = 0;
                int flagged = 0;
                for (Tile neighbor : tile.getNeighbors()) {
                    if (neighbor == null) continue;
                    if (!neighbor.isRevealed()) unrevealed++;
                    if (neighbor.isFlagged()) flagged++;
                }

                if (unrevealed == tile.getValue() && flagged != tile.getValue()) {
                    game.flagNeighbors(tile);
                    return sr(tile);
                }
            }
        }

        return sr(null);
    }

    // Reveal all neighbors if the amount of flagged neighbors equal tile value
    private static boolean revealKnownSafe(Game game) {
        Tile[][] tiles = game.getBoard().getTiles();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (!tile.isRevealed() || tile.isFlagged() || tile.getValue() == 0) continue;

                int flags = 0;
                boolean needsRevealing = false;
                for (Tile neighbor : tile.getNeighbors()) {
                    if (neighbor == null) continue;
                    if (neighbor.isFlagged()) flags++;
                    else if (!neighbor.isRevealed()) needsRevealing = true;
                }

                if (flags == tile.getValue() && needsRevealing) {
                    game.revealNeighbors(tile);
                    return sr(tile);
                }
            }
        }

        return sr(null);
    }

    private static boolean sr(Tile tile) {
        lastMove = tile;
        return tile != null;
    }
}
