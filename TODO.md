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

### Skill/Service Workflow UX

- [ ] Document that backend Skill is synonymous with Resolving Architecture Service in IntelliJ-facing docs.
- [ ] Update action descriptions and notifications to use service/skill terminology consistently.
- [ ] Add MCP response parsing for backend-generated workflow questions.
- [ ] Add UI rendering for questions-required responses.
- [ ] Add notification or tool-window panel for workflow questions.
- [ ] Show missing-context category for each question when available.
- [ ] Show why-it-matters explanation for each question when available.
- [ ] Show required vs optional question status.
- [ ] Add support for submitting answers through MCP when supported.
- [ ] Add action placeholder for `Meridian: Answer Workflow Questions`.
- [ ] Ensure question responses are not shown as review failures.
- [ ] Ensure question responses clearly indicate Meridian needs more context before stronger analysis.

### Architecture Model Privacy Boundary

- [ ] Add user-facing copy explaining that the Architecture Model is persisted by MCP locally.
- [ ] Add user-facing copy explaining that backend does not persist raw Architecture Models.
- [ ] Ensure plugin does not persist Architecture Models in settings.
- [ ] Ensure plugin does not persist raw file contents in durable plugin state.
- [ ] Ensure plugin does not log raw Architecture Model payloads.
- [ ] Ensure plugin does not log question answers unnecessarily.
- [ ] Ensure diagnostics do not include raw source, API keys, secrets, or raw Architecture Model content.
- [ ] Add tests for question response parsing.
- [ ] Add tests for privacy-safe rendering of workflow questions.
- [ ] Add tests proving raw Architecture Model content is not stored by the plugin.

### MCP Compatibility

- [ ] Update strict MCP response parser to support findings, readiness, questions, limitations, and full-review recommendations.
- [ ] Add parser compatibility for skill workflow IDs.
- [ ] Add parser compatibility for question IDs.
- [ ] Add parser compatibility for question answer types.
- [ ] Add parser compatibility for related skill/domain/stack metadata.
- [ ] Add parser compatibility for privacy boundary metadata if MCP exposes it. 

## Backlog

Keep the existing backlog items from the previous TODO as product hardening work:
MCP update management, multi-project support, richer IDE UX, diagnostics, telemetry decisions, and documentation expansion.
