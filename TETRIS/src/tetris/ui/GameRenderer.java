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

/**
 * 게임의 모든 그래픽 요소를 화면에 그리고, 창과 입력을 관리하는 메인 UI 클래스.
 * JFrame을 상속받아 게임 창의 역할을 함.
 */
public class GameRenderer extends JFrame {
	
    // --- Static Fields ---
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = false;
	private static final int FIELD_UI_PADDING = 200;

	// UI 컴포넌트 위치 상수
	private static final Point POCKET_POS = new Point(533, 50);
	private static final Point PREVIEW_POS = new Point(553, 120);
	private static final int PREVIEW_VERTICAL_SPACING = 50;
	private static final Point HOLD_POS = new Point(41, 50);

	// UI 배경 사각형 크기 상수
	private static final int HOLD_BG_WIDTH = 140;
	private static final int HOLD_BG_HEIGHT = 80;
	private static final int POCKET_BG_WIDTH = 140;
	private static final int POCKET_BG_HEIGHT = 80;
	private static final int PREVIEW_BG_WIDTH = 100;
	private static final int PREVIEW_BG_HEIGHT = PREVIEW_VERTICAL_SPACING * 5 + 10;
	
	// 좌측 정보 텍스트의 시작 X 좌표
	private static final int INFO_Y_POS = 20;

    // --- Instance Fields ---
	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	// 이미지 리소스
	private final Image[] tetImages;
	private final BufferedImage[] ghostImages;

	// UI 컴포넌트
	private final ImagePanel imagePanel;

    // --- Constructor ---
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

    // --- Public Methods ---
	/**
	 * 화면을 다시 그리도록 요청.
	 * Swing의 이벤트 스레드에 의해 안전하게 처리됨.
	 */
	public void refreshScreen() {
		imagePanel.repaint();
	}

    // --- Private Methods ---
	/** JFrame(게임 창)의 기본 속성을 설정하고 화면에 표시. */
	private void setUpFrame() {
		this.getContentPane().add(imagePanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(TETROMINO_SIZE * FIELD_X_COUNT + FIELD_UI_PADDING * 2, TETROMINO_SIZE * FIELD_Y_COUNT + 40);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/** 테트로미노 ImageIcon을 실제 그리기에 사용할 Image 객체로 변환. */
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

	/** 블록의 상대 좌표를 실제 필드 좌표로 변환하는 헬퍼 메서드. */
	private Point[] translateCoords(Point[] coords, Point offset) {
		Point[] translated = new Point[coords.length];
		for (int i = 0; i < coords.length; i++) {
			translated[i] = new Point(coords[i].x + offset.x, coords[i].y + offset.y);
		}
		return translated;
	}

	/** 게임 필드 내에 블록들을 그리는 범용 메서드 (버퍼 존은 제외). */
	private void drawBlocks(Graphics g, Point[] blocks, Image[] images, int imageIndex) {
		if (imageIndex >= images.length) return;
		for (Point p : blocks) {
			// 버퍼 존(y < 20)에 있는 블록은 화면에 그리지 않음
			if (p.y >= BUFFER_ZONE) {
				int drawX = p.x * TETROMINO_SIZE + FIELD_UI_PADDING;
				// 필드 y좌표를 화면 y좌표로 변환 (버퍼 존만큼 빼줌)
				int drawY = (p.y - BUFFER_ZONE) * TETROMINO_SIZE;
				g.drawImage(images[imageIndex], drawX, drawY, TETROMINO_SIZE, TETROMINO_SIZE, this);
			}
		}
	}

	/** UI 영역(Next, Hold 등)에 블록들을 그리는 범용 메서드 (화면 절대 좌표와 크기 지정). */
	private void drawBlocks(Graphics g, Point[] blocks, int offsetX, int offsetY, int size, Image[] images, int imageIndex) {
		if (imageIndex >= images.length) return;
		for (Point p : blocks) {
			g.drawImage(images[imageIndex], offsetX + p.x * size, offsetY + p.y * size, size, size, this);
		}
	}

	/** 필드에 쌓여있는 모든 블록(고정된 블록)을 그림. */
	private void drawFieldBlocks(Graphics g) {
		for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++) {
			for (int x = 0; x < FIELD_X_COUNT; x++) {
				int blockType = gameData.getCell(y, x); // TODO
				// 필드 데이터가 유효한 블록 타입일 때만 그림
				if (blockType >= 0 && blockType < tetImages.length) {
					int drawX = x * TETROMINO_SIZE + FIELD_UI_PADDING;
					int drawY = (y - BUFFER_ZONE) * TETROMINO_SIZE;
					g.drawImage(tetImages[blockType], drawX, drawY, TETROMINO_SIZE, TETROMINO_SIZE, this);
				}
			}
		}
	}

	/** 고스트 블록(블록이 떨어질 위치 미리보기)을 그림. */
	private void drawGhostBlock(Graphics g, Point[] coords, Point ghostOffset, int tetIndex) {
		Point[] ghostBlocks = translateCoords(coords, ghostOffset);
		drawBlocks(g, ghostBlocks, ghostImages, tetIndex);
	}

	/** 현재 조작 중인 블록을 그림. */
	private void drawCurrentBlock(Graphics g, Point[] coords, Point currentOffset, int tetIndex) {
		Point[] currentBlocks = translateCoords(coords, currentOffset);
		drawBlocks(g, currentBlocks, tetImages, tetIndex);
	}

	/** Next, Preview, Hold UI 영역의 배경을 그림. */
	private void drawUIBackgrounds(Graphics g) {
		Color pink = new Color(255, 182, 193);
		g.setColor(pink);
		g.fillRoundRect(HOLD_POS.x - 10, HOLD_POS.y - 10, HOLD_BG_WIDTH, HOLD_BG_HEIGHT, 20, 20);
		g.fillRoundRect(POCKET_POS.x - 10, POCKET_POS.y - 10, POCKET_BG_WIDTH, POCKET_BG_HEIGHT, 20, 20);
		g.fillRoundRect(PREVIEW_POS.x - 10, PREVIEW_POS.y + 40, PREVIEW_BG_WIDTH, PREVIEW_BG_HEIGHT, 20, 20);
	}

	/** '다음 블록'과 '미리보기 큐'의 블록들을 그림. */
	private void drawNextAndPreview(Graphics g) {
		Tetromino[] previewQueue = gameEngine.getBagPreviewQueue();
		if (previewQueue == null || previewQueue.length < 6) return;

		// '다음 블록' (Pocket) 그리기 (큰 사이즈)
		Tetromino next = previewQueue[0];
		drawBlocks(g, next.getMinos(), POCKET_POS.x, POCKET_POS.y, TETROMINO_SIZE, tetImages, next.ordinal());

		// '미리보기 큐' (5개 블록) 그리기 (작은 사이즈)
		for (int i = 1; i < 6; i++) {
			Tetromino preview = previewQueue[i];
			drawBlocks(g, preview.getMinos(), PREVIEW_POS.x, PREVIEW_POS.y + PREVIEW_VERTICAL_SPACING * i,
					TETROMINO_PREVIEW_SIZE, tetImages, preview.ordinal());
		}
	}

	/** 홀드된 블록을 그림. */
	private void drawHoldBlock(Graphics g) {
		Tetromino held = gameEngine.getHoldHandler().getHeldTetromino();
		// 홀드된 블록이 있을 때만 그림
		if (held != null) {
			drawBlocks(g, held.getMinos(), HOLD_POS.x, HOLD_POS.y, TETROMINO_SIZE, tetImages, held.ordinal());
		}
	}

	/** 점수, 레벨, 시간 등 모든 텍스트 정보를 그림. */
	private void drawGameInfo(Graphics g) {
		g.setFont(new Font("Consolas", Font.BOLD, 37));
		g.setColor(Color.BLACK);

		// PAUSED 상태일 때 메시지 표시
		if (gameData.getGameState().isPaused()) {
			g.drawString("-- PAUSE --", 254, 100);
		}

		// 상세 정보 폰트 설정
		g.setFont(new Font("Consolas", Font.BOLD, 18));
		
		// 필요한 정보들을 ScoreManager 등에서 가져옴
		long score = gameEngine.getScoreManager().getScore();
		long playTime = 0; // TODO: 플레이 시간 구현 필요
		int linesCleared = gameEngine.getScoreManager().getTotalClearedLine();
		int level = gameEngine.getScoreManager().getLevel();
		int goal = 10 - (linesCleared % 10);
		int tetrinCount = gameEngine.getScoreManager().getTetrisCount();
		int tSpinCount = gameEngine.getScoreManager().getTSpinCount();
		int comboCount = gameEngine.getScoreManager().getComboCount();
		ScoreAction lastAction = gameEngine.getScoreManager().getLastAction();
		boolean isB2B = gameEngine.getScoreManager().getIsB2B();

		// 각 정보를 화면에 그림
		g.drawString("SCORE:", INFO_Y_POS, 150);
		g.drawString(String.valueOf(score), 20, 175);
		g.drawString("TIME:", INFO_Y_POS, 200);
		g.drawString("testtesttest" + playTime, INFO_Y_POS, 225); // TODO
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
		
		// GAME_OVER 상태일 때 메시지 표시
		g.setFont(new Font("Consolas", Font.BOLD, 30));
		if (gameData.getGameState().getCurrentState() == GameStateCode.GAME_OVER)
			g.drawString("Press R to Restart Game", 150, 145);
	}

	/** 디버깅 정보를 콘솔에 출력 (DEBUG 플래그가 true일 때만). */
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

    // --- Inner Class ---
	/**
	 * 실제 모든 그래픽 요소를 그리는 작업을 담당하는 내부 패널 클래스.
	 * paintComponent 메서드 오버라이드를 통해 커스텀 그리기를 구현.
	 */
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			// --- 1. 그리기 위한 데이터 준비 ---
			Tetromino current = gameData.getTetrominoState().getCurrentTetromino();
			if (current == null) return;

			int tetIndex = current.ordinal();
			// 안전장치: 올바르지 않은 인덱스면 중단
			if (tetIndex >= tetImages.length) {
				System.err.println("Bag Index error!");
				return;
			}
			Point[] coords = gameData.getTetrominoState().getTetrominoCoords();
			Point ghostOffset = gameEngine.getTetrominoMover().getHardDropOffset();
			Point currentOffset = gameData.getTetrominoState().getTetrominoOffset();
			
			debugPrint(coords, current, ghostOffset, currentOffset);

			// --- 2. 그리기 순서 (레이어를 고려) ---
			// 배경 -> 쌓인 블록 -> 고스트 -> 현재 블록 -> UI 정보 순으로 그려야 올바르게 보임.
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