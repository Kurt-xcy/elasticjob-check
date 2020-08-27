package com.xcy.elasticjob.elasticjobmission1.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    //获取文件行数
    public static int getTotalLines(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        Reader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        long endTime = System.currentTimeMillis();
        System.out.println("统计文件行数运行时间： " + (endTime - startTime) + "ms");
        return lines;
    }

    //写文件
    //注意，在调用该函数后需手动关闭close
    public static void writeFile(BufferedWriter bw,String data){
        try{
            bw.write(data+"\t\n");

        }catch(Exception e){
            System.out.println(e);
        }

    }

    public static void closeFile(FileWriter fw,BufferedWriter bw){
        try{
            bw.close();
            fw.close();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * 读一行，如果输出为null，则已读完
     * @param br
     * @return 一行的String
     */
    public static String readOneLine(BufferedReader br){

        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return line;
    }

    /**
     * 读到指定行,从0行开始,如输入num =5，则0,1,2,3,4已读，下一行为5
     * @param br
     * @return 指定行的String
     */
    public static String readToLine(BufferedReader br, long num){
        try {
            br.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        long count = 0;
        try {
            while (true){
                if (count==num){
                    break;
                }
                line = br.readLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return line;
    }

    public static void readSortWriteFile(List<BufferedReader> tempBufferedReaders, BufferedWriter bw) throws IOException {
        ArrayList<String> getLines = new ArrayList<>(tempBufferedReaders.size());
        for (BufferedReader br:tempBufferedReaders){
            String line = br.readLine();
           // System.out.println(line);
            getLines.add(line);
        }

        int count = 1;
        while (!isAllNull(getLines)){
            int minIndex = getMinIndex(getLines);
//            System.out.println(getLines);
//            System.out.println(minIndex);
            System.out.println(getLines.get(minIndex));
            FileUtils.writeFile(bw,getLines.get(minIndex));
            count++;
            getLines.set(minIndex,tempBufferedReaders.get(minIndex).readLine());
        }
        System.out.println("分步排序写入完成"+count);
    }

    private static int getMinIndex(ArrayList<String> getLines){
        int index =0;
        ArrayList<Long> serialNumbers = new ArrayList<>();

        for (String line:getLines){
            Long serialNumber = CheckUtils.getSerialNumber(line);
            serialNumbers.add(serialNumber);
        }
        while (serialNumbers.get(index)==null){
            index++;
        }
        long min = serialNumbers.get(index);
        Long l = null;
        for (;index<serialNumbers.size();index++){
            l = serialNumbers.get(index);
            if (l!=null){
                if (l<min){
                    min = l;
                }
            }
        }
        index = serialNumbers.indexOf(min);
        return index;

    }




    private static boolean isAllNull(ArrayList<String> getLines){
        for (String str:getLines){
            if (str!=null){
                return false;
            }
        }
        return true;

    }
}
