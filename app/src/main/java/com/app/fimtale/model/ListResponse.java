package com.app.fimtale.model;

import java.util.List;

/**
 * 分页列表响应包装
 */
public class ListResponse<T> {
    private List<T> list;
    private int total;

    public List<T> getList() { return list; }
    public int getTotal() { return total; }
}
