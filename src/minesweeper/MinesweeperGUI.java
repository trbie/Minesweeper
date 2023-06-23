package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class MinesweeperGUI extends JFrame {
	private Canvas canvas;
	private Game game;
	private int time;
	public Timer gameTimer;

	private Timer autoPlayTimer;
	private Timer restartTimer;
	
	private JPanel gameInfo;
	private JLabel flaggedLbl;
	private JLabel timerLbl;
	
	public MinesweeperGUI(Game game, String windowTheme, int tileSize) {
		this.game = game;
		ImageManager.load(windowTheme, tileSize);
		
		FlatLightLaf.setup();
		setLayout(new MigLayout("fill, insets 0, gap 0"));


		time = 0;
		gameTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game.isGameOver()) {
					gameTimer.stop();
					return;
				}
				
				time++;
				timerLbl.setText(getTimeString());
			}
		}) {
			@Override
			public void restart() {
				time = 0;
				timerLbl.setText("0:00");
			}
		};
		
		game.setTimer(gameTimer);
		
		JMenuBar menuBar = new JMenuBar();		
		
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');

		JMenuItem newGameBtn = new JMenuItem(new AbstractAction("New Game") {
			public void actionPerformed(ActionEvent e) {
				game.newGame();
				canvas.resetCursor();
				canvas.repaint();
			}
		});
		newGameBtn.setMnemonic('N');
		gameMenu.add(newGameBtn);

		JMenu difficultyMenu = new JMenu("Difficulty");
		difficultyMenu.setMnemonic('D');
		ButtonGroup difficultyBtnGroup = new ButtonGroup();
		Difficulty[] difficulties = { 
			new Difficulty(), 
			new Difficulty("Medium", 40, 16, 16), 
			new Difficulty("Hard", 99, 30, 16), 
			new Difficulty("Expert", 250, 30, 16),
			new Difficulty("Impossible", 899, 30, 30),
			new Difficulty("Custom", 0, 0, 0)
		};
		for (Difficulty d : difficulties) {
			if (d.name.equals("Custom")) difficultyMenu.addSeparator();
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(d.name);
			btn.setSelected(d.name.equals("Easy"));
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (d.name.equals("Custom")) {
						DifficultyPrompt prompt = new DifficultyPrompt();
						Difficulty custom = prompt.getDifficulty();
						if (custom == null) return;
						game.newGame(custom);
					}

					else {
						game.newGame(d);
					}

					resizeCanvas();
					canvas.resetCursor();
					canvas.repaint();
				}
			});

			difficultyBtnGroup.add(btn);
			difficultyMenu.add(btn);
		}

		gameMenu.add(difficultyMenu);

		JMenu cheatMenu = new JMenu("Cheats");
		cheatMenu.setMnemonic('C');
		JCheckBoxMenuItem firstClickIsSafe = new JCheckBoxMenuItem("1st click is safe", true);
		firstClickIsSafe.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				game.setFirstClickIsSafe(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cheatMenu.add(firstClickIsSafe);

		JCheckBoxMenuItem alwaysWin = new JCheckBoxMenuItem("Always win", false);
		alwaysWin.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				game.setAlwaysWin(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cheatMenu.add(alwaysWin);

		gameMenu.add(cheatMenu);

		JMenuItem exitBtn = new JMenuItem(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		exitBtn.setMnemonic('E');
		gameMenu.add(exitBtn);

		menuBar.add(gameMenu);

		JMenu autoMenu = new JMenu("Auto");
		autoMenu.setMnemonic('A');

		JCheckBoxMenuItem autoPlay = new JCheckBoxMenuItem("Play");
		autoPlay.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (game.isGameOver()) { game.newGame(); canvas.repaint(); }
					autoPlayTimer.start();
				} else {
					autoPlayTimer.stop();
					restartTimer.stop();
				}
			}
		});
		autoMenu.add(autoPlay);

		JMenuItem moveBtn = new JMenuItem(new AbstractAction("Move Once") {
			public void actionPerformed(ActionEvent e) {
				Tile move = Solver.doBestMove(game);
				if (move != null) { 
					Point pos = game.getBoard().getIndex(move);
					canvas.setCursor((int)pos.getX(), (int)pos.getY()); 
				}
				canvas.repaint();
			}
		});
		autoMenu.add(moveBtn);

		JMenu moveDelay = new JMenu("Move Delay");
		DelayPrompt moveDelayPrompt = new DelayPrompt(500) {
			@Override
			public void valueChanged() {
				autoPlayTimer.setDelay(getValue());
			}
		};
		moveDelay.add(moveDelayPrompt);
		autoMenu.add(moveDelay);

		autoMenu.addSeparator();

		JCheckBoxMenuItem restartOnLose = new JCheckBoxMenuItem("Restart on Lose", true);
		autoMenu.add(restartOnLose);

		JCheckBoxMenuItem restartOnWin = new JCheckBoxMenuItem("Restart on Win");
		autoMenu.add(restartOnWin);

		JMenu restartDelay = new JMenu("Restart Delay");
		DelayPrompt restartDelayPrompt = new DelayPrompt(1000) {
			@Override
			public void valueChanged() {
				restartTimer.setInitialDelay(getValue());
			}
		};
		restartDelay.add(restartDelayPrompt);
		autoMenu.add(restartDelay);

		autoPlayTimer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game.isGameOver()) {
					autoPlayTimer.stop();
					restartTimer.start();
					return;
				}

				Tile move = Solver.doBestMove(game);
				if (move != null) { 
					Point pos = game.getBoard().getIndex(move);
					canvas.setCursor((int)pos.getX(), (int)pos.getY()); 
				}
				canvas.repaint();
			}
		});

		restartTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((!game.isGameWon() && !restartOnLose.isSelected()) || (game.isGameWon() && !restartOnWin.isSelected())) {
					autoPlay.setSelected(false);
					return;
				}

				game.newGame();
				canvas.resetCursor();
				canvas.repaint();

				autoPlayTimer.start();
			}
		});
		restartTimer.setRepeats(false);

		menuBar.add(autoMenu);
		
		
		JMenu settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic('S');
		JMenu themeMenu = new JMenu("Theme");
		themeMenu.setMnemonic('T');
		JMenu sizeMenu = new JMenu("Size");
		sizeMenu.setMnemonic('S');
		
		ButtonGroup themeBtnGroup = new ButtonGroup();
		for (int t = 0; t < ImageManager.THEME_NAMES.length; t++) {
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(ImageManager.THEME_NAMES[t]);
			
			btn.setToolTipText(ImageManager.THEME_IDS[t]);
			btn.setSelected(ImageManager.THEME_IDS[t].equals(windowTheme));
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String selected = btn.getToolTipText();
					if (selected.equals(ImageManager.theme)) return;
					ImageManager.load(selected);
					canvas.repaint();
				}
			});
			
			themeBtnGroup.add(btn);
			themeMenu.add(btn);
		}
		JCheckBoxMenuItem uniformBackground = new JCheckBoxMenuItem("Uniform Background", false);
		uniformBackground.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				canvas.repaint();
			}
		});
		themeMenu.addSeparator();
		themeMenu.add(uniformBackground);
		
		settingsMenu.add(themeMenu);
		
		ButtonGroup sizeBtnGroup = new ButtonGroup();
		for (int s : ImageManager.THEME_SIZES) {
			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(s + "x" + s);
			btn.setSelected(s == tileSize);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (s == ImageManager.size) return;
					ImageManager.load(s);

					flaggedLbl.setFont(new Font(flaggedLbl.getFont().getName(), Font.PLAIN, s - s / 4));
					timerLbl.setFont(new Font(timerLbl.getFont().getName(), Font.PLAIN, s - s / 4));

					resizeCanvas();
				}
			});
			
			sizeBtnGroup.add(btn);
			sizeMenu.add(btn);
		}
		settingsMenu.add(sizeMenu);


		String[] fontSizes = new String[ImageManager.THEME_SIZES.length];
		for (int i = 0; i < fontSizes.length; i++) {
			int s = ImageManager.THEME_SIZES[i];
			fontSizes[i] = s - s / 4 + "";
		}

		JMenu fontMenu = new JMenu("Font");
		fontMenu.setMnemonic('F');
		JFontChooser fontSelector = new JFontChooser(fontSizes) {
			@Override
			protected void updateSampleFont() {
				super.updateSampleFont();

				if (flaggedLbl == null || timerLbl == null) return;

				int s = ImageManager.size;
				Font font = getSelectedFont();
				flaggedLbl.setFont(new Font(font.getName(), font.getStyle(), s - s / 4));
				timerLbl.setFont(new Font(font.getName(), font.getStyle(), s - s / 4));
			}
		};
		fontMenu.add(fontSelector);
		settingsMenu.add(fontMenu);

		settingsMenu.addSeparator();
		
		JCheckBoxMenuItem topMostBtn = new JCheckBoxMenuItem("Always on top");
		topMostBtn.setMnemonic('A');
		topMostBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setAlwaysOnTop(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		settingsMenu.add(topMostBtn);

		JCheckBoxMenuItem useCursorBtn = new JCheckBoxMenuItem("Keyboard mode");
		useCursorBtn.setMnemonic('K');
		useCursorBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				canvas.enableCursor(e.getStateChange() == ItemEvent.SELECTED);
				canvas.repaint();
			}
		});
		settingsMenu.add(useCursorBtn);
		
		menuBar.add(settingsMenu);
		add(menuBar, "wrap");
		
		gameInfo = new JPanel(new MigLayout("insets 5 10 0 10, fill")) {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				Image image;
				if (game.isGameWon()) image = ImageManager.FLAG;
				else if (game.isGameOver()) image = ImageManager.BOMB_HIT;
				else if (gameTimer.isRunning()) image = ImageManager.NUMBERS[0];
				else image = ImageManager.HIDDEN;

				g.drawImage(image, getWidth() / 2 - ImageManager.size / 2, getHeight() / 2 - ImageManager.size / 2, this);
			}
		};
		gameInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int xMin = gameInfo.getWidth() / 2 - ImageManager.size / 2;
				int xMax = xMin + ImageManager.size;

				int yMin = gameInfo.getHeight() / 2 - ImageManager.size / 2;
				int yMax = yMin + ImageManager.size;

				if (e.getX() < xMin || xMax < e.getX()) return;
				if (e.getY() < yMin || yMax < e.getY()) return;

				game.newGame();
				canvas.resetCursor();
				canvas.repaint();
			}
		});
		
		flaggedLbl = new JLabel(new ImageIcon(ImageManager.FLAG));
		flaggedLbl.setText("0/10");
		flaggedLbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (game.getBoard().getAmountFlagged() != game.getBoard().getAmountOfBombs() || game.isGameOver()) return;
				
				game.revealAll();
				canvas.repaint();
			}
		});
		gameInfo.add(flaggedLbl);
		
		timerLbl = new JLabel("0:00");
		gameInfo.add(timerLbl, "wrap, right");
		add(gameInfo, "wrap");
		
		canvas = new Canvas(game) {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				int flagged = game.getBoard().getAmountFlagged();
				int total = game.getBoard().getAmountOfBombs();
				
				if (uniformBackground.isSelected()) {
					gameInfo.setBackground(new Color(ImageManager.bgColor));
					timerLbl.setForeground((ImageManager.bgColor > 8388607) ? Color.BLACK : Color.WHITE);
					flaggedLbl.setForeground(timerLbl.getForeground());
				} else {
					gameInfo.setBackground(Color.WHITE);
					timerLbl.setForeground(Color.BLACK);
					flaggedLbl.setForeground(Color.BLACK);
				}
				flaggedLbl.setIcon(new ImageIcon(ImageManager.FLAG));
				
				if (game.isGameWon()) flaggedLbl.setText(total + "/" + total);
				else flaggedLbl.setText(flagged + "/" + total);
				
				gameInfo.setPreferredSize(new Dimension(getWidth(), flaggedLbl.getHeight()));
				gameInfo.repaint();

				resizeCanvas();
			}
		};
		add(canvas, "wrap");
		resizeCanvas();

		addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
				if (!useCursorBtn.isSelected()) return;

                if (e.getKeyCode() == KeyEvent.VK_LEFT) canvas.moveCursor(-1, 0);
                if (e.getKeyCode() == KeyEvent.VK_UP) canvas.moveCursor(0, -1);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) canvas.moveCursor(1, 0);
                if (e.getKeyCode() == KeyEvent.VK_DOWN) canvas.moveCursor(0, 1);

				if (game.isGameOver()) return;

				if (e.getKeyCode() == KeyEvent.VK_SPACE) canvas.useCursor(0, true);
				if (e.getKeyCode() == KeyEvent.VK_F) canvas.useCursor(1, true);
            }

			@Override
			public void keyReleased(KeyEvent e) {
				if (!useCursorBtn.isSelected()) return;

				if (e.getKeyCode() == KeyEvent.VK_R) {
					game.newGame();
					canvas.resetCursor();
					canvas.repaint();
				}

				if (game.isGameOver()) return;

				if (e.getKeyCode() == KeyEvent.VK_SPACE) canvas.useCursor(0, false);
			}
        });

		getContentPane().setBackground(Color.WHITE);
		pack();
		setResizable(false);
		setTitle("Minesweeper");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void resizeCanvas() {
		remove(canvas);

		int minWidth = 9 * 16 + Canvas.MARGIN * 2;
		int minHeight = 9 * 16 + Canvas.MARGIN * 2;

		canvas.setPreferredSize(new Dimension(Math.max(game.getWidth()*ImageManager.size + Canvas.MARGIN * 2, minWidth), 
											  Math.max(game.getHeight()*ImageManager.size + Canvas.MARGIN * 2, minHeight)));
		
		add(canvas, "wrap");
		pack();
	}
	
	private String getTimeString() {
		String t = time / 60 + ":";
		
		int seconds = time % 60;
		if (seconds == 0) return t += "00";
		else if (seconds < 10) t += "0";
		
		return t + seconds;
	}
}
