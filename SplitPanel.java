import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;
import javax.swing.*;

class SplitPanel extends JPanel {

    private final FileSplitMerge compApp;
    private File selectedFile;
    private File selectedFolder;
    int numOfChunks;
    short magicNumber;

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
        JButton selectFileButton = new JButton("Select Input File");
        selectFileButton.setBounds(20, 20, 150, 30);
        selectFileButton.setBackground(new Color(206, 225, 242));
        panel.add(selectFileButton);

        JLabel fileLabel = new JLabel("No file selected");
        fileLabel.setBounds(180, 20, 500, 30);
        panel.add(fileLabel);

        // Row 2: Select Output Folder
        JButton selectFolderButton = new JButton("Select Output Folder");
        selectFolderButton.setBounds(20, 70, 150, 30);
        selectFolderButton.setBackground(new Color(206, 225, 242));
        panel.add(selectFolderButton);

        JLabel folderLabel = new JLabel("No folder selected");
        folderLabel.setBounds(180, 70, 500, 30);
        panel.add(folderLabel);

        // Row 3: Enter number of chunks and split
        JLabel numOfChunksLabel = new JLabel("Set the number of chunks (2-100): ");
        numOfChunksLabel.setBounds(20, 120, 200, 30);
        panel.add(numOfChunksLabel);

        JTextField txtNumOfChunks = new JTextField();
        txtNumOfChunks.setBounds(220, 120, 100, 30);
        // Add key listener to restrict input
        txtNumOfChunks.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtNumOfChunks.getText();

                // Allow backspace
                if (c == '\b') return;

                // Block non-digits
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }

                // Predict future text after typing this character
                String futureText = currentText + c;
                try {
                    int value = Integer.parseInt(futureText);
                    if (value < 1 || value > 100) {
                        e.consume(); // Block out-of-range value
                    }
                } catch (NumberFormatException ex) {
                    e.consume(); // Block overflow or bad format
                }
            }
        });
        panel.add(txtNumOfChunks);

        JButton btnSplit = new JButton("Split");
        btnSplit.setBounds(330, 120, 100, 30);
        btnSplit.setBackground(new Color(206, 225, 242));
        panel.add(btnSplit);

        // Row 4
        JTextArea fileListArea = new JTextArea();
        fileListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(fileListArea);
        scrollPane.setBounds(20, 170, 660, 200);
        panel.add(scrollPane);

        selectFileButton.addActionListener(e -> selectFile(fileLabel));
        selectFolderButton.addActionListener(e -> selectOutputFolder(folderLabel));
        btnSplit.addActionListener(e -> splitFile(fileListArea, txtNumOfChunks));

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

    private void splitFile(JTextArea fileListArea, JTextField txtNumOfChunks) {
        numOfChunks = Integer.parseInt(txtNumOfChunks.getText().toString());

        if(numOfChunks < 2 || numOfChunks > 100)
        {
            JOptionPane.showMessageDialog(null, "Number of chunks must be between 2 and 100.");
        }

        magicNumber = generateMagicNumber();
        SplitFile split = new SplitFile(selectedFile, selectedFolder, numOfChunks, fileListArea);
        split.createParts();
    }

    public short generateMagicNumber() {
        Random random = new Random();
        return (short) (random.nextInt(Short.MAX_VALUE)); // Generates between 0 and 32,767
    }
}