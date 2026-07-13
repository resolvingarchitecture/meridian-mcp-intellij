# TODO - meridian-intellij

## MVP

### IntelliJ Plugin Project Setup

- [x] Create the IntelliJ Platform plugin project structure.
  - [x] Add `build.gradle.kts` or equivalent Gradle build configuration.
  - [ ] Add Gradle wrapper files if this repository should build independently.
  - [x] Add `settings.gradle.kts`.
  - [x] Remove legacy Maven `pom.xml` so Gradle is the single source of truth.
  - [x] Add `src/main/java/`.
  - [x] Add `src/main/resources/META-INF/plugin.xml`.
  - [x] Configure plugin ID, name, vendor, version, and description.
  - [x] Configure supported IntelliJ Platform version range.
  - [x] Configure Java target compatibility.
  - [ ] Add basic plugin verification task.
  - [x] Add local run configuration documentation for launching a sandbox IDE.

### Implemented MVP Features

- [x] Register core Meridian actions in `plugin.xml`.
- [x] Add `Meridian: Scan Project`.
- [x] Add `Meridian: Review Current File`.
- [x] Add `Meridian: Add Selection as Context`.
- [x] Add `Meridian: Clear Context`.
- [x] Add `Meridian: Clear Findings`.
- [x] Add `Meridian: Open Findings`.
- [x] Add `Meridian: Diagnostics`.
- [x] Add Meridian settings state.
- [x] Add Meridian settings UI.
- [x] Add MCP process wrapper.
- [x] Pass `MERIDIAN_API_KEY` to MCP process.
- [x] Add MCP initialize handshake.
- [x] Add JSON-RPC tool call scaffolding.
- [x] Add findings model.
- [x] Add findings state service.
- [x] Add findings tool window.
- [x] Add startup activity.
- [x] Keep architecture scanning and architecture judgment out of this plugin.

### Remaining MVP Hardening

- [ ] Replace placeholder finding parser with strict MCP response parsing.
- [ ] Add editor annotations/highlighting.
- [ ] Refresh findings tool window automatically after reviews.
- [ ] Add realtime review on save or document synchronization.
- [ ] Add request timeout handling.
- [ ] Add cancellation support where practical.
- [ ] Add safer notification group registration.
- [ ] Store API key using IntelliJ secure storage if available.
- [ ] Add bundled MCP binary resolution.
- [ ] Add plugin verifier task.
- [ ] Add tests for MCP client behavior.
- [ ] Add tests for findings filtering.
- [ ] Add manual sandbox test checklist.

## Backlog

Keep the existing backlog items from the previous TODO as product hardening work:
MCP update management, multi-project support, richer IDE UX, diagnostics, telemetry decisions, and documentation expansion.
