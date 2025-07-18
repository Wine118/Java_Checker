import javax.swing.*;
import java.awt.*;

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
    private static JFrame frame; // Move this outside launchGame()

    public static void launchGame(String player1, String player2) {
        SwingUtilities.invokeLater(() -> {
            player1Name = player1;
            player2Name = player2;
            currentPlayer = player1Name;//Start with player 1
            playerColor = "Black";

            CheckerGameLogic gameLogic = new CheckerGameLogic(board);//pass board
            frame = new JFrame("Checker Game");
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

            // Bottom Panel with BorderLayout to hold left and right sub-panels
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setPreferredSize(new Dimension(800,40));

            //----------------Left panel : Restart & Reset Buttons ---------------
            JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JButton restartButton = new JButton("Restart");
            restartButton.setFont(new Font("SansSerif",Font.PLAIN,14));
            restartButton.setFocusPainted(false);
            restartButton.addActionListener(e -> {
                BoardBuilder.resetBoard(board,gameLogic);

                resetGameToOriginalState(player1,player2);



            });

            JButton resetButton = new JButton("Reset");
            resetButton.setFont(new Font("SansSerif",Font.PLAIN,14));
            resetButton.setFocusPainted(false);
            resetButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(null,
                        "Do you want to reset and choose new player names?",
                                "Reset Game",
                                 JOptionPane.YES_NO_OPTION);

                if(result == JOptionPane.YES_OPTION){
                    System.out.println("Yes");

                    //Close current game window


                    //Receive new players names again
                    String[] newPlayers = new String[2];
                    String p1 = JOptionPane.showInputDialog(frame, "Enter name for Player1:","Player Setup",JOptionPane.PLAIN_MESSAGE);
                    if(p1 == null || p1.trim().isEmpty()) p1 = "Player 1";

                    String p2 = JOptionPane.showInputDialog(frame, "Enter name for Player2:","Player Setup",JOptionPane.PLAIN_MESSAGE);
                    if(p2 == null || p2.trim().isEmpty()) p2 = "Player 2";
                    frame.dispose();

                    newPlayers [0] = p1;
                    newPlayers [1] = p2;
                    launchGame(newPlayers[0], newPlayers[1]);
                }
            });

            JButton endAndCheckTheWinnerButton = new JButton("Check Winner To End");
            endAndCheckTheWinnerButton.setFont(new Font("SansSerif",Font.PLAIN,14));
            endAndCheckTheWinnerButton.setFocusPainted(false);
            endAndCheckTheWinnerButton.addActionListener(e -> {
                Color winner = gameLogic.checkWinner();
                disPlayingWinningState(winner);
                if(winner.equals(Color.BLACK) || winner.equals(Color.BLUE) || winner.equals(Color.CYAN)){
                    BoardBuilder.resetBoard(board,gameLogic);
                    resetGameToOriginalState(player1,player2);
                }
            });


            leftButtonsPanel.add(restartButton);
            leftButtonsPanel.add(resetButton);
            leftButtonsPanel.add(endAndCheckTheWinnerButton);

            //-------RIGHT Panel: Done Button ---------------------
            JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton doneButton = new JButton("Done");
            doneButton.setFocusPainted(false);
            doneButton.setFont(new Font("SansSerif",Font.BOLD, 14));
            doneButton.addActionListener(e -> {
                boolean forceEndTurn = gameLogic.getForceEndTurn();
                boolean turnAble = gameLogic.getTurnAble();
                Color winner = gameLogic.checkWinner();


                if(turnAble){
                    MakeDoneButtonAction(gameLogic);
                } else if (!forceEndTurn) {
                    JOptionPane.showMessageDialog(null,
                            "Switch Turn Please",
                            "Warning to swich turn",
                            JOptionPane.WARNING_MESSAGE);
                }else {
                    MakeDoneButtonAction(gameLogic);
                }

                if(winner.equals(Color.BLACK) || winner.equals(Color.BLUE) || winner.equals(Color.CYAN)){
                    BoardBuilder.resetBoard(board,gameLogic);
                    resetGameToOriginalState(player1,player2);
                }
            });

            rightButtonsPanel.add(doneButton);

            // Add both sub-panels to the bottomPanel
            bottomPanel.add(leftButtonsPanel, BorderLayout.WEST);
            bottomPanel.add(rightButtonsPanel,BorderLayout.EAST);


            JPanel boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(8, 8, 2, 2)); // spacing between cells
            boardPanel.setBackground(Color.DARK_GRAY); // background between cells

            BoardBuilder.buildCheckerBoard(boardPanel,board,gameLogic);



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

    private static void resetGameToOriginalState(String player1, String player2) {
        player1Name = player1;
        player2Name = player2;
        currentPlayer = player1Name;//Start with player 1
        playerColor = "Black";
        playerturnLabel.setText(currentPlayer + "'s Turn (" + playerColor + ")");
    }



    private static void disPlayingWinningState(Color winner) {

        if(winner != null){
            if(winner.equals(PLAYER1_COLOR)){
                JOptionPane.showMessageDialog(null,
                        player1Name+" (Black) "+" wins the game!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }else if (winner.equals(PLAYER2_COLOR)){
                JOptionPane.showMessageDialog(null,
                        player2Name+" (Blue) "+" wins the game!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (winner.equals(Color.CYAN)) {
                JOptionPane.showMessageDialog(null,
                        "It is a tie. ",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            } else{
                JOptionPane.showMessageDialog(null,
                        "Oops!!! Something wrongs with winner Checking process",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Winner can't be decided yet,\nDo you want to restart? Then press Restart button.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
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

    public static void toswitchTurn() {
        JOptionPane.showMessageDialog(null,
                "Switch Turn Please",
                "Warning to swich turn",
                JOptionPane.WARNING_MESSAGE);
    }

    public static Color getCurrentPlayerColor() {
        return currentPlayer.equals(player1Name) ? PLAYER1_COLOR : PLAYER2_COLOR;
    }

    public static void MakeDoneButtonAction(CheckerGameLogic gameLogic) {
        gameLogic.resetSelection();        // Clear selected piece
        gameLogic.clearForceEndTurn();     // Allow next player to move
        gameLogic.clearTurnAble();//switch off turnable feature

        Color winner = gameLogic.checkWinner();
        if(winner != null){
            disPlayingWinningState(winner);
        }else{
            switchTurn();                      // Now switch turn
        }
    }
}
