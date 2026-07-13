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

public final class AddSelectionAsContextAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = MeridianActionUtil.requireProject(event);
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (editor == null || !editor.getSelectionModel().hasSelection()) {
            MeridianNotifications.warn(project, "Meridian context unavailable", "Select editor text before adding context.");
            return;
        }

        VirtualFile file = MeridianActionUtil.currentFile(editor);
        String relativePath = MeridianActionUtil.relativePath(project, file);
        String selectedText = editor.getSelectionModel().getSelectedText();

        if (selectedText == null || selectedText.isBlank()) {
            MeridianNotifications.warn(project, "Meridian context unavailable", "Selected text is empty.");
            return;
        }

        project.getService(MeridianProjectService.class)
                .addSelectionAsContext(relativePath, selectedText);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setEnabled(event.getProject() != null && editor != null && editor.getSelectionModel().hasSelection());
    }
}