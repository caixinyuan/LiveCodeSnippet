package com.cxy.livecodetemplet.model;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * markdown 表格分析类
 */
public class MarkdownTableModel {
    private List<String> header;
    private String type;
    private List<List<String>> data;
    private String codetemplet;

    public MarkdownTableModel() {
        this.header = new ArrayList<>();
        this.type = "";
        this.data = new ArrayList<>();
        this.codetemplet = "";
    }

    public void addRow(String row) {
        String[] columns = row.split("\\|");
        List<String> rowData = new ArrayList<>();
        for (int i = 1; i < columns.length; i++) {
            rowData.add(columns[i].trim());
        }
        if (header.isEmpty()) {  // 如果当前还没有表头，则认为当前行为表头
            header = rowData;
        } else {
            data.add(rowData);
        }
    }

    public void addCodeTemplet(String line, String codeType) {
        if (!StrUtil.equals(line, "```")) {
            codetemplet += line + "\n";
        }
    }

    public void addType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public List<List<String>> getData() {
        return this.data;
    }

    public String getCodetemplet() {
        return this.codetemplet;
    }

    public boolean isValid() {
        return !header.isEmpty() && !data.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Codetemplet: ").append(codetemplet).append("\n");
        sb.append("Header: ").append(header).append("\n");
        sb.append("Data: ").append(data).append("\n");
        sb.append("Type: ").append(type).append("\n");

        return sb.toString();
    }
}
