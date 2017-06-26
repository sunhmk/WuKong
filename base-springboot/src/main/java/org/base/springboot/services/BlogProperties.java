package org.base.springboot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @date 16/5/5 下午12:16.
 * @blog http://blog.didispace.com
 */
@Component
public class BlogProperties {

    @Value("${org.base.springboot.services.blog.name}")
    private String name;
    @Value("${org.base.springboot.services.blog.title}")
    private String title;
    @Value("${org.base.springboot.services.blog.desc}")
    private String desc;

    @Value("${org.base.springboot.services.blog.value}")
    private String value;
    @Value("${org.base.springboot.services.blog.number}")
    private Integer number;
    @Value("${org.base.springboot.services.blog.bignumber}")
    private Long bignumber;
    @Value("${org.base.springboot.services.blog.test1}")
    private Integer test1;
    @Value("${org.base.springboot.services.blog.test2}")
    private Integer test2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getBignumber() {
        return bignumber;
    }

    public void setBignumber(Long bignumber) {
        this.bignumber = bignumber;
    }

    public Integer getTest1() {
        return test1;
    }

    public void setTest1(Integer test1) {
        this.test1 = test1;
    }

    public Integer getTest2() {
        return test2;
    }

    public void setTest2(Integer test2) {
        this.test2 = test2;
    }
}
