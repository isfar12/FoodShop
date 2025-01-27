import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginScreen {
    private JPanel panel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginScreen(JFrame frame) {
        // Create a panel with a background image
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("images/background.jpeg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);

        // Create a container for the login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Welcome to Food Order Shop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(Color.WHITE);

        emailField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);

        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(72, 201, 176));
        loginButton.setForeground(Color.WHITE);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(93, 173, 226));
        registerButton.setForeground(Color.WHITE);

        // Add components to the form panel
        formPanel.add(titleLabel, gbc);
        gbc.gridy++;
        formPanel.add(emailLabel, gbc);
        gbc.gridy++;
        formPanel.add(emailField, gbc);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(loginButton, gbc);
        gbc.gridy++;
        formPanel.add(registerButton, gbc);

        // Add the form panel to the main panel
        panel.add(formPanel);

        // Button actions
        loginButton.addActionListener(e -> login(frame));
        registerButton.addActionListener(e -> openRegisterScreen(frame));
    }

    public JPanel getPanel() {
        return panel;
    }

    private void login(JFrame frame) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT id FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.setContentPane(new OrderMenu(frame, userId).getPanel());
                frame.revalidate();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Try again.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void openRegisterScreen(JFrame frame) {
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        registerPanel.add(nameLabel, gbc);
        gbc.gridx++;
        registerPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        registerPanel.add(emailLabel, gbc);
        gbc.gridx++;
        registerPanel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        registerPanel.add(passwordLabel, gbc);
        gbc.gridx++;
        registerPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        registerPanel.add(registerButton, gbc);
        gbc.gridx++;
        registerPanel.add(cancelButton, gbc);

        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(400, 300);
        registerFrame.setContentPane(registerPanel);
        registerFrame.setVisible(true);

        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(registerFrame, "Registration Successful! You can now log in.");
                registerFrame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(registerFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> registerFrame.dispose());
    }
}
