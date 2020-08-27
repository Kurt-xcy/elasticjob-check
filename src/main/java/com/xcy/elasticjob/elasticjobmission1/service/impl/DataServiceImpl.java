package com.xcy.elasticjob.elasticjobmission1.service.impl;

import com.xcy.elasticjob.elasticjobmission1.mapper.DataMapper;
import com.xcy.elasticjob.elasticjobmission1.model.FileData;
import com.xcy.elasticjob.elasticjobmission1.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    DataMapper dataMapper;

    @Override
    public Integer insertService(FileData fileData) {
        return dataMapper.insert(fileData);
    }


    @Override
    public List<FileData> selByPage(int from, int to) {
        return dataMapper.selPage(from,to);
    }
}
