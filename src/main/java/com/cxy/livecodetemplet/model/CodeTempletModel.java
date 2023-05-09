package com.cxy.livecodetemplet.model;

import java.util.Arrays;
import java.util.List;

/**
 * 代码模板类
 */
public class CodeTempletModel {
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
    private String codeTemplet;


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

    public String getCodeTemplet() {
        return this.codeTemplet;
    }

    public void setCodeTemplet(String codeTemplet) {
        this.codeTemplet = codeTemplet;
    }
}
