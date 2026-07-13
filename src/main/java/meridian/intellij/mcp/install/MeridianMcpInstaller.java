package meridian.intellij.mcp.install;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import meridian.intellij.notifications.MeridianNotifications;
import meridian.intellij.settings.MeridianSettingsState;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

public final class MeridianMcpInstaller {
    private static final Logger LOG = Logger.getInstance(MeridianMcpInstaller.class);

    private final Project project;
    private final MeridianMcpBinaryResolver resolver;

    public MeridianMcpInstaller(Project project, MeridianSettingsState settings) {
        this.project = project;
        this.resolver = new MeridianMcpBinaryResolver(settings);
    }

    public CompletableFuture<File> installManagedBinary() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MeridianPlatform platform = MeridianPlatform.current();
                if (!platform.isSupported()) {
                    throw new IllegalStateException("Unsupported platform for automatic MCP install: " + platform.classifier());
                }

                File installDirectory = resolver.managedInstallDirectory();
                if (!installDirectory.exists() && !installDirectory.mkdirs()) {
                    throw new IllegalStateException("Could not create MCP install directory: " + installDirectory);
                }

                File target = resolver.managedBinaryFile();
                File temporary = new File(target.getParentFile(), target.getName() + ".download");

                URI uri = downloadUri(platform);

                try (InputStream inputStream = uri.toURL().openStream()) {
                    Files.copy(inputStream, temporary.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                if (!temporary.setExecutable(true, true)) {
                    LOG.warn("Could not mark MCP binary executable: " + temporary);
                }

                Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                MeridianNotifications.info(project, "Meridian MCP installed", "Installed Meridian MCP binary at: " + target);
                return target;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private URI downloadUri(MeridianPlatform platform) {
        /*
         * Replace this placeholder with the real Meridian MCP release URL.
         *
         * Recommended release asset names:
         * meridian-mcp-darwin-arm64
         * meridian-mcp-darwin-x64
         * meridian-mcp-linux-x64
         * meridian-mcp-win32-x64.exe
         */
        String baseUrl = "https://example.com/meridian-mcp/releases/latest/";
        String fileName = platform.executableName().endsWith(".exe")
                ? "meridian-mcp-" + platform.classifier() + ".exe"
                : "meridian-mcp-" + platform.classifier();

        return URI.create(baseUrl + fileName);
    }
}