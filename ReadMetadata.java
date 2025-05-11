import java.io.*;
import java.nio.charset.StandardCharsets;

public class ReadMetadata {
    private final File metadataFile;

    // Metadata field sizes (in bytes)
    private static final int INDEX_SIZE = 1;
    private static final int MAGIC_NUMBER_SIZE = 2;
    private static final int FILENAME_SIZE = 256;
    private static final int FILESIZE_SIZE = 8;

    public ReadMetadata(File metadataFile) {
        if (!metadataFile.exists() || !metadataFile.isFile()) {
            throw new IllegalArgumentException("Metadata file not found.");
        }
        this.metadataFile = metadataFile;
    }

    // Reads the index number
    public int readIndex() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(metadataFile, "r")) {
            return raf.readUnsignedByte();
        } catch (EOFException e) {
            System.err.println("Error: Metadata file is incomplete (Index not found).");
            return -1;
        }
    }

    // Reads the magic number
    public short readMagicNumber() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(metadataFile, "r")) {
            raf.seek(INDEX_SIZE); // Skip index byte
            return raf.readShort();
        } catch (EOFException e) {
            System.err.println("Error: Metadata file is incomplete (Magic Number not found).");
            return -1;
        }
    }

    // Reads the original filename
    public String readOriginalFileName() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(metadataFile, "r")) {
            raf.seek(INDEX_SIZE + MAGIC_NUMBER_SIZE);
            byte[] filenameBytes = new byte[FILENAME_SIZE];
            raf.readFully(filenameBytes);
            return new String(filenameBytes, StandardCharsets.UTF_8).trim();
        } catch (EOFException e) {
            System.err.println("Error: Metadata file is incomplete (File Name not found).");
            return null;
        }
    }

    // Reads the original file size
    public long readOriginalFileSize() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(metadataFile, "r")) {
            raf.seek(INDEX_SIZE + MAGIC_NUMBER_SIZE + FILENAME_SIZE);
            return raf.readLong();
        } catch (EOFException e) {
            System.err.println("Error: Metadata file is incomplete (File Size not found).");
            return -1;
        }
    }

    // Reads the number of chunks
    public int readNumberOfChunks() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(metadataFile, "r")) {
            raf.seek(INDEX_SIZE + MAGIC_NUMBER_SIZE + FILENAME_SIZE + FILESIZE_SIZE);
            return raf.readInt();
        } catch (EOFException e) {
            System.err.println("Error: Metadata file is incomplete (Number of Chunks not found).");
            return -1;
        }
    }
}
