package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import meridian.intellij.mcp.MeridianProjectService;
import meridian.intellij.notifications.MeridianNotifications;
import org.jetbrains.annotations.NotNull;

public final class ReviewCurrentFileAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = MeridianActionUtil.requireProject(event);
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (editor == null) {
            MeridianNotifications.warn(project, "Meridian review unavailable", "Open an editor file before reviewing.");
            return;
        }

        VirtualFile file = MeridianActionUtil.currentFile(editor);
        String relativePath = MeridianActionUtil.relativePath(project, file);
        String content = editor.getDocument().getText();

        project.getService(MeridianProjectService.class)
                .reviewCurrentFile(relativePath, content);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(event.getProject() != null && event.getData(CommonDataKeys.EDITOR) != null);
    }
}