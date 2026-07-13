package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import meridian.intellij.mcp.install.MeridianMcpInstaller;
import meridian.intellij.notifications.MeridianNotifications;
import meridian.intellij.settings.MeridianSettingsState;
import org.jetbrains.annotations.NotNull;

public final class InstallMcpBinaryAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = MeridianActionUtil.requireProject(event);

        int choice = Messages.showYesNoDialog(
                project,
                """
                Meridian needs a local MCP binary to review projects.

                Do you want Meridian to download and install the MCP binary into plugin-managed storage?
                """,
                "Install Meridian MCP Binary",
                "Install",
                "Cancel",
                Messages.getQuestionIcon()
        );

        if (choice != Messages.YES) {
            return;
        }

        MeridianSettingsState settings = ServiceManager.getService(MeridianSettingsState.class);

        new MeridianMcpInstaller(project, settings)
                .installManagedBinary()
                .thenAccept(file -> MeridianNotifications.info(
                        project,
                        "Meridian MCP installed",
                        "Installed MCP binary at: " + file.getAbsolutePath()
                ))
                .exceptionally(error -> {
                    MeridianNotifications.error(
                            project,
                            "Meridian MCP install failed",
                            error.getMessage() == null ? "Could not install Meridian MCP binary." : error.getMessage()
                    );
                    return null;
                });
    }
}