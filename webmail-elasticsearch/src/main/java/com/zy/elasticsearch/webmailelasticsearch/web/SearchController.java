package com.zy.elasticsearch.webmailelasticsearch.web;

import com.zy.elasticsearch.webmailelasticsearch.Service.MailSearchService;
import com.zy.elasticsearch.webmailelasticsearch.vo.SearchParams;
import com.zy.elasticsearch.webmailelasticsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

    @Autowired
    MailSearchService mailSearchService;

    @GetMapping(path = {"/list.html","/"})
    public String index(SearchParams params, Model model){
        //通过es查询返回数据
        SearchResult result = mailSearchService.search(params);
        model.addAttribute("result",result);
        return "index.html";
    }
}
