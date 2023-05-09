package com.cxy.livecodetemplet.contextType;

import com.intellij.codeInsight.template.TemplateContextType;

public class LiveCodeTempletContextType extends TemplateContextType {
    protected LiveCodeTempletContextType() {
        super("JAVA_CODE", "LiveCodeTemplet");
    }

    public static LiveCodeTempletContextType getContext() {
        return new LiveCodeTempletContextType();
    }
}
