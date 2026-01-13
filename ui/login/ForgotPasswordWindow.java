package ui.login;

import auth.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ForgotPasswordWindow {

    private static String generatedOTP;

    public ForgotPasswordWindow() {
        createUI();
    }

    private void createUI() {

        JFrame frame = new JFrame("Forgot Password");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background
        ImageIcon bgIcon=(new ImageIcon("ui/login/assets/1.jpg"));
        Image bgImage=bgIcon.getImage();
        JLabel bg=new JLabel();
        bg.setLayout(new GridBagLayout());
        frame.setContentPane(bg);
        // Background resizing for forgot pass
        frame.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                int width=frame.getWidth();
                int height=frame.getHeight();
                if(width>0 && height>0){
                    Image scaled=bgImage.getScaledInstance(width, height,Image.SCALE_SMOOTH);
                    bg.setIcon(new ImageIcon(scaled));
                }
            }
        });

        // Form panel
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setPreferredSize(new Dimension(420, 170));
        panel.setBackground(new Color(255, 255, 255, 200));

        JLabel emailLabel = new JLabel("Enter Registered Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton sendBtn = new JButton("Send OTP");
        sendBtn.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(sendBtn);

        bg.add(panel);

        frame.pack();
        frame.setVisible(true);

        // send OTP
        sendBtn.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter your registered email.",
                        "Empty Email",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!AuthService.emailExists(email)) {
                JOptionPane.showMessageDialog(frame,
                        "Email not registered in system!",
                        "Invalid Email",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            generatedOTP = String.valueOf(new Random().nextInt(900000) + 100000);
            System.out.println("Generated OTP: " + generatedOTP);

            showOtpPopup(frame, email);
        });
    }

    private void showOtpPopup(JFrame parent, String email) {

        JTextField otpField = new JTextField();
        otpField.setFont(new Font("Arial", Font.PLAIN, 16));

        Object[] msg = {
                "Enter the OTP sent to your email:",
                otpField
        };

        int result = JOptionPane.showConfirmDialog(
                parent,
                msg,
                "OTP Verification",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        if (!otpField.getText().trim().equals(generatedOTP)) {
            JOptionPane.showMessageDialog(parent,
                    "Incorrect OTP! Try again.",
                    "OTP Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        showPasswordResetPopup(parent, email);
    }

    private void showPasswordResetPopup(JFrame parent, String email) {

        JPasswordField pass1 = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();

        Object[] msg = {
                "Enter New Password:", pass1,
                "Confirm Password:", pass2
        };

        int res = JOptionPane.showConfirmDialog(
                parent,
                msg,
                "Reset Password",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return;

        String p1 = new String(pass1.getPassword()).trim();
        String p2 = new String(pass2.getPassword()).trim();

        if (p1.isEmpty() || p2.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Password cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(parent,
                    "Passwords do not match!",
                    "Mismatch",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        AuthService.updatePassword(email, p1);

        JOptionPane.showMessageDialog(parent,
                "Password Reset Successful!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        parent.dispose();
        new loginwindow();
    }
}
