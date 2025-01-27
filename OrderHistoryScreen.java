import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderHistoryScreen {
    private JPanel panel;

    public OrderHistoryScreen(JFrame frame, int userId) {
        panel = new JPanel(new BorderLayout());

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        JButton backButton = new JButton("Back");

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        updateHistory(historyArea, userId);

        backButton.addActionListener(e -> frame.setContentPane(new OrderMenu(frame, userId).getPanel()));
        frame.revalidate();
    }

    private void updateHistory(JTextArea historyArea, int userId) {
        historyArea.setText("Order History:\n");
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT order_id, item_type, details, total_price, status, order_date FROM orders WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String itemType = rs.getString("item_type");
                String details = rs.getString("details");
                double totalPrice = rs.getDouble("total_price");
                String status = rs.getString("status");
                String orderDate = rs.getString("order_date");

                historyArea.append(String.format("Order ID: %d | Item: %s | Details: %s | Price: $%.2f | Status: %s | Date: %s%n",
                        orderId, itemType, details, totalPrice, status, orderDate));
            }
        } catch (Exception e) {
            historyArea.append("Error retrieving order history.\n");
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}
