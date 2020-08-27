package com.xcy.elasticjob.elasticjobmission1.model;

import lombok.Data;

@Data
public class FileData {
    //用户ID（相当于用户名）
    String userId ;
    //交易流水号
    String serialNumber;
    //卡号
    String cardNumber;
    //交易时间 格式为（yyyyMMddHHmmssSSS）
    String dealTime;
    //转入/转出  转入1 转出0
    Integer inOrOut;


}
