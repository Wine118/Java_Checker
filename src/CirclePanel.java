import javax.swing.*;
import java.awt.*;


public class CirclePanel extends JPanel {
    private Color color;
    private final int row, col;
    private boolean isKing = false;



    public CirclePanel(Color color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(50, 50));
        setOpaque(true);
    }

    public void setCircleColor(Color color) {
        this.color = color;
        repaint();  // ✅ This is key
    }


    public Color getCircleColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (color != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Set diameter smaller than panel
            int diameter = isKing ? Math.min(getWidth(), getHeight()) - 10 : Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            g2.setColor(color);
            g2.fillOval(x, y, diameter, diameter);

            if (isKing) {
                g.drawString("K", getWidth() / 2 - 4, getHeight() / 2 + 5);
            }
        }
    }


}
