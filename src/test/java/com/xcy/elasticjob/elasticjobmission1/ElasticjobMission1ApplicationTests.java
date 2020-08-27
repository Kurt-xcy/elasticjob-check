package com.xcy.elasticjob.elasticjobmission1;


import com.xcy.elasticjob.elasticjobmission1.model.FileData;
import com.xcy.elasticjob.elasticjobmission1.service.DataService;
import com.xcy.elasticjob.elasticjobmission1.util.OrderUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest
class ElasticjobMission1ApplicationTests {

    @Autowired
    DataService dataService;

    @Test
    void writeDataToDatabase(){
        int count = 0;

        while (count<10000) {
            FileData data = new FileData();
            data.setSerialNumber(OrderUtils.getOrderCode(count));
            data.setUserId(Integer.toString(count));
            data.setCardNumber(Long.toString(Long.parseLong("6212261202030400000") + count));
            //交易时间，为防止时间过于相似，略作打乱
            long randomHours = new Double(Math.random()).longValue();
            long randomMinutes = new Double(Math.random()*60).longValue();
            long randomSeconds = new Double(Math.random()*60).longValue();
            LocalDateTime now=LocalDateTime.now().plusHours(randomHours).plusMinutes(randomMinutes).plusSeconds(randomSeconds);
            String dealTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            data.setDealTime(dealTime);
            data.setInOrOut(new Double(Math.random() * 2).intValue());
            dataService.insertService(data);
            count++;
        }


    }

}
