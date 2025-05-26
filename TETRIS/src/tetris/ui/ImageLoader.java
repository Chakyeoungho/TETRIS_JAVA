package tetris.ui;

import javax.swing.ImageIcon;

public class ImageLoader {
    private final static ImageIcon[] tetromino_image = new ImageIcon[8];
    private static ImageIcon tetris_icon = new ImageIcon();

    static {
        tetromino_image[0] = loadIcon("/Tetromino/MITet.png");
        tetromino_image[1] = loadIcon("/Tetromino/MLTet.png");
        tetromino_image[2] = loadIcon("/Tetromino/MJTet.png");
        tetromino_image[3] = loadIcon("/Tetromino/MOTet.png");
        tetromino_image[4] = loadIcon("/Tetromino/MSTet.png");
        tetromino_image[5] = loadIcon("/Tetromino/MTTet.png");
        tetromino_image[6] = loadIcon("/Tetromino/MZTet.png");
        tetromino_image[7] = loadIcon("/Tetromino/M_Tet.png");
        
        tetris_icon = loadIcon("/tetris.ico");
    }

    private static ImageIcon loadIcon(String path) {
        java.net.URL imgURL = ImageLoader.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("이미지 로딩 실패: " + path);
            return null;
        }
    }

    public static ImageIcon[] getTetrominoImage() {
        return tetromino_image;
    }
    
    public static ImageIcon getTetrisIcon() {
    	return tetris_icon;
    }
}
