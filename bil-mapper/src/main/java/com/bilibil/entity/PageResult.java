package com.bilibil.entity;

import java.util.List;

/**
 * Date:  2023/8/22
 * 分页查询
 */
public class PageResult<T> {
    // 结果总数
    private Integer total;
    // 当前页列表
    private List<T> list;

    public PageResult(Integer total, List<T> list){
        this.total = total;
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
