package com.cxy.livecodetemplet.provider;


import com.cxy.livecodetemplet.Util.UtilState;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameViewModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SnippetChooseByNameItemProvider implements ChooseByNameItemProvider {

    @Override
    public @NotNull List<String> filterNames(@NotNull ChooseByNameViewModel base, String @NotNull [] names, @NotNull String pattern) {
        return Arrays.stream(names)
                .filter(s -> s.toLowerCase().contains(pattern.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean filterElements(@NotNull ChooseByNameViewModel base, @NotNull String pattern, boolean everywhere, @NotNull ProgressIndicator cancelled, @NotNull Processor<Object> consumer) {
        UtilState.getInstance().getCodeTempletList().forEach(i -> {
            if (i.getTitle().contains(pattern) || i.getTitle().toLowerCase().contains(pattern)) {
                consumer.process(i);
            }
        });
        return true;
    }
}