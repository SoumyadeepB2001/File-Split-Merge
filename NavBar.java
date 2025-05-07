import javax.swing.*;
import java.awt.*;

public class NavBar {
    public static JPanel createNavBar(FileSplitMerge compApp, String activePanel) {

        JPanel navBar = new JPanel(new GridLayout(1, 4));
        navBar.setBounds(0, 0, 720, 28);

        JButton btnImageEncode = new JButton("Split");
        JButton btnImageDecode = new JButton("Merge Parts");

        btnImageEncode.setBackground(Color.WHITE);
        btnImageDecode.setBackground(Color.WHITE);

        Color activeTabColor = new Color(0, 24, 46);

        if (activePanel.equals("Split")) {
            btnImageEncode.setBackground(activeTabColor);
            btnImageEncode.setEnabled(false);
        } else {
            btnImageEncode.addActionListener(e -> compApp.switchPanel(new SplitPanel(compApp)));
        }

        if (activePanel.equals("Merge Parts")) {
            btnImageDecode.setBackground(activeTabColor);
            btnImageDecode.setEnabled(false);
        } else {
            btnImageDecode.addActionListener(e -> compApp.switchPanel(new MergePanel(compApp)));
        }

        navBar.add(btnImageEncode);
        navBar.add(btnImageDecode);

        return navBar;
    }
}