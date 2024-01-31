package com.kob.backend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rankList")
public class GetRankListController {
    private final GetRankListService getRankListService;

    @Autowired
    public GetRankListController(GetRankListService getRankListService) {
        this.getRankListService = getRankListService;
    }

    @GetMapping("/getList")
    public JSONObject getList(@RequestParam Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return getRankListService.getList(page);
    }
}
