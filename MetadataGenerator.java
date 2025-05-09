import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.swing.JTextArea;

public class MetadataGenerator {

    // Fixed Size Definitions
    private static final int MAX_FILENAME_LENGTH = 256;
    private static final int METADATA_SIZE = 271;
    private static final int MIN_CHUNKS = 2;
    private static final int MAX_CHUNKS = 100;

    public static void generateMetadata(File originalFile, File outputPath, short magicNumber, int numberOfChunks,
            JTextArea fileListArea) {
        if (!originalFile.exists() || !originalFile.isFile()) {
            throw new IllegalArgumentException("Invalid file provided.");
        }

        if (!outputPath.exists() || !outputPath.isDirectory()) {
            throw new IllegalArgumentException("Output path is invalid or not a directory.");
        }

        if (numberOfChunks < MIN_CHUNKS || numberOfChunks > MAX_CHUNKS) {
            throw new IllegalArgumentException("Number of chunks must be between 2 and 100.");
        }

        String filename = originalFile.getName();
        long fileSize = originalFile.length();
        long chunkSize = fileSize / numberOfChunks;
        long residual = fileSize % numberOfChunks;

        // Create metadata.part in the specified output directory
        File metadataFile = new File(outputPath, "metadata.part");

        try (FileOutputStream fos = new FileOutputStream(metadataFile)) {
            ByteBuffer buffer = ByteBuffer.allocate(METADATA_SIZE);

            // 1. Index Number (1 byte) → Always 0
            buffer.put((byte) 0);

            // 2. Magic Number (2 bytes)
            buffer.putShort(magicNumber);

            // 3. Original Filename (256 bytes, padded with null bytes if shorter)
            byte[] filenameBytes = filename.getBytes(StandardCharsets.UTF_8);
            if (filenameBytes.length > MAX_FILENAME_LENGTH) {
                throw new IllegalArgumentException("Filename is too long.");
            }
            buffer.put(filenameBytes);
            buffer.position(buffer.position() + (MAX_FILENAME_LENGTH - filenameBytes.length)); // Padding

            // 4. Original File Size (8 bytes)
            buffer.putLong(fileSize);

            // 5. Number of Chunks (4 bytes)
            buffer.putInt(numberOfChunks);

            // Write buffer to the metadata file
            fos.write(buffer.array());
            String result = "Metadata file 'metadata.part' created successfully at: " + metadataFile.getAbsolutePath()
                    + "\n" + "Chunk Size: " + chunkSize + " bytes, Residual: " + residual + " bytes" + "\n";
            fileListArea.setText(result);

        } catch (IOException e) {
            System.err.println("Error writing metadata file: " + e.getMessage());
        }
    }
}
