# TODO - meridian-intellij

## MVP

### IntelliJ Plugin Project Setup

- [ ] Create the IntelliJ Platform plugin project structure.
    - [ ] Add `build.gradle.kts` or equivalent Gradle build configuration.
    - [ ] Add Gradle wrapper files if this repository should build independently.
    - [ ] Add `settings.gradle.kts`.
    - [x] Add `src/main/kotlin/` or `src/main/java/`.
    - [ ] Add `src/main/resources/META-INF/plugin.xml`.
    - [ ] Configure plugin ID, name, vendor, version, and description.
    - [ ] Configure supported IntelliJ Platform version range.
    - [ ] Configure Java/Kotlin target compatibility.
    - [ ] Add basic plugin verification task.
    - [ ] Add local run configuration documentation for launching a sandbox IDE.

- [ ] Define package/module boundaries.
    - [ ] Add lifecycle/startup package.
    - [ ] Add actions package.
    - [ ] Add MCP client/process package.
    - [ ] Add findings model/package.
    - [ ] Add editor annotations/highlighting package.
    - [ ] Add tool-window UI package.
    - [ ] Add settings/configuration package.
    - [ ] Add notifications/status package.
    - [ ] Keep architecture scanning and architecture judgment out of this plugin.

- [ ] Update repository documentation for developer setup.
    - [ ] Document required JDK version.
    - [ ] Document Gradle commands for build, test, verify, and run IDE.
    - [ ] Document how to install the plugin locally.
    - [ ] Document current implementation status.
    - [ ] Document that `meridian-intellij` is a thin IDE adapter over `meridian-mcp`.

### Plugin Lifecycle and Activation

- [ ] Implement plugin startup/lifecycle coordination.
    - [ ] Initialize Meridian services when an IntelliJ project opens.
    - [ ] Read persisted Meridian settings on startup.
    - [ ] Initialize MCP client lazily or during project startup.
    - [ ] Initialize findings state.
    - [ ] Initialize editor annotation/highlighting manager.
    - [ ] Initialize status/notification integration.
    - [ ] Dispose MCP process and listeners when the project closes.
    - [ ] Avoid repeated noisy restarts if MCP startup fails.
    - [ ] Ensure plugin unload/project close cleans up resources.

- [ ] Add project-awareness.
    - [ ] Resolve IntelliJ project base path.
    - [ ] Support selected content root where relevant.
    - [ ] Handle projects without a base path.
    - [ ] Prepare for multi-module/content-root project support.
    - [ ] Ensure actions gracefully disable or fail with actionable messages when no project is available.

### MCP Client and Process Management

- [ ] Implement MCP process wrapper.
    - [ ] Resolve MCP binary path from settings.
    - [ ] Start `meridian-mcp` as a child process.
    - [ ] Pass required environment variables, including Meridian API key.
    - [ ] Use stdin/stdout for MCP JSON-RPC communication.
    - [ ] Route MCP stderr to IDE diagnostics/logging.
    - [ ] Ensure stdout is treated as protocol-only.
    - [ ] Dispose process on plugin/project shutdown.
    - [ ] Detect unexpected MCP process exit.
    - [ ] Prevent runaway restart loops.

- [ ] Implement JSON-RPC/MCP request handling.
    - [ ] Send initialize handshake.
    - [ ] Serialize newline-delimited JSON-RPC requests.
    - [ ] Parse newline-delimited JSON-RPC responses.
    - [ ] Correlate responses by request ID.
    - [ ] Surface JSON-RPC errors to the UI.
    - [ ] Add request timeout handling.
    - [ ] Add cancellation support where practical.
    - [ ] Validate response payloads before rendering findings.
    - [ ] Ignore malformed responses safely and log diagnostics.

- [ ] Implement MCP tool calls.
    - [ ] Add project scan request.
    - [ ] Add current-file review request.
    - [ ] Add selected-text context request where supported.
    - [ ] Add clear-context request where supported.
    - [ ] Add diagnostics/doctor request if exposed by MCP.
    - [ ] Keep all scanning, caching, and backend relay behavior delegated to `meridian-mcp`.

- [ ] Harden MCP error handling.
    - [ ] Handle missing binary.
    - [ ] Handle binary permission errors.
    - [ ] Handle process startup failure.
    - [ ] Handle initialize failure.
    - [ ] Handle request timeout.
    - [ ] Handle backend unreachable responses.
    - [ ] Handle unauthorized API key responses.
    - [ ] Handle invalid finding payloads.
    - [ ] Preserve existing findings when a review fails.
    - [ ] Provide actionable notification text for each expected failure mode.

### MCP Binary Resolution and Distribution

- [ ] Implement MCP binary resolution order.
    - [ ] Prefer explicitly configured user binary path.
    - [ ] Check plugin-managed updated binary location.
    - [ ] Fall back to bundled plugin binary.
    - [ ] Optionally fall back to `meridian-mcp` from `PATH`.
    - [ ] Log which resolution path was used without exposing secrets.
    - [ ] Treat `PATH` resolution as development/fallback behavior, not the ideal end-user path.

- [ ] Add bundled binary layout.
    - [ ] Add expected `bin/darwin-arm64/meridian-mcp` packaging path.
    - [ ] Add expected `bin/darwin-x64/meridian-mcp` packaging path.
    - [ ] Add expected `bin/linux-x64/meridian-mcp` packaging path.
    - [ ] Add expected `bin/win32-x64/meridian-mcp.exe` packaging path.
    - [ ] Resolve current operating system.
    - [ ] Resolve current CPU architecture.
    - [ ] Select the correct bundled binary.
    - [ ] Ensure executable permissions are set or documented for macOS/Linux.

- [ ] Add plugin-managed MCP update scaffolding.
    - [ ] Define update metadata storage location.
    - [ ] Define downloaded binary storage location.
    - [ ] Ensure downloaded binaries do not replace bundled binaries.
    - [ ] Add manifest model for version, protocol version, plugin compatibility, URLs, and checksums.
    - [ ] Add update check action or background check stub.
    - [ ] Require explicit user approval before installing an executable update.
    - [ ] Provide "Later" and "Disable MCP Update Checks" options.
    - [ ] Provide a way to clear a failed downloaded binary.

- [ ] Add binary verification requirements.
    - [ ] Download candidate binary to a temporary location.
    - [ ] Verify SHA-256 checksum before use.
    - [ ] Prefer signed manifest or signed binary verification when available.
    - [ ] Move verified binary atomically into plugin-managed storage.
    - [ ] Never execute partially downloaded files.
    - [ ] Never execute unverified downloaded files.
    - [ ] Fall back to bundled binary when update validation fails.
    - [ ] Fall back to bundled binary when updated binary fails to start.

### User Actions

- [ ] Register core Meridian actions in `plugin.xml`.
    - [ ] Add `Meridian: Scan Project`.
    - [ ] Add `Meridian: Review Current File`.
    - [ ] Add `Meridian: Add Selection as Context`.
    - [ ] Add `Meridian: Clear Context`.
    - [ ] Add `Meridian: Clear Findings`.
    - [ ] Add `Meridian: Open Findings`.
    - [ ] Add `Meridian: Diagnostics`.

- [ ] Implement project scan action.
    - [ ] Resolve project base path or selected content root.
    - [ ] Ensure MCP client is initialized.
    - [ ] Call MCP project scan tool.
    - [ ] Update status on scan start.
    - [ ] Update status on scan success.
    - [ ] Show actionable notification on scan failure.
    - [ ] Avoid parsing imports or building the architecture model inside the plugin.

- [ ] Implement current-file review action.
    - [ ] Resolve current editor file.
    - [ ] Read file path and current document content.
    - [ ] Ensure MCP client is initialized.
    - [ ] Call MCP review tool.
    - [ ] Apply configured confidence threshold.
    - [ ] Update editor annotations/highlighting.
    - [ ] Update findings tool window.
    - [ ] Update status display.
    - [ ] Preserve previous findings if review fails.

- [ ] Implement selected-context action.
    - [ ] Enable action only when editor selection exists.
    - [ ] Capture selected text.
    - [ ] Send selected text to MCP context tool where supported.
    - [ ] Avoid storing selected text in durable plugin state.
    - [ ] Avoid logging selected text.
    - [ ] Show success or unsupported-operation feedback.

- [ ] Implement clear-context action.
    - [ ] Call MCP clear-context tool where supported.
    - [ ] Show success or unsupported-operation feedback.
    - [ ] Do not imply deletion of backend account state or usage records.

- [ ] Implement clear-findings action.
    - [ ] Clear in-memory findings.
    - [ ] Clear editor annotations/highlighting.
    - [ ] Clear findings tool-window contents.
    - [ ] Update status display.
    - [ ] Keep clear-findings as a local UI-only operation.

- [ ] Implement open-findings action.
    - [ ] Focus the Meridian findings tool window.
    - [ ] Select current file findings when possible.
    - [ ] Create tool window if not already initialized.

- [ ] Implement diagnostics action.
    - [ ] Show configured MCP path.
    - [ ] Show active resolved MCP path.
    - [ ] Show MCP process status.
    - [ ] Show API key presence without showing the key.
    - [ ] Show realtime review setting.
    - [ ] Show confidence threshold.
    - [ ] Show last scan/review status.
    - [ ] Include MCP stderr/log excerpts only when safe.

### Settings and Configuration

- [ ] Add persistent plugin settings.
    - [ ] Meridian API key.
    - [ ] MCP binary path.
    - [ ] Enable realtime review.
    - [ ] Confidence threshold.
    - [ ] Optional auto-check MCP updates setting.
    - [ ] Optional auto-install MCP updates setting, default disabled.

- [ ] Add settings UI.
    - [ ] Add Meridian settings page.
    - [ ] Add API key input field.
    - [ ] Mask API key where appropriate.
    - [ ] Add MCP binary path field with file chooser.
    - [ ] Add realtime review checkbox.
    - [ ] Add confidence threshold numeric field or slider.
    - [ ] Validate confidence threshold range.
    - [ ] Add reset/default behavior.
    - [ ] Add helpful descriptions for each setting.

- [ ] Handle configuration changes.
    - [ ] Restart MCP process when binary path changes.
    - [ ] Restart MCP process when API key changes if required.
    - [ ] Apply confidence threshold immediately to rendered findings where possible.
    - [ ] Apply realtime setting to save/document listeners immediately.
    - [ ] Avoid logging secret values.

### Realtime Review

- [ ] Implement review-on-save or document synchronization listener.
    - [ ] Trigger only when realtime review is enabled.
    - [ ] Resolve saved document file path.
    - [ ] Debounce rapid save/document events.
    - [ ] Avoid concurrent duplicate reviews for the same file.
    - [ ] Keep editing non-blocking while review runs.
    - [ ] Avoid repeated prompts on every save.
    - [ ] Preserve existing findings when realtime review fails.

- [ ] Add realtime safeguards.
    - [ ] Do not run without configured API key unless prompting once.
    - [ ] Do not run if MCP is unavailable unless prompting once.
    - [ ] Add timeout behavior.
    - [ ] Add cancellation or superseding behavior for rapid edits.
    - [ ] Avoid sending temporary or unsupported file types if filtering is added.
    - [ ] Make behavior clearly configurable.

### Findings Model and Filtering

- [ ] Define local finding model.
    - [ ] Add severity field.
    - [ ] Add type/category field.
    - [ ] Add file field.
    - [ ] Add line field.
    - [ ] Add title field.
    - [ ] Add explanation field.
    - [ ] Add consequence field.
    - [ ] Add suggestion field.
    - [ ] Add ADR/reference field.
    - [ ] Add confidence field.
    - [ ] Add optional range/column fields if MCP/backend provides them.

- [ ] Validate returned findings.
    - [ ] Ignore entries without required fields.
    - [ ] Normalize severity values.
    - [ ] Clamp or validate confidence values.
    - [ ] Handle missing file or line gracefully.
    - [ ] Log invalid payload diagnostics without exposing source content.
    - [ ] Do not reinterpret architectural correctness locally.

- [ ] Implement finding filtering.
    - [ ] Filter by configured confidence threshold.
    - [ ] Prepare severity-based sorting.
    - [ ] Prepare grouping by severity.
    - [ ] Prepare grouping by file.
    - [ ] Prepare grouping by type.
    - [ ] Prepare grouping by ADR reference.
    - [ ] Ensure low-confidence findings do not create editor noise.

### Editor Annotations and Highlighting

- [ ] Implement editor annotation/highlighting manager.
    - [ ] Highlight relevant lines or ranges.
    - [ ] Map finding severities to IntelliJ highlight severities.
    - [ ] Show concise finding title inline or on hover.
    - [ ] Include explanation, consequence, suggestion, ADR reference, and confidence in detail text where appropriate.
    - [ ] Clear highlights when findings are cleared.
    - [ ] Replace highlights when a new successful review completes.
    - [ ] Avoid adding duplicate highlights.
    - [ ] Handle closed or unavailable editors safely.

- [ ] Add navigation behavior.
    - [ ] Navigate from finding to file and line.
    - [ ] Open file if not already open.
    - [ ] Handle missing files gracefully.
    - [ ] Handle line numbers outside current file range gracefully.

- [ ] Prepare richer IDE integrations.
    - [ ] Evaluate inspection-style presentation.
    - [ ] Evaluate intention actions or quick-fix placeholders.
    - [ ] Evaluate hover detail formatting.
    - [ ] Ensure any remediation suggestion remains backend-provided or clearly advisory.

### Findings Tool Window

- [ ] Implement Meridian findings tool window.
    - [ ] Register tool window in `plugin.xml`.
    - [ ] Display current review findings.
    - [ ] Show severity.
    - [ ] Show type/category.
    - [ ] Show file and line.
    - [ ] Show title.
    - [ ] Show explanation.
    - [ ] Show consequence.
    - [ ] Show suggestion.
    - [ ] Show ADR/reference.
    - [ ] Show confidence.
    - [ ] Provide navigation to source location.
    - [ ] Provide clear-findings control.
    - [ ] Provide refresh/review-current-file control if appropriate.

- [ ] Improve findings presentation.
    - [ ] Group findings by file.
    - [ ] Group findings by severity.
    - [ ] Sort findings by severity and location.
    - [ ] Show empty state before first review.
    - [ ] Show loading state during review.
    - [ ] Show last review error without deleting existing findings.
    - [ ] Show confidence threshold applied to current view.

### Status, Notifications, and Diagnostics

- [ ] Add Meridian status display.
    - [ ] Show idle state.
    - [ ] Show scanning state.
    - [ ] Show reviewing state.
    - [ ] Show MCP unavailable state.
    - [ ] Show missing API key state.
    - [ ] Show backend unreachable state.
    - [ ] Show last successful scan/review time if useful.
    - [ ] Keep status messages concise.

- [ ] Add user notifications.
    - [ ] Notify when API key is missing.
    - [ ] Notify when MCP binary cannot be found.
    - [ ] Notify when MCP process fails to start.
    - [ ] Notify when backend is unreachable.
    - [ ] Notify when API key is unauthorized.
    - [ ] Notify when binary update fails and fallback is used.
    - [ ] Avoid repeated noisy notifications during realtime review.

- [ ] Add diagnostic logging.
    - [ ] Log MCP process lifecycle events.
    - [ ] Log MCP stderr safely.
    - [ ] Log request timeout diagnostics.
    - [ ] Log invalid payload diagnostics.
    - [ ] Redact API keys and secrets.
    - [ ] Avoid logging raw file contents or selected text.
    - [ ] Provide enough details for support/debugging.

### Security and Privacy

- [ ] Protect API keys.
    - [ ] Store API key using appropriate IntelliJ secure storage if available.
    - [ ] Never log API key values.
    - [ ] Never include API key in error text.
    - [ ] Pass API key to MCP only as required.
    - [ ] Mask API key in settings and diagnostics.

- [ ] Protect source content.
    - [ ] Do not persist raw file contents in plugin-owned durable storage.
    - [ ] Do not log raw selected text.
    - [ ] Do not log raw reviewed file content.
    - [ ] Send file content only for explicit review workflows or configured realtime review.
    - [ ] Make realtime review clearly configurable.
    - [ ] Avoid telemetry unless privacy-safe and explicitly governed.

- [ ] Preserve architecture boundaries.
    - [ ] Do not call AI providers directly from the plugin.
    - [ ] Do not implement billing locally.
    - [ ] Do not implement usage enforcement locally.
    - [ ] Do not build local architecture model in the plugin.
    - [ ] Do not duplicate deterministic architecture rules in the plugin.
    - [ ] Treat backend/MCP findings as review output to render, filter, sort, and navigate.

- [ ] Harden executable handling.
    - [ ] Verify downloaded MCP binaries.
    - [ ] Prefer signed manifests or signed binaries.
    - [ ] Require user approval before installing executable updates.
    - [ ] Keep bundled binary as safe fallback.
    - [ ] Do not execute unverified binaries.
    - [ ] Do not write updates into the installed plugin directory.

### Testing

- [ ] Add unit tests for MCP client behavior.
    - [ ] Test JSON-RPC request serialization.
    - [ ] Test JSON-RPC response parsing.
    - [ ] Test request ID correlation.
    - [ ] Test error response handling.
    - [ ] Test timeout handling.
    - [ ] Test malformed response handling.
    - [ ] Test process startup failure handling.

- [ ] Add tests for settings.
    - [ ] Test default settings.
    - [ ] Test confidence threshold validation.
    - [ ] Test binary path changes.
    - [ ] Test realtime setting changes.
    - [ ] Test API key redaction/masking behavior where practical.

- [ ] Add tests for findings.
    - [ ] Test finding payload validation.
    - [ ] Test confidence filtering.
    - [ ] Test severity normalization.
    - [ ] Test sorting/grouping behavior.
    - [ ] Test invalid finding entries are ignored safely.

- [ ] Add tests for UI-facing behavior where practical.
    - [ ] Test annotation creation from findings.
    - [ ] Test annotation clearing.
    - [ ] Test navigation target calculation.
    - [ ] Test findings tool-window model updates.
    - [ ] Test previous findings are preserved after review failure.

- [ ] Add integration/manual test checklist.
    - [ ] Install plugin in sandbox IDE.
    - [ ] Configure API key.
    - [ ] Configure MCP binary path.
    - [ ] Scan project.
    - [ ] Review current file.
    - [ ] Trigger realtime review on save.
    - [ ] Add selected text as context.
    - [ ] Clear context.
    - [ ] Clear findings.
    - [ ] Verify annotations and tool-window results.
    - [ ] Verify missing API key error.
    - [ ] Verify missing MCP binary error.
    - [ ] Verify backend unreachable error.

### Packaging and Release

- [ ] Package the IntelliJ plugin.
    - [ ] Configure plugin artifact metadata.
    - [ ] Include bundled MCP binaries when available.
    - [ ] Ensure platform-specific binaries are packaged in expected layout.
    - [ ] Verify plugin artifact contents.
    - [ ] Run IntelliJ plugin verifier.
    - [ ] Document local installation from artifact.

- [ ] Add release automation.
    - [ ] Build plugin artifact in CI.
    - [ ] Run tests in CI.
    - [ ] Run plugin verifier in CI.
    - [ ] Attach plugin artifact to releases.
    - [ ] Coordinate MCP binary versions with plugin releases.
    - [ ] Ensure release notes mention bundled MCP version.
    - [ ] Document compatibility between plugin version and MCP protocol version.

- [ ] Add marketplace/publishing preparation.
    - [ ] Add plugin icon if needed.
    - [ ] Add plugin description.
    - [ ] Add change notes.
    - [ ] Add vendor information.
    - [ ] Add license metadata.
    - [ ] Confirm JetBrains Marketplace requirements.
    - [ ] Confirm privacy/security disclosures for source review behavior.

## Backlog

### Advanced MCP Update Management

- [ ] Implement periodic MCP update checks.
- [ ] Implement release manifest fetching.
- [ ] Implement manifest signature verification.
- [ ] Implement compatible-version filtering.
- [ ] Implement update approval prompt.
- [ ] Implement update download progress UI.
- [ ] Implement checksum verification.
- [ ] Implement atomic install into plugin-managed storage.
- [ ] Implement fallback to bundled binary on updated binary failure.
- [ ] Implement clear downloaded binary action.
- [ ] Implement disable update checks option.

### Multi-Project and Multi-Module Support

- [ ] Support selecting content root for project scans.
- [ ] Support multiple IntelliJ modules in one project.
- [ ] Associate findings with the correct module/content root.
- [ ] Handle multiple open projects.
- [ ] Avoid cross-project MCP/finding state leakage.
- [ ] Decide whether each project gets its own MCP process.
- [ ] Document multi-project behavior.

### Richer IDE UX

- [ ] Add severity/type filters in findings tool window.
- [ ] Add ADR-reference grouping.
- [ ] Add search within findings.
- [ ] Add finding suppression or hide-local-display behavior if supported by backend/MCP.
- [ ] Add richer hover formatting.
- [ ] Add inspection-style integration.
- [ ] Add intention-action placeholders for backend-suggested remediation.
- [ ] Add keyboard shortcuts for common Meridian actions.
- [ ] Add editor gutter icons if useful.
- [ ] Add status-bar popup with quick actions.

### Diagnostics and Supportability

- [ ] Add Meridian doctor command integration.
- [ ] Add copy-safe diagnostic summary.
- [ ] Add MCP version display.
- [ ] Add active protocol version display.
- [ ] Add last MCP error display.
- [ ] Add last backend error display.
- [ ] Add log file location guidance.
- [ ] Add troubleshooting documentation.

### Privacy-Safe Telemetry

- [ ] Decide whether the IntelliJ plugin should emit telemetry.
- [ ] Define allowed telemetry events if telemetry is added.
- [ ] Ensure telemetry never includes source code, selected text, secrets, or raw findings unless explicitly governed.
- [ ] Add opt-in/opt-out controls if telemetry is added.
- [ ] Document telemetry behavior clearly.

### Documentation

- [ ] Expand README with installation instructions.
- [ ] Expand README with configuration instructions.
- [ ] Expand README with MCP binary path behavior.
- [ ] Expand README with bundled binary/update behavior.
- [ ] Expand README with realtime review behavior.
- [ ] Expand README with troubleshooting steps.
- [ ] Expand README with security and privacy notes.
- [ ] Keep README aligned with DESIGN.md.
- [ ] Keep TODO.md aligned with DESIGN.md as implementation progresses.