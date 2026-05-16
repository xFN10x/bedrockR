package fn10.bedrockr.utils;

import com.formdev.flatlaf.util.SystemInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class GithubIntegration {

    public static final Path ghProgramFolderPath = RFileOperations.getBaseDirectory("tools", "gh").toPath();
    public static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    public static GithubIntegration get() throws IOException, InterruptedException {
        String releaseURL = null;
        boolean zip = false;
        if (SystemInfo.isWindows) {
            zip = true;
            if (System.getProperty("sun.arch.data.model").endsWith("arm64")) {
                releaseURL = "https://github.com/cli/cli/releases/download/v2.92.0/gh_2.92.0_windows_arm64.zip";
            } else {
                releaseURL = "https://github.com/cli/cli/releases/download/v2.92.0/gh_2.92.0_windows_amd64.zip";
            }
        } else if (SystemInfo.isLinux) {
            if (System.getProperty("sun.arch.data.model").endsWith("arm64")) {
                releaseURL = "https://github.com/cli/cli/releases/download/v2.92.0/gh_2.92.0_linux_arm64.tar.gz";
            } else if (System.getProperty("sun.arch.data.model").endsWith("amd64")) {
                releaseURL = "https://github.com/cli/cli/releases/download/v2.92.0/gh_2.92.0_linux_amd64.tar.gz";
            } else {
                releaseURL = "https://github.com/cli/cli/releases/download/v2.92.0/gh_2.92.0_linux_armv6.tar.gz";
            }
        }
        Path archive = ghProgramFolderPath.resolve("gh." + (zip ? "zip" : "tar.gz"));

        HttpRequest req = HttpRequest.newBuilder(URI.create(releaseURL)).build();
        HttpResponse<Path> res = client.send(req, HttpResponse.BodyHandlers.ofFile(archive, StandardOpenOption.CREATE, StandardOpenOption.WRITE));

        Path folder = res.body();
        ZipFile zipFile = new ZipFile(folder.toFile());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
        }
        return null;
    }

    static void main() throws IOException, InterruptedException {
        Logger.getGlobal().info(System.getProperty("os.arch"));
        get();
    }
}
