package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import meridian.intellij.mcp.MeridianProjectService;
import org.jetbrains.annotations.NotNull;

public final class DiagnosticsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = MeridianActionUtil.requireProject(event);
        String diagnostics = project.getService(MeridianProjectService.class).diagnostics();

        Messages.showInfoMessage(project, diagnostics, "Meridian Diagnostics");
    }
}