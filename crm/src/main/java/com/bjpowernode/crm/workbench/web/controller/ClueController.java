package com.bjpowernode.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClueController {
    @RequestMapping("/workbench/clue/index.do")
    public String index(){
        return "workbench/clue/index";
    }
}
