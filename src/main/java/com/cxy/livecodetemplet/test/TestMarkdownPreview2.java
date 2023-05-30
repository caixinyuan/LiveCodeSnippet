package com.cxy.livecodetemplet.test;

import com.cxy.livecodetemplet.Util.MarkDownUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.ui.jcef.JBCefBrowser;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefContextMenuHandlerAdapter;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.intellij.plugins.markdown.ui.preview.MarkdownPreviewFileEditorProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TestMarkdownPreview2 extends AnAction {

    @NotNull
    protected final FileEditorProvider mySecondProvider = new MarkdownPreviewFileEditorProvider();

    private String selectText;

    @Override
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("TestMarkdownPreview");
        JBCefBrowser jbCefBrowser = JBCefBrowser.createBuilder().createBrowser();
        jbCefBrowser.getJBCefClient().addContextMenuHandler(new MenuHandler(), jbCefBrowser.getCefBrowser());
        jbCefBrowser.loadHTML(MarkDownUtil.getInstance().getMarkdownHtml("# Test"));


        //配置一个查询路由,html页面可使用 window.java({}) 和 window.javaCancel({}) 来调用此方法
        CefMessageRouter.CefMessageRouterConfig cmrc = new CefMessageRouter.CefMessageRouterConfig("java", "javaCancel");
        //创建查询路由
        CefMessageRouter cmr = CefMessageRouter.create(cmrc);
        cmr.addHandler(new CefMessageRouterHandlerAdapter() {
            @Override
            public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
                if (StringUtils.isNotEmpty(request)) {
                    selectText = request;
                }
                return super.onQuery(browser, frame, queryId, request, persistent, callback);
            }
        }, true);

        jbCefBrowser.getCefBrowser().getClient().addMessageRouter(cmr);
        JComponent jbCefBrowserComponent = jbCefBrowser.getComponent();
        frame.setContentPane(jbCefBrowserComponent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}


class MenuHandler extends CefContextMenuHandlerAdapter {
    private final static int MENU_ID_ADDTEXT = 10001;

    @Override
    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
        //清除菜单项
        model.clear();
        //剪切、复制、粘贴
        model.addItem(CefMenuModel.MenuId.MENU_ID_COPY, "复制");
        model.addSeparator();
        model.addItem(MENU_ID_ADDTEXT, "插入选定的内容");
    }

    /*
     * @see org.cef.handler.CefContextMenuHandler#onContextMenuCommand(org.cef.browser.CefBrowser, org.cef.browser.CefFrame, org.cef.callback.CefContextMenuParams, int, int)
     */
    @Override
    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
        switch (commandId) {
            case MENU_ID_ADDTEXT:
                String script = "window.getSelection().toString();";
                browser.executeJavaScript(script, "", 0);


                frame.executeJavaScript("var a= window.getSelection().toString();" + "window.java({\n" +
                        "    request: a,\n" +
                        "    persistent:false,\n" +
                        "    onSuccess: function(response) {\n" +
                        "      alert(\"返回的数据:\"+response);\n" +
                        "    },\n" +
                        "    onFailure: function(error_code, error_message) {}\n" +
                        "});", frame.getURL(), 0);


                return true;
        }
        return false;
    }
}