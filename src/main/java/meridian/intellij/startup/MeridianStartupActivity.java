package meridian.intellij.startup;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import meridian.intellij.mcp.MeridianProjectService;
import org.jetbrains.annotations.NotNull;

public final class MeridianStartupActivity implements StartupActivity.DumbAware {
    @Override
    public void runActivity(@NotNull Project project) {
        project.getService(MeridianProjectService.class).initialize();
    }
}