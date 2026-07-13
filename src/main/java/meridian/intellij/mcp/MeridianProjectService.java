package meridian.intellij.mcp;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import meridian.intellij.findings.MeridianFinding;
import meridian.intellij.findings.MeridianFindingsState;
import meridian.intellij.notifications.MeridianNotifications;
import meridian.intellij.settings.MeridianSettingsState;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service(Service.Level.PROJECT)
public final class MeridianProjectService {
    private static final Logger LOG = Logger.getInstance(MeridianProjectService.class);

    private final Project project;
    private MeridianMcpClient client;

    public MeridianProjectService(Project project) {
        this.project = project;
    }

    public synchronized void initialize() {
        project.getService(MeridianFindingsState.class).setStatus("Ready");
        Disposer.register(project, this::shutdown);
    }

    public synchronized MeridianMcpClient client() {
        if (client == null) {
            client = new MeridianMcpClient(project, settings());
        }

        return client;
    }

    public synchronized void restartMcp() {
        shutdown();
        client = null;
    }

    public synchronized void shutdown() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public CompletableFuture<Void> scanProject() {
        MeridianFindingsState state = project.getService(MeridianFindingsState.class);
        state.setStatus("Scanning project");

        String basePath = project.getBasePath();
        if (basePath == null || basePath.isBlank()) {
            String message = "Project base path is unavailable.";
            state.setLastError(message);
            MeridianNotifications.warn(project, "Meridian scan unavailable", message);
            return CompletableFuture.completedFuture(null);
        }

        return client().scanProject(basePath)
                .thenAccept(response -> {
                    state.setStatus("Project scan complete");
                    MeridianNotifications.info(project, "Meridian", "Project scan complete.");
                })
                .exceptionally(error -> {
                    LOG.warn("Meridian project scan failed", error);
                    state.setStatus("Scan failed");
                    state.setLastError(error.getMessage());
                    MeridianNotifications.error(project, "Meridian scan failed", safeMessage(error));
                    return null;
                });
    }

    public CompletableFuture<Void> reviewCurrentFile(String relativePath, String content) {
        MeridianFindingsState state = project.getService(MeridianFindingsState.class);
        state.setStatus("Reviewing file");

        return client().reviewFile(relativePath, content)
                .thenAccept(findings -> {
                    double threshold = settings().getConfidenceThreshold();

                    List<MeridianFinding> filtered = findings.stream()
                            .map(MeridianFinding::normalized)
                            .filter(MeridianFinding::isRenderable)
                            .filter(finding -> finding.confidence() >= threshold)
                            .toList();

                    state.replaceFindings(filtered);
                    state.setStatus("Review complete");
                    MeridianNotifications.info(project, "Meridian review complete", filtered.size() + " finding(s) rendered.");
                })
                .exceptionally(error -> {
                    LOG.warn("Meridian file review failed", error);
                    state.setStatus("Review failed");
                    state.setLastError(error.getMessage());
                    MeridianNotifications.error(project, "Meridian review failed", safeMessage(error));
                    return null;
                });
    }

    public CompletableFuture<Void> addSelectionAsContext(String relativePath, String selectedText) {
        return client().addContext(relativePath, selectedText)
                .thenAccept(response -> MeridianNotifications.info(project, "Meridian", "Selection added as architecture context."))
                .exceptionally(error -> {
                    MeridianNotifications.error(project, "Meridian context failed", safeMessage(error));
                    return null;
                });
    }

    public CompletableFuture<Void> clearContext() {
        return client().clearContext()
                .thenAccept(response -> MeridianNotifications.info(project, "Meridian", "Architecture context cleared."))
                .exceptionally(error -> {
                    MeridianNotifications.error(project, "Meridian clear context failed", safeMessage(error));
                    return null;
                });
    }

    public String diagnostics() {
        MeridianSettingsState settings = settings();

        return """
                Meridian diagnostics

                Project: %s
                API key configured: %s
                Configured MCP path: %s
                MCP running: %s
                Realtime review: %s
                Confidence threshold: %.2f
                Status: %s
                Last error: %s
                """.formatted(
                project.getName(),
                settings.hasApiKey() ? "yes" : "no",
                settings.getMcpBinaryPath().isBlank() ? "(PATH fallback: meridian-mcp)" : settings.getMcpBinaryPath(),
                client != null && client.isRunning() ? "yes" : "no",
                settings.isRealtimeReviewEnabled() ? "enabled" : "disabled",
                settings.getConfidenceThreshold(),
                project.getService(MeridianFindingsState.class).getStatus(),
                project.getService(MeridianFindingsState.class).getLastError()
        );
    }

    private MeridianSettingsState settings() {
        return ServiceManager.getService(MeridianSettingsState.class);
    }

    private static String safeMessage(Throwable error) {
        String message = error.getMessage();
        return message == null || message.isBlank() ? "Unexpected Meridian error." : message;
    }
}