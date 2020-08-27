package com.xcy.elasticjob.elasticjobmission1.mapper;

import com.xcy.elasticjob.elasticjobmission1.model.Result;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ResultMapper {
    @Insert("insert into checkResult(id,type,wrongFile1,wrongFile2,wrongLine1,wrongLine2,discrip,generateTime) values(default,#{type},#{wrongFile1},#{wrongFile2},#{wrongLine1},#{wrongLine2},#{discrip},#{generateTime})")
    public Integer insert(Result result);
}
