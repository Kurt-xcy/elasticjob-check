package com.xcy.elasticjob.elasticjobmission1.service;

import com.xcy.elasticjob.elasticjobmission1.model.FileData;


import java.util.List;

public interface DataService {
    Integer insertService(FileData fileData);



    List<FileData> selByPage(int from,int to);
}
