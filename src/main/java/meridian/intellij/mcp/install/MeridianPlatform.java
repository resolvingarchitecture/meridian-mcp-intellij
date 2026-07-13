package meridian.intellij.mcp.install;

public record MeridianPlatform(String os, String arch, String executableName) {
    public static MeridianPlatform current() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String archName = System.getProperty("os.arch", "").toLowerCase();

        String os;
        if (osName.contains("mac") || osName.contains("darwin")) {
            os = "darwin";
        } else if (osName.contains("win")) {
            os = "win32";
        } else if (osName.contains("linux")) {
            os = "linux";
        } else {
            os = "unknown";
        }

        String arch;
        if (archName.equals("aarch64") || archName.equals("arm64")) {
            arch = "arm64";
        } else if (archName.equals("x86_64") || archName.equals("amd64")) {
            arch = "x64";
        } else {
            arch = archName;
        }

        String executableName = os.equals("win32") ? "meridian-mcp.exe" : "meridian-mcp";
        return new MeridianPlatform(os, arch, executableName);
    }

    public String classifier() {
        return os + "-" + arch;
    }

    public boolean isSupported() {
        return switch (classifier()) {
            case "darwin-arm64", "darwin-x64", "linux-x64", "win32-x64" -> true;
            default -> false;
        };
    }
}