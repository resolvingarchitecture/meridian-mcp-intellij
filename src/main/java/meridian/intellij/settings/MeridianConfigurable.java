package meridian.intellij.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.util.Arrays;

public final class MeridianConfigurable implements Configurable {
    private JBPasswordField apiKeyField;
    private JBTextField mcpBinaryPathField;
    private JBCheckBox realtimeReviewCheckbox;
    private JSpinner confidenceThresholdSpinner;
    private JBCheckBox autoCheckMcpUpdatesCheckbox;
    private JBCheckBox autoInstallMcpUpdatesCheckbox;

    @Override
    public @Nls String getDisplayName() {
        return "Meridian";
    }

    @Override
    public @Nullable JComponent createComponent() {
        apiKeyField = new JBPasswordField();
        mcpBinaryPathField = new JBTextField();
        realtimeReviewCheckbox = new JBCheckBox("Enable realtime review on save");
        confidenceThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.70, 0.0, 1.0, 0.05));
        autoCheckMcpUpdatesCheckbox = new JBCheckBox("Auto-check MCP updates");
        autoInstallMcpUpdatesCheckbox = new JBCheckBox("Auto-install MCP updates");

        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Meridian API key", apiKeyField, 1, false)
                .addLabeledComponent("MCP binary path", mcpBinaryPathField, 1, false)
                .addComponent(realtimeReviewCheckbox, 1)
                .addLabeledComponent("Confidence threshold", confidenceThresholdSpinner, 1, false)
                .addComponent(autoCheckMcpUpdatesCheckbox, 1)
                .addComponent(autoInstallMcpUpdatesCheckbox, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        MeridianSettingsState settings = settings();

        return !new String(apiKeyField.getPassword()).equals(settings.getApiKey())
                || !mcpBinaryPathField.getText().equals(settings.getMcpBinaryPath())
                || realtimeReviewCheckbox.isSelected() != settings.isRealtimeReviewEnabled()
                || ((Double) confidenceThresholdSpinner.getValue()).doubleValue() != settings.getConfidenceThreshold()
                || autoCheckMcpUpdatesCheckbox.isSelected() != settings.isAutoCheckMcpUpdates()
                || autoInstallMcpUpdatesCheckbox.isSelected() != settings.isAutoInstallMcpUpdates();
    }

    @Override
    public void apply() {
        MeridianSettingsState settings = settings();

        settings.setApiKey(new String(apiKeyField.getPassword()));
        settings.setMcpBinaryPath(mcpBinaryPathField.getText());
        settings.setRealtimeReviewEnabled(realtimeReviewCheckbox.isSelected());
        settings.setConfidenceThreshold((Double) confidenceThresholdSpinner.getValue());
        settings.setAutoCheckMcpUpdates(autoCheckMcpUpdatesCheckbox.isSelected());
        settings.setAutoInstallMcpUpdates(autoInstallMcpUpdatesCheckbox.isSelected());

        Arrays.stream(ProjectManager.getInstance().getOpenProjects())
                .forEach(project -> project.getService(meridian.intellij.mcp.MeridianProjectService.class).restartMcp());
    }

    @Override
    public void reset() {
        MeridianSettingsState settings = settings();

        apiKeyField.setText(settings.getApiKey());
        mcpBinaryPathField.setText(settings.getMcpBinaryPath());
        realtimeReviewCheckbox.setSelected(settings.isRealtimeReviewEnabled());
        confidenceThresholdSpinner.setValue(settings.getConfidenceThreshold());
        autoCheckMcpUpdatesCheckbox.setSelected(settings.isAutoCheckMcpUpdates());
        autoInstallMcpUpdatesCheckbox.setSelected(settings.isAutoInstallMcpUpdates());
    }

    @Override
    public void disposeUIResources() {
        apiKeyField = null;
        mcpBinaryPathField = null;
        realtimeReviewCheckbox = null;
        confidenceThresholdSpinner = null;
        autoCheckMcpUpdatesCheckbox = null;
        autoInstallMcpUpdatesCheckbox = null;
    }

    private MeridianSettingsState settings() {
        return ServiceManager.getService(MeridianSettingsState.class);
    }
}