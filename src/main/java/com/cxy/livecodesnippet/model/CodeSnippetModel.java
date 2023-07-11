package com.cxy.livecodesnippet.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 代码模板类
 */
public class CodeSnippetModel {

    private Integer id;


    //标题
    private String title;

    //描述
    private String describe;

    //标签
    private List<String> tag;

    //添加人
    private String people;

    //版本
    private Float version;

    //代码模板
    private String codeSnippet;

    //代码类型
    private String codeType;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = (Arrays.asList(tag.split(",")));
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public Float getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = Float.parseFloat(version);
    }

    public String getCodeSnippet() {
        return this.codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getCodeType() {
        return this.codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeSnippetModel model = (CodeSnippetModel) o;
        return Objects.equals(id, model.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
