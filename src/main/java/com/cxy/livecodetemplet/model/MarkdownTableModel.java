package com.cxy.livecodetemplet.model;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * markdown 表格分析类
 */
public class MarkdownTableModel {

    private List<String> TitleRow;

    private String CodeBlockType;

    private String CodeBlock;


    public List<String> getTitleRow() {
        return this.TitleRow;
    }

    public String getCodeBlockType() {
        return this.CodeBlockType;
    }

    public String getCodeBlock() {
        return this.CodeBlock;
    }

    public void setTitleRow(List<String> titleRow) {
        this.TitleRow = titleRow;
    }

    public void setCodeBlockType(String codeBlockType) {
        this.CodeBlockType = codeBlockType;
    }

    public void setCodeBlock(String codeBlock) {
        this.CodeBlock = codeBlock;
    }


    public boolean isValid() {
        return !TitleRow.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TitleRow: ").append(TitleRow).append("\n");
        sb.append("CodeBlockType: ").append(CodeBlockType).append("\n");
        sb.append("CodeBlock: ").append(CodeBlock).append("\n");
        return sb.toString();
    }
}
