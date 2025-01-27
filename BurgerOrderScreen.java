import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BurgerOrderScreen {
    private JPanel panel;

    public BurgerOrderScreen(JFrame frame, int userId) {
        // Main panel with background image
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("images/burger.jpeg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(new GridBagLayout());

        // Components panel
        JPanel contentPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        contentPanel.setOpaque(false);

        JLabel bunLabel = new JLabel("Choose Bun Type:");
        bunLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bunLabel.setForeground(Color.WHITE);

        String[] buns = {"Sesame", "Brioche"};
        JComboBox<String> bunBox = new JComboBox<>(buns);

        JLabel pattyLabel = new JLabel("Enter number of patties ($3 each):");
        pattyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pattyLabel.setForeground(Color.WHITE);

        JTextField pattyField = new JTextField();

        JLabel addonsLabel = new JLabel("Add-ons:");
        addonsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addonsLabel.setForeground(Color.WHITE);

        JCheckBox cheeseCheck = new JCheckBox("Cheese ($2)");
        JCheckBox picklesCheck = new JCheckBox("Pickles ($2)");
        JCheckBox tomatoCheck = new JCheckBox("Tomato ($2)");

        JButton orderButton = new JButton("Order Burger");
        JButton backButton = new JButton("Back");

        // Style buttons
        JButton[] buttons = {orderButton, backButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(new Color(93, 173, 226));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        contentPanel.add(bunLabel);
        contentPanel.add(bunBox);
        contentPanel.add(pattyLabel);
        contentPanel.add(pattyField);
        contentPanel.add(addonsLabel);
        contentPanel.add(cheeseCheck);
        contentPanel.add(picklesCheck);
        contentPanel.add(tomatoCheck);
        contentPanel.add(orderButton);
        contentPanel.add(backButton);

        panel.add(contentPanel);

        // Order button action
        orderButton.addActionListener(e -> {
            int pattyCount;
            try {
                pattyCount = Integer.parseInt(pattyField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number for patties.");
                return;
            }

            int addonsPrice = (cheeseCheck.isSelected() ? 2 : 0) +
                    (picklesCheck.isSelected() ? 2 : 0) +
                    (tomatoCheck.isSelected() ? 2 : 0);
            int totalPrice = (pattyCount * 3) + addonsPrice;

            try (Connection conn = Database.getConnection()) {
                String query = "INSERT INTO orders (user_id, item_type, details, total_price) VALUES (?, 'Burger', ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, "Bun: " + buns[bunBox.getSelectedIndex()] + ", Patties=" + pattyCount
                        + ", Add-ons: Cheese=" + cheeseCheck.isSelected()
                        + ", Pickles=" + picklesCheck.isSelected()
                        + ", Tomato=" + tomatoCheck.isSelected());
                stmt.setDouble(3, totalPrice);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Burger ordered successfully! Total price: $" + totalPrice);
                frame.setContentPane(new OrderMenu(frame, userId).getPanel());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> frame.setContentPane(new OrderMenu(frame, userId).getPanel()));
        frame.revalidate();
    }

    public JPanel getPanel() {
        return panel;
    }
}
