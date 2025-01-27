import javax.swing.*;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderStatusScreen {
    private JPanel panel;

    public OrderStatusScreen(JFrame frame, int userId) {
        // Main panel with background image
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("images/status_bg.jpeg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Order Status - On the Way", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Text area for status
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statusArea.setForeground(Color.WHITE);
        statusArea.setOpaque(false);

        // Wrap the text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Buttons
        JButton backButton = new JButton("Back");
        JButton markReceivedButton = new JButton("Mark Selected Order as Received");

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(markReceivedButton);
        buttonPanel.add(backButton);

        // Add components to the main panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Populate the order status area
        updateStatus(statusArea, userId);

        // Mark order as received functionality
        markReceivedButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter the Order ID to mark as received:");
            if (input != null && !input.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(input);
                    markOrderAsReceived(orderId, userId, statusArea, frame);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Order ID format. Please enter a number.");
                }
            }
        });

        // Back button functionality
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll(); // Clear the current content
            frame.add(new OrderMenu(frame, userId).getPanel()); // Add MainMenu panel
            frame.revalidate(); // Refresh the frame
            frame.repaint();
        });
    }

    // Method to fetch and display order status
    private void updateStatus(JTextArea statusArea, int userId) {
        SwingUtilities.invokeLater(() -> {
            statusArea.setText("Current Orders - On the Way:\n\n");
            try (Connection conn = Database.getConnection()) {
                String query = "SELECT order_id, item_type, status FROM orders WHERE user_id = ? AND status = 'On the Way'";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                boolean hasOrders = false;
                while (rs.next()) {
                    hasOrders = true;
                    int orderId = rs.getInt("order_id");
                    String itemType = rs.getString("item_type");
                    String status = rs.getString("status");
                    statusArea.append("Order ID: " + orderId + ", Item: " + itemType + ", Status: " + status + "\n");
                }

                if (!hasOrders) {
                    statusArea.append("No orders currently on the way.\n");
                }
            } catch (Exception e) {
                statusArea.append("Error retrieving order status.\n");
                e.printStackTrace();
            }
        });
    }

    // Method to mark an order as received
    private void markOrderAsReceived(int orderId, int userId, JTextArea statusArea, JFrame frame) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE orders SET status = 'Received' WHERE order_id = ? AND user_id = ? AND status = 'On the Way'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);
            stmt.setInt(2, userId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(frame, "Order marked as received!");
                updateStatus(statusArea, userId); // Refresh the displayed orders
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Order ID or Order is not 'On the Way'.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}
