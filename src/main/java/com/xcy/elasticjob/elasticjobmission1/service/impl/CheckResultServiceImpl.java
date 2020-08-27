package com.xcy.elasticjob.elasticjobmission1.service.impl;

import com.xcy.elasticjob.elasticjobmission1.mapper.ResultMapper;
import com.xcy.elasticjob.elasticjobmission1.model.Result;
import com.xcy.elasticjob.elasticjobmission1.service.CheckResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckResultServiceImpl implements CheckResultService {

    @Autowired
    ResultMapper resultMapper;

    @Override
    public Integer insertResult(Result result) {
        return resultMapper.insert(result);
    }
}
