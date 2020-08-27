package com.xcy.elasticjob.elasticjobmission1.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xcy.elasticjob.elasticjobmission1.mapper.ResultMapper;
import com.xcy.elasticjob.elasticjobmission1.model.CheckJobDTO;
import com.xcy.elasticjob.elasticjobmission1.model.Result;
import com.xcy.elasticjob.elasticjobmission1.service.CheckResultService;
import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import com.xcy.elasticjob.elasticjobmission1.util.CheckUtils;
import com.xcy.elasticjob.elasticjobmission1.util.FileUtils;
import com.xcy.elasticjob.elasticjobmission1.util.GenerateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

/**
 * 数据比对任务，此为第4步
 * @author Administrator
 * @version 1.0
 **/
@Component
@Slf4j
public class FileCheckJob implements SimpleJob {


    @Autowired
    JobStateService jobStateService;

    @Autowired
    ResultMapper resultMapper;

    //主机的文件目录即为排序后的文件目录
    //主机的文件即为排序后的文件

    //平台的文件目录
    public static final String sortFile2Path= "D:/Users/kurtfile/IT/FileSort2Dir/";

    //平台的文件名
    public static final String sortFileName = "sortFile_";

    @Autowired
    CheckResultService checkResultService;


    //任务执行代码逻辑
    @Override
    public void execute(ShardingContext shardingContext) {
        if (jobStateService.isCheckJobReady() && jobStateService.isSortJobFinish()){
            log.info("作业分片："+shardingContext.getShardingItem()+"对账开始");
            check(shardingContext);
            log.info("作业分片："+shardingContext.getShardingItem()+"对账结束");
            CheckJobDTO job = new CheckJobDTO();
            job.setId(shardingContext.getShardingItem());
            job.setFileCheckJobState("finish");
            jobStateService.updateCheckJobState(job);
        }

    }

    /**
     * 对于分割后排序的文件进行检查对账
     * @return
     */
    public void check(ShardingContext shardingContext){

        FileReader reader1 =null;
        BufferedReader bufferedReader1 =null;

        FileReader reader2 =null;
        BufferedReader bufferedReader2 =null;
        //当前读取行数
        int num = 0;
        //当前分片数
        int shardingItem = shardingContext.getShardingItem();

        ArrayList<String> list1 = null;
        ArrayList<String> list2 = null;

        try {
            //主机方
            reader1 = new FileReader(FileSortJob.sortFilePath + FileSortJob.sortFileName + shardingItem + ".txt");
            bufferedReader1 = new BufferedReader(reader1);

            //平台方
            reader2 = new FileReader(sortFile2Path + sortFileName + shardingItem + ".txt");
            bufferedReader2 = new BufferedReader(reader2);

            int lineNum1 = FileUtils.getTotalLines(new File(FileSortJob.sortFilePath + FileSortJob.sortFileName + shardingItem + ".txt"));
            int lineNum2 = FileUtils.getTotalLines(new File(sortFile2Path + sortFileName + shardingItem + ".txt"));

            if (lineNum1 != lineNum2) {
                //日志写文件行数不相等
                //输出到mybatis
            }

            list1 = new ArrayList<>(lineNum1+20);
            list2 = new ArrayList<>(lineNum2+20);

            int i1 = 0, i2 = 0;
            while (i1 < lineNum1 || i2 < lineNum2) {
                String line1 = FileUtils.readOneLine(bufferedReader1);
                String line2 = FileUtils.readOneLine(bufferedReader2);
                if (line1 != null) {
                    list1.add(line1);
                }
                if (line2 != null) {
                    list2.add(line2);
                }
                i1++;
                i2++;
            }
            list1.add(null);
            list1.add(null);
            list2.add(null);
            list2.add(null);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader1.close();
                reader1.close();
                bufferedReader2.close();
                reader2.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
            //主机方文件当前读到的行数，从0开始,为1则第0行已读
            int count1 = 0;
            //平台方文件当前读到的行数，从0开始,为1则第0行已读
            int count2 = 0;

            while(list1.get(count1)!=null || list2.get(count2)!=null){

                String line1 = list1.get(count1);
                count1++;
                String line2 = list2.get(count2);
                count2++;

                if (line1==null){
                    //数据库报l2多数据
                    continue;
                }
                if (line2==null){
                    //数据库报l1多数据
                    continue;
                }

                if (line1.equals(line2)){//对账成功，直接跳到下一条数据
                    continue;
                }else{//数据不相等,可把现场信息存放在数据库
                    if (CheckUtils.checkSerialNumber(line1,line2)){//比较流水号是否相等
                        if (CheckUtils.checkUerId(line1,line2)){//比较用户ID是否相等
                           if (CheckUtils.checkCardNumber(line1,line2)){//比较卡号
                               //比对数据过程中的未知错误（或者不存在）
                           }else {
                               //卡号不相等报数据库
                               log.info("卡号不相等报数据库");
                               String discript = CheckUtils.getCardNumber(line1)+"!="+CheckUtils.getCardNumber(line2);
                               Result result = GenerateResult.getWrongCardResult(shardingItem,count1,count2,discript);
                               checkResultService.insertResult(result);
                               continue;
                           }

                        }else{
                            //用户id不相等报数据库
                            log.info("用户id不相等报数据库");
                            String discript = CheckUtils.getUserId(line1)+"!="+CheckUtils.getUserId(line2);
                            Result result = GenerateResult.getWrongUserIdResult(shardingItem,count1,count2,discript);
                            checkResultService.insertResult(result);
                        }

                    }else{
                        //流水号不相等，不是同一条数据
                        long l1 = CheckUtils.getSerialNumber(line1);
                        long l2 = CheckUtils.getSerialNumber(line2);
                        //判断谁的流水号更大
                        if (l1>l2){
                            while (l1>l2){
                                //报数据库l2这条数据多了
                                log.info("报数据库l2这条数据多了");
                                String discript = "The Data "+CheckUtils.getSerialNumber(line2)+" in line "+count2+" is more";
                                Result result = GenerateResult.getMoreDataResult(shardingItem,count1,count2,discript);
                                checkResultService.insertResult(result);
                                line2 = list2.get(count2);
                                count2++;
                                l2 = CheckUtils.getSerialNumber(line2);
                            }
                            if (l1==l2){
                                //通过
                                continue;
                            }else if (l1<l2){
                                count1--;
                                count2--;
                                continue;
                            }
                        }else if (l1<l2){
                            while (l1<l2){
                                //报数据库l1这条数据多了
                                log.info("报数据库l1这条数据多了");
                                String discript = "The Data "+CheckUtils.getSerialNumber(line1)+" in line "+count1+" is more";
                                Result result = GenerateResult.getMoreDataResult(shardingItem,count1,count2,discript);
                                checkResultService.insertResult(result);
                                line1 = list1.get(count1);
                                count1++;
                                l1 = CheckUtils.getSerialNumber(line1);
                            }
                            if (l1==l2){
                                //通过
                                continue;
                            }else if (l2<l1){
                                count1--;
                                count2--;
                                continue;
                            }
                        }
                    }

                }
            }

    }


}
