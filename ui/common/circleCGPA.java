package ui.common;

import javax.swing.*;
import java.awt.*;

public class circleCGPA extends JPanel {

    private double value;     // 0..10
    private String label = "CGPA"; // default text under the circle

    // Existing constructor (kept for compatibility)
    public circleCGPA(double cgpa) {
        this.value = cgpa;
        setPreferredSize(new Dimension(260, 260));
        setOpaque(false);
    }

    // NEW: overloaded constructor to fix "constructor undefined" error
    public circleCGPA(double cgpa, String label) {
        this(cgpa);
        if (label != null && !label.isBlank()) {
            this.label = label;
        }
    }

    // Optional helpers if you want to update later without recreating the component
    public void setValue(double cgpa) {
        this.value = cgpa;
        repaint();
    }

    public void setLabel(String label) {
        if (label != null && !label.isBlank()) {
            this.label = label;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int stroke = 20;
        int pad = 15;
        int size = Math.min(getWidth(), getHeight()) - (pad * 2);
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Base ring
        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(220, 220, 220));
        g2.drawOval(x, y, size, size);

        // Foreground arc for value (value/10 * 360 degrees)
        double clamped = Math.max(0, Math.min(10, value));
        int sweep = (int) Math.round((clamped / 10.0) * 360.0);
        g2.setColor(new Color(102, 16, 153)); // purple theme
        g2.drawArc(x, y, size, size, 90, -sweep);

        // Value text
        g2.setFont(new Font("Arial", Font.BOLD, 34));
        String text = String.format("%.2f", clamped);
        FontMetrics fm = g2.getFontMetrics();
        int tx = getWidth() / 2 - fm.stringWidth(text) / 2;
        int ty = getHeight() / 2 + fm.getAscent() / 3;
        g2.setColor(Color.BLACK);
        g2.drawString(text, tx, ty);

        // Label under the circle
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String lbl = label;
        int lx = getWidth() / 2 - g2.getFontMetrics().stringWidth(lbl) / 2;
        g2.drawString(lbl, lx, y + size + 28);

        g2.dispose();
    }
}
