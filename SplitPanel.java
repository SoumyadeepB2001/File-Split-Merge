import java.awt.Color;
import java.io.File;

import javax.swing.*;

class SplitPanel extends JPanel {

    private final FileSplitMerge compApp;
    private File selectedFile;
    private File selectedFolder;

    public SplitPanel(FileSplitMerge compApp) {
        this.compApp = compApp;
        setLayout(null);

        JPanel navBar = NavBar.createNavBar(compApp, "Split");
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

        selectFileButton.addActionListener(e -> selectFile(fileLabel));
        selectFolderButton.addActionListener(e -> selectOutputFolder(folderLabel));

        return panel;
    }

    private void selectFile(JLabel fileLabel) {

        JFileChooser j = new JFileChooser();
        j.setAcceptAllFileFilterUsed(true);

        int r = j.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            selectedFile = j.getSelectedFile();
            fileLabel.setText(selectedFile.getAbsolutePath());
        }
    }

    private void selectOutputFolder(JLabel folderLabel) {

        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only folders
        folderChooser.setDialogTitle("Select Output Folder");

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFolder = folderChooser.getSelectedFile();
            folderLabel.setText(selectedFolder.getAbsolutePath());
        }
    }
}