package com.xcy.elasticjob.elasticjobmission1.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;


import com.xcy.elasticjob.elasticjobmission1.model.DivideJobDTO;
import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import com.xcy.elasticjob.elasticjobmission1.util.CheckUtils;
import com.xcy.elasticjob.elasticjobmission1.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 文件分割任务，先分割后排序再比对，此为第2步
 * @author Administrator
 * @version 1.0
 **/
@Component
@Slf4j
public class FileDivideJob implements SimpleJob {


    @Autowired
    JobStateService jobStateService;

    //分割后的文件路径
    public static final String filePath = "D:/Users/kurtfile/IT/FileDivideDir/";

    //分割后的文件名
    //后缀自行添加
    public static final String fileName = "shardFile_";


    //任务执行代码逻辑
    @Override
    public void execute(ShardingContext shardingContext) {
        if (jobStateService.isImportJobFinish()&&jobStateService.isDivideJobReady()){
            log.info("作业分片："+shardingContext.getShardingItem()+"分割文件开始");
            getShardData(shardingContext);
            log.info("作业分片："+shardingContext.getShardingItem()+"分割文件完成");
            DivideJobDTO job = new DivideJobDTO();
            job.setId(shardingContext.getShardingItem());
            job.setFileDivideJobState("finish");
            jobStateService.updateDivideJobState(job);
        }

    }

    /**
     * 获取流水尾号为分片数的数据
     * @return
     */
    public void getShardData(ShardingContext shardingContext){

        FileReader reader =null;
        BufferedReader bufferedReader =null;
        //当前读取行数
        int num = 0;
        //当前分片数
        int shardingItem = shardingContext.getShardingItem();

        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath+fileName+shardingItem+".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(fw);
        for (int j=0;j<10;j++){
            try {
                reader = new FileReader(FileImportJob.filePath+FileImportJob.fileName+j+".txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bufferedReader = new BufferedReader(reader);
            String line =null;
            while(true){
                try {
                    line =bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (CheckUtils.checkData(line,shardingItem)){
                    FileUtils.writeFile(bw,line);
                }
                num++;

                if(line ==null){
                    break;
                }
                System.out.println(line);
            }

            try {
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        FileUtils.closeFile(fw,bw);



        log.info("分片："+shardingItem+"分割完成");
    }


}
