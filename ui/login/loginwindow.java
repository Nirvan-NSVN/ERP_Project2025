package ui.login;

import auth.AuthService;
import auth.UserSession;
import ui.studentdash.StudentDashboard;
import ui.instructordash.InstructorDashboard;
import ui.admindash.AdminDashboard;
import ui.common.RoundButton;
import ui.common.RoundPassField;
import ui.common.RoundTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class loginwindow {

    private int loginAttempts = 0;
    private long lockEndTime = 0;

    public loginwindow() {
        createUI();
    }

    private void createUI() {

        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        ImageIcon originalIcon = new ImageIcon("ui/login/assets/1.jpg");
        Image originalImage = originalIcon.getImage();
        JLabel background = new JLabel();
        background.setLayout(new GridBagLayout());
        frame.setContentPane(background);

        // Background resizing for login screen 
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                int height = frame.getHeight();
                if (width > 0 && height > 0) {
                    Image scaled = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    background.setIcon(new ImageIcon(scaled));
                }
            }
        });
        // LOGIN PANEL
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setPreferredSize(new Dimension(400, 200));
        loginPanel.setBackground(new Color(255, 255, 255, 200));

        JTextField usernameField = new RoundTextField(15);
        JPasswordField passwordField = new RoundPassField(15);

        JButton loginBtn = new RoundButton("Login");
        JLabel forgotLabel = new JLabel("<HTML><U>Forgot Password?</U></HTML>");

        forgotLabel.setForeground(Color.BLUE);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        usernameField.setPreferredSize(new Dimension(250, 40));
        passwordField.setPreferredSize(new Dimension(250, 40));

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginBtn);
        loginPanel.add(forgotLabel);

        background.add(loginPanel);
        frame.pack();
        frame.setVisible(true);
    
        loginBtn.addActionListener(e -> {

            long currentTime = System.currentTimeMillis();

            // Check lockout
            if (currentTime < lockEndTime) {
                long remaining = (lockEndTime - currentTime) / 1000;
                JOptionPane.showMessageDialog(frame,
                        "Too many failed attempts! Try again in " + remaining + " seconds.");
                return;
            }

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            boolean success = AuthService.authenticate(username, password);

            if (!success) {
                loginAttempts++;
                if (loginAttempts >= 5) {
                    lockEndTime = currentTime + (30 * 60 * 1000);
                    JOptionPane.showMessageDialog(frame,
                            "5 failed attempts! Login blocked for 30 minutes.");
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid username or password. Attempts left: " + (5 - loginAttempts));
                }
                return;
            }

            // Reset if successful
            loginAttempts = 0;
            lockEndTime = 0;

            frame.dispose();
            redirectUser();
        });
        // FORGOT PASSWORD CLICK
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new ForgotPasswordWindow();
            }
        });
    }
   
    private void redirectUser() {

    String role = UserSession.getRole();

    // Show loading screen immediately
    JDialog loading = new JDialog();
    loading.setUndecorated(true);
    loading.setSize(300, 120);
    loading.setLocationRelativeTo(null);
    loading.add(new JLabel("Loading dashboard, please wait...", SwingConstants.CENTER));
    loading.setVisible(true);

    // Background loading
    new SwingWorker<Void, Void>() {
    // I have seen this from on YouTube .
        @Override
        protected Void doInBackground() {

            switch (role.toLowerCase()) {
                case "student":
                    new StudentDashboard();
                    break;

                case "instructor":
                    new InstructorDashboard();
                    break;

                case "admin":
                    new AdminDashboard();
                    break;
            }

            return null;
        }

        @Override
        protected void done() {
            loading.dispose();
        }

    }.execute();
}

}
