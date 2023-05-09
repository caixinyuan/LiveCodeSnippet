package com.cxy.livecodetemplet.Util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.cxy.livecodetemplet.model.MarkdownTableModel;
import com.cxy.livecodetemplet.services.LiveCodeTempletService;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.util.CharsetUtil.CHARSET_UTF_8;

public class Util {

    public final static URI localMdFilePath = Paths.get(PathManager.getConfigPath(), "CodeTemplet.md").toUri();

    public final static String defaultRemoteURL = "https://raw.githubusercontent.com/caixinyuan/LiveCodeTemplet/main/CodeTemplet.lmd";

    private static final Logger log = Logger.getInstance(LiveCodeTempletService.class);


    public static File downLoadRemoteMdFile(String url) throws Exception {
        File mdFile = new File(localMdFilePath);
        long fileSize = HttpUtil.downloadFile(url, mdFile, 3000);
        if (fileSize > 0 && mdFile.exists()) {
            PluginMessage.notifyInfo("已从远程地址下载模板");
            return mdFile;
        }
        throw new Exception("FileDownloadFail");
    }

    /**
     * 加载远程模板
     */
    public static void loadRemoteMd(String url) {
        ProgressManager.getInstance().run(new Task.Backgroundable(null, "正在从远程地址下载文件") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    File mdFile = downLoadRemoteMdFile(url);
                    setCodeTempletList(getCodeTempletList(mdFile));
                    PluginMessage.notifyInfo("加载模板完成");
                } catch (Exception ex) {
                    log.error("从远程加载模板超时 url:[{}]", ex, url);
                    PluginMessage.notifyError(String.format("从远程加载模板超时 url:[%s]", url));
                }
            }
        });
    }


    public static void setCodeTempletList(List<CodeTempletModel> codeTempletList) {
        UtilState.getInstance().clearCodeTempletList();
        UtilState.getInstance().setCodeTempletList(codeTempletList);
    }

    /**
     * 本地模板文件是否存在
     *
     * @return
     */
    public static Boolean loaclMdFileExists() {
        File localMdFile = new File(localMdFilePath);
        return localMdFile.exists();
    }


    /**
     * 获得代码模板集合
     *
     * @param file
     * @return
     */
    public static List<CodeTempletModel> getCodeTempletList(File file) {
        try {
            List<CodeTempletModel> codeTempletList = new ArrayList<>();
            List<MarkdownTableModel> mdTableList = Util.getMdTables(file);
            mdTableList.forEach(i -> {
                CodeTempletModel codeTemplet = new CodeTempletModel();
                List<String> rowData = i.getData().get(1);
                codeTemplet.setTitle(rowData.get(0));
                codeTemplet.setDescribe(rowData.get(1));
                codeTemplet.setTag(rowData.get(2));
                codeTemplet.setPeople(rowData.get(3));
                codeTemplet.setVersion(rowData.get(4));
                codeTemplet.setCodeTemplet(i.getCodetemplet());
                codeTempletList.add(codeTemplet);
            });
            return codeTempletList;
        } catch (Exception ex) {
            log.error("从文件中获取模板集合失败", ex);
        }
        return new ArrayList();
    }

    /**
     * 解析MarkDown文件
     *
     * @param file
     * @return
     */
    private static List<MarkdownTableModel> getMdTables(File file) {
        List<MarkdownTableModel> tables = new ArrayList<>();
        List<String> mdLines = FileUtil.readLines(file, CHARSET_UTF_8);
        log.debug(mdLines.get(0));

        // 查找md所有的分隔线
        List<Integer> separatorIndices = new ArrayList<>();
        for (int i = 0; i < mdLines.size(); i++) {
            if (mdLines.get(i).matches("^(\\||\\-)+$")) {
                separatorIndices.add(i);
            }
        }
        MarkdownTableModel currentTable = null;
        Integer startIndex = 0, endIndex = 0;
        boolean inCodeBlock = false;
        for (Integer i : separatorIndices) {
            endIndex = (i + 1);
            List<String> table = mdLines.subList(startIndex, endIndex);
            for (String l : table) {
                if (null == l) {
                    continue;
                }
                if (l.startsWith("|")) {
                    // 如果当前行以 "|" 开头，则认为是表格的一行
                    if (currentTable == null) {
                        // 如果当前没有找到表格，则新建一个表格
                        currentTable = new MarkdownTableModel();
                    }
                    currentTable.addRow(l);
                } else if (l.startsWith("```") && l.endsWith("java")) {
                    // 如果当前行以 ```java 开头，则认为进入代码块
                    inCodeBlock = true;
                } else if (l.equals("```") && inCodeBlock) {
                    // 如果当前行为 ```，则认为离开代码块
                    inCodeBlock = false;
                } else if (l.startsWith("---") || l.startsWith("***")) {
                    // 如果当前行为分割线，则认为当前表格结束
                    if (currentTable != null && currentTable.isValid()) {
                        // 如果当前表格是有效的，则添加到表格列表中
                        tables.add(currentTable);
                    }
                    currentTable = null;
                } else {
                    // 其他情况，认为当前不是表格的一行
                    if (currentTable != null && inCodeBlock) {
                        // 如果当前已找到表格，则将其添加到表格中
                        currentTable.addCodeTemplet(l);
                    }
                }
            }
            startIndex = endIndex;
        }
        return tables;
    }
}
