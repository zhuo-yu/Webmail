package com.zy.elasticsearch.webmailelasticsearch.Service;

import com.zy.elasticsearch.webmailelasticsearch.vo.SearchParams;
import com.zy.elasticsearch.webmailelasticsearch.vo.SearchResult;

public interface MailSearchService {
    /**
     * 查询返回商品信息
     * @param params
     * @return
     */
    public SearchResult search(SearchParams params);
}