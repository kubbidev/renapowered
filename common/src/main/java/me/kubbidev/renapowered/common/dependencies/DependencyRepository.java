package me.kubbidev.renapowered.common.dependencies;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Represents a repository which contains {@link Dependency}s.
 */
public enum DependencyRepository {

    /**
     * Maven Central.
     */
    MAVEN_CENTRAL("https://repo1.maven.org/maven2/");

    private final String url;

    DependencyRepository(String url) {
        this.url = url;
    }

    /**
     * Opens a connection to the given {@code dependency}.
     *
     * @param dependency the dependency to download
     * @return the connection
     * @throws IOException if unable to open a connection
     */
    private URLConnection openConnection(Dependency dependency) throws IOException {
        URL dependencyUrl = new URL(this.url + dependency.getMavenRepoPath());
        return dependencyUrl.openConnection();
    }

    /**
     * Downloads the raw bytes of the {@code dependency}.
     *
     * @param dependency the dependency to download
     * @return the downloaded bytes
     * @throws DependencyDownloadException if unable to download
     */
    public byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException {
        try {
            URLConnection connection = openConnection(dependency);
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new DependencyDownloadException("Empty stream");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new DependencyDownloadException(e);
        }
    }

    /**
     * Downloads the raw bytes of the {@code dependency}, and ensures the downloaded
     * bytes match the checksum.
     *
     * @param dependency the dependency to download
     * @return the downloaded bytes
     * @throws DependencyDownloadException if unable to download
     */
    public byte[] download(Dependency dependency) throws DependencyDownloadException {
        byte[] bytes = downloadRaw(dependency);

        // compute a hash for the downloaded file
        byte[] hash = Dependency.createDigest().digest(bytes);

        // ensure the hash matches the expected checksum
        if (!dependency.checksumMatches(hash)) {
            throw new DependencyDownloadException("Downloaded file had an invalid hash. " +
                    "Expected: " + Base64.getEncoder().encodeToString(dependency.getChecksum()) + " " +
                    "Actual: " + Base64.getEncoder().encodeToString(hash));
        }

        return bytes;
    }

    /**
     * Downloads the the {@code dependency} to the {@code file}, ensuring the
     * downloaded bytes match the checksum.
     *
     * @param dependency the dependency to download
     * @param file the file to write to
     * @throws DependencyDownloadException if unable to download
     */
    public void download(Dependency dependency, Path file) throws DependencyDownloadException {
        try {
            Files.write(file, download(dependency));
        } catch (IOException e) {
            throw new DependencyDownloadException(e);
        }
    }
}