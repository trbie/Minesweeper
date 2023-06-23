package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Canvas extends JPanel {
    public static final int MARGIN = 10;

    private Game game;
    private ArrayList<Tile> pressedTiles;

    private Point cursor;
    private boolean useCursor;

    public Canvas(Game game) {
        this.game = game;
        pressedTiles = new ArrayList<Tile>();

        cursor = new Point(game.getWidth() / 2, game.getHeight() / 2);
        useCursor = false;

        addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
                if (game.isGameOver()) return;
                Tile tile = getTileFromCoords(e.getPoint());
                if (tile == null) return;

                if (SwingUtilities.isLeftMouseButton(e) && !tile.isRevealed() &&  !tile.isFlagged()) {
                    pressedTiles.add(tile);
                }

                else if (SwingUtilities.isMiddleMouseButton(e) || (SwingUtilities.isLeftMouseButton(e) && tile.isRevealed())) {
                    if (!tile.isRevealed() && !tile.isFlagged()) pressedTiles.add(tile);
                    for (Tile neighbor : tile.getNeighbors()) {
                        if (neighbor != null && !neighbor.isRevealed() && !neighbor.isFlagged()) pressedTiles.add(neighbor);
                    }
                }

                else if (SwingUtilities.isRightMouseButton(e) && !tile.isRevealed()) {
                    game.flag(tile, !tile.isFlagged());
                }

                repaint();
			}

            @Override
            public void mouseReleased(MouseEvent e) {
                if (game.isGameOver()) return;
                Tile tile = getTileFromCoords(e.getPoint());
                if (tile == null) return;

                if (SwingUtilities.isLeftMouseButton(e) && !tile.isRevealed() && !tile.isFlagged())
                    game.reveal(tile);

                else if ((SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isLeftMouseButton(e)) && tile.isRevealed()) {
                    Tile[] neighbors = tile.getNeighbors();
                    int flagged = 0;
                    for (Tile neighbor : neighbors) {
                        if (neighbor != null && neighbor.isFlagged()) flagged++;
                    }

                    if (flagged == tile.getValue()) {
                        game.revealNeighbors(tile);
                    }
                }

                pressedTiles.clear();
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (game.isGameOver()) return;
                Tile tile = getTileFromCoords(e.getPoint());
                if (tile == null) return;

                pressedTiles.clear();
                if (SwingUtilities.isLeftMouseButton(e) && !tile.isRevealed() && !tile.isFlagged()) {
                    pressedTiles.add(tile);
                }

                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        setPreferredSize(new Dimension(game.getWidth()*ImageManager.size, game.getHeight()*ImageManager.size));
        g.setColor(new Color(ImageManager.bgColor));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                g.drawImage(getTileImage(game.getBoard().getTile(x, y), game.isGameOver()), x*ImageManager.size + MARGIN, y*ImageManager.size + MARGIN, this);
            }
        }

        for (Tile tile : pressedTiles) {
            Point p = getCoordsFromTile(tile);
            g.drawImage(ImageManager.HIDDEN_PRESSED, p.x, p.y, this);
        }

        if (useCursor) drawCursor(g);
    }

    public void enableCursor(boolean b) {
        useCursor = b;
    }

    private void drawCursor(Graphics g) {
        g.setColor(new Color(0x7F000000, true));

        int size = ImageManager.size / 16;

        boolean draw = false;
        for (int r = 0; r < ImageManager.size / size; r++) {
            draw = !draw;
            for (int c = 0; c < ImageManager.size / size; c++) {
                if (draw)
                    g.fillRect(c * size + cursor.x * ImageManager.size + MARGIN,
                            r * size + cursor.y * ImageManager.size + MARGIN, 
                            size, size);

                draw = !draw;
            }
        }
    }

    public void useCursor(int code, boolean pressed) {
        // Codes
        //  0 - Reveal
        //  1 - Flag

        Tile tile = game.getBoard().getTile((int)cursor.getX(), (int)cursor.getY());
        if (tile == null) return;

        if (pressed) {
            pressedTiles.clear();

            if (code == 0) {
                if (!tile.isRevealed() && !tile.isFlagged()) pressedTiles.add(tile);
            
                else if (tile.isRevealed()) {
                    for (Tile neighbor : tile.getNeighbors()) {
                        if (neighbor != null && !neighbor.isRevealed() && !neighbor.isFlagged()) pressedTiles.add(neighbor);
                    }
                }
            }

            if (code == 1 && !tile.isRevealed()) game.flag(tile, !tile.isFlagged());
        }

        else {
            if (code == 0) {
                if (!tile.isRevealed() && !tile.isFlagged()) game.reveal(tile);
                else if (tile.isRevealed()) {
                    Tile[] neighbors = tile.getNeighbors();
                    int flagged = 0;
                    for (Tile neighbor : neighbors) {
                        if (neighbor != null && neighbor.isFlagged()) flagged++;
                    }

                    if (flagged == tile.getValue()) {
                        game.revealNeighbors(tile);
                    }
                }
            }

            pressedTiles.clear();
        }

        repaint();
    }

    public void resetCursor() {
        cursor.setLocation(game.getWidth() / 2, game.getHeight() / 2);
    }

    public void setCursor(int x, int y) {
        cursor.setLocation(x, y);
        moveCursor(0, 0);
    }

    public void moveCursor(int x, int y) {
        cursor.setLocation(cursor.getX() + x, cursor.getY() + y);

        if (cursor.getX() < 0) cursor.setLocation(0, cursor.getY());
        if (game.getWidth() <= cursor.getX()) cursor.setLocation(game.getWidth() - 1, cursor.getY());

        if (cursor.getY() < 0) cursor.setLocation(cursor.getX(), 0);
        if (game.getHeight() <= cursor.getY()) cursor.setLocation(cursor.getX(), game.getHeight() - 1);

        repaint();
    }

    private Image getTileImage(Tile t, boolean showAll) {
        if (t == null) return null;
        
        if (showAll) {
            if (t.isFlagged()) {
                if (t.isBomb()) return ImageManager.FLAG;
                else return ImageManager.BAD_FLAG;
            }
            
            if (t.isBomb()) { 
                if (t.isRevealed()) return ImageManager.BOMB_HIT;
                else return ImageManager.BOMB;
            }

            return ImageManager.NUMBERS[t.getValue()];
        }

        if (t.isFlagged()) return ImageManager.FLAG;
        if (!t.isRevealed()) return ImageManager.HIDDEN;
        return ImageManager.NUMBERS[t.getValue()];
    }

	private Tile getTileFromCoords(Point point) {
		if (point.x < MARGIN || point.y < MARGIN) return null;
		
		int x = (point.x - MARGIN) / ImageManager.size;
		int y = (point.y - MARGIN) / ImageManager.size;
		
		return game.getBoard().getTile(x, y);
	}

    private Point getCoordsFromTile(Tile tile) {
        Point p = game.getBoard().getIndex(tile);
        if (p == null) return null;

        int x = p.x * ImageManager.size + MARGIN;
        int y = p.y * ImageManager.size + MARGIN;

        return new Point(x, y);
    }
}
