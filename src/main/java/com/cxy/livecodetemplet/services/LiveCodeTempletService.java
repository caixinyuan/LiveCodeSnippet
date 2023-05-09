package com.cxy.livecodetemplet.services;

import com.cxy.livecodetemplet.Util.PluginMessage;
import com.cxy.livecodetemplet.Util.Util;
import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.contextType.LiveCodeTempletContextType;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.cxy.livecodetemplet.storage.LiveCodeTempletStorageSetting;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.codeInsight.template.impl.TemplateContext;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LiveCodeTempletService implements Disposable {
    private static final Logger log = Logger.getInstance(LiveCodeTempletService.class);

    @Override
    public void dispose() {

    }

    public LiveCodeTempletService(@NotNull Project project) {
        super();
        //从本地读取代码片段
        loadMdTemplet();
//        TemplateImpl template = new TemplateImpl("tmKey", "codeString", "groupName");
//        template.setToReformat(true);
//        template.setGroupName("groupName");
//        template.setDescription("");
//        template.createContext().setEnabled(LiveCodeTempletContextType.getContext(), true);
        //TemplateSettings.getInstance().addTemplate(template);
        //TemplateSettings.getInstance().getTemplateById()
    }

    //加载本地代码模板
    public static void loadMdTemplet() {
        File localMdFile = new File(Util.localMdFilePath);
        if (!localMdFile.exists() || !localMdFile.isFile()) {
            try {
                //默认从github下载示例模板
                String url = Util.defaultRemoteURL;
                LiveCodeTempletStorageSetting storageSetting = LiveCodeTempletStorageSetting.getInstance();
                if (null != storageSetting.getState() && StringUtils.isNotEmpty(storageSetting.getState().getUrl())) {
                    url = storageSetting.getState().getUrl();
                }
                Util.loadRemoteMd(url);
            } catch (Exception ex) {
                log.error("FileDownloadFail", ex);
                PluginMessage.notifyError(String.format("FileDownloadFail"));
                return;
            }
        } else {
            UtilState.getInstance().setCodeTempletList(Util.getCodeTempletList(localMdFile));
        }
    }
}
