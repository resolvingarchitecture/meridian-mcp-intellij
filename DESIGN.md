# DESIGN.md - meridian-intellij

## 1. Overview

* **Purpose:** `meridian-intellij` is the JetBrains IntelliJ Platform presentation layer for Meridian. It integrates Meridian findings into the developer workflow by activating inside JetBrains IDEs, invoking the local `meridian-mcp` runtime over MCP stdio, and rendering returned findings in editor and tool-window surfaces.
* **Scope:** This package owns IntelliJ plugin activation, user-facing actions, MCP process/client wiring, save-file or analysis triggers, editor annotations/highlighting, findings display, status display, and plugin configuration. It does not own architecture scanning, architecture judgment, deterministic rules, AI provider calls, billing, account state, or durable product data.
* **Key Capabilities:**
    * Start and communicate with the local `meridian-mcp` binary.
    * Trigger project scans through MCP.
    * Trigger current-file reviews through MCP.
    * Add selected editor text as architecture context through MCP where supported.
    * Clear backend/local review context through MCP where supported.
    * Render findings as editor annotations, highlights, or inspections-style UI.
    * Show findings in a JetBrains tool window or equivalent findings surface.
    * Show Meridian status in the IDE status bar or notification surfaces.
    * Support realtime review on save or document synchronization when enabled.
    * Provide configuration for API key, MCP binary path, realtime behavior, and finding confidence threshold.

## 2. Position in the Meridian Architecture

`meridian-intellij` is a thin presentation layer. It is not the local architecture engine.

The local developer runtime is `meridian-mcp`, a Rust binary that contains the MCP server, CLI, scanner, architecture model cache, and backend relay path. The IntelliJ plugin talks to that runtime over MCP stdio.

High-level flow:

    Developer uses IntelliJ-based IDE
      → meridian-intellij activates
        → starts or connects to meridian-mcp
          → meridian-mcp scans/caches local architecture model
          → meridian-mcp relays review requests to meridian-backend
            → meridian-backend validates access, runs rules-assisted review, calls AI providers, and returns findings
          ← findings
        ← findings
      → meridian-intellij renders findings in editor and tool-window UI

Architectural boundaries:

| Area                                      | Owner                   |
|-------------------------------------------|-------------------------|
| IntelliJ plugin activation and actions    | `meridian-intellij`     |
| Editor annotations/highlighting           | `meridian-intellij`     |
| Findings tool window or panel             | `meridian-intellij`     |
| Status display and notifications          | `meridian-intellij`     |
| MCP stdio client wiring                   | `meridian-intellij`     |
| Local project scanning                    | `meridian-mcp`          |
| Local architecture model cache            | `meridian-mcp`          |
| CLI commands                              | `meridian-mcp`          |
| Backend HTTP relay                        | `meridian-mcp`          |
| API-key validation and usage enforcement  | `meridian-backend`      |
| Rules-assisted review pipeline            | `meridian-backend`      |
| AI provider calls                         | `meridian-backend`      |
| Billing, payment, and account state       | `meridian-backend`      |
| Internal rule mining                      | `meridian-rule-miner`   |

## 3. Package Structure

* `meridian-intellij/`
    * `.idea/` — Local IDE project metadata.
    * `README.md` — User/developer overview.
    * `DESIGN.md` — This design document.
    * `TODO.md` — Plugin backlog.
    * `cursorrules` — Repository-local AI/editor guidance.

Expected implementation structure as the plugin matures:

* `src/main/kotlin/` or `src/main/java/`
    * Plugin startup/activity classes for lifecycle coordination.
    * Action classes for scan, review, context, and clear commands.
    * MCP client/process wrapper classes.
    * Editor annotation/highlighting classes.
    * Tool-window or findings-panel classes.
    * Settings/configuration classes.
    * Notification/status classes.
* `src/main/resources/META-INF/plugin.xml` — IntelliJ plugin descriptor.
* `build.gradle.kts` or equivalent Gradle build file — Plugin build, dependencies, and packaging configuration.

The exact source layout may evolve, but the architectural responsibility should remain a thin IDE adapter over `meridian-mcp`.

## 4. Design Patterns

* **Primary Pattern:** Thin IDE adapter over a local MCP runtime.
* **Supporting Patterns:**
    * IntelliJ Platform plugin lifecycle and project activity.
    * Action system integration for user commands.
    * Process wrapper for MCP stdio communication.
    * Request/response correlation using JSON-RPC IDs.
    * Tool-window or panel presentation model for findings.
    * Editor annotation/highlighting integration.
    * Persistent settings for user configuration.
* **Key Principles:**
    * **Presentation-only responsibility:** The plugin renders findings and drives IDE workflow, but does not duplicate product intelligence.
    * **MCP runtime delegation:** Scanning, cache management, and backend relay belong to `meridian-mcp`.
    * **Backend-owned judgment:** Architectural analysis, rule execution, AI orchestration, and final finding validation belong to `meridian-backend`.
    * **Low-noise UX:** Findings should be filtered and presented according to confidence and severity.
    * **Protocol-safe process handling:** MCP stdout is reserved for JSON-RPC messages; diagnostic output belongs on stderr or IDE logging/output surfaces.
    * **User override support:** Advanced users can point the plugin at a custom MCP binary path.
    * **Safe executable distribution:** Bundled or downloaded MCP binaries must be verified and have a safe fallback path.

## 5. Runtime Component Interaction

### 5.1 Plugin Activation

1. A JetBrains IDE loads the Meridian plugin.
2. The plugin reads Meridian configuration from IDE settings.
3. The plugin initializes UI components:
    * status display;
    * findings tool window or panel;
    * editor annotation/highlighting manager.
4. The plugin initializes the MCP client using the configured or resolved `meridian-mcp` binary path.
5. The MCP client starts the local `meridian-mcp` process over stdio.
6. The plugin sends the MCP initialize handshake.
7. Actions and save/document listeners become available.

### 5.2 Project Scan

1. The user runs the project scan action.
2. The plugin determines the IntelliJ project base path or selected content root.
3. The plugin calls the MCP tool for project scanning.
4. `meridian-mcp` scans the project, builds a compact architecture model, and stores it in local cache.
5. The plugin updates status based on success or failure.

The plugin does not parse imports, infer layers, detect architecture patterns, or build the architecture model itself.

### 5.3 File Review

1. The user manually reviews the current file or saves/synchronizes a document while realtime review is enabled.
2. The plugin reads the current editor file path and content.
3. The plugin calls the MCP review tool.
4. `meridian-mcp` loads the cached architecture model and relays review context to `meridian-backend`.
5. `meridian-backend` validates access, enforces usage policy, runs deterministic rules, constructs prompts, calls the AI provider, validates output, and returns findings.
6. `meridian-mcp` returns structured findings to the plugin.
7. The plugin filters or displays findings according to configured confidence threshold.
8. The plugin updates editor annotations/highlighting, findings tool window, and status display.

### 5.4 Context Actions

Where supported by the MCP/backend contract, the plugin allows selected editor text to be added as architecture context and allows clearing context.

The plugin only captures the selected text and invokes the relevant MCP tool. It does not interpret the context, persist product state, or perform architecture analysis.

### 5.5 Clear Findings

The plugin may clear currently displayed findings from editor annotations/highlighting and the findings tool window.

Clearing displayed findings is a local UI action. It does not imply deletion of backend account state, usage records, or durable product data.

### 5.6 Skill workflow questions and Architecture Model privacy

In Meridian, backend **Skill** is synonymous with Resolving Architecture **Service**.

The IntelliJ plugin should treat backend skill workflow responses as architecture-assistant guidance delivered through MCP. The plugin should not implement skill workflow process logic locally.

When MCP returns backend-generated questions, the plugin should be able to present them to the user or agent-facing IDE surface.

Question responses may occur when:

- a full review lacks enough context;
- an intermediate review lacks a prior baseline or needs clarification;
- a selected skill/service needs business, technical, stakeholder, decision, stack, or scope context;
- the backend cannot make a credible recommendation without more information.

The plugin should render question responses as workflow guidance, not as failures.

The Architecture Model is persisted by `meridian-mcp`, not by the IntelliJ plugin and not by the backend.

The plugin should preserve this boundary:

- do not persist Architecture Models in plugin settings or workspace state;
- do not persist raw source files as plugin telemetry;
- do not log raw Architecture Model payloads;
- do not imply that backend stores the Architecture Model;
- explain, where useful, that MCP keeps the local model and backend persists only analysis results and privacy-safe records.

Conceptual flow:

```text
IntelliJ action 
    → meridian-intellij calls MCP 
    → meridian-mcp sends local Architecture Model to backend 
    → backend returns findings, limitations, or questions 
    → meridian-intellij displays findings or questions 
    → user/agent answers questions 
    → meridian-mcp updates local Architecture Model 
    → backend workflow continues when requested
```
The plugin remains a presentation layer.

## 6. MCP Client Design

The IntelliJ MCP client owns the local process and JSON-RPC wiring for the MCP server.

Responsibilities:

* Resolve the configured, bundled, updated, or fallback `meridian-mcp` binary.
* Spawn the local `meridian-mcp` binary.
* Pass required environment variables, including the Meridian API key.
* Send the MCP initialize request.
* Serialize requests as newline-delimited JSON-RPC messages.
* Parse newline-delimited JSON-RPC responses from stdout.
* Correlate responses by request ID.
* Surface MCP errors to plugin UI and logs.
* Apply request timeouts.
* Dispose the child process when the project/plugin is closed or unloaded.

Non-responsibilities:

* Build architecture models.
* Implement review rules.
* Call AI providers.
* Own billing or usage enforcement.
* Persist account state.
* Perform CLI workflows.

MCP stdout must remain protocol-safe. Logs from the MCP process should be routed through stderr and surfaced through IDE logging, an event log notification, or a Meridian diagnostic/output surface.

## 7. Actions and Configuration

### 7.1 Actions

The plugin should contribute these user-facing actions:

| Action                              | Responsibility                                                                                       |
|-------------------------------------|------------------------------------------------------------------------------------------------------|
| Meridian: Scan Project              | Ask `meridian-mcp` to scan and cache project model                                                   |
| Meridian: Review Current File       | Review the current file through MCP                                                                  |
| Meridian: Add Selection as Context  | Send selected text as architecture context                                                           |
| Meridian: Clear Context             | Clear active architecture context where supported                                                    |
| Meridian: Clear Findings            | Clear locally displayed findings                                                                     |
| Meridian: Open Findings             | Focus the Meridian findings tool window or panel                                                     |
| Meridian: Diagnostics               | Show local plugin/MCP configuration health                                                           |
| Meridian: Answer Workflow Questions | Present and submit answers to backend-generated skill workflow questions through MCP where supported |

### 7.2 Configuration

The plugin should expose these settings:

| Setting                          | Purpose                                                         |
|----------------------------------|-----------------------------------------------------------------|
| Meridian API key                 | Meridian API key passed to the local MCP process                |
| MCP binary path                  | Optional path to `meridian-mcp`; defaults to resolved binary    |
| Enable realtime review           | Enables review-on-save or review-on-document-sync behavior      |
| Confidence threshold             | Minimum confidence required before displaying a finding         |

Future MCP binary distribution settings may include:

| Setting                          | Purpose                                                    |
|----------------------------------|------------------------------------------------------------|
| Auto-check MCP updates           | Enable background checks for MCP binary updates            |
| Auto-install MCP updates         | Allow automatic MCP binary installation if explicitly set  |

Auto-installing MCP updates should default to disabled because MCP updates are executable binary updates.

## 8. Findings Presentation

The plugin presents findings in two complementary surfaces:

1. **Editor annotations/highlighting**
    * Highlight relevant lines or ranges.
    * Show severity and concise title.
    * Provide hover text or inspection-style details where appropriate.
    * Avoid visual noise for low-confidence or suppressed results.

2. **Findings tool window or panel**
    * Lists findings for the current review session.
    * Shows severity, type, file, line, title, explanation, consequence, suggestion, ADR reference, and confidence where available.
    * Provides a broader explanation than inline annotations.
    * Allows navigation from a finding to the relevant file and line.

Finding concept:

| Field           | Purpose                                      |
|-----------------|----------------------------------------------|
| `severity`      | Criticality of architectural risk             |
| `type`          | Violation or risk category                    |
| `file`          | File containing the finding                   |
| `line`          | Relevant line number                          |
| `title`         | Short finding summary                         |
| `explanation`   | Why the issue matters                         |
| `consequence`   | Architectural impact if ignored               |
| `suggestion`    | Recommended remediation direction             |
| `adr_reference` | Related ADR or architecture reference         |
| `confidence`    | Confidence score used for filtering/display   |

The plugin should treat findings as backend-produced review output. It may filter, sort, group, navigate, and render findings, but it should not reinterpret architectural correctness.

## 9. MCP Binary Distribution and Updates

The intended product experience is that the IntelliJ plugin provides a working `meridian-mcp` binary as part of installation. Users should not be required to install Rust, Cargo, or `meridian-mcp` manually for the default path to work.

### 9.1 Bundled Binary

The plugin package should include prebuilt `meridian-mcp` binaries for supported platforms and architectures.

Recommended layout:

    bin/
      darwin-arm64/meridian-mcp
      darwin-x64/meridian-mcp
      linux-x64/meridian-mcp
      win32-x64/meridian-mcp.exe

On startup, the plugin should resolve the correct bundled binary for the current platform and architecture.

The bundled binary acts as the baseline, known-good MCP runtime shipped with the plugin.

### 9.2 Binary Resolution Order

The plugin should resolve the MCP binary in this order:

1. A user-configured binary path, if explicitly set.
2. A plugin-managed updated binary stored in JetBrains plugin/application storage.
3. The bundled binary shipped with the plugin.
4. Optionally, `meridian-mcp` from `PATH` as a fallback.

This keeps advanced user overrides possible while ensuring the plugin works immediately after installation.

### 9.3 Plugin-Managed Updates

The plugin may check for newer compatible `meridian-mcp` binaries after startup or on a periodic background schedule.

Update checks should be automatic, but installing an updated executable binary should require explicit user approval.

When a newer compatible binary is available, the plugin should prompt the user with options such as:

* Install Update
* View Release Notes
* Later
* Disable MCP Update Checks

The prompt should clearly show the current binary version and the available version.

### 9.4 Update Storage

Downloaded MCP binaries should not replace the bundled binary and should not be written into the installed plugin directory.

Instead, approved updates should be installed into plugin-managed application storage, for example:

    <application/plugin-data>/
      meridian/
        mcp/
          0.1.2/
            meridian-mcp
            manifest.json

The bundled binary should remain available as a safe fallback if the downloaded binary is missing, invalid, incompatible, or fails to start.

### 9.5 Update Manifest

The plugin should check a Meridian-controlled release manifest that describes available MCP binaries by version, platform, architecture, compatibility, URL, and checksum.

Example manifest shape:

    {
      "version": "0.1.2",
      "protocolVersion": "2024-11-05",
      "minPluginVersion": "0.1.0",
      "maxPluginVersion": "0.1.x",
      "downloads": {
        "linux-x64": {
          "url": "https://releases.resolvingarchitecture.io/meridian-mcp/0.1.2/linux-x64/meridian-mcp",
          "sha256": "..."
        },
        "darwin-arm64": {
          "url": "https://releases.resolvingarchitecture.io/meridian-mcp/0.1.2/darwin-arm64/meridian-mcp",
          "sha256": "..."
        },
        "darwin-x64": {
          "url": "https://releases.resolvingarchitecture.io/meridian-mcp/0.1.2/darwin-x64/meridian-mcp",
          "sha256": "..."
        },
        "win32-x64": {
          "url": "https://releases.resolvingarchitecture.io/meridian-mcp/0.1.2/win32-x64/meridian-mcp.exe",
          "sha256": "..."
        }
      }
    }

The plugin should only offer an update when:

* the current platform and architecture are supported;
* the available MCP version is newer than the active MCP version;
* the MCP protocol version is compatible with the plugin;
* the plugin version is within the supported compatibility range.

### 9.6 Download and Verification Requirements

Executable downloads must be handled defensively.

The plugin should:

1. Download the binary to a temporary location.
2. Verify the downloaded file checksum before use.
3. Prefer signed manifests or signed binaries in addition to checksums.
4. Set executable permissions on macOS and Linux.
5. Move the verified binary into its final storage location atomically.
6. Never execute a partially downloaded or unverified file.
7. Fall back to the bundled binary if the updated binary fails validation or startup.

### 9.7 Failure Handling

If the plugin-managed updated binary fails to start, the plugin should:

1. Mark the updated binary as failed for the current session.
2. Retry using the bundled binary.
3. Notify the user that Meridian fell back to the bundled MCP runtime.
4. Provide an action to clear the downloaded binary and retry the update later.

## 10. Data and State

`meridian-intellij` should keep durable state minimal.

Owned or locally managed state:

* JetBrains IDE settings.
* Current in-memory findings.
* UI state for annotations/highlighting and tool windows.
* Optional plugin-managed MCP binary update metadata.
* Optional cached failure marker for a bad downloaded MCP binary during the current session.

Not owned by the plugin:

* Architecture model cache.
* Backend account state.
* Usage records.
* Payment state.
* AI provider state.
* Rule telemetry.
* Customer review history beyond the current UI display.

The architecture model cache belongs to `meridian-mcp`. Product state belongs to `meridian-backend`.

## 11. Security and Privacy Boundaries

The plugin participates in Meridian's local developer workflow and must preserve local/client trust boundaries.

Security requirements:

* Treat the API key as secret.
* Do not log API keys, secrets, or raw selected text unnecessarily.
* Do not persist raw file contents in plugin-owned durable storage.
* Do not call AI providers directly.
* Do not implement billing or usage checks locally.
* Do not retain customer source code as telemetry.
* Keep MCP stdout reserved for protocol messages.
* Route diagnostics through stderr, IDE logs, notifications, or explicit diagnostic surfaces.
* Verify downloaded executable binaries before use.
* Prefer explicit user approval before installing MCP binary updates.
* Fall back safely to the bundled MCP binary if an updated binary fails.

Privacy requirements:

* The plugin should send file contents only as part of explicit review workflows or configured realtime review-on-save behavior.
* The plugin should make realtime review behavior configurable.
* The plugin should avoid storing raw customer source outside the editor/session.
* The plugin should rely on `meridian-mcp` and `meridian-backend` for scan, cache, review, and product policy behavior.

## 12. Error Handling and UX Constraints

Expected error classes:

| Error Class                  | UX Response                                                   |
|------------------------------|---------------------------------------------------------------|
| Missing API key              | Prompt user to configure Meridian API key                     |
| MCP binary not found         | Offer setup guidance, bundled binary fallback, or path config |
| MCP process failed to start  | Show actionable error and avoid repeated noisy restarts       |
| MCP request timeout          | Show transient failure and allow retry                        |
| Backend unreachable          | Show network/backend status and preserve local UI stability   |
| Unauthorized API key         | Prompt user to update API key                                 |
| Review failed                | Keep previous findings until cleared or replaced              |
| Invalid finding payload      | Ignore invalid entries and surface diagnostic information     |
| Binary update failed         | Fall back to bundled binary and offer retry/clear action      |

UX principles:

* Prefer actionable errors over raw stack traces.
* Do not block editing while reviews run.
* Avoid repeated prompts on every save.
* Preserve current findings until a successful replacement or explicit clear.
* Make realtime review opt-out through configuration.
* Keep status messages concise.
* Put detailed diagnostics in a log, notification detail, tool window, or output-like diagnostic surface.

## 13. Dependencies

* **Internal:**
    * `meridian-intellij` → `meridian-mcp` for local MCP server, scanner, cache, CLI, and backend relay.
    * `meridian-intellij` → `meridian-backend` indirectly through `meridian-mcp` for review execution and product policy.
* **External:**
    * IntelliJ Platform SDK.
    * Java or Kotlin plugin runtime.
    * Gradle IntelliJ plugin tooling when implementation is added.
    * JetBrains settings, action, editor, notification, and tool-window APIs.
    * Prebuilt `meridian-mcp` binaries for supported platforms when bundled distribution is enabled.

## 14. Current Implementation Notes

The current repository state documents the intended IntelliJ thin IDE adapter design and MCP binary distribution strategy.

Implemented or established areas include:

* Repository boundary for the IntelliJ plugin.
* Design direction for MCP binary bundling and update management.
* Alignment with the Meridian architecture where `meridian-mcp` owns local scanning, cache, CLI, and backend relay.
* Alignment with the principle that IDE integrations remain presentation layers and do not duplicate architecture judgment.

Important implementation caveats:

* The plugin must use `meridian-mcp` as the local scanner/cache/backend-relay runtime.
* The plugin must avoid growing scanning or architecture judgment logic.
* The plugin implementation structure may still need to be added or expanded.
* Bundled MCP binary distribution and update management are design targets and may require additional packaging/release automation before being fully implemented.
* If the plugin temporarily uses `meridian-mcp` from `PATH`, that should be treated as a development or fallback path, not the ideal end-user installation experience.

## 15. Future Considerations

* Add IntelliJ Platform plugin source structure and build configuration.
* Implement platform-specific bundled MCP binary packaging.
* Add plugin-managed MCP binary update checks with user approval.
* Add checksum or signature verification for downloaded binaries.
* Add IDE logging or diagnostic output for MCP stderr and plugin diagnostics.
* Improve MCP startup recovery and fallback behavior.
* Add a command/action for Meridian doctor/diagnostics if exposed through MCP or CLI.
* Add project-root/content-root selection for multi-module IntelliJ projects.
* Add cancellation or debouncing for rapid save/document events.
* Add finding grouping by severity, type, file, module, or ADR reference.
* Add richer hover, intention, or inspection-style content for editor annotations.
* Add tests for MCP response parsing and request timeout behavior.
* Add tests for confidence filtering and annotation rendering.
* Add telemetry only if privacy-safe and explicitly governed.
