package tetris.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * 게임에 필요한 모든 이미지 리소스(블록, 아이콘 등)를 로드하고 관리하는 유틸리티 클래스.
 * 모든 멤버가 static으로, 프로그램 시작 시 한 번만 로드하여 어디서든 접근 가능.
 */
public class ImageLoader {
	
    // --- Static Fields ---
	// 테트로미노 이미지 아이콘 (0~6: 블록, 7: 빈칸)
	private static final ImageIcon[] TETROMINO_IMAGES = new ImageIcon[8];
	// 고스트 이미지 (0~6번 블록만 해당, 투명도 적용된 BufferedImage)
	private static final BufferedImage[] GHOST_IMAGES = new BufferedImage[7];
	// 게임 아이콘 (타이틀, 창 아이콘 등)
	private static ImageIcon TETRIS_ICON;

	// static 초기화 블록: 클래스가 메모리에 로드될 때 단 한 번 실행됨.
	static {
		loadTetrominoImages();
		loadGameIcon();
		createGhostImages();
	}

    // --- Public Static Methods ---
	/** 로드된 테트로미노 ImageIcon 배열을 반환 (방어적 복사). */
	public static ImageIcon[] getTetrominoImages() {
		return TETROMINO_IMAGES.clone();
	}

	/** 생성된 고스트 블록 BufferedImage 배열을 반환 (방어적 복사). */
	public static BufferedImage[] getGhostImages() {
		return GHOST_IMAGES.clone();
	}

	/** 로드된 게임 아이콘을 반환. */
	public static ImageIcon getTetrisIcon() {
		return TETRIS_ICON;
	}
    
    // --- Private Static Methods ---
	/** 리소스 폴더에서 테트로미노 이미지들을 로드. */
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

	/** 리소스 폴더에서 게임 아이콘 이미지를 로드. */
	private static void loadGameIcon() {
		TETRIS_ICON = loadIcon("/tetris.ico");
	}

	/** 로드된 테트로미노 이미지에 투명도를 적용하여 고스트 이미지를 생성. */
	private static void createGhostImages() {
		for (int i = 0; i < GHOST_IMAGES.length; i++) {
			ImageIcon icon = TETROMINO_IMAGES[i];
			if (icon != null) {
				GHOST_IMAGES[i] = applyTransparency(icon.getImage(), 0.3f);
			}
		}
	}

	/**
	 * 지정된 경로에서 ImageIcon을 로드. 실패 시 null 반환.
	 * @param path 리소스 루트부터의 경로 (e.g., "/images/icon.png")
	 * @return 로드된 ImageIcon 또는 null
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
	 * 주어진 Image에 투명도(alpha)를 적용하여 새로운 BufferedImage로 반환.
	 * @param image 원본 이미지
	 * @param alpha 0.0f(완전투명) ~ 1.0f(완전불투명) 사이의 투명도
	 * @return 투명도가 적용된 BufferedImage
	 */
	private static BufferedImage applyTransparency(Image image, float alpha) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		// 원본 Image를 BufferedImage로 변환 (픽셀 제어를 위해)
		BufferedImage baseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = baseImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// 투명도를 적용할 새 BufferedImage 생성
		BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = transparentImage.createGraphics();
		// AlphaComposite를 사용하여 투명도 설정
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		// 원본 이미지를 투명도를 적용하여 그림
		g.drawImage(baseImage, 0, 0, null);
		g.dispose();

		return transparentImage;
	}
}