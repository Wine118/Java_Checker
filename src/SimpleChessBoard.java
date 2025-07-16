import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleChessBoard {
    private static String currentPlayer;
    private static String player1Name;
    private static String player2Name;
    private static String playerColor;
    private static JLabel playerturnLabel;
    private static final Color PLAYER1_COLOR = Color.BLACK;
    private static final Color PLAYER2_COLOR = Color.BLUE;
    private static CirclePanel selectedPanel = null;
    private static final int BOARD_SIZE = 8;
    private static CirclePanel[][] board = new CirclePanel[BOARD_SIZE][BOARD_SIZE];

    public static void launchGame(String player1, String player2) {
        SwingUtilities.invokeLater(() -> {
            player1Name = player1;
            player2Name = player2;
            currentPlayer = player1Name;//Start with player 1
            playerColor = "Black";

            CheckerGameLogic gameLogic = new CheckerGameLogic(board);//pass board
            JFrame frame = new JFrame("Checker Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 650);

            //Title Panel
            JLabel titleLabel = new JLabel("Welcome to Checker Game",SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD,24));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10,0,5,0));

            //Player Board Panel
            JLabel playerLabel = new JLabel(player1+" (Black) : "+player2+" (Blue)",SwingConstants.CENTER);
            playerLabel.setFont(new Font("Serif", Font.BOLD,22));
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

            //Player Turn Label
            playerturnLabel = new JLabel(currentPlayer+"'s Turn"+" ("+playerColor+")",SwingConstants.CENTER);
            playerturnLabel.setFont(new Font("Serif",Font.ITALIC,20));
            playerturnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerturnLabel.setBorder(BorderFactory.createEmptyBorder(5,0,10,0));

            //Create a vertical panel (BoxLayout)
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
            topPanel.add(titleLabel);
            topPanel.add(playerLabel);
            topPanel.add(playerturnLabel);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setPreferredSize(new Dimension(800,40));


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



            //Container Panel using BorderLayout
            JPanel container = new JPanel(new BorderLayout());
            container.add(topPanel,BorderLayout.NORTH);
            container.add(boardPanel,BorderLayout.CENTER);
            container.add(bottomPanel,BorderLayout.SOUTH);

            frame.add(container);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.revalidate();
            frame.repaint();

        });
    }

    public static void switchTurn() {
        if(currentPlayer.equals(player1Name)){
            currentPlayer = player2Name;
            playerColor = "Blue";
        }else {
            currentPlayer = player1Name;
            playerColor = "Black";
        }
        playerturnLabel.setText(currentPlayer+"'s Turn"+" ("+playerColor+")");
    }

    public static Color getCurrentPlayerColor() {
        return currentPlayer.equals(player1Name) ? PLAYER1_COLOR : PLAYER2_COLOR;
    }
}
