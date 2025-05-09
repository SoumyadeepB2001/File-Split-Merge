import java.io.*;
import java.nio.ByteBuffer;
import javax.swing.JTextArea;

public class SplitFile {

    File selectedFile, selectedFolder;
    int numOfChunks;
    JTextArea fileListArea;
    short magicNumber;

    SplitFile(File selectedFile, File selectedFolder, int numOfChunks, JTextArea fileListArea) {
        this.selectedFile = selectedFile;
        this.selectedFolder = selectedFolder;
        this.numOfChunks = numOfChunks;
        this.fileListArea = fileListArea;
    }

    public void createParts() {
        MetadataGenerator.generateMetadata(selectedFile, selectedFolder, magicNumber, numOfChunks, fileListArea);

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(selectedFile))) {

            long startTime = System.nanoTime();

            long totalSize = selectedFile.length();
            long chunkSize = totalSize / numOfChunks;
            long remainder = totalSize % numOfChunks;

            String dir = selectedFolder.getAbsolutePath();

            if (dir == null)
                dir = "."; // Current directory fallback

            byte[] buffer = new byte[4096]; // 4KB buffer

            for (byte i = 1; i <= numOfChunks; i++) {
                long bytesToWrite = chunkSize;
                if (i == numOfChunks) {
                    bytesToWrite += remainder; // Add leftover bytes to last chunk
                }
                String chunkName = dir + File.separator + "chunk_" + i + ".part";

                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(chunkName))) {

                    // 1. Write Index (1 byte)
                    out.write(i);

                    // 2. Write Magic Number (2 bytes)
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.putShort(magicNumber);
                    out.write(bb.array());

                    // 3. Write Chunk Size (4 bytes)
                    ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
                    sizeBuffer.putInt((int) bytesToWrite);
                    out.write(sizeBuffer.array());

                    // 4. Write Data
                    while (bytesToWrite > 0) {
                        int readLen = (int) Math.min(buffer.length, bytesToWrite);
                        int bytesRead = in.read(buffer, 0, readLen);
                        if (bytesRead == -1)
                            break;
                        out.write(buffer, 0, bytesRead);
                        bytesToWrite -= bytesRead;
                    }
                    // Print the chunk file name to JTextArea
                    fileListArea.append("Created: " + chunkName + "\n");
                }
            }

            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000_000.0;
            fileListArea.append(String.format("File split into %d chunks in %.3f seconds.%n", numOfChunks, duration));

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}