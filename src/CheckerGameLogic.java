import javax.swing.*;
import java.awt.*;

public class CheckerGameLogic {
    private CirclePanel selectedPanel = null;
    private final CirclePanel[][] board;
    private boolean forceEndTurn = false;


    public CheckerGameLogic(CirclePanel[][] board) {
        this.board = board;
    }

    public void handleCellClick(CirclePanel clickedPanel){
        Color currentTurnColor = SimpleChessBoard.getCurrentPlayerColor();
        // If a regular move was made and player clicks anything else, switch turn
        if (forceEndTurn && selectedPanel != null) {
            SimpleChessBoard.toswitchTurn();
            selectedPanel = null;
            return;
        }

        if(selectedPanel == null){// Checking first click
            if (forceEndTurn) {//if continue to play while should switch turn
                SimpleChessBoard.toswitchTurn();
                return;
            } else {
                //First click - select a piece that matches current player's color
                Color pieceColor = clickedPanel.getCircleColor();
                if(pieceColor != null && pieceColor.equals(currentTurnColor)){
                    selectedPanel = clickedPanel;
                    System.out.println("Selected piece at (" + clickedPanel.getRow() + "," + clickedPanel.getCol() + ")");
                } else if (pieceColor!=null) { // picking opponent's piece
                    showingPickingWrongPiece();
                    selectedPanel = null;
                } else {
                    System.out.println("No piece to select. ");
                    selectedPanel = null;
                }
            }

        } else {
            //Second click - attempt to move
            if(clickedPanel != selectedPanel && clickedPanel.getCircleColor() == null){ //not reselecting current piece and null place
                int fromRow = selectedPanel.getRow();
                int fromCol = selectedPanel.getCol();
                int toRow = clickedPanel.getRow();
                int toCol = clickedPanel.getCol();

                int rowDiff = Math.abs(toRow - fromRow);
                int colDiff = Math.abs(toCol - fromCol);

                int rowDir = toRow - fromRow;
                Color pieceColor = selectedPanel.getCircleColor();


                //Check for valid capture move

                if(rowDiff == 2 && colDiff == 2){
                    if ((pieceColor.equals(Color.BLACK) && rowDir == 2) ||
                            (pieceColor.equals(Color.BLUE) && rowDir == -2)) {
                        int midRow = (fromRow + toRow) / 2;
                        int midCol = (fromCol + toCol) /2;
                        CirclePanel mid = findPanelByCoord(midRow,midCol); //search for mid  cell
                        if(mid != null) { //if mid cell is not null
                            Color midColor = mid.getCircleColor();
                            if(midColor!=null && !midColor.equals(pieceColor)){ //Check mid cell is not empty and midcell do not equal the current color

                                mid.setCircleColor(null); //Remove component
                                System.out.println("Captured opponent at (" + midRow + "," + midCol + ")");
                                clickedPanel.setCircleColor(pieceColor);
                                selectedPanel.setCircleColor(null);
                                System.out.println("Moved from (" + fromRow + "," + fromCol +
                                        ") to (" + toRow + "," + toCol + ") Ater capturing the opponent");


                                // Check for possible additional jumps
                                if (canCaptureFrom(clickedPanel)) {
                                    selectedPanel = null;
                                    return;                                   // wait for next capture
                                } else {
                                    selectedPanel = null;
                                    forceEndTurn = true;
                                    return;
                                }



                            }else{
                                JOptionPane.showMessageDialog(null,"Sorry, You are Jumping or trying to eat yourself","Invalid Selection",JOptionPane.WARNING_MESSAGE);
                                // ✅ Reset the selection so the player can try again
                                selectedPanel = null;
                                return;
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,"Invalid move. Only one step moves allowed","Invalid Selection",JOptionPane.WARNING_MESSAGE);
                            // ✅ Reset the selection so the player can try again
                            selectedPanel = null;
                            return;
                        }
                    }//Checking for 2 cell jumping
                    else{
                        JOptionPane.showMessageDialog(null,
                                "Invalid destination. Backwarding",
                                "Invalid Input",
                                JOptionPane.WARNING_MESSAGE);
                        selectedPanel = null;
                        return;
                    }


                }//Check for regular move
                else if (rowDiff == 1 && colDiff == 1) {//Checking for one cell moving
                    if ((pieceColor.equals(Color.BLACK) && rowDir == 1) ||
                            (pieceColor.equals(Color.BLUE) && rowDir == -1)) {
                        clickedPanel.setCircleColor(pieceColor);
                        selectedPanel.setCircleColor(null);
                        System.out.println("Moved from (" + fromRow + "," + fromCol +
                                ") to (" + toRow + "," + toCol + ")" +"Simply with one move");
                        forceEndTurn = true;//Turn done and now force to press done
                        selectedPanel = clickedPanel;
                        return;
                    }else{
                        JOptionPane.showMessageDialog(null,
                                "Invalid destination. Backwarding",
                                "Invalid Input",
                                JOptionPane.WARNING_MESSAGE);
                        selectedPanel = null;
                        return;
                    }
                }else{//Checking for further jumping
                    JOptionPane.showMessageDialog(null,
                            "Invalid destination. You are further jumping",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    selectedPanel = null;
                    return;

                }





            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid destination.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                // ✅ Reset the selection so the player can try again
                selectedPanel = null;
                if (selectedPanel != null) {
                    selectedPanel.revalidate();
                    selectedPanel.repaint();
                }
                clickedPanel.revalidate();
                clickedPanel.repaint();


            }
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

        //Skip if no piece
        if(myColor == null) return false;

        //Determine forward directions basd on piece color
        int rowDir = myColor.equals(Color.BLACK) ? 1 : -1;

        //Check only forward-left and forward-right
        int[][] directions = {
                {rowDir,-1},//forward-left
                {rowDir,1}//forward-right
        };

        for(int[]dir: directions) {
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            int destRow = row + 2 * dir[0];
            int destCol = col + 2 * dir[1];
            Color midColor=null;
            Color destColor=null;

            if(isInBounds(midRow,midCol) && isInBounds(destRow,destCol)){
                CirclePanel mid = findPanelByCoord(midRow,midCol);
                CirclePanel dest = findPanelByCoord(destRow,destCol);

                if(mid != null && dest != null) {
                    midColor = mid.getCircleColor();
                    destColor = dest.getCircleColor();
                    // Opponent in the middle and empty space to land
                    if(midColor != null && !midColor.equals(myColor)&&destColor==null){
                        return true;
                    }
                }


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

    public void showingPickingWrongPiece() {
        JOptionPane.showMessageDialog(null,
                "Not your piece! You can only move your own discs.",
                "Invalid Selection",
                JOptionPane.WARNING_MESSAGE);
    }

    public Color checkWinner() {
        boolean blackHasMoves = false;
        boolean blueHasMoves = false;
        int blackCount = 0;
        int blueCount = 0;

        for(int row=0; row < 8; row++){
            for(int col=0; col<8;col++){
               CirclePanel piece = board[row][col];
               Color color = piece.getCircleColor();

               if(color != null){
                   if(color.equals(Color.BLACK)){
                       blackCount++;
                       if(canAnyMove(row,col,Color.BLACK) && blackCount >0){
                            blackHasMoves = true;
                       }
                   } else if (color.equals(Color.BLUE)) {
                       blueCount++;
                       if(canAnyMove(row,col,Color.BLUE) && blueCount > 0){
                           blueHasMoves = true;
                       }

                   }
               }
            }
        }
        if(blackCount == 0 || !blackHasMoves) return Color.BLUE;
        if(blueCount == 0 || !blueHasMoves) return Color.BLACK;
        return  null;//No winner yet
    }

    public Color checkWinnerEarly() {
        int blackCount = 0;
        int blueCount = 0;
        boolean bluegoal = false;
        boolean blackgoal = false;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                CirclePanel panel = board[row][col];
                if (panel.getCircleColor() != null) {
                    if (panel.getCircleColor().equals(Color.BLACK)) {
                        blackCount++;
                    } else if (panel.getCircleColor().equals(Color.BLUE)) {
                        blueCount++;
                    }
                }
            }
        }

        // 🏆 Rule 1: One player has no pieces left
        if (blackCount == 0 && blueCount > 0) {
            return Color.BLUE;
        }
        if (blueCount == 0 && blackCount > 0) {
            return Color.BLACK;
        }

        // ⚠️ Rule 2: Early victory guess based on large piece difference
        if (blackCount - blueCount >= 3 && blueCount <= 2) {
            return Color.BLACK;
        }
        if (blueCount - blackCount >= 3 && blackCount <= 2) {
            return Color.BLUE;
        }

        // 😐 No winner yet
        return null;
    }


    public boolean canAnyMove(int row, int col, Color myColor){
        int rowDir = myColor.equals(Color.BLACK) ? 1 : -1;
        int[][] directions = {
                {rowDir,-1},{rowDir,1}//forward-left and forward-right
        };

        for(int[] dir: directions){
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            int destRow = row + 2 * dir[0];
            int destCol = col + 2 * dir[1];

            // Regular move
            if (isInBounds(newRow, newCol)) {
                CirclePanel dest = board[newRow][newCol];
                if (dest.getCircleColor() == null) return true;
            }

            //Capture move
            if(isInBounds(midRow,midCol) && isInBounds(destRow,destCol)){
                CirclePanel mid = board[midRow][midCol];
                CirclePanel dest = board[destRow][destCol];
                if(mid.getCircleColor()!=null && !mid.getCircleColor().equals(myColor)
                && dest.getCircleColor() == null){
                    return true;
                }
            }
        }
        return  false;
    }


}


