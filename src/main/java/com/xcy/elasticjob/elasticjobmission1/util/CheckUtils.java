package com.xcy.elasticjob.elasticjobmission1.util;

import java.math.BigInteger;

public class CheckUtils {

    /**
     * 检查流水尾号是否为某值，是则返回true
     * @return
     */
    public static boolean checkData(String data, int num){
        if (data == null || data == "" || num>9 || num < 0)
            return false;
        String[] datas = data.split("\\t");
        String serialNumber = datas[0];
        int last = Integer.parseInt(serialNumber.charAt(serialNumber.length()-1)+"");
        System.out.println(last);
        if (last == num)
            return true;
        return false;
    }

    /**
     * 获取流水号
     * @param data
     * @return
     */
    public static Long getSerialNumber(String data){
        if (data == null || data == "")
            return null;

        String[] datas = data.split("\\t");
        String serialNumberString = datas[0];
        //原字符串太长，取后19位
        serialNumberString = serialNumberString.substring(8);
        long serialNumber = Long.parseLong(serialNumberString+"");
        return serialNumber;
    }

    public static Long getUserId(String data){
        if (data == null || data == "")
            return null;

        String[] datas = data.split("\\t");
        String userIdString = datas[1];
        long userId = Long.parseLong(userIdString+"");
        return userId;
    }

    public static Long getCardNumber(String data) {
        if (data == null || data == "")
            return null;

        String[] datas = data.split("\\t");
        String cardNumberString = datas[2];
        long cardNumber = Long.parseLong(cardNumberString+"");
        return cardNumber;
    }

    /**
     * 检查流水号是否相等
     * @param data1
     * @param data2
     * @return
     */
    public static boolean checkSerialNumber(String data1,String data2){
        if (data1 == null || data1 == "" || data2 == null || data2 == "")
            throw new RuntimeException("校验数据为空");
        long l1 = getSerialNumber(data1);
        long l2 = getSerialNumber(data2);
        if (l1==l2){
            return  true;
        }
        return false;
    }

    public static boolean checkUerId(String data1, String data2) {
        if (data1 == null || data1 == "" || data2 == null || data2 == "")
            throw new RuntimeException("校验数据为空");
        long l1 = getUserId(data1);
        long l2 = getUserId(data2);
        if (l1==l2){
            return  true;
        }
        return false;
    }

    public static boolean checkCardNumber(String data1, String data2) {
        if (data1 == null || data1 == "" || data2 == null || data2 == "")
            throw new RuntimeException("校验数据为空");
        long l1 = getCardNumber(data1);
        long l2 = getCardNumber(data2);
        if (l1==l2){
            return  true;
        }
        return false;
    }


}
