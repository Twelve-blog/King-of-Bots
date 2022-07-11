package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotInfo {
    @RequestMapping("getbotinfo/")
    public String getBotInfo(){
        return "Hhhh";
    }
    @RequestMapping("list/")
    public List<String> getString(){
        List<String> list = new LinkedList<>();
        list.add("asdf");
        list.add("xxxx");
        return list;
    }
    @RequestMapping("map/")
    public Map<String,String> getStrings(){
        Map<String,String> map = new HashMap<>();
        map.put("name","tigger");
        map.put("ratting","999");
        return map;
    }
}
