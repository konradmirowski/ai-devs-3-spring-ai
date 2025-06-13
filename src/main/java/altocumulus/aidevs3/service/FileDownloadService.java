package altocumulus.aidevs3.service;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileDownloadService {

    /**
     * Downloads a file from a URL, saves it with a unique name to a specified directory,
     * and returns it as a MultipartFile.
     *
     * @param urlString The URL of the file to download.
     * @param destinationDirectoryPath The local directory path where the file will be saved.
     * @return A MultipartFile object representing the downloaded file.
     * @throws IOException              if an I/O error occurs during download or saving.
     * @throws NoSuchAlgorithmException if the MD5 hashing algorithm is not available.
     * @throws URISyntaxException       if the provided URL string violates RFC 2396.
     */
    public MultipartFile downloadFile(String urlString, String destinationDirectoryPath) throws IOException, NoSuchAlgorithmException, URISyntaxException {
        // 1. Setup connection and download content
        URL url = new URI(urlString).toURL();
        URLConnection connection = url.openConnection();
        
        // Use a try-with-resources statement to ensure the stream is closed
        byte[] content;
        try (InputStream inputStream = connection.getInputStream()) {
            content = inputStream.readAllBytes();
        }

        // 2. Determine content type automatically
        String contentType = connection.getContentType();
        if (contentType == null) {
            // Fallback if the server doesn't provide a content type
            contentType = URLConnection.guessContentTypeFromName(url.getFile());
            if(contentType == null){
                contentType = "application/octet-stream"; // Generic binary data
            }
        }

        // 3. Generate a unique filename to prevent overwriting
        String originalFileName = getOriginalFileName(connection);
        String uniqueFileName = generateUniqueFileName(originalFileName);
        
        // 4. Save the file to the specified path
        Path destinationDirectory = Paths.get(destinationDirectoryPath);
        Files.createDirectories(destinationDirectory); // Ensure the directory exists
        
        Path destinationPath = destinationDirectory.resolve(uniqueFileName);
        Files.write(destinationPath, content);

        // 5. Create and return the MockMultipartFile object
        return new MockMultipartFile(
            "file",            // The name of the form field, as requested
            uniqueFileName,    // The new, unique filename
            contentType,       // The automatically detected content type
            content            // The raw byte content of the file
        );
    }
    
    /**
     * Extracts the filename by first checking the 'Content-Disposition' header,
     * then falling back to the URL path.
     *
     * @param connection The active URLConnection.
     * @return The determined filename.
     */
    private String getOriginalFileName(URLConnection connection) {
        String disposition = connection.getHeaderField("Content-Disposition");
        String fileName = null;

        // Check Content-Disposition header first
        if (disposition != null) {
            // Using regex to extract filename from "filename=..."
            Pattern pattern = Pattern.compile("filename=\"?([^;\"]+)\"?");
            Matcher matcher = pattern.matcher(disposition);
            if (matcher.find()) {
                fileName = matcher.group(1);
            }
        }

        // Fallback to URL path if header is not present or doesn't contain filename
        if (fileName == null) {
            String path = connection.getURL().getPath();
            fileName = path.substring(path.lastIndexOf('/') + 1);
        }

        // Final fallback for empty filenames (e.g., from "http://example.com/download/")
        if (fileName.trim().isEmpty()) {
            fileName = "downloaded_file";
        }
        
        return fileName;
    }


    /**
     * Generates a unique filename by appending a hash of the current timestamp
     * to the base name of the original filename.
     * e.g., "audio.mp3" -> "audio_a1b2c3d4e5f6.mp3"
     */
    private String generateUniqueFileName(String originalFileName) throws NoSuchAlgorithmException {
        String baseName = originalFileName;
        String extension = "";

        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
            baseName = originalFileName.substring(0, dotIndex);
            extension = originalFileName.substring(dotIndex);
        }

        // Use MD5 hash of the current nanosecond time for uniqueness
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(String.valueOf(System.nanoTime()).getBytes());
        String timeHash = new BigInteger(1, md.digest()).toString(16).substring(0, 12);

        return String.format("%s_%s%s", baseName, timeHash, extension);
    }
}
