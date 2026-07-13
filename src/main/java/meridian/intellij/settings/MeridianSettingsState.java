package meridian.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "MeridianSettings",
        storages = @Storage("meridian-settings.xml")
)
public final class MeridianSettingsState implements PersistentStateComponent<MeridianSettingsState.State> {
    public static final class State {
        public String apiKey = "";
        public String mcpBinaryPath = "";
        public boolean realtimeReviewEnabled = false;
        public double confidenceThreshold = 0.70;
        public boolean autoCheckMcpUpdates = false;
        public boolean autoInstallMcpUpdates = false;
    }

    private State state = new State();

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public String getApiKey() {
        return state.apiKey == null ? "" : state.apiKey;
    }

    public void setApiKey(String apiKey) {
        state.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public String getMcpBinaryPath() {
        return state.mcpBinaryPath == null ? "" : state.mcpBinaryPath.trim();
    }

    public void setMcpBinaryPath(String mcpBinaryPath) {
        state.mcpBinaryPath = mcpBinaryPath == null ? "" : mcpBinaryPath.trim();
    }

    public boolean isRealtimeReviewEnabled() {
        return state.realtimeReviewEnabled;
    }

    public void setRealtimeReviewEnabled(boolean realtimeReviewEnabled) {
        state.realtimeReviewEnabled = realtimeReviewEnabled;
    }

    public double getConfidenceThreshold() {
        return state.confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        state.confidenceThreshold = Math.max(0.0, Math.min(1.0, confidenceThreshold));
    }

    public boolean isAutoCheckMcpUpdates() {
        return state.autoCheckMcpUpdates;
    }

    public void setAutoCheckMcpUpdates(boolean autoCheckMcpUpdates) {
        state.autoCheckMcpUpdates = autoCheckMcpUpdates;
    }

    public boolean isAutoInstallMcpUpdates() {
        return state.autoInstallMcpUpdates;
    }

    public void setAutoInstallMcpUpdates(boolean autoInstallMcpUpdates) {
        state.autoInstallMcpUpdates = autoInstallMcpUpdates;
    }

    public boolean hasApiKey() {
        return !getApiKey().isBlank();
    }

    public @Nullable String safeConfiguredMcpPath() {
        String path = getMcpBinaryPath();
        return path.isBlank() ? null : path;
    }
}