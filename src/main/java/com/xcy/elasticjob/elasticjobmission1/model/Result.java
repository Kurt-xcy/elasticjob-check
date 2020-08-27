package com.xcy.elasticjob.elasticjobmission1.model;

import lombok.Data;

import java.util.Date;

@Data
public class Result {
   public int id;
    public String type;
    public String wrongFile1;
    public String wrongFile2;
    public int wrongLine1;
    public int wrongLine2;
    public String discrip;
    public Date generateTime;
}
