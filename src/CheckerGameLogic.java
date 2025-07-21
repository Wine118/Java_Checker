import javax.swing.*;
import java.awt.*;
import java.util.List;       // ✅ Correct
import java.util.ArrayList;  // ✅ Needed for `new ArrayList<>();`


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

        if (SimpleChessBoard.isIsAI() && SimpleChessBoard.getCurrentPlayerColor().equals(Color.BLUE)) {
            return; // Ignore user clicks during AI turn
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
        boolean isKing = selectedPanel.isKing();
        // Determine move direction for non-king
        int direction = pieceColor.equals(Color.BLACK) ? 2 : -2;

        if (mid != null && dest != null) {//Both middle cell and destination cell exist
            Color midColor = mid.getCircleColor();
            Color destColor = dest.getCircleColor();
            if ( (midColor != null && !midColor.equals(pieceColor)&& destColor==null&& isKing) ||
                    (midColor != null && !midColor.equals(pieceColor)&& destColor==null && !isKing && (toRow - fromRow == direction))) {
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
        int blackKingCount = 0;
        int blueKingCount = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                CirclePanel panel = board[row][col];
                Color c = panel.getCircleColor();
                if (Color.BLACK.equals(c)) {
                    blackCount++;
                    if (!blackCanMove && hasAnyValidMove(panel)) {
                        blackCanMove = true;
                    }
                    if (panel.isKing()) {
                        blackKingCount++;
                    }
                } else if (Color.BLUE.equals(c)) {
                    blueCount++;
                    if (!blueCanMove && hasAnyValidMove(panel)) {
                        blueCanMove = true;
                    }
                    if (panel.isKing()) {
                        blueKingCount++;
                    }
                }
            }
        }
        if ((blackCount == 0 || !blackCanMove || (blackCount == 0 && blueCount > 0) || (blackCount <= 3 && blueCount >= 5))) {
            return Color.BLUE;
        }

        if ((blueCount == 0 || !blueCanMove || (blueCount == 0 && blackCount > 0) || (blueCount <= 3 && blackCount >= 5))) {
            return Color.BLACK;
        }

        if (blueCount == blackCount && blueCount <= 3 && blackCount <= 3 || (blackCount == blackKingCount && blueCount == blueKingCount)) {
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


    public void resetGameState() {
        selectedPanel = null;
        forceEndTurn = false;
        turnAble = false;
    }

    public List<Move> getValidMoves(CirclePanel panel) {
        List<Move> moves = new ArrayList<>();

        if(panel.getCircleColor() == null) return moves;

        int row = panel.getRow();
        int col = panel.getCol();
        Color color = panel.getCircleColor();
        boolean isKing = panel.isKing();

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

        // Add normal moves
        for (int[] d : moveDirs) {
            int newRow = row + d[0];
            int newCol = col + d[1];
            CirclePanel dest = findPanelByCoord(newRow, newCol);
            if (dest != null && dest.getCircleColor() == null) {
                moves.add(new Move(row, col, newRow, newCol));
            }
        }

        // Add capture moves
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

                moves.add(new Move(row, col, newRow, newCol));
            }
        }

        return moves;
    }

    public void makeAIMove() {
        List<CirclePanel> bluePieces = getAllPieces(Color.BLUE);
        Move bestCapture = null;
        CirclePanel capturingPiece = null;

        //First, look for a capture move
        for(CirclePanel piece: bluePieces){
            List<Move> moves = getValidMoves(piece);
            for(Move move : moves){
                if(Math.abs(move.getToRow()-move.getFromRow()) == 2) {
                    bestCapture = move;
                    capturingPiece = piece;
                    break;//Take the first capture found
                }
            }
            if(bestCapture != null) break;
        }

        //If capture found, use it
        if(bestCapture != null){
            movePiece(bestCapture);
            //Check for chain capture

            CirclePanel finalCapturingPiece = capturingPiece;
            SwingUtilities.invokeLater(() -> tryChainCapture(finalCapturingPiece));
            return;
        }

        //otherwis, just do the first available normal move
        for(CirclePanel piece: bluePieces) {
            List<Move> validMoves = getValidMoves(piece);
            if(!validMoves.isEmpty()) {
                Move move = validMoves.get(0); // pick first valid move
                movePiece(move);
                break;
            }
        }
    }



    public List<CirclePanel> getAllPieces(Color color){
        List<CirclePanel> pieces = new ArrayList<>();
        for(int row = 0; row<board.length; row++){
            for(int col = 0; col < board.length; col++){
                CirclePanel p = board[row][col];
                if(color.equals(p.getCircleColor())) {
                    pieces.add(p);
                }
            }
        }
        return pieces;
    }

    public void movePiece(Move move){
        CirclePanel from = board[move.getFromRow()][move.getFromCol()];
        CirclePanel to = board[move.getToRow()][move.getToCol()];

        Color color = from.getCircleColor();
        boolean isKing = from.isKing();

        // Handle capture
        int rowDiff = move.getToRow() - move.getFromRow();
        int colDiff = move.getToCol() - move.getFromCol();

        if(Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1){
            from.setCircleColor(null);
            from.setKing(false);

            to.setCircleColor(color);
            to.setKing(isKing);
        } else if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2) {
            int capturedRow = move.getFromRow() + rowDiff / 2;
            int capturedCol = move.getFromCol() + colDiff / 2;
            CirclePanel captured = board[capturedRow][capturedCol];
            captured.setCircleColor(null);
            captured.setKing(false);
            from.setCircleColor(null);
            from.setKing(false);

            to.setCircleColor(color);
            to.setKing(isKing);
        }


        // Check for promotion
        if (color.equals(Color.BLACK) && move.getToRow() == 7) {
            to.setKing(true);
        } else if (color.equals(Color.BLUE) && move.getToRow() == 0) {
            to.setKing(true);
        }

        SimpleChessBoard.switchTurn();
        //If AI is on and it's AI's turn now, call AI again
        if(SimpleChessBoard.isIsAI()&&SimpleChessBoard.getCurrentPlayerColor()==Color.BLUE){
            makeAIMove();
        }

        //Optionally check winner
        Color winner = checkWinner();
        if(winner != null){
           SimpleChessBoard.disPlayingWinningState(winner);
        }

        if(winner.equals(Color.BLACK) || winner.equals(Color.BLUE) || winner.equals(Color.CYAN)){
            CheckerGameLogic gameLogic = null;
            SimpleChessBoard.restartTheGame(gameLogic);
        }
    }

    private void tryChainCapture(CirclePanel piece) {
        List<Move> chainMoves = getValidMoves(piece);
        for(Move move: chainMoves) {
            if(Math.abs(move.getToRow() - move.getFromRow()) == 2) {
                movePiece(move);
                SwingUtilities.invokeLater(() -> tryChainCapture(piece));
                return;
            }
        }

        //No more jumps -> switch turn
        SimpleChessBoard.switchTurn();
    }
}


