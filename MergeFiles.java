import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class MergeFiles {
    private File metadataFile = null, outputFolder = null;
    private List<File> chunkFiles;
    private int index;
    private short magicNumber;
    private String originalFileName;
    private long originalFileSize;
    private int numberOfChunks;
    private boolean isMetadataPresent = true;

    MergeFiles(File metadataFile, File outputFolder, List<File> chunkFiles) {
        this.metadataFile = metadataFile;
        this.outputFolder = outputFolder;
        this.chunkFiles = new ArrayList<>(chunkFiles);
    }

    public String mergeFile() {
        try {
            validateMetadataFile();
        } catch (IOException e) {

            System.out.println("Validation failed: " + e.getMessage());

            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Validation failed. Would you like to continue?",
                    "Validation Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                isMetadataPresent = false;
                return "Metadata validation failed.";
            }

        }

        Map<Integer, File> mapping = createFileMapping();
        if (mapping == null) {
            System.out.println("Error: Invalid file mapping.");
            return "Error: Invalid file mapping.";
        } else {
            System.out.println("Merge can proceed.");
        }

        String outputFileLocation = createNewFile(mapping);
        if (outputFileLocation != null) {
            return "File merged successfully at: " + outputFileLocation;
        }

        return "An error occured";
    }

    private void validateMetadataFile() throws IOException {

        if (metadataFile == null || !metadataFile.exists() || !metadataFile.isFile()) {
            throw new IOException("Metadata file is missing or not a valid file.");
        }

        ReadMetadata meta = new ReadMetadata(metadataFile);

        if (!meta.isValid()) {
            throw new IOException("Metadata file is empty or too short.");
        }

        // Read the index
        index = meta.readIndex();

        // Validation check
        if (index == -1) {
            throw new IOException("Metadata file is incomplete or corrupted.");
        }

        if (index != 0) {
            throw new IOException("Metadata index is incorrect. Expected 0 but found: " + index);
        }

        magicNumber = meta.readMagicNumber();
        if (magicNumber == -1) {
            throw new IOException("Failed to read the magic number.");
        }

        originalFileName = meta.readOriginalFileName();
        if (originalFileName == null) {
            throw new IOException("Failed to read the original file name.");
        }

        originalFileSize = meta.readOriginalFileSize();
        if (originalFileSize == -1) {
            throw new IOException("Failed to read the original file size.");
        }

        numberOfChunks = meta.readNumberOfChunks();
        if (numberOfChunks == -1) {
            throw new IOException("Failed to read the number of chunks.");
        }

        // You can print these for debugging if you want
        System.out.println("Validation successful:");
        System.out.println("Magic Number: " + magicNumber);
        System.out.println("Original Filename: " + originalFileName);
        System.out.println("Original File Size: " + originalFileSize);
        System.out.println("Number of Chunks: " + numberOfChunks);
    }

    private Map<Integer, File> createFileMapping() {
        Map<Integer, File> fileMapping = new HashMap<>();

        // If metadata is not present, we don't know the number of chunks
        if (!isMetadataPresent) {
            System.out.println("Metadata file is missing. Performing sequence gap check...");

            List<Integer> foundIndexes = new ArrayList<>();

            for (File chunk : chunkFiles) {
                try (RandomAccessFile raf = new RandomAccessFile(chunk, "r")) {
                    int chunkIndex = raf.readUnsignedByte();
                    short chunkMagicNumber = raf.readShort();

                    // If the magic number is incorrect, fail immediately
                    if (chunkMagicNumber != magicNumber) {
                        System.out.println("Error: Chunk " + chunk.getName() + " has a mismatched magic number.");
                        return null;
                    }

                    // If the index is already present, fail
                    if (fileMapping.containsKey(chunkIndex)) {
                        System.out.println("Error: Duplicate chunk index found for " + chunk.getName());
                        return null;
                    }

                    // Add to map and keep track of found indexes
                    fileMapping.put(chunkIndex, chunk);
                    foundIndexes.add(chunkIndex);

                } catch (IOException e) {
                    System.out.println("Error: Failed to read chunk file: " + chunk.getName());
                    return null;
                }
            }

            // Sort the found indexes and check for gaps
            Collections.sort(foundIndexes);
            for (int i = 1; i < foundIndexes.size(); i++) {
                if (foundIndexes.get(i) != foundIndexes.get(i - 1) + 1) {
                    System.out.println(
                            "Error: Missing chunk between " + foundIndexes.get(i - 1) + " and " + foundIndexes.get(i));
                    return null;
                }
            }

        } else {
            // Normal case when metadata is present
            for (File chunk : chunkFiles) {
                try (RandomAccessFile raf = new RandomAccessFile(chunk, "r")) {
                    int chunkIndex = raf.readUnsignedByte();
                    short chunkMagicNumber = raf.readShort();

                    if (chunkMagicNumber != magicNumber) {
                        System.out.println("Error: Chunk " + chunk.getName() + " has a mismatched magic number.");
                        return null;
                    }

                    if (fileMapping.containsKey(chunkIndex)) {
                        System.out.println("Error: Duplicate chunk index found for " + chunk.getName());
                        return null;
                    }

                    fileMapping.put(chunkIndex, chunk);

                } catch (IOException e) {
                    System.out.println("Error: Failed to read chunk file: " + chunk.getName());
                    return null;
                }
            }

            // Verify if all expected chunks are present
            for (int i = 1; i <= numberOfChunks; i++) {
                if (!fileMapping.containsKey(i)) {
                    System.out.println("Error: Missing chunk with index " + i);
                    return null;
                }
            }
        }

        System.out.println("File mapping created successfully.");
        return fileMapping;
    }

    // Merges all the chunks into a single output file
    private String createNewFile(Map<Integer, File> mapping) {

        // Determine the output filename
        String outputFileName;
        if (originalFileName != null && !originalFileName.isEmpty()) {
            outputFileName = outputFolder.getAbsolutePath() + File.separator + originalFileName;
        } else {
            outputFileName = outputFolder.getAbsolutePath() + File.separator + "output";
        }

        if(isMetadataPresent)
        {
            numberOfChunks = mapping.size();
        }

        // Start merging process
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName))) {
            long startTime = System.nanoTime();
            byte[] buffer = new byte[4096];

            // Iterate through all the chunks in order
            for (int i = 1; i <= numberOfChunks; i++) {
                File chunkFile = mapping.get(i);

                if (chunkFile == null || !chunkFile.exists()) {
                    return "Error: Missing or invalid chunk: chunk_" + i + ".part";
                }

                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(chunkFile))) {
                    // Skip the 7-byte header 
                    // 1 byte for index, 2 bytes for magic number, 4 bytes for chunk size
                    long skipped = in.skip(7);

                    // Validation: If the header is not fully skipped, something is wrong
                    if (skipped < 7) {
                        return "Error: Failed to skip header in chunk: " + chunkFile.getName();
                    }

                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    return "Error reading chunk: " + chunkFile.getName() + " - " + e.getMessage();
                }
            }

            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000_000.0;
            System.out.printf("File combined into %s in %.3f seconds.%n", outputFileName, duration);

            return outputFileName;

        } catch (IOException e) {
            return "Error during file merge: " + e.getMessage();
        }
    }
}