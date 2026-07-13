package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import meridian.intellij.mcp.MeridianProjectService;
import org.jetbrains.annotations.NotNull;

public final class ClearContextAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        MeridianActionUtil.requireProject(event)
                .getService(MeridianProjectService.class)
                .clearContext();
    }
}