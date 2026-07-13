package meridian.intellij.notifications;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public final class MeridianNotifications {
    private MeridianNotifications() {
    }

    public static void info(Project project, String title, String content) {
        notify(project, title, content, NotificationType.INFORMATION);
    }

    public static void warn(Project project, String title, String content) {
        notify(project, title, content, NotificationType.WARNING);
    }

    public static void error(Project project, String title, String content) {
        notify(project, title, content, NotificationType.ERROR);
    }

    private static void notify(Project project, String title, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("IDE and Plugin Updates")
                .createNotification(title, content, type)
                .notify(project);
    }
}