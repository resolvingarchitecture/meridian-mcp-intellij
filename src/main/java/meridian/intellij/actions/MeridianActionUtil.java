package meridian.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.nio.file.Path;

final class MeridianActionUtil {
    private MeridianActionUtil() {
    }

    static Project requireProject(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            throw new IllegalStateException("No active IntelliJ project.");
        }

        return project;
    }

    static VirtualFile currentFile(Editor editor) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        if (file == null) {
            throw new IllegalStateException("No active editor file.");
        }

        return file;
    }

    static String relativePath(Project project, VirtualFile file) {
        String basePath = project.getBasePath();
        if (basePath == null || basePath.isBlank()) {
            return file.getPath();
        }

        return Path.of(basePath).relativize(Path.of(file.getPath())).toString();
    }
}