package com.xcy.elasticjob.elasticjobmission1.mapper;

import com.xcy.elasticjob.elasticjobmission1.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface JobStateMapper {

    @Update("update importJobState set fileImportJobState=#{fileImportJobState} where id=#{id};")
    public Integer updateFileImportJob(ImportJobDTO job);

    @Update("update divideJobState set fileDivideJobState=#{fileDivideJobState} where id=#{id};")
    public Integer updateFileDivideJob(DivideJobDTO job);

    @Update("update sortJobState set fileSortJobState=#{fileSortJobState} where id=#{id};")
    public Integer updateFileSortJob(SortJobDTO job);

    @Update("update checkJobState set fileCheckJobState=#{fileCheckJobState} where id=#{id};")
    public Integer updateFileCheckJob(CheckJobDTO job);

    @Select("select * from importJobState where fileImportJobState='ready';")
    public List<ImportJobDTO> selectImportJobReady();

    @Select("select * from importJobState where fileImportJobState='finish';")
    public List<ImportJobDTO> selectImportJobFinish();

    @Select("select * from divideJobState where fileDivideJobState='ready'")
    public List<DivideJobDTO> selectDivideJobReady();

    @Select("select * from divideJobState where fileDivideJobState='finish'")
    public List<DivideJobDTO> selectDivideJobFinish();

    @Select("select * from sortJobState where fileSortJobState='ready'")
    public List<SortJobDTO> selectSortJobReady();

    @Select("select * from sortJobState where fileSortJobState='finish'")
    public List<SortJobDTO> selectSortJobFinish();

    @Select("select * from checkJobState where fileCheckJobState='ready'")
    public List<CheckJobDTO> selectCheckJobReady();

    @Select("select * from checkJobState where fileCheckJobState='finish'")
    public List<CheckJobDTO> selectCheckJobFinish();

    @Select("select * from jobStatus where id = 1;")
    public JobStatusDTO selectJobStatus();
}
