package tetris.ui;

import static tetris.data.constants.GameConstants.BUFFER_ZONE;
import static tetris.data.constants.GameConstants.FIELD_X_COUNT;
import static tetris.data.constants.GameConstants.FIELD_Y_COUNT;
import static tetris.data.constants.GameConstants.TETROMINO_SIZE;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tetris.data.constants.Tetromino;
import tetris.data.model.DataManager;

public class GameRenderer extends JFrame {
    private static final long serialVersionUID = 1L;

    // 테트로미노 종류별 이미지 배열 (0~7)
    private Image[] tetImage = new Image[8];
    private ImagePanel imagePanel;

    // 게임 상태 및 필드 데이터를 관리하는 클래스
    private final DataManager manager;

    public GameRenderer(DataManager manager) {
        super("TETRIS");

        this.manager = manager;
        
        // 이미지 로딩
        ImageIcon[] tetIcon = ImageLoader.getTetrominoImage();
        for (int i = 0; i < 8; i++) {
            if (tetIcon[i] != null) {
                tetImage[i] = tetIcon[i].getImage();
            } else {
                System.err.println("Failed to load image index: " + i + " -> " + Tetromino.fromOrdinal(i));
            }
        }

        Container c = this.getContentPane();
        imagePanel = new ImagePanel();
        imagePanel.setDoubleBuffered(true);
        c.add(imagePanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(TETROMINO_SIZE * FIELD_X_COUNT + 400, TETROMINO_SIZE * FIELD_Y_COUNT + 200);
        this.setVisible(true);

        // 키 입력 등 포커스를 바로 받도록 설정
        imagePanel.requestFocus();
    }

    // 외부에서 화면 갱신을 요청할 때 사용
    public void refreshScreen() {
        imagePanel.repaint();
    }

    // 게임 화면을 그리는 내부 패널 클래스
    class ImagePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int y = BUFFER_ZONE; y < FIELD_Y_COUNT + BUFFER_ZONE; y++) {
                for (int x = 0; x < FIELD_X_COUNT; x++) {
                    int adjustedY = y - BUFFER_ZONE;  // 실제 화면 표시용 y 좌표
                    int blockType = manager.getCell(y, x);
                    g.drawImage(
                        tetImage[blockType],
                        x * TETROMINO_SIZE,
                        adjustedY * TETROMINO_SIZE,
                        TETROMINO_SIZE,
                        TETROMINO_SIZE,
                        this
                    );
                }
            }
        }
    }
}
