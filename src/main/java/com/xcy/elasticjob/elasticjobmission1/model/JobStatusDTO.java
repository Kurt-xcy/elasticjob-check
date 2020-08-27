package com.xcy.elasticjob.elasticjobmission1.model;

import lombok.Data;

@Data
public class JobStatusDTO {
    int id;
    String fileCheckJobState;
    int checkFinishNum;
    String fileDivideJobState;
    int divideFinishNum;
    String fileImportJobState;
    int importFinishNum;
    String fileSortJobState;
    int sortFinishNum;
}
