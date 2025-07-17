import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardBuilder {
    public static void buildCheckerBoard(JPanel boardPanel, CirclePanel[][] board, CheckerGameLogic gameLogic) {
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

    }
}
