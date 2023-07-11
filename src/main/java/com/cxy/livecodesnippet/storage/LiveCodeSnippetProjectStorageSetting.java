package com.cxy.livecodesnippet.storage;

import com.cxy.livecodesnippet.model.PluginSettingModel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "LiveCodeSnippetProjectStorageSetting", storages = {@Storage(value = "LiveCodeSnippetProjectStorageSetting.xml")})
public class LiveCodeSnippetProjectStorageSetting implements PersistentStateComponent<PluginSettingModel> {

    public LiveCodeSnippetProjectStorageSetting() {
    }

    private PluginSettingModel pluginSetting = new PluginSettingModel();

    @Override
    public @Nullable PluginSettingModel getState() {
        return pluginSetting;
    }

    @Override
    public void loadState(@NotNull PluginSettingModel state) {
        this.pluginSetting = state;
    }

    public static LiveCodeSnippetProjectStorageSetting getInstance() {
        return ApplicationManager.getApplication().getService(LiveCodeSnippetProjectStorageSetting.class);
    }
}

