package ui.common;
import java.awt.*;
import javax.swing.*;
public class RoundButton extends JButton {
    private int cornerRadius=27;
    public RoundButton(String text){
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBackground(new Color(66,133,241));
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif",Font.BOLD,15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
     @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background Fill
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 50)); // light shadow border
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }
    
}
