package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import meridian.intellij.findings.MeridianFindingsState;
import meridian.intellij.notifications.MeridianNotifications;
import org.jetbrains.annotations.NotNull;

public final class ClearFindingsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = MeridianActionUtil.requireProject(event);
        project.getService(MeridianFindingsState.class).clearFindings();
        MeridianNotifications.info(project, "Meridian", "Local findings cleared.");
    }
}