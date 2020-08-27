package com.xcy.elasticjob.elasticjobmission1.util;

import com.xcy.elasticjob.elasticjobmission1.job.FileCheckJob;
import com.xcy.elasticjob.elasticjobmission1.job.FileSortJob;
import com.xcy.elasticjob.elasticjobmission1.model.Result;

import java.util.Date;

public class GenerateResult {
    //卡号不相等
    public static final String wrongCard = "wrongCard";

    //用户ID不相等
    public static final String wrongUserId = "wrongUserId";

    //多出一条数据
    public static final String moreData = "moreData";


    public static Result getWrongCardResult(int shardingItem, int wrongLine1, int wrongLine2,String discript){
        Result result = new Result();
        result.setType(wrongCard);
        result.setWrongFile1(FileSortJob.sortFilePath + FileSortJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine1(wrongLine1);
        result.setWrongFile2(FileCheckJob.sortFile2Path + FileCheckJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine2(wrongLine2);
        result.setDiscrip(discript);
        result.setGenerateTime(new Date());
        return result;
    }

    public static Result getWrongUserIdResult(int shardingItem, int wrongLine1, int wrongLine2,String discript){
        Result result = new Result();
        result.setType(wrongUserId);
        result.setWrongFile1(FileSortJob.sortFilePath + FileSortJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine1(wrongLine1);
        result.setWrongFile2(FileCheckJob.sortFile2Path + FileCheckJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine2(wrongLine2);
        result.setDiscrip(discript);
        result.setGenerateTime(new Date());
        return result;
    }

    public static Result getMoreDataResult(int shardingItem, int wrongLine1, int wrongLine2,String discript){
        Result result = new Result();
        result.setType(moreData);
        result.setWrongFile1(FileSortJob.sortFilePath + FileSortJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine1(wrongLine1);
        result.setWrongFile2(FileCheckJob.sortFile2Path + FileCheckJob.sortFileName + shardingItem + ".txt");
        result.setWrongLine2(wrongLine2);
        result.setDiscrip(discript);
        result.setGenerateTime(new Date());
        return result;
    }
}
