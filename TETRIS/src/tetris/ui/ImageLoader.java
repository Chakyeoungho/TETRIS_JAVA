package tetris.ui;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

public class ImageLoader {

    // 일반 테트로미노 이미지 아이콘 (0~6: 블록, 7: 빈칸)
    private static final ImageIcon[] tetromino_image = new ImageIcon[8];

    // 고스트 이미지 (BufferedImage 형태, 투명도 적용됨, 0~6번만 유효)
    private static final BufferedImage[] ghost_image = new BufferedImage[7];

    // 게임 전체 아이콘 (타이틀, 창 아이콘 등)
    private static ImageIcon tetris_icon = new ImageIcon();

    static {
        // 테트로미노 이미지 로딩
        tetromino_image[0] = loadIcon("/Tetromino/MITet.png");
        tetromino_image[1] = loadIcon("/Tetromino/MLTet.png");
        tetromino_image[2] = loadIcon("/Tetromino/MJTet.png");
        tetromino_image[3] = loadIcon("/Tetromino/MOTet.png");
        tetromino_image[4] = loadIcon("/Tetromino/MSTet.png");
        tetromino_image[5] = loadIcon("/Tetromino/MTTet.png");
        tetromino_image[6] = loadIcon("/Tetromino/MZTet.png");
        tetromino_image[7] = loadIcon("/Tetromino/M_Tet.png"); // 빈 블록

        // 게임 아이콘 로딩
        tetris_icon = loadIcon("/tetris.ico");

        // 고스트 이미지 생성 (투명도 적용)
        for (int i = 0; i < 7; i++) {
            ghost_image[i] = applyTransparency(tetromino_image[i], 0.3f); // 30% 투명도
        }
    }

    /**
     * 지정된 경로의 이미지 아이콘을 로드한다.
     * 
     * @param path 리소스 경로 (예: "/Tetromino/MITet.png")
     * @return ImageIcon 객체, 실패 시 null 반환
     */
    private static ImageIcon loadIcon(String path) {
        java.net.URL imgURL = ImageLoader.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("이미지 로딩 실패: " + path);
            return null;
        }
    }

    /**
     * ImageIcon을 투명도 적용된 BufferedImage로 변환한다.
     * 
     * @param icon 원본 이미지 아이콘
     * @param alpha 0.0f ~ 1.0f 사이 투명도 값 (예: 0.3f = 30% 불투명)
     * @return 투명도가 적용된 BufferedImage
     */
    private static BufferedImage applyTransparency(ImageIcon icon, float alpha) {
        Image image = icon.getImage();

        // 원본 ImageIcon을 BufferedImage로 변환
        BufferedImage base = new BufferedImage(
            image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = base.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        // 투명도 적용
        BufferedImage transparent = new BufferedImage(
            base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = transparent.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(base, 0, 0, null);
        g.dispose();

        return transparent;
    }

    /** 테트로미노 이미지 아이콘 배열을 반환한다. */
    public static ImageIcon[] getTetrominoImage() {
        return tetromino_image;
    }

    /** 고스트용 BufferedImage 배열을 반환한다. */
    public static BufferedImage[] getGhostImage() {
        return ghost_image;
    }

    /** 게임 아이콘 반환 */
    public static ImageIcon getTetrisIcon() {
        return tetris_icon;
    }
}
