package com.snrt.helloworld.vo;

import java.util.List;

public class DataVo {
    private Long timestamp;
    private Integer total;

    private List<InfoVO> info;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<InfoVO> getInfo() {
        return info;
    }

    public void setInfo(List<InfoVO> info) {
        this.info = info;
    }
}
