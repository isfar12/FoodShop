import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderMenu {
    private JPanel panel;
    private JButton pizzaOrderButton;
    private JButton burgerOrderButton;
    private JButton orderStatusButton;
    private JButton orderHistoryButton;
    private JButton userInfoButton;

    public OrderMenu(JFrame frame, int userId) {
        // Create the main panel with a background image
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("images/order_bg.jpeg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(new GridBagLayout());

        // Create a container for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setOpaque(false);

        pizzaOrderButton = new JButton("Order Pizza");
        burgerOrderButton = new JButton("Order Burger");
        orderStatusButton = new JButton("Order Status");
        orderHistoryButton = new JButton("Order History");
        userInfoButton = new JButton("User Info");

        // Style buttons for a good look
        JButton[] buttons = {pizzaOrderButton, burgerOrderButton, orderStatusButton, orderHistoryButton, userInfoButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(new Color(93, 173, 226));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        buttonPanel.add(pizzaOrderButton);
        buttonPanel.add(burgerOrderButton);
        buttonPanel.add(orderStatusButton);
        buttonPanel.add(orderHistoryButton);
        buttonPanel.add(userInfoButton);

        panel.add(buttonPanel);

        // Add action listeners for each button
        pizzaOrderButton.addActionListener(e -> frame.setContentPane(new PizzaOrderScreen(frame, userId).getPanel()));
        burgerOrderButton.addActionListener(e -> frame.setContentPane(new BurgerOrderScreen(frame, userId).getPanel()));

        orderStatusButton.addActionListener(e -> frame.setContentPane(new OrderStatusScreen(frame, userId).getPanel()));
        orderHistoryButton.addActionListener(e -> fetchOrderHistory(frame, userId));
        userInfoButton.addActionListener(e -> showUserInfo(frame, userId));
    }

    public JPanel getPanel() {
        return panel;
    }

    private void fetchOrderHistory(JFrame frame, int userId) {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                StringBuilder history = new StringBuilder();
                try (Connection conn = Database.getConnection()) {
                    String query = "SELECT * FROM orders WHERE user_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        history.append("Order ID: ").append(rs.getInt("order_id"))
                                .append(", Item: ").append(rs.getString("item_type"))
                                .append(", Details: ").append(rs.getString("details"))
                                .append(", Total Price: $").append(rs.getDouble("total_price"))
                                .append(", Status: ").append(rs.getString("status"))
                                .append("\n");
                    }
                } catch (Exception ex) {
                    return "Error: " + ex.getMessage();
                }
                return history.length() > 0 ? history.toString() : "No order history found.";
            }

            @Override
            protected void done() {
                try {
                    String history = get();
                    JOptionPane.showMessageDialog(frame, history);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void showUserInfo(JFrame frame, int userId) {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try (Connection conn = Database.getConnection()) {
                    String query = "SELECT name, email FROM users WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        return "Name: " + rs.getString("name") + "\nEmail: " + rs.getString("email");
                    }
                } catch (Exception ex) {
                    return "Error: " + ex.getMessage();
                }
                return "User info not found.";
            }

            @Override
            protected void done() {
                try {
                    String userInfo = get();
                    JOptionPane.showMessageDialog(frame, userInfo);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
