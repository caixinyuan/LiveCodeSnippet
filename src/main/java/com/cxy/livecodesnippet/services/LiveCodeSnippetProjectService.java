package com.cxy.livecodesnippet.services;

import com.cxy.livecodesnippet.Util.PluginMessage;
import com.cxy.livecodesnippet.Util.SQLiteUtil;
import com.cxy.livecodesnippet.Util.Util;
import com.cxy.livecodesnippet.storage.LiveCodeSnippetProjectStorageSetting;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class LiveCodeSnippetProjectService implements Disposable {
    private static final Logger log = Logger.getInstance(LiveCodeSnippetProjectService.class);

    @Override
    public void dispose() {

    }

    public LiveCodeSnippetProjectService(@NotNull Project project) {
        super();
        //从本地读取代码片段
        loadMdTemplet();
    }

    //加载本地代码模板
    public static void loadMdTemplet() {
        if (SQLiteUtil.getInstance().dbFileIsExist()&&SQLiteUtil.getInstance().getSnippetCount()==0){
            try {
                //默认从github下载示例模板
                String url = Util.defaultRemoteURL;
                LiveCodeSnippetProjectStorageSetting storageSetting = LiveCodeSnippetProjectStorageSetting.getInstance();
                if (null != storageSetting.getState() && StringUtils.isNotEmpty(storageSetting.getState().getUrl())) {
                    url = storageSetting.getState().getUrl();
                }
                Util.loadRemoteMd(url);
            } catch (Exception ex) {
                log.error("从GitHub加载默认模板失败", ex);
                PluginMessage.notifyError("从GitHub加载默认模板失败");
            }
        }
    }
}
