import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleChessBoard {
    private static CirclePanel selectedPanel = null;
    private static final int BOARD_SIZE = 8;
    private static CirclePanel[][] board = new CirclePanel[BOARD_SIZE][BOARD_SIZE];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckerGameLogic gameLogic = new CheckerGameLogic(board);//pass board
            JFrame frame = new JFrame("Street Chess - Test Board");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JPanel boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(8, 8, 2, 2)); // spacing between cells
            boardPanel.setBackground(Color.DARK_GRAY); // background between cells

            // Fill 8x8 board with black circles and blue discs
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    // Checkerboard square background color
                    Color squareColor = ((row + col) % 2 == 0) ? Color.WHITE : new Color(181, 136, 99);


                    // Checker piece color (only on dark squares)
                    Color circleColor = null;
                    if ((row + col) % 2 != 0) { // only dark squares
                        if (row < 3) {
                            circleColor = Color.BLACK;
                        } else if (row > 4) {
                            circleColor = Color.BLUE;
                        }
                    }

                    final CirclePanel piece = new CirclePanel(circleColor, row, col);
                    board[row][col] = piece; //Store in board

                    piece.setBackground(squareColor); //set cell background color
                    piece.setOpaque(true);
                    piece.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                    //Add moouse listener her
                    piece.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            gameLogic.handleCellClick(piece);
                        }
                    });
                    boardPanel.add(piece);
                }
            }

            frame.add(boardPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.revalidate();
            frame.repaint();

        });
    }
}
