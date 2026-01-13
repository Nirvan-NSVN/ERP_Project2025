package ui.common;
import javax.swing.*;
import java.awt.*;
public class RoundTextField extends JTextField{
     private int arc = 20;

    public RoundTextField(int columns) {
        super(columns);
        setOpaque(false);
        setFont(new Font("Arial Black", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }
    // seen from internet
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(180, 180, 180));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
    }
}
