package com.snrt.helloworld.vo;

import java.util.List;

/**
 * 搜索结果VO类
 * 包含搜索返回的歌曲列表和分页信息
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class SearchResult {

    // 歌曲列表
    private List<Song> songs;

    // 搜索关键词
    private String keyword;

    // 总数量
    private Integer total;

    // 当前页码
    private Integer page;

    // 每页数量
    private Integer pageSize;

    // 是否有更多结果
    private Boolean hasMore;

    public SearchResult() {
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * 获取格式化后的搜索结果信息
     *
     * @return 结果描述
     */
    public String getResultInfo() {
        if (songs == null || songs.isEmpty()) {
            return "未找到相关歌曲";
        }
        return String.format("找到 %d 首歌曲", total);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "keyword='" + keyword + '\'' +
                ", total=" + total +
                ", page=" + page +
                ", hasMore=" + hasMore +
                '}';
    }
}
