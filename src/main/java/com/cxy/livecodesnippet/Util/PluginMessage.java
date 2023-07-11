package com.cxy.livecodesnippet.Util;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.Nullable;

public class PluginMessage {
    private static NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("LiveCodeSnippetNotificationGroup");


    public static void notifyError(@Nullable String content) {

        notificationGroup.createNotification(content, NotificationType.ERROR).notify(UtilState.getInstance().getProject());
    }

    public static void notifyInfo(@Nullable String content) {
        notificationGroup.createNotification(content, NotificationType.INFORMATION).notify(UtilState.getInstance().getProject());
    }

    public static void notifyWarning(@Nullable String content) {
        notificationGroup.createNotification(content, NotificationType.WARNING).notify(UtilState.getInstance().getProject());
    }
}
