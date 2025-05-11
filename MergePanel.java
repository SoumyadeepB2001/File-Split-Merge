import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class MergePanel extends JPanel {

    File metadataFile, outputFolder;
    List<File> chunkFiles = new ArrayList<>();
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

        // Row 1: Select Metadata File
        JButton selectMetadataButton = new JButton("Select Metadata");
        selectMetadataButton.setBounds(20, 20, 150, 30);
        selectMetadataButton.setBackground(new Color(206, 225, 242));
        panel.add(selectMetadataButton);

        JLabel metadataLabel = new JLabel("No metadata file selected");
        metadataLabel.setBounds(180, 20, 500, 30);
        panel.add(metadataLabel);

        // Row 2: Select Chunk Files
        JButton selectChunkFilesButton = new JButton("Select Chunk Files");
        selectChunkFilesButton.setBounds(20, 70, 150, 30);
        selectChunkFilesButton.setBackground(new Color(206, 225, 242));
        panel.add(selectChunkFilesButton);

        JTextArea chunkFilesArea = new JTextArea();
        chunkFilesArea.setEditable(false);
        JScrollPane chunkScrollPane = new JScrollPane(chunkFilesArea);
        chunkScrollPane.setBounds(180, 70, 500, 150);
        panel.add(chunkScrollPane);

        // Row 3: Select Output Folder
        JButton selectFolderButton = new JButton("Select Output Folder");
        selectFolderButton.setBounds(20, 240, 150, 30);
        selectFolderButton.setBackground(new Color(206, 225, 242));
        panel.add(selectFolderButton);

        JLabel folderLabel = new JLabel("No folder selected");
        folderLabel.setBounds(180, 240, 500, 30);
        panel.add(folderLabel);

        // Row 4: Merge button and ouput location
        JButton mergeButton = new JButton("Merge Parts");
        mergeButton.setBounds(20, 290, 150, 30);
        mergeButton.setBackground(new Color(206, 225, 242));
        panel.add(mergeButton);

        JLabel outputFileLabel = new JLabel("Merged file location will be shown here");
        outputFileLabel.setBounds(180, 290, 660, 30);
        panel.add(outputFileLabel);

        selectMetadataButton.addActionListener(e -> selectMetadataFile(metadataLabel));
        selectChunkFilesButton.addActionListener(e -> selectChunkFiles(chunkFilesArea));
        selectFolderButton.addActionListener(e -> selectOutputFolder(folderLabel));
        mergeButton.addActionListener(e -> startMergingParts(outputFileLabel));

        return panel;
    }

    private void startMergingParts(JLabel outputFileLabel) {
        MergeFiles merge = new MergeFiles(metadataFile, outputFolder, chunkFiles);
        String outputFileLocation = merge.mergeFile();
        outputFileLabel.setText(outputFileLocation);
    }

    private void selectMetadataFile(JLabel metadataLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Metadata Files", "part"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            metadataFile = fileChooser.getSelectedFile();
            metadataLabel.setText(metadataFile.getAbsolutePath());
        }
    }

    private void selectChunkFiles(JTextArea chunkFilesArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chunk Files", "part"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            chunkFiles.clear();
            chunkFilesArea.setText("");
            for (File file : files) {
                chunkFiles.add(file);
                chunkFilesArea.append(file.getAbsolutePath() + "\n");
            }
        }
    }

    private void selectOutputFolder(JLabel folderLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            outputFolder = fileChooser.getSelectedFile();
            folderLabel.setText(outputFolder.getAbsolutePath());
        }
    }
}