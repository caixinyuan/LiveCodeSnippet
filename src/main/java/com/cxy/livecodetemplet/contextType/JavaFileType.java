package com.cxy.livecodetemplet.contextType;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class JavaFileType extends LanguageFileType {
    public static final @NonNls String DEFAULT_EXTENSION = "java";
    public static final @NonNls String DOT_DEFAULT_EXTENSION = ".java";
    public static final JavaFileType INSTANCE = new JavaFileType();

    private JavaFileType() {
        super(JavaLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "JAVA";
    }

    @Override
    public @NotNull String getDescription() {
        return "filetype.java.description";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @Override
    public boolean isJVMDebuggingSupported() {
        return true;
    }
}
