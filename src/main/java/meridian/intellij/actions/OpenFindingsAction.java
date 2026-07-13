package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public final class OpenFindingsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = MeridianActionUtil.requireProject(event);

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Meridian");
        if (toolWindow != null) {
            toolWindow.show();
        }
    }
}