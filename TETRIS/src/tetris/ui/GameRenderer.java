package tetris.ui;

import static tetris.data.constant.GameConstants.BUFFER_ZONE;
import static tetris.data.constant.GameConstants.FIELD_X_COUNT;
import static tetris.data.constant.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constant.GameConstants.TETROMINO_PREVIEW_SIZE;
import static tetris.data.constant.GameConstants.TETROMINO_SIZE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tetris.data.constant.GameConstants.GameStateCode;
import tetris.data.constant.Tetromino;
import tetris.logic.TetrisEngine;
import tetris.logic.data.DataManager;
import tetris.logic.scoring.ScoreAction;

public class GameRenderer extends JFrame {
	private static final long serialVersionUID = 1L;

	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	private final Image[] tetImages;
	private final BufferedImage[] ghostImages;

	private final ImagePanel imagePanel;

	private static final boolean DEBUG = false;
	private static final int FIELD_UI_PADDING = 200;

	// UI 위치 상수
	private static final Point POCKET_POS = new Point(533, 50);
	private static final Point PREVIEW_POS = new Point(553, 120);
	private static final int PREVIEW_VERTICAL_SPACING = 50;
	private static final Point HOLD_POS = new Point(41, 50);

	// UI 배경 사각형 크기
	private static final int HOLD_BG_WIDTH = 140;
	private static final int HOLD_BG_HEIGHT = 80;
	private static final int POCKET_BG_WIDTH = 140;
	private static final int POCKET_BG_HEIGHT = 80;
	private static final int PREVIEW_BG_WIDTH = 100;
	private static final int PREVIEW_BG_HEIGHT = PREVIEW_VERTICAL_SPACING * 5 + 10;
	
	// UI 정보 좌표
	private static final int INFO_Y_POS = 20;

	public GameRenderer(DataManager gameData, TetrisEngine gameEngine) {
		super("TETRIS");

		this.gameData = gameData;
		this.gameEngine = gameEngine;

		this.tetImages = loadTetrominoImages();
		this.ghostImages = ImageLoader.getGhostImages();

		this.imagePanel = new ImagePanel();
		this.imagePanel.setDoubleBuffered(true);
		this.imagePanel.setFocusable(true);
		this.imagePanel.addKeyListener(new InputHandler(gameEngine));

		setUpFrame();
	}

	private void setUpFrame() {
		this.getContentPane().add(imagePanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(TETROMINO_SIZE * FIELD_X_COUNT + FIELD_UI_PADDING * 2, TETROMINO_SIZE * FIELD_Y_COUNT + 40);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private Image[] loadTetrominoImages() {
		ImageIcon[] icons = ImageLoader.getTetrominoImages();
		Image[] images = new Image[icons.length];
		for (int i = 0; i < icons.length; i++) {
			if (icons[i] != null) {
				images[i] = icons[i].getImage();
			} else {
				System.err.println("이미지 로딩 실패: 인덱스 " + i);
			}
		}
		return images;
	}

	public void refreshScreen() {
		imagePanel.repaint();
	}

	private Point[] translateCoords(Point[] coords, Point offset) {
		Point[] translated = new Point[coords.length];
		for (int i = 0; i < coords.length; i++) {
			translated[i] = new Point(coords[i].x + offset.x, coords[i].y + offset.y);
		}
		return translated;
	}

	private void drawBlocks(Graphics g, Point[] blocks, Image[] images, int imageIndex) {
		if (imageIndex >= images.length)
			return;
		for (Point p : blocks) {
			if (p.y >= BUFFER_ZONE && p.y < FIELD_Y_COUNT + BUFFER_ZONE) {
				int drawX = p.x * TETROMINO_SIZE + FIELD_UI_PADDING;
				int drawY = (p.y - BUFFER_ZONE) * TETROMINO_SIZE;
				g.drawImage(images[imageIndex], drawX, drawY, TETROMINO_SIZE, TETROMINO_SIZE, this);
			}
		}
	}

	private void drawBlocks(Graphics g, Point[] blocks, int offsetX, int offsetY, int size, Image[] images,
			int imageIndex) {
		if (imageIndex >= images.length)
			return;
		for (Point p : blocks) {
			g.drawImage(images[imageIndex], offsetX + p.x * size, offsetY + p.y * size, size, size, this);
		}
	}

	private void drawFieldBlocks(Graphics g) {
		for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++) {
			for (int x = 0; x < FIELD_X_COUNT; x++) {
				int blockType = gameData.getCell(y, x);
				if (blockType >= 0 && blockType < tetImages.length) {
					int drawX = x * TETROMINO_SIZE + FIELD_UI_PADDING;
					int drawY = (y - BUFFER_ZONE) * TETROMINO_SIZE;
					g.drawImage(tetImages[blockType], drawX, drawY, TETROMINO_SIZE, TETROMINO_SIZE, this);
				}
			}
		}
	}

	private void drawGhostBlock(Graphics g, Point[] coords, Point ghostOffset, int tetIndex) {
		Point[] ghostBlocks = translateCoords(coords, ghostOffset);
		drawBlocks(g, ghostBlocks, ghostImages, tetIndex);
	}

	private void drawCurrentBlock(Graphics g, Point[] coords, Point currentOffset, int tetIndex) {
		Point[] currentBlocks = translateCoords(coords, currentOffset);
		drawBlocks(g, currentBlocks, tetImages, tetIndex);
	}

	private void drawUIBackgrounds(Graphics g) {
		Color pink = new Color(255, 182, 193);
		g.setColor(pink);
		g.fillRoundRect(HOLD_POS.x - 10, HOLD_POS.y - 10, HOLD_BG_WIDTH, HOLD_BG_HEIGHT, 20, 20);
		g.fillRoundRect(POCKET_POS.x - 10, POCKET_POS.y - 10, POCKET_BG_WIDTH, POCKET_BG_HEIGHT, 20, 20);
		g.fillRoundRect(PREVIEW_POS.x - 10, PREVIEW_POS.y + 40, PREVIEW_BG_WIDTH, PREVIEW_BG_HEIGHT, 20, 20);
	}

	private void drawNextAndPreview(Graphics g) {
		Tetromino[] previewQueue = gameEngine.getBagPreviewQueue();
		if (previewQueue == null || previewQueue.length < 6)
			return;

		// Next block (Pocket)
		Tetromino next = previewQueue[0];
		drawBlocks(g, next.getBlocks(), POCKET_POS.x, POCKET_POS.y, TETROMINO_SIZE, tetImages, next.ordinal());

		// Preview blocks (5 blocks)
		for (int i = 1; i < 6; i++) {
			Tetromino preview = previewQueue[i];
			drawBlocks(g, preview.getBlocks(), PREVIEW_POS.x, PREVIEW_POS.y + PREVIEW_VERTICAL_SPACING * i,
					TETROMINO_PREVIEW_SIZE, tetImages, preview.ordinal());
		}
	}

	private void drawHoldBlock(Graphics g) {
		Tetromino held = gameEngine.getHoldHandler().getHeldTetromino();
		if (held != null) {
			drawBlocks(g, held.getBlocks(), HOLD_POS.x, HOLD_POS.y, TETROMINO_SIZE, tetImages, held.ordinal());
		}
	}

	private void drawGameInfo(Graphics g) {
		g.setFont(new Font("Consolas", Font.BOLD, 37));
		g.setColor(Color.BLACK);

		if (gameData.getGameState().isPaused()) {
			g.drawString("-- PAUSE --", 254, 100);
		}

		g.setFont(new Font("Consolas", Font.BOLD, 18));

		long score = gameEngine.getScoreManager().getScore();
		long playTime = 0;
		int linesCleared = gameEngine.getScoreManager().getTotalClearedLine();
		int level = gameEngine.getScoreManager().getLevel();
		int goal = 10 - (linesCleared % 10);
		int tetrinCount = gameEngine.getScoreManager().getTetrisCount();
		int tSpinCount = gameEngine.getScoreManager().getTSpinCount();
		int comboCount = gameEngine.getScoreManager().getComboCount();
		ScoreAction lastAction = gameEngine.getScoreManager().getLastAction();
		boolean isB2B = gameEngine.getScoreManager().getIsB2B();

		g.drawString("SCORE:", INFO_Y_POS, 150);
		g.drawString(String.valueOf(score), 20, 175);
		g.drawString("TIME:", INFO_Y_POS, 200);
		g.drawString("123123test" + playTime, INFO_Y_POS, 225);
		g.drawString("LINES:      " + linesCleared, INFO_Y_POS, 260);
		g.drawString("LEVEL:      " + level, INFO_Y_POS, 280);
		g.drawString("GOAL:       " + goal, INFO_Y_POS, 300);
		g.drawString("TETRISES:   " + tetrinCount, INFO_Y_POS, 340);
		g.drawString("T-SPINS:    " + tSpinCount, INFO_Y_POS, 360);
		g.drawString("COMBOS:     " + comboCount, INFO_Y_POS, 380);
		g.drawString("LastAction: ", INFO_Y_POS, 420);
		g.drawString("" + lastAction, INFO_Y_POS, 440);
		g.drawString("B2B Combo: " + isB2B, INFO_Y_POS, 465);
		
		g.drawString("GAMESTATE: ", INFO_Y_POS - 10, 520);
		g.drawString("" + gameData.getGameState().getCurrentState(), 20, 545);
		
		g.setFont(new Font("Consolas", Font.BOLD, 30));
		if (gameData.getGameState().getCurrentState() == GameStateCode.GAME_OVER)
			g.drawString("Press R to Restart Game", 150, 145);
	}

	private void debugPrint(Point[] coords, Tetromino current, Point ghostOffset, Point currentOffset) {
		if (!DEBUG) return;
		
		System.out.println("===== Debug Info =====");
		System.out.println("Current Tetromino: " + current);
		System.out.println("Ghost Offset: " + ghostOffset);
		System.out.println("Current Offset: " + currentOffset);

		for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++) {
			System.out.print(gameData.getPlayField().getRowBlockCount()[y]);
		}
		System.out.println();

		for (Point p : coords) {
			System.out.printf("Block coord: (%d, %d)%n", p.x, p.y);
		}
		System.out.println("======================");
	}

	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Tetromino current = gameData.getTetrominoState().getCurrentTetromino();
			if (current == null)
				return;

			int tetIndex = current.ordinal();
			if (tetIndex >= tetImages.length) {
				System.err.println("Bag Index error!");
				return;
			}

			Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
			Point ghostOffset = gameEngine.getTetrominoMover().getHardDropOffset();
			Point currentOffset = gameData.getTetrominoState().getTetrominoOffset();

			debugPrint(coords, current, ghostOffset, currentOffset);

			drawFieldBlocks(g);
			drawGhostBlock(g, coords, ghostOffset, tetIndex);
			drawCurrentBlock(g, coords, currentOffset, tetIndex);

			drawUIBackgrounds(g);
			drawNextAndPreview(g);
			drawHoldBlock(g);
			drawGameInfo(g);
		}
	}
}
