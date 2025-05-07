import javax.swing.*;
import java.awt.*;

public class FileSplitMerge {
    private JFrame frame;

    public FileSplitMerge() {
        frame = new JFrame("Split and Merge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 480);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Start with CompressionPanel
        frame.add(new SplitPanel(this), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void switchPanel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileSplitMerge::new);
    }
}