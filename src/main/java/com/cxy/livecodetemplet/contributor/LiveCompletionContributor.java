package com.cxy.livecodetemplet.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import org.jetbrains.annotations.NotNull;

public class LiveCompletionContributor extends CompletionContributor {
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        if (result.getPrefixMatcher().isStartMatch(".")) {
            return;
        }
        String[] key={"2","4"};
        for (String s : key) {
            result.getPrefixMatcher().prefixMatches(s);
        }

//        List<CodeTempletModel> codeTempletList = LiveCodeTempletService.getCodeTempletList();
//        codeTempletList.forEach(i -> {
//            result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder
//                    .create(i.getCodeTemplet())
//                    .withInsertHandler((Context, item) -> {
//                        Context.getStartOffset();
//                        item.getLookupString();
//                    })
//                    .withLookupStrings(i.getTag())
//                    .withPresentableText(i.getCodeTemplet())
//                    .withCaseSensitivity(true)//大小写不敏感
//                    .withTypeText(i.getDescribe(), true)
//                    .bold(), 0));
//        });
    }
}