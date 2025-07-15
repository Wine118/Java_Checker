import java.awt.*;

public class CheckerGameLogic {
    private CirclePanel selectedPanel = null;
    private final CirclePanel[][] board;

    public CheckerGameLogic(CirclePanel[][] board) {
        this.board = board;
    }

    public void handleCellClick(CirclePanel clickedPanel){
        if(selectedPanel == null){
            //First click - select a piece
            if(clickedPanel.getCircleColor() != null){
                selectedPanel = clickedPanel;
                System.out.println("Selected piece at (" + clickedPanel.getRow() + "," + clickedPanel.getCol() + ")");
            } else {
                System.out.println("No piece to select. ");
            }
        } else {
            //Second click - attempt to move
            if(clickedPanel != selectedPanel && clickedPanel.getCircleColor() == null){
                int fromRow = selectedPanel.getRow();
                int fromCol = selectedPanel.getCol();
                int toRow = clickedPanel.getRow();
                int toCol = clickedPanel.getCol();

                int rowDiff = Math.abs(toRow - fromRow);
                int colDiff = Math.abs(toCol - fromCol);

                int rowDir = toRow - fromRow;
                Color pieceColor = selectedPanel.getCircleColor();
                boolean isValidMove = false;
                boolean isCapture = false;
                //Check for valid capture move
                if(rowDiff == 2 && colDiff == 2){
                    int midRow = (fromRow + toRow) / 2;
                    int midCol = (fromCol + toCol) /2;
                    CirclePanel mid = findPanelByCoord(midRow,midCol);
                    if(mid != null) {
                        Color midColor = mid.getCircleColor();
                        if(midColor!=null && !midColor.equals(pieceColor)){
                            //Valid capture
                            isValidMove = true;
                            isCapture = true;
                            mid.setCircleColor(null); //Remove component
                            System.out.println("Captured opponent at (" + midRow + "," + midCol + ")");

                        }
                    }

                }//Check for regular move
                else if (rowDiff == 1 && colDiff == 1) {
                    if ((pieceColor.equals(Color.BLACK) && rowDir == 1) ||
                            (pieceColor.equals(Color.BLUE) && rowDir == -1)) {
                        isValidMove = true;
                    }
                }


                if(isValidMove){
                    clickedPanel.setCircleColor(pieceColor);
                    selectedPanel.setCircleColor(null);
                    System.out.println("Moved from (" + fromRow + "," + fromCol +
                            ") to (" + toRow + "," + toCol + ")");
                    //If it was a capture, check for another capture
                    if(isCapture && canCaptureFrom(clickedPanel)){
                        System.out.println("Another capture available!");
                        selectedPanel = clickedPanel;//keep selection for next jump
                    }else{
                        selectedPanel = null;
                    }

                }else{
                    System.out.println("Invalid move. Only forward diagonal moves allowed");

                }
                if (selectedPanel != null) {
                    selectedPanel.revalidate();
                    selectedPanel.repaint();
                }
                clickedPanel.revalidate();
                clickedPanel.repaint();

            } else {
                System.out.println("Invalid destination.");

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

}


