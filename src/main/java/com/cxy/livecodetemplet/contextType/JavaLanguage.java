package com.cxy.livecodetemplet.contextType;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public final class JavaLanguage extends Language {

    @NotNull
    public static final JavaLanguage INSTANCE = new JavaLanguage();

    private JavaLanguage() {
        super("JAVA2", "text/x-java-source", "text/java", "application/x-java", "text/x-java");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Java";
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
