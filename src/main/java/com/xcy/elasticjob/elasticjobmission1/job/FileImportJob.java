package com.xcy.elasticjob.elasticjobmission1.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xcy.elasticjob.elasticjobmission1.model.FileData;
import com.xcy.elasticjob.elasticjobmission1.model.ImportJobDTO;
import com.xcy.elasticjob.elasticjobmission1.service.DataService;
import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * 文件导出，此为第1步
 */
@Component
@Slf4j
public class FileImportJob implements SimpleJob {

    @Autowired
    DataService dataService;

    //文件路径
    public static final String filePath = "D:/Users/kurtfile/IT/FileDir/";

    //文件名
    public static final String fileName = "DatabaseData_";

    @Autowired
    JobStateService jobStateService;

    @Override
    public void execute(ShardingContext shardingContext) {
        if (jobStateService.isImportJobReady()){
            log.info("作业分片："+shardingContext.getShardingItem()+"导入文件开始");
            importFile(shardingContext);
            log.info("作业分片："+shardingContext.getShardingItem()+"导入文件结束");
            ImportJobDTO job = new ImportJobDTO();
            job.setId(shardingContext.getShardingItem());
            job.setFileImportJobState("finish");
            jobStateService.updateImportJobState(job);
        }

    }

    private void importFile(ShardingContext shardingContext){
        //超时报错，整个项目中仅有此处有该功能，作为示例
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime>5*60*1000){//超时5分钟报错
                        log.error("FileImportJob的分片"+shardingContext.getShardingItem()+"处理超时");
                    }
                }
            }
        }).start();


        int sharedNum = shardingContext.getShardingItem();
        List<FileData> list = dataService.selByPage(1000*sharedNum,1000);
        int size = list.size();
        int count = 0;
        FileWriter fw =null;
        BufferedWriter bw =null;

        try{
            fw = new FileWriter(filePath+fileName+sharedNum+".txt");
            bw = new BufferedWriter(fw);
            for (;count<size;count++){
                FileData data = list.get(count);
                String line = data.getSerialNumber()+"\t"+data.getUserId()+"\t"+data.getCardNumber()+"\t"+data.getDealTime().toString()+"\t"+data.getInOrOut();
                bw.write(line+"\t\n");
            }
        }catch(Exception e){
            System.out.println(e);
        }
        finally{
            try{
                bw.close();
                fw.close();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }



}
