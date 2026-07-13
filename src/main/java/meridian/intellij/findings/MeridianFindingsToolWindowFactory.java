package meridian.intellij.findings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public final class MeridianFindingsToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MeridianFindingsPanel panel = new MeridianFindingsPanel(project);
        Content content = toolWindow.getContentManager()
                .getFactory()
                .createContent(panel, "Findings", false);

        toolWindow.getContentManager().addContent(content);
    }
}