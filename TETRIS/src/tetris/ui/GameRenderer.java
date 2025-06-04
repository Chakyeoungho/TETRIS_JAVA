package tetris.ui;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.GameConstants.FIELD_X_COUNT;
import static tetris.data.constants.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constants.GameConstants.TETROMINO_SIZE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tetris.data.constants.Tetromino;
import tetris.data.dto.DataManager;
import tetris.logic.TetrisEngine;

public class GameRenderer extends JFrame {
	private static final long serialVersionUID = 1L;

	private final DataManager gameData;
	private final TetrisEngine gameEngine;

	// ImageIcon 배열은 Image로 변환해서 사용
	private Image[] tetImages = new Image[8]; // 0~7: 테트로미노 이미지 (빈 블록 포함)
	private BufferedImage[] ghostImages; // 고스트 이미지 (투명도 적용)
	private Image[] currentImages = new Image[8]; // 현재 테트로미노 이미지 (필요하면 별도 처리)

	private ImagePanel imagePanel;

	private static final boolean DEBUG = false; // 디버그 출력 on/off

	public GameRenderer(DataManager gameData, TetrisEngine gameEngine) {
		super("TETRIS");
		this.gameData = gameData;
		this.gameEngine = gameEngine;

		ImageIcon[] iconImages = ImageLoader.getTetrominoImage();
		for (int i = 0; i < iconImages.length; i++) {
			if (iconImages[i] != null) {
				tetImages[i] = iconImages[i].getImage();
				currentImages[i] = iconImages[i].getImage();
			} else {
				System.err.println("이미지 로딩 실패: 인덱스 " + i);
			}
		}

		ghostImages = ImageLoader.getGhostImage();

		imagePanel = new ImagePanel();
		imagePanel.setDoubleBuffered(true);
		imagePanel.setFocusable(true);

		// 키 입력 처리
		InputHandler inputHandler = new InputHandler(gameData, gameEngine);
		imagePanel.addKeyListener(inputHandler);

		this.getContentPane().add(imagePanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(TETROMINO_SIZE * FIELD_X_COUNT + 200, TETROMINO_SIZE * FIELD_Y_COUNT + 40);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void refreshScreen() {
		imagePanel.repaint();
	}

	/**
	 * 좌표 배열에 오프셋을 적용한 새로운 리스트 반환
	 */
	private List<Point> translateCoords(Point[] coords, Point offset) {
		List<Point> translated = new ArrayList<>(coords.length);
		for (Point p : coords) {
			translated.add(new Point(p.x + offset.x, p.y + offset.y));
		}
		return translated;
	}

	/**
	 * 블록 리스트를 이미지 배열과 인덱스를 이용해 그리기
	 */
	private void drawBlocks(Graphics g, List<Point> blocks, Image[] images, int imageIndex) {
		if (imageIndex >= 7)
			return;
		for (Point p : blocks) {
			if (p.y >= BUFFER_ZONE && p.y < FIELD_Y_COUNT + BUFFER_ZONE) {
				g.drawImage(images[imageIndex], p.x * TETROMINO_SIZE, (p.y - BUFFER_ZONE) * TETROMINO_SIZE,
						TETROMINO_SIZE, TETROMINO_SIZE, this);
			}
		}
	}

	/**
	 * 게임 화면을 그리는 패널
	 */
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Tetromino current = gameData.getTetrominoState().getCurrentTetromino();
			if (current == null)
				return;

			int tetIndex = current.ordinal();
			if (tetIndex < 0 || tetIndex > tetImages.length)
				return;

			Point[] coords = gameData.getTetrominoState().getTetrominoCoords();

			Point ghostOffset = gameEngine.getTetrominoMover().getHardDropOffset();
			Point currentOffset = gameData.getTetrominoState().getTetrominoOffset();

			if (DEBUG) {
				System.out.println("===== Debug Info =====");
				System.out.println("Current Tetromino: " + current);
				System.out.println("Ghost Offset: " + ghostOffset);
				System.out.println("Current Offset: " + currentOffset);
				for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++)
					System.out.print(gameData.getPlayField().getRowBlockCount()[y]);
				System.out.println();
				for (Point p : coords) {
					System.out.printf("Block coord: (%d, %d)\n", p.x, p.y);
				}
				System.out.println("======================");
			}

			// 필드 배경 블록 그리기
			for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++) {
				for (int x = 0; x < FIELD_X_COUNT; x++) {
					int blockType = gameData.getCell(y, x);
					if (blockType >= 0 && blockType < tetImages.length) {
						g.drawImage(tetImages[blockType], x * TETROMINO_SIZE, (y - BUFFER_ZONE) * TETROMINO_SIZE,
								TETROMINO_SIZE, TETROMINO_SIZE, this);
					}
				}
			}

			// 고스트 블록 그리기
			List<Point> ghostBlocks = translateCoords(coords, ghostOffset);
			drawBlocks(g, ghostBlocks, ghostImages, tetIndex);

			// 현재 테트로미노 블록 그리기
			List<Point> currentBlocks = translateCoords(coords, currentOffset);
			drawBlocks(g, currentBlocks, currentImages, tetIndex);

			// TODO 포켓 미리 보여주기

			g.setFont(new Font("Consolas", Font.BOLD, 18)); // 글꼴 설정 (Bold, 크기 18)
			g.setColor(Color.BLACK); // 글자 색상 설정

			long score = gameEngine.getScoreManager().getScore();
			int level = gameEngine.getScoreManager().getLevel();
			Tetromino heloTetromino = gameEngine.getHoldHandler().getHeldTetromino();
			Tetromino[][] bag = gameData.getBagCopy();

			g.drawString("Score: " + score, 320, 20);
			g.drawString("Level: " + level, 320, 40);

			g.drawString("Hold: " + heloTetromino, 320, 60); // 출력
			for (int i = 0; i < 7; i++)
				if (bag[0][i] != null)
					g.drawString(bag[0][i].toString(), 320 + (i * 10), 80); // 출력
			for (int i = 0; i < 7; i++)
				if (bag[1][i] != null)
					g.drawString(bag[1][i].toString(), 320 + (i * 10), 100); // 출력

		}
	}
}
