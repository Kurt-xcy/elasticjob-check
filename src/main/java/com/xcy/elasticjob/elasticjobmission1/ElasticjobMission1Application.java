package com.xcy.elasticjob.elasticjobmission1;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ElasticjobMission1Application {

    public static void main(String[] args) {
        SpringApplication.run(ElasticjobMission1Application.class, args);
        //new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }

}
