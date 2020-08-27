package com.xcy.elasticjob.elasticjobmission1.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;


import com.xcy.elasticjob.elasticjobmission1.model.SortJobDTO;
import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import com.xcy.elasticjob.elasticjobmission1.util.CheckUtils;
import com.xcy.elasticjob.elasticjobmission1.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件排序任务，先分割后排序再比对，此为第3步
 * @author Administrator
 * @version 1.0
 **/
@Component
@Slf4j
public class FileSortJob implements SimpleJob {


    @Autowired
    JobStateService jobStateService;

    //排序后文件路径
    public static final String sortFilePath = "D:/Users/kurtfile/IT/FileSortDir/";

    //分片后文件名
    //后缀自行添加.txt
    public static final String sortFileName = "sortFile_";

    //超过该行数，排序算法改为分步读取文件，避免OOM问题
    public  static final int sortLines = 5000;


    public  static int totalLines = 0;


    //任务执行代码逻辑
    @Override
    public void execute(ShardingContext shardingContext) {

        // log.info("作业分片："+shardingContext.getShardingItem()+"在线");
        if (jobStateService.isDivideJobFinish() && jobStateService.isSortJobReady()){
            try {
            totalLines = FileUtils.getTotalLines(new File(FileDivideJob.filePath+FileDivideJob.fileName+shardingContext.getShardingItem()+".txt"));
            } catch (IOException e) {
            e.printStackTrace();
            }
            log.info("作业分片："+shardingContext.getShardingItem()+"排序开始");
            log.info("作业分片："+shardingContext.getShardingItem()+"sortLines---->"+sortLines+"   totalLines------>"+totalLines);
            if (totalLines<=sortLines){
                log.info("作业分片："+shardingContext.getShardingItem()+"进入普通排序");
                getSortData(shardingContext);
                SortJobDTO job = new SortJobDTO();
                job.setId(shardingContext.getShardingItem());
                job.setFileSortJobState("finish");
                jobStateService.updateSortJobState(job);
            }else{
                log.info("作业分片："+shardingContext.getShardingItem()+"进入分割排序");
                divideFile(shardingContext);
                sortDivideFile(shardingContext);
                SortJobDTO job = new SortJobDTO();
                job.setId(shardingContext.getShardingItem());
                job.setFileSortJobState("finish");
                jobStateService.updateSortJobState(job);
            }
            log.info("作业分片："+shardingContext.getShardingItem()+"排序结束");
        }


    }

    /**
     * 各个分片读取不同的分割文件，并进行排序
     * @return
     */
    public void getSortData(ShardingContext shardingContext){

        FileReader reader =null;
        BufferedReader bufferedReader =null;

        //当前分片数
        int shardingItem = shardingContext.getShardingItem();
        try{
            reader = new FileReader(FileDivideJob.filePath+FileDivideJob.fileName+shardingItem+".txt");
            bufferedReader = new BufferedReader(reader);
            String line =null;
            FileWriter fw = new FileWriter(sortFilePath+sortFileName+shardingItem+".txt");
            BufferedWriter bw = new BufferedWriter(fw);

            int linesNum = FileUtils.getTotalLines(new File(FileDivideJob.filePath+FileDivideJob.fileName+shardingItem+".txt"));
            ArrayList<Long> list = new ArrayList<>(linesNum);
            Map<Long,String> map = new HashMap<>();
            while(true){
                line =bufferedReader.readLine();
                Long serialNumber = CheckUtils.getSerialNumber(line);
                if (serialNumber!=null){
                    list.add(serialNumber);
                    map.put(serialNumber,line);
                }
                if(line ==null){
                    break;
                }
            }

            Long[] sort = new Long[list.size()];
            int i =0;
            for (Long l:list){
                sort[i++] = l;
            }
            Arrays.sort(sort);
            for (Long l:sort){
                FileUtils.writeFile(bw,map.get(l));
            }
            FileUtils.closeFile(fw,bw);
        }catch(Exception e){
            System.out.println(e);
        }
        finally{
            try{
                bufferedReader.close();
                reader.close();

            }catch(Exception e){
                System.out.println(e);
            }
        }
        log.info("分片："+shardingItem+"排序完成");
    }



    public void divideFile(ShardingContext shardingContext){
        FileReader reader =null;
        BufferedReader bufferedReader =null;

        FileWriter fw = null;
        BufferedWriter bw = null;


        //分割数
        int divideNum = (int)Math.ceil((double) totalLines/sortLines);
        System.out.println("divideNum"+"------------"+divideNum);

        //当前分片数
        int shardingItem = shardingContext.getShardingItem();

        //读取分割后的文件reader
        ArrayList<FileReader> tempFileReaders = null;

        //读取分割后的文件BufferedReader
        ArrayList<BufferedReader> tempBufferedReaders = null;

        //读、写
        try {
                reader = new FileReader(FileDivideJob.filePath+FileDivideJob.fileName+shardingItem+".txt");
                bufferedReader = new BufferedReader(reader);
                String line =null;

                for(int i =0; i<divideNum ;i++) {
                    fw=new FileWriter(sortFilePath+sortFileName+shardingItem+"_temp_"+i+".txt");
                    bw=new BufferedWriter(fw);
                    int count=0;
                    while (true){
                        if (count!=0 && count%sortLines==0){
                            break;
                        }
                        line = bufferedReader.readLine();
                        if (line==null){
                            break;
                        }
                        bw.write(line+"\t\n");
                        count++;
                    }

                    bw.close();
                    fw.close();
                }
                bufferedReader.close();
                reader.close();
            } catch (FileNotFoundException e) {
                  e.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortDivideFile(ShardingContext shardingContext){

        int shardingItem = shardingContext.getShardingItem();
        //分割数
        int divideNum = (int)Math.ceil((double) totalLines/sortLines);

        //读取分割后的文件reader
        ArrayList<FileReader> tempFileReaders = new ArrayList<>(divideNum);

        //读取分割后的文件BufferedReader
        ArrayList<BufferedReader> tempBufferedReaders = new ArrayList<>();

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(sortFilePath+sortFileName+shardingItem+".txt");
            bw = new BufferedWriter(fw);

            for (int i =0;i<divideNum;i++){
                String tempFilePath = sortFilePath+sortFileName+shardingItem+"_temp_"+i+".txt";
                FileReader frTemp = new FileReader(tempFilePath);
                BufferedReader brTemp = new BufferedReader(frTemp);
                tempFileReaders.add(frTemp);
                tempBufferedReaders.add(brTemp);
            }

            FileUtils.readSortWriteFile(tempBufferedReaders,bw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {

                for (BufferedReader br:tempBufferedReaders){
                    br.close();
                }
                for (FileReader fr:tempFileReaders){
                    fr.close();
                }
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


}
