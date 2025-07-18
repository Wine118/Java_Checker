import javax.swing.*;
import java.awt.*;

public class CheckerGameLogic {
    private CirclePanel[][] board;
    private CirclePanel selectedPanel = null;
    private boolean forceEndTurn = false;
    private boolean turnAble = false;

    public CheckerGameLogic(CirclePanel[][] board) {
        this.board = board;
    }



    public void handleClick(CirclePanel clickedPanel) {
        if (forceEndTurn && selectedPanel != null) {//Cheating on others' turn
            SimpleChessBoard.toswitchTurn();
            selectedPanel = null;
            return;
        }

        if(selectedPanel==null && clickedPanel.getCircleColor()!=null){//First Click
            Color currentColor = SimpleChessBoard.getCurrentPlayerColor();
            if(currentColor.equals(clickedPanel.getCircleColor())){//Checking if picking the current player color
                selectedPanel = clickedPanel;

            }else{//if picking other player's color show warning and return
                showingPickingWrongPiece();
                return;
            }
        }else{//Second Click
            attemptMoveOrCapture(clickedPanel);

        }
    }



    public void resetSelection() {
        selectedPanel = null;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private CirclePanel findPanelByCoord(int row, int col) {
        if (isInBounds(row, col)) {
            return board[row][col];
        }
        return null;
    }

    public boolean canCaptureFrom(CirclePanel currentPanel){
        int row = currentPanel.getRow();
        int col = currentPanel.getCol();
        Color myColor = currentPanel.getCircleColor();
        boolean isKing = currentPanel.isKing();

        int[][] directions = isKing
                ? new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}
                : myColor.equals(Color.BLACK)
                ? new int[][]{{2, -2}, {2, 2}}
                : new int[][]{{-2, -2}, {-2, 2}};

        for(int[]dir: directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int midRow = row + dir[0] / 2;
            int midCol = col + dir[1] / 2;

            CirclePanel mid = findPanelByCoord(midRow, midCol);
            CirclePanel dest = findPanelByCoord(newRow, newCol);

            if (mid != null && dest != null // mid and dest cell can be found
                    && dest.getCircleColor() == null
                    && mid.getCircleColor() != null
                    && !mid.getCircleColor().equals(myColor)) {
                return true;
            }
        }
      return false;
    }

    public void clearForceEndTurn() {
        forceEndTurn = false;
    }

    public boolean getForceEndTurn() {
        return forceEndTurn;
    }

    public boolean getTurnAble() {
        return turnAble;
    }

    public void clearTurnAble() {
        turnAble = false;
    }

    public void showingPickingWrongPiece() {
        JOptionPane.showMessageDialog(null,
                "Not your piece! You can only move your own discs.",
                "Invalid Selection",
                JOptionPane.WARNING_MESSAGE);
    }



    public Color checkWinner() {
        int blackCount = 0;
        int blueCount = 0;
        boolean blackCanMove = false;
        boolean blueCanMove = false;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                CirclePanel panel = board[row][col];
                Color c = panel.getCircleColor();
                if (Color.BLACK.equals(c)) {
                    blackCount++;
                    if (!blackCanMove && hasAnyValidMove(panel)) {
                        blackCanMove = true;
                    }
                } else if (Color.BLUE.equals(c)) {
                    blueCount++;
                    if (!blueCanMove && hasAnyValidMove(panel)) {
                        blueCanMove = true;
                    }
                }
            }
        }
        if (blackCount == 0 || !blackCanMove) return Color.BLUE;
        if (blueCount == 0 || !blueCanMove) return Color.BLACK;

        if (blackCount == 0 && blueCount > 0) {
            return Color.BLUE;
        } else if (blueCount == 0 && blackCount > 0) {
            return Color.BLACK;
        } else if (blackCount <= 1 && blueCount >= 3) {
            return Color.BLUE;
        } else if (blueCount <= 1 && blackCount >= 3) {
            return Color.BLACK;
        }else if(blueCount == blackCount && blueCount <= 3 && blackCount <=3) {
            return Color.CYAN;
        }
        return null;
    }



    private void promoteToKing(CirclePanel panel) {
        Color color = panel.getCircleColor();
        if ((color.equals(Color.BLACK) && panel.getRow() == 7) ||
                (color.equals(Color.BLUE) && panel.getRow() == 0)) {
            panel.setKing(true);
        }
    }




    private void attemptMoveOrCapture(CirclePanel clickedPanel){
        int fromRow = selectedPanel.getRow();
        int fromCol = selectedPanel.getCol();
        int toRow = clickedPanel.getRow();
        int toCol = clickedPanel.getCol();
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        Color pieceColor = selectedPanel.getCircleColor();
        boolean isKing = selectedPanel.isKing();

        // Determine move direction for non-king
        int direction = pieceColor.equals(Color.BLACK) ? 1 : -1;

        // Normal forward move
        if (rowDiff == 1 && colDiff == 1 && (!isKing && (toRow - fromRow == direction) || isKing)&&
             clickedPanel.getCircleColor() == null ) {
            clickedPanel.setCircleColor(pieceColor);
            clickedPanel.setKing(isKing); // retain king status
            selectedPanel.setCircleColor(null);
            selectedPanel.setKing(false);
            selectedPanel = null;
            forceEndTurn = true;
            promoteToKing(clickedPanel);
            return;
        }

        // Attempt capture move
        if (rowDiff == 2 && colDiff == 2) {
            attemptCapture(fromRow, fromCol, toRow, toCol, clickedPanel);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Invalid move. You can only move diagonally.",
                    "Invalid Move",
                    JOptionPane.WARNING_MESSAGE);
            selectedPanel = null;
        }
    }

    private void attemptCapture(int fromRow, int fromCol, int toRow, int toCol, CirclePanel clickedPanel) {
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        CirclePanel mid = findPanelByCoord(midRow, midCol);
        CirclePanel dest = findPanelByCoord(toRow, toCol);
        Color pieceColor = selectedPanel.getCircleColor();

        if (mid != null && dest != null) {//Both middle cell and destination cell exist
            Color midColor = mid.getCircleColor();
            Color destColor = dest.getCircleColor();
            if (midColor != null && !midColor.equals(pieceColor)&& destColor==null) {
                // Perform capture
                clickedPanel.setCircleColor(pieceColor);
                clickedPanel.setKing(selectedPanel.isKing());
                selectedPanel.setCircleColor(null);
                selectedPanel.setKing(false);
                mid.setCircleColor(null);

                promoteToKing(clickedPanel);

                if (canCaptureFrom(clickedPanel)) {
                    selectedPanel = clickedPanel;
                    turnAble = true;
                } else {
                    selectedPanel = null;
                    forceEndTurn = true;
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid capture. No opponent piece to jump over.",
                        "Invalid Move",
                        JOptionPane.WARNING_MESSAGE);
                selectedPanel = null;
            }
        }
    }

    private boolean hasAnyValidMove(CirclePanel panel) {
        if (panel.getCircleColor() == null) return false;

        int row = panel.getRow();
        int col = panel.getCol();
        boolean isKing = panel.isKing();
        Color color = panel.getCircleColor();

        int[][] moveDirs = isKing
                ? new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}
                : color.equals(Color.BLACK)
                ? new int[][]{{1, -1}, {1, 1}}
                : new int[][]{{-1, -1}, {-1, 1}};

        int[][] captureDirs = isKing
                ? new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}
                : color.equals(Color.BLACK)
                ? new int[][]{{2, -2}, {2, 2}}
                : new int[][]{{-2, -2}, {-2, 2}};

        // Normal move check
        for (int[] d : moveDirs) {
            int newRow = row + d[0];
            int newCol = col + d[1];
            CirclePanel dest = findPanelByCoord(newRow, newCol);
            if (dest != null && dest.getCircleColor() == null) {
                return true;
            }
        }

        // Capture move check
        for (int[] d : captureDirs) {
            int midRow = row + d[0] / 2;
            int midCol = col + d[1] / 2;
            int newRow = row + d[0];
            int newCol = col + d[1];

            CirclePanel mid = findPanelByCoord(midRow, midCol);
            CirclePanel dest = findPanelByCoord(newRow, newCol);
            if (mid != null && dest != null &&
                    dest.getCircleColor() == null &&
                    mid.getCircleColor() != null &&
                    !mid.getCircleColor().equals(color)) {
                return true;
            }
        }

        return false;
    }



}


