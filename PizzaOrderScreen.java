import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class PizzaOrderScreen {
    private JPanel panel;

    public PizzaOrderScreen(JFrame frame, int userId) {
        // Main panel with background image
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("images/pizza.jpeg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(new GridBagLayout());

        // Components panel
        JPanel contentPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        contentPanel.setOpaque(false);

        JLabel sizeLabel = new JLabel("Choose Pizza Size:");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sizeLabel.setForeground(Color.WHITE);

        String[] sizes = {"Small ($8)", "Medium ($10)", "Large ($12)"};
        JComboBox<String> sizeBox = new JComboBox<>(sizes);

        JLabel addonsLabel = new JLabel("Add-ons:");
        addonsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addonsLabel.setForeground(Color.WHITE);

        JCheckBox pepperoniCheck = new JCheckBox("Pepperoni ($2)");
        JCheckBox cheeseCheck = new JCheckBox("Cheese ($2)");
        JCheckBox meatCheck = new JCheckBox("Meat Pieces ($2)");

        JButton orderButton = new JButton("Order Pizza");
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

        contentPanel.add(sizeLabel);
        contentPanel.add(sizeBox);
        contentPanel.add(addonsLabel);
        contentPanel.add(pepperoniCheck);
        contentPanel.add(cheeseCheck);
        contentPanel.add(meatCheck);
        contentPanel.add(orderButton);
        contentPanel.add(backButton);

        panel.add(contentPanel);

        // Order button action
        orderButton.addActionListener(e -> {
            int sizePrice = switch (sizeBox.getSelectedIndex()) {
                case 0 -> 8;
                case 1 -> 10;
                case 2 -> 12;
                default -> 8;
            };
            int addonsPrice = (pepperoniCheck.isSelected() ? 2 : 0) +
                    (cheeseCheck.isSelected() ? 2 : 0) +
                    (meatCheck.isSelected() ? 2 : 0);
            int totalPrice = sizePrice + addonsPrice;

            try (Connection conn = Database.getConnection()) {
                String query = "INSERT INTO orders (user_id, item_type, details, total_price) VALUES (?, 'Pizza', ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, "Size: " + sizes[sizeBox.getSelectedIndex()] + ", Add-ons: "
                        + (pepperoniCheck.isSelected() ? "Pepperoni " : "")
                        + (cheeseCheck.isSelected() ? "Cheese " : "")
                        + (meatCheck.isSelected() ? "Meat " : ""));
                stmt.setDouble(3, totalPrice);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Pizza ordered successfully! Total price: $" + totalPrice);
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
