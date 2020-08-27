package com.xcy.elasticjob.elasticjobmission1.service.impl;

import com.xcy.elasticjob.elasticjobmission1.mapper.JobStateMapper;
import com.xcy.elasticjob.elasticjobmission1.model.*;
import com.xcy.elasticjob.elasticjobmission1.service.JobStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class JobStateServiceImpl implements JobStateService {

    @Autowired
    JobStateMapper jobStateMapper;

    @Override
    @Transactional
    public Integer updateImportJobState(ImportJobDTO job) {
        return jobStateMapper.updateFileImportJob(job);
    }

    @Override
    @Transactional
    public Integer updateDivideJobState(DivideJobDTO job) {
        return jobStateMapper.updateFileDivideJob(job);
    }

    @Override
    @Transactional
    public Integer updateSortJobState(SortJobDTO job) {
        return jobStateMapper.updateFileSortJob(job);
    }

    @Override
    @Transactional
    public Integer updateCheckJobState(CheckJobDTO job) {
        return jobStateMapper.updateFileCheckJob(job);
    }

    @Override
    @Transactional
    public boolean isImportJobReady() {
        List<ImportJobDTO> list = jobStateMapper.selectImportJobReady();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isImportJobFinish() {
        List<ImportJobDTO> list = jobStateMapper.selectImportJobFinish();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isDivideJobReady() {
        List<DivideJobDTO> list = jobStateMapper.selectDivideJobReady();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isDivideJobFinish() {
        List<DivideJobDTO> list = jobStateMapper.selectDivideJobFinish();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isSortJobReady() {
        List<SortJobDTO> list = jobStateMapper.selectSortJobReady();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isSortJobFinish() {
        List<SortJobDTO> list = jobStateMapper.selectSortJobFinish();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isCheckJobReady() {
        List<CheckJobDTO> list = jobStateMapper.selectCheckJobReady();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isCheckJobFinish() {
        List<CheckJobDTO> list = jobStateMapper.selectCheckJobFinish();
        if (list!=null){
            if (list.size()==10)
                return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updImportJobReady() {
        ImportJobDTO job = new ImportJobDTO();
        job.setFileImportJobState("ready");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateImportJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updImportJobFinish() {
        ImportJobDTO job = new ImportJobDTO();
        job.setFileImportJobState("finish");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateImportJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updDivideJobReady() {
        DivideJobDTO job = new DivideJobDTO();
        job.setFileDivideJobState("ready");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateDivideJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updDivideJobFinish() {
        DivideJobDTO job = new DivideJobDTO();
        job.setFileDivideJobState("finish");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateDivideJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updSortJobReady() {
        SortJobDTO job = new SortJobDTO();
        job.setFileSortJobState("ready");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateSortJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updSortJobFinish() {
        SortJobDTO job = new SortJobDTO();
        job.setFileSortJobState("finish");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateSortJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updCheckJobReady() {
        CheckJobDTO job = new CheckJobDTO();
        job.setFileCheckJobState("ready");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateCheckJobState(job);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updCheckJobFinish() {
        CheckJobDTO job = new CheckJobDTO();
        job.setFileCheckJobState("finish");
        for (int i=0;i<10;i++){
            job.setId(i);
            updateCheckJobState(job);
        }
        return true;
    }


}
