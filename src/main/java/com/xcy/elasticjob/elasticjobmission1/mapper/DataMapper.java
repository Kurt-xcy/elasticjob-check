package com.xcy.elasticjob.elasticjobmission1.mapper;

import com.xcy.elasticjob.elasticjobmission1.model.FileData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DataMapper {

    @Insert("insert into data(serialNumber,userId,cardNumber,dealTime,inOrOut) values(#{serialNumber},#{userId},#{cardNumber},#{dealTime},#{inOrOut});")
    public Integer insert(FileData fileData);

    @Select("select * from data;")
    public List<FileData> selAll();

    @Select("select * from data limit #{from},#{to};")
    public List<FileData> selPage(int from,int to);
}
