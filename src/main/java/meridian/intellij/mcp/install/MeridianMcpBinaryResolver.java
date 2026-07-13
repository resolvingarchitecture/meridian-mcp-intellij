package meridian.intellij.mcp.install;

import com.intellij.openapi.application.PathManager;
import meridian.intellij.settings.MeridianSettingsState;

import java.io.File;

public final class MeridianMcpBinaryResolver {
    private final MeridianSettingsState settings;

    public MeridianMcpBinaryResolver(MeridianSettingsState settings) {
        this.settings = settings;
    }

    public String resolveExistingBinary() {
        String configuredPath = settings.getMcpBinaryPath();
        if (!configuredPath.isBlank()) {
            File configuredBinary = new File(configuredPath);
            if (configuredBinary.isFile() && configuredBinary.canExecute()) {
                return configuredBinary.getAbsolutePath();
            }

            throw new IllegalStateException("""
                    Meridian MCP binary was not found or is not executable at the configured path.

                    Configured path:
                    %s

                    Open Settings → Meridian and choose a valid meridian-mcp executable.
                    """.formatted(configuredPath));
        }

        File managedBinary = managedBinaryFile();
        if (managedBinary.isFile() && managedBinary.canExecute()) {
            return managedBinary.getAbsolutePath();
        }

        String pathBinary = findOnPath(MeridianPlatform.current().executableName());
        if (pathBinary != null) {
            return pathBinary;
        }

        throw new IllegalStateException("""
                Meridian MCP binary is not installed.

                Use Meridian: Install MCP Binary, or configure a local binary path in Settings → Meridian.

                Development fallback:
                cargo install meridian-mcp
                """);
    }

    public File managedBinaryFile() {
        MeridianPlatform platform = MeridianPlatform.current();

        return new File(
                PathManager.getPluginsPath(),
                "meridian/mcp/current/" + platform.classifier() + "/" + platform.executableName()
        );
    }

    public File managedInstallDirectory() {
        return managedBinaryFile().getParentFile();
    }

    private static String findOnPath(String executableName) {
        String path = System.getenv("PATH");
        if (path == null || path.isBlank()) {
            return null;
        }

        String[] entries = path.split(File.pathSeparator);
        for (String entry : entries) {
            File candidate = new File(entry, executableName);
            if (candidate.isFile() && candidate.canExecute()) {
                return candidate.getAbsolutePath();
            }
        }

        return null;
    }
}