# meridian-intellij

JetBrains IntelliJ Platform plugin wrapper for Meridian, an AI architecture review assistant.

`meridian-intellij` is the JetBrains IDE presentation layer for Meridian. It is intended to start a local `meridian-mcp` process, communicate with it over MCP JSON-RPC via stdio, and render architecture findings inside IntelliJ-based IDEs through editor annotations, highlights, notifications, status UI, and a findings tool window or panel.

## What this plugin does

The plugin is designed to:

- Start the local `meridian-mcp` runtime during plugin/project activation.
- Pass your Meridian API key to the MCP process through `MERIDIAN_API_KEY`.
- Scan the current IntelliJ project through the MCP `scan_project` tool.
- Review the current file through the MCP review tool.
- Review files automatically on save or document synchronization when realtime review is enabled.
- Add selected editor text as architecture context through MCP where supported.
- Clear persisted architecture context through MCP where supported.
- Display findings as:
    - editor annotations or highlights;
    - hover or inspection-style details;
    - a Meridian findings tool window or panel;
    - IDE status or notification state.

## What this plugin does not do

This package is intentionally thin. It does **not**:

- scan source files itself;
- infer project architecture;
- run architecture rules locally;
- call Claude, Anthropic, or other AI providers directly;
- call the Meridian Java backend directly;
- own billing, usage enforcement, or account state;
- persist customer source code as plugin-owned durable state.

Architecture scanning, cache management, and backend relay are delegated to `meridian-mcp`. Product judgment, API-key validation, usage enforcement, rules-assisted review, and AI provider calls belong to `meridian-backend`.

## Current implementation status

This repository currently documents and scaffolds the IntelliJ plugin boundary and design. The implementation is not yet at the same MVP maturity as the VS Code extension.

| Area                              | Status                                                              |
|-----------------------------------|---------------------------------------------------------------------|
| Repository boundary               | Established for `meridian-intellij`                                 |
| Design document                   | Present in `DESIGN.md`                                              |
| Maven project file                | Present as initial Java 21 project scaffolding                      |
| Source tree                       | Present as `src/main/java/` scaffold                                |
| IntelliJ plugin descriptor        | Not implemented yet                                                 |
| IntelliJ action classes           | Not implemented yet                                                 |
| MCP process wrapper               | Design target                                                       |
| MCP initialize handshake          | Design target                                                       |
| Project scan action               | Design target via `scan_project`                                    |
| Current-file review action        | Design target via MCP review tool                                   |
| Review on save/sync               | Design target                                                       |
| Context actions                   | Design target                                                       |
| Editor annotations/highlighting   | Design target                                                       |
| Findings tool window or panel     | Design target                                                       |
| Status/notification integration   | Design target                                                       |
| Bundled MCP binary                | Design target; fallback may use `meridian-mcp` from `PATH`          |
| MCP binary update manager         | Design target; not implemented yet                                  |

## Architecture

High-level runtime flow:

```text
Developer uses an IntelliJ-based IDE
  -> meridian-intellij activates
    -> starts meridian-mcp over stdio
      -> meridian-mcp scans/caches local architecture model
      -> meridian-mcp relays review requests to meridian-backend
        -> meridian-backend validates access, runs review, calls AI providers
      <- findings
    <- findings
  -> meridian-intellij renders findings in editor and tool-window UI
```

Ownership boundaries:

| Responsibility                         | Owner                 |
|----------------------------------------|-----------------------|
| IntelliJ plugin activation and actions | `meridian-intellij`   |
| Editor annotations/highlighting        | `meridian-intellij`   |
| Findings tool window or panel          | `meridian-intellij`   |
| Status display and notifications       | `meridian-intellij`   |
| MCP stdio client wiring                | `meridian-intellij`   |
| Local project scanning                 | `meridian-mcp`        |
| Architecture model cache               | `meridian-mcp`        |
| Backend HTTP relay                     | `meridian-mcp`        |
| API-key validation                     | `meridian-backend`    |
| Usage enforcement                      | `meridian-backend`    |
| Rules-assisted review pipeline         | `meridian-backend`    |
| AI provider calls                      | `meridian-backend`    |
| Billing and account state              | `meridian-backend`    |

## Project structure

Current repository structure:

```text
meridian-intellij/
  src/
    main/
      java/          Java source scaffold
  pom.xml           Initial Maven project file
  DESIGN.md         Design and architecture notes
  README.md         This file
  TODO.md           Backlog
  LICENSE           License
  cursorrules       Repository-local AI/editor guidance
```

Expected implementation structure as the plugin matures:

```text
meridian-intellij/
  src/
    main/
      java/ or kotlin/
        ... plugin startup/activity classes
        ... action classes for scan, review, context, and clear workflows
        ... MCP client/process wrapper classes
        ... editor annotation/highlighting classes
        ... findings tool-window or panel classes
        ... settings/configuration classes
        ... notification/status classes
      resources/
        META-INF/
          plugin.xml
  pom.xml or build.gradle.kts
  DESIGN.md
  README.md
  TODO.md
  LICENSE
```

The exact source layout may evolve, but the architectural responsibility should remain a thin JetBrains IDE adapter over `meridian-mcp`.

## Planned actions

The plugin should contribute these user-facing actions:

| Action                             | Purpose                                      |
|------------------------------------|----------------------------------------------|
| Meridian: Scan Project             | Scan/cache the current project architecture  |
| Meridian: Review Current File      | Review the active editor file through MCP    |
| Meridian: Add Selection as Context | Send selected text as architecture context   |
| Meridian: Clear Context            | Clear active architecture context            |
| Meridian: Clear Findings           | Clear locally displayed findings             |
| Meridian: Open Findings            | Focus the Meridian findings tool window      |
| Meridian: Diagnostics              | Show plugin and MCP configuration health     |

## Planned configuration

The plugin should expose these settings:

| Setting                | Description                                            |
|------------------------|--------------------------------------------------------|
| Meridian API key       | Meridian API key passed to the local MCP process       |
| MCP binary path        | Optional path to `meridian-mcp`; defaults to resolved binary |
| Enable realtime review | Review files automatically on save or document sync    |
| Confidence threshold   | Minimum confidence required before displaying a finding |

Future MCP binary distribution settings may include:

| Setting                  | Description                                               |
|--------------------------|-----------------------------------------------------------|
| Auto-check MCP updates   | Enable background checks for MCP binary updates           |
| Auto-install MCP updates | Allow automatic MCP binary installation if explicitly set |

Auto-installing MCP updates should default to disabled because MCP updates are executable binary updates.

## Development setup

### Prerequisites

- Java 21 or newer.
- A JetBrains IDE suitable for IntelliJ Platform plugin development.
- Maven, while the current scaffold remains Maven-based.
- A local `meridian-mcp` binary available on `PATH`, or a future configured path setting.
- A Meridian API key for end-to-end review flows.

### Build scaffold

The current repository contains a Maven scaffold:

```bash
mvn test
```

Compile the Java scaffold with:

```bash
mvn compile
```

As IntelliJ Platform plugin implementation is added, the build may move to or add Gradle IntelliJ Platform tooling.

## Findings

A Meridian finding is expected to include:

| Field           | Purpose                                  |
|-----------------|------------------------------------------|
| `severity`      | Criticality of architectural risk         |
| `type`          | Finding category or rule/type identifier |
| `file`          | File associated with the finding         |
| `line`          | Relevant source line                     |
| `title`         | Short summary                            |
| `explanation`   | Why the issue matters                    |
| `consequence`   | Architectural consequence if ignored     |
| `suggestion`    | Recommended remediation direction        |
| `adr_reference` | Related ADR/reference when available     |
| `confidence`    | Confidence score                         |

The plugin should render findings in multiple IDE surfaces:

- editor annotations or highlights;
- hover or inspection-style details;
- a Meridian findings tool window or panel;
- IDE status and notification surfaces.

The plugin may filter, sort, group, navigate, and render findings, but it should not reinterpret architectural correctness.

## MCP binary distribution roadmap

The intended product experience is that the IntelliJ plugin ships with prebuilt `meridian-mcp` binaries for supported platforms so users do not need to install Rust, Cargo, or the MCP binary manually.

Recommended bundled binary layout:

```text
bin/
  darwin-arm64/meridian-mcp
  darwin-x64/meridian-mcp
  linux-x64/meridian-mcp
  win32-x64/meridian-mcp.exe
```

Planned binary resolution order:

1. user-configured MCP binary path;
2. plugin-managed updated binary in JetBrains plugin/application storage;
3. bundled binary shipped with the plugin;
4. optional `meridian-mcp` from `PATH` as a fallback.

Downloaded MCP binaries should not replace the bundled binary and should not be written into the installed plugin directory. Approved updates should be stored in plugin-managed application storage, for example:

```text
<application/plugin-data>/
  meridian/
    mcp/
      0.1.2/
        meridian-mcp
        manifest.json
```

The bundled binary should remain available as a safe fallback if a downloaded binary is missing, invalid, incompatible, or fails to start.

## Security and privacy notes

- The API key should be treated as secret.
- The API key should be passed to `meridian-mcp` as `MERIDIAN_API_KEY`.
- MCP stdout must remain reserved for JSON-RPC protocol messages.
- MCP stderr should be routed to IDE logs, notifications, or a Meridian diagnostic surface.
- File contents should be sent only during explicit review workflows or configured realtime review-on-save behavior.
- The plugin should not persist raw source code in durable plugin-owned storage.
- The plugin does not call AI providers directly.
- The plugin does not implement billing or usage checks locally.
- Downloaded executable update flows, when implemented, must verify checksums or signatures before execution.
- Installing updated executable binaries should require explicit user approval.

## Error handling goals

Expected error classes and UX responses:

| Error class                 | UX response                                                   |
|-----------------------------|---------------------------------------------------------------|
| Missing API key             | Prompt user to configure Meridian API key                     |
| MCP binary not found        | Offer setup guidance, bundled binary fallback, or path config |
| MCP process failed to start | Show actionable error and avoid repeated noisy restarts       |
| MCP request timeout         | Show transient failure and allow retry                        |
| Backend unreachable         | Show network/backend status and preserve IDE stability        |
| Unauthorized API key        | Prompt user to update API key                                 |
| Review failed               | Keep previous findings until cleared or replaced              |
| Invalid finding payload     | Ignore invalid entries and surface diagnostic information     |
| Binary update failed        | Fall back to bundled binary and offer retry/clear action      |

## Roadmap

- Add IntelliJ Platform plugin descriptor.
- Add plugin startup/project activity classes.
- Add user-facing Meridian actions.
- Implement MCP stdio process wrapper.
- Implement MCP initialize handshake and request/response correlation.
- Add project scan workflow.
- Add current-file review workflow.
- Add context add/clear workflows.
- Add realtime review on save or document sync.
- Add editor annotations/highlighting for findings.
- Add findings tool window or panel with navigation.
- Add plugin settings UI.
- Add status and notification integration.
- Implement platform-specific bundled MCP binary packaging.
- Add plugin-managed MCP binary update checks with user approval.
- Add checksum or signature verification for downloaded binaries.
- Add diagnostics action for plugin/MCP health.
- Add tests for MCP response parsing, timeouts, finding filtering, and UI presentation.

## License

See [`LICENSE`](./LICENSE).
