package com.xcy.elasticjob.elasticjobmission1.controller;

import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JobStateController {

    @Autowired
    JobStateService jobStateService;

    @RequestMapping("/setImportJobReady")
    @ResponseBody
    public String setImportJobReady(){
        jobStateService.updImportJobReady();
        return "success";
    }

    @RequestMapping("/setImportJobFinish")
    @ResponseBody
    public String setImportJobFinish(){
        jobStateService.updImportJobFinish();
        return "success";
    }

    @RequestMapping("/setDivideJobReady")
    @ResponseBody
    public String setDivideJobReady(){
        jobStateService.updDivideJobReady();
        return "success";
    }

    @RequestMapping("/setDivideJobFinish")
    @ResponseBody
    public String setDivideJobFinish(){
        jobStateService.updImportJobFinish();
        return "success";
    }

    @RequestMapping("/setSortJobReady")
    @ResponseBody
    public String setSortJobReady(){
        jobStateService.updSortJobReady();
        return "success";
    }

        @RequestMapping("/setSortJobFinish")
    @ResponseBody
    public String setSortJobFinish(){
        jobStateService.updSortJobFinish();
        return "success";
    }

    @RequestMapping("/setCheckJobReady")
    @ResponseBody
    public String setCheckJobReady(){
        jobStateService.updCheckJobReady();
        return "success";
    }

    @RequestMapping("/srtCheckJobFinish")
    @ResponseBody
    public String srtCheckJobFinish(){
        jobStateService.updCheckJobFinish();
        return "success";
    }

}
