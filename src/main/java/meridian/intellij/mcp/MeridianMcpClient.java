package meridian.intellij.mcp;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import meridian.intellij.findings.MeridianFinding;
import meridian.intellij.settings.MeridianSettingsState;
import meridian.intellij.mcp.install.MeridianMcpBinaryResolver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public final class MeridianMcpClient implements Closeable {
    private static final Logger LOG = Logger.getInstance(MeridianMcpClient.class);

    private final Project project;
    private final MeridianSettingsState settings;
    private final AtomicLong requestId = new AtomicLong(1);

    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;

    public MeridianMcpClient(Project project, MeridianSettingsState settings) {
        this.project = project;
        this.settings = settings;
    }

    public boolean isRunning() {
        return process != null && process.isAlive();
    }

    public CompletableFuture<String> scanProject(String projectPath) {
        String params = """
                {"name":"scan_project","arguments":{"project_path":%s}}
                """.formatted(json(projectPath));

        return callTool(params);
    }

    public CompletableFuture<List<MeridianFinding>> reviewFile(String relativePath, String content) {
        String params = """
                {"name":"review_file","arguments":{"file":%s,"content":%s}}
                """.formatted(json(relativePath), json(content));

        return callTool(params).thenApply(this::parseFindingsPlaceholder);
    }

    public CompletableFuture<String> addContext(String relativePath, String selectedText) {
        String params = """
                {"name":"add_context","arguments":{"file":%s,"text":%s}}
                """.formatted(json(relativePath), json(selectedText));

        return callTool(params);
    }

    public CompletableFuture<String> clearContext() {
        String params = """
                {"name":"clear_context","arguments":{}}
                """;

        return callTool(params);
    }

    private CompletableFuture<String> callTool(String toolParamsJson) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ensureStarted();

                long id = requestId.getAndIncrement();
                String request = """
                        {"jsonrpc":"2.0","id":%d,"method":"tools/call","params":%s}
                        """.formatted(id, toolParamsJson).trim();

                writer.write(request);
                writer.newLine();
                writer.flush();

                String line = reader.readLine();
                if (line == null) {
                    throw new IllegalStateException("meridian-mcp closed stdout before responding.");
                }

                if (line.contains("\"error\"")) {
                    throw new IllegalStateException("MCP returned error: " + abbreviate(line));
                }

                return line;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private synchronized void ensureStarted() throws Exception {
        if (isRunning()) {
            return;
        }

        if (!settings.hasApiKey()) {
            throw new IllegalStateException("Meridian API key is not configured.");
        }

        String binary = new MeridianMcpBinaryResolver(settings).resolveExistingBinary();

        ProcessBuilder builder = new ProcessBuilder(binary);
        builder.environment().put("MERIDIAN_API_KEY", settings.getApiKey());
        builder.redirectError(ProcessBuilder.Redirect.PIPE);

        process = builder.start();
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

        Thread stderrThread = new Thread(() -> {
            try (BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = stderr.readLine()) != null) {
                    LOG.info("meridian-mcp: " + line);
                }
            } catch (Exception exception) {
                LOG.warn("Failed reading meridian-mcp stderr", exception);
            }
        }, "meridian-mcp-stderr-" + project.getName());

        stderrThread.setDaemon(true);
        stderrThread.start();

        initialize();
    }

    private void initialize() throws Exception {
        long id = requestId.getAndIncrement();

        String request = """
                {"jsonrpc":"2.0","id":%d,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"meridian-intellij","version":"0.1.0"}}}
                """.formatted(id).trim();

        writer.write(request);
        writer.newLine();
        writer.flush();

        String response = reader.readLine();
        if (response == null) {
            throw new IllegalStateException("meridian-mcp did not respond to initialize.");
        }

        if (response.contains("\"error\"")) {
            throw new IllegalStateException("meridian-mcp initialize failed: " + abbreviate(response));
        }
    }

    private List<MeridianFinding> parseFindingsPlaceholder(String response) {
        /*
         * MVP parser placeholder:
         * This keeps the IDE integration usable while meridian-mcp payload shape stabilizes.
         * Once the MCP response contract is final, replace this with strict JSON parsing.
         */
        List<MeridianFinding> findings = new ArrayList<>();

        if (response.contains("\"findings\"") && response.contains("\"title\"")) {
            findings.add(new MeridianFinding(
                    "INFO",
                    "architecture-review",
                    "unknown",
                    1,
                    "Meridian returned findings",
                    "The MCP response contained findings. Strict parsing is not yet wired in this MVP client.",
                    "Open diagnostics/logs to inspect MCP response shape.",
                    "Implement strict finding JSON parsing once the MCP response schema is finalized.",
                    "",
                    1.0
            ));
        }

        return findings;
    }

    private static String json(String value) {
        if (value == null) {
            return "\"\"";
        }

        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    private static String abbreviate(String value) {
        if (value == null) {
            return "";
        }

        return value.length() <= 500 ? value : value.substring(0, 500) + "...";
    }

    @Override
    public synchronized void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception ignored) {
        }

        if (process != null) {
            process.destroy();
        }

        process = null;
        writer = null;
        reader = null;
    }
}