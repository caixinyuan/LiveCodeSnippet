package com.cxy.livecodetemplet.provider;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

public class LiveTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[0];
    }

    @Override
    public String @Nullable [] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
