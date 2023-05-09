package com.cxy.livecodetemplet.test;

import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestMd {
    public static void main(String[] args) throws IOException {

        String filename = "/home/caixy/code/Temp/CodeTemplet.md";
        List<MarkdownTable> tables = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            // 查找所有的分隔线
            List<Integer> separatorIndices = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).matches("^(\\||\\-)+$")) {
                    separatorIndices.add(i);
                }
            }

            MarkdownTable currentTable = null;
            Integer startIndex = 0, endIndex = 0;
            boolean inCodeBlock = false;
            for (Integer i : separatorIndices) {
                endIndex = (i + 1);
                List<String> table = lines.subList(startIndex, endIndex);
                for (String l : table) {
                    if (null == l) {
                        continue;
                    }
                    if (l.startsWith("|")) {
                        // 如果当前行以 "|" 开头，则认为是表格的一行
                        if (currentTable == null) {
                            // 如果当前没有找到表格，则新建一个表格
                            currentTable = new MarkdownTable();
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


//            MarkdownTable currentTable = null;
//            boolean inCodeBlock = false;
//
//
//            while ((line = br.readLine()) != null) {
//                if (line.startsWith("|")) {  // 如果当前行以 "|" 开头，则认为是表格的一行
//                    if (currentTable == null) {  // 如果当前没有找到表格，则新建一个表格
//                        currentTable = new MarkdownTable();
//                    }
//
//                    currentTable.addRow(line);
//                } else if (line.startsWith("```") && line.endsWith("java")) {  // 如果当前行以 ```java 开头，则认为进入代码块
//                    inCodeBlock = true;
//                } else if (line.equals("```") && inCodeBlock) {  // 如果当前行为 ```，则认为离开代码块
//                    inCodeBlock = false;
//                } else if (line.startsWith("---") || line.startsWith("***")) {  // 如果当前行为分割线，则认为当前表格结束
//                    if (currentTable != null && currentTable.isValid()) {  // 如果当前表格是有效的，则添加到表格列表中
//                        tables.add(currentTable);
//                    }
//                    currentTable = null;
//                } else {  // 其他情况，认为当前不是表格的一行
//                    if (currentTable != null) {  // 如果当前已找到表格，则将其添加到表格中
//                        currentTable.addDescription(line);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

            for (MarkdownTable table : tables) {
                System.out.println(table);
            }
        }
    }
}

class MarkdownTable {
    private List<String> header;
    private List<List<String>> data;
    private String codetemplet;

    public MarkdownTable() {
        this.header = new ArrayList<>();
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

    public void addDescription(String line) {
        codetemplet += line + "\n";
    }

    public void addCodeTemplet(String line) {
        if (!StrUtil.equals(line, "```")) {
            codetemplet += line + "\n";
        }
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

        return sb.toString();
    }
}