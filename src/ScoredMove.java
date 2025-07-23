public class ScoredMove {
    Move move;
    CirclePanel piece;
    int score;

    public ScoredMove(Move move, CirclePanel piece, int score) {
        this.move = move;
        this.piece = piece;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Move[from=(" + move.getFromRow() + "," + move.getFromCol() + ") to=(" +
                move.getToRow() + "," + move.getToCol() + "), score=" + score + "]";
    }
}
