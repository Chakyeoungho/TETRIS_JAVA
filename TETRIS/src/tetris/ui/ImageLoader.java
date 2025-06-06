package tetris.ui;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

public class ImageLoader {

	// 테트로미노 이미지 아이콘 (0~6: 블록, 7: 빈칸)
	private static final ImageIcon[] TETROMINO_IMAGES = new ImageIcon[8];

	// 고스트 이미지 (0~6번 블록만 해당, 투명도 적용된 BufferedImage)
	private static final BufferedImage[] GHOST_IMAGES = new BufferedImage[7];

	// 게임 아이콘 (타이틀, 창 아이콘 등)
	private static ImageIcon TETRIS_ICON;

	static {
		loadTetrominoImages();
		loadGameIcon();
		createGhostImages();
	}

	private static void loadTetrominoImages() {
		TETROMINO_IMAGES[0] = loadIcon("/tetromino/MITet.png");
		TETROMINO_IMAGES[1] = loadIcon("/tetromino/MLTet.png");
		TETROMINO_IMAGES[2] = loadIcon("/tetromino/MJTet.png");
		TETROMINO_IMAGES[3] = loadIcon("/tetromino/MOTet.png");
		TETROMINO_IMAGES[4] = loadIcon("/tetromino/MSTet.png");
		TETROMINO_IMAGES[5] = loadIcon("/tetromino/MTTet.png");
		TETROMINO_IMAGES[6] = loadIcon("/tetromino/MZTet.png");
		TETROMINO_IMAGES[7] = loadIcon("/tetromino/M_Tet.png"); // 빈 칸
	}

	private static void loadGameIcon() {
		TETRIS_ICON = loadIcon("/tetris.ico");
	}

	private static void createGhostImages() {
		for (int i = 0; i < GHOST_IMAGES.length; i++) {
			ImageIcon icon = TETROMINO_IMAGES[i];
			if (icon != null) {
				GHOST_IMAGES[i] = applyTransparency(icon.getImage(), 0.3f);
			}
		}
	}

	/**
	 * 리소스 경로에서 ImageIcon을 로드한다. 실패하면 null 반환.
	 */
	private static ImageIcon loadIcon(String path) {
		java.net.URL imgURL = ImageLoader.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}
		System.err.println("이미지 로딩 실패: " + path);
		return null;
	}

	/**
	 * 주어진 Image에 투명도(alpha)를 적용하여 BufferedImage로 반환.
	 * 
	 * @param image 원본 이미지
	 * @param alpha 0.0f ~ 1.0f 투명도 (0 완전투명, 1 완전불투명)
	 * @return 투명도가 적용된 BufferedImage
	 */
	private static BufferedImage applyTransparency(Image image, float alpha) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		BufferedImage baseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = baseImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = transparentImage.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.drawImage(baseImage, 0, 0, null);
		g.dispose();

		return transparentImage;
	}

	/** 테트로미노 이미지 아이콘 배열 반환 */
	public static ImageIcon[] getTetrominoImages() {
		return TETROMINO_IMAGES.clone();
	}

	/** 고스트 이미지 배열 반환 */
	public static BufferedImage[] getGhostImages() {
		return GHOST_IMAGES.clone();
	}

	/** 게임 아이콘 반환 */
	public static ImageIcon getTetrisIcon() {
		return TETRIS_ICON;
	}
}
