import java.awt.Color;
import javax.swing.*;

class MergePanel extends JPanel {

    private final FileSplitMerge compApp;

    public MergePanel(FileSplitMerge compApp) {
        this.compApp = compApp;
        setLayout(null);

        JPanel navBar = NavBar.createNavBar(compApp, "Merge Parts");
        add(navBar);

        JPanel contentPanel = createContentPanel();
        contentPanel.setBounds(0, 28, 720, 480);
        add(contentPanel);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);

        // Row 1: Select File
        JButton selectFileButton = new JButton("Select File");
        selectFileButton.setBounds(20, 20, 150, 30);
        panel.add(selectFileButton);

        JLabel fileLabel = new JLabel("No file selected");
        fileLabel.setBounds(180, 20, 500, 30);
        panel.add(fileLabel);

        // Row 2: Select Output Folder
        JButton selectFolderButton = new JButton("Select Output Folder");
        selectFolderButton.setBounds(20, 70, 150, 30);
        panel.add(selectFolderButton);

        JLabel folderLabel = new JLabel("No folder selected");
        folderLabel.setBounds(180, 70, 500, 30);
        panel.add(folderLabel);

        // Row 3
        JTextArea fileListArea = new JTextArea();
        fileListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(fileListArea);
        scrollPane.setBounds(20, 120, 660, 250);
        panel.add(scrollPane);

        selectFileButton.addActionListener(e -> selectFiles());
        selectFolderButton.addActionListener(e -> selectOutputFolder(folderLabel));

        return panel;
    }

    private void selectFiles() {

    }

    private void selectOutputFolder(JLabel folderLabel) {

    }
}