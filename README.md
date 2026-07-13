# meridian-intellij

JetBrains IntelliJ Platform plugin wrapper for Meridian, an AI architecture review assistant.

`meridian-intellij` is the JetBrains IDE presentation layer for Meridian. It starts or connects to a local `meridian-mcp` process, communicates with it over MCP JSON-RPC via stdio, and renders architecture findings inside IntelliJ-based IDEs.

## Current implementation status

This repository now contains an MVP IntelliJ Platform plugin implementation.

| Area                              | Status                                                                  |
|-----------------------------------|-------------------------------------------------------------------------|
| Gradle IntelliJ build             | Implemented                                                             |
| IntelliJ plugin descriptor        | Implemented in `src/main/resources/META-INF/plugin.xml`                 |
| Settings UI                       | Implemented                                                             |
| Meridian actions                  | Implemented                                                             |
| MCP process wrapper               | MVP implemented                                                         |
| MCP initialize handshake          | MVP implemented                                                         |
| Project scan action               | Implemented via MCP `scan_project` tool call                            |
| Current-file review action        | Implemented via MCP `review_file` tool call                             |
| Context actions                   | Implemented via MCP `add_context` and `clear_context` tool calls         |
| Findings tool window              | Implemented                                                             |
| Editor annotations/highlighting   | Not implemented yet                                                     |
| Realtime review on save           | Not implemented yet                                                     |
| Bundled MCP binary                | Not implemented yet; uses configured path or `meridian-mcp` from `PATH` |
| Strict finding JSON parsing       | Not implemented yet; response parser placeholder included               |

## Development setup

### Prerequisites

- Java 21 or newer.
- A JetBrains IDE suitable for IntelliJ Platform plugin development.
- Gradle.
- A local `meridian-mcp` binary available on `PATH`, or a configured MCP binary path in Meridian settings.
- A Meridian API key.

### Build

```bash
./gradlew buildPlugin
```

If the repository does not yet include a Gradle wrapper, use a local Gradle installation:

```bash
gradle buildPlugin
```

### Run in sandbox IDE

```bash
./gradlew runIde
```

Or:

```bash
gradle runIde
```

### Configure Meridian

Inside the sandbox IDE:

1. Open **Settings / Preferences**.
2. Open **Meridian**.
3. Enter your Meridian API key.
4. Optionally enter the full path to `meridian-mcp`.
5. Apply settings.

If no MCP binary path is configured, the plugin tries to execute `meridian-mcp` from `PATH`.

## Available IDE actions

The plugin contributes a **Tools → Meridian** menu containing:

| Action                             | Purpose                                      |
|------------------------------------|----------------------------------------------|
| Meridian: Scan Project             | Scan/cache the current project architecture  |
| Meridian: Review Current File      | Review the active editor file through MCP    |
| Meridian: Add Selection as Context | Send selected text as architecture context   |
| Meridian: Clear Context            | Clear active architecture context            |
| Meridian: Clear Findings           | Clear locally displayed findings             |
| Meridian: Open Findings            | Focus the Meridian findings tool window      |
| Meridian: Diagnostics              | Show plugin and MCP configuration health     |

## Security and privacy notes

- The API key is never intentionally logged.
- The API key is passed to `meridian-mcp` as `MERIDIAN_API_KEY`.
- MCP stdout is treated as JSON-RPC protocol output.
- MCP stderr is routed to IDE logging.
- File contents are sent only when the user explicitly invokes review.
- Selected text is sent only when the user explicitly invokes add-context.
- The plugin does not call AI providers directly.
- The plugin does not implement billing or usage enforcement locally.
- The plugin remains a thin IDE adapter over `meridian-mcp`.

## Next implementation steps

1. Replace the MVP finding parser with strict JSON parsing after the MCP response schema is finalized.
2. Add editor annotations/highlighting.
3. Add realtime review on save.
4. Add bundled platform-specific MCP binaries.
5. Add MCP binary checksum/signature verification.
6. Add plugin verifier CI.
```