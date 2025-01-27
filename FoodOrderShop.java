import javax.swing.*;

public class FoodOrderShop {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Food Order Shop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);
            frame.setContentPane(new LoginScreen(frame).getPanel());
            frame.setVisible(true);
        });
    }
}
