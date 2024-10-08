package com.kob.backend.controller.record;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class GetRecordListController {
    private final GetRecordListService getRecordListService;

    @Autowired
    public GetRecordListController (GetRecordListService getRecordListService) {
        this.getRecordListService = getRecordListService;
    }

    @GetMapping("/getList")
    public JSONObject getList(@RequestParam Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return getRecordListService.getList(page);
    }
}
