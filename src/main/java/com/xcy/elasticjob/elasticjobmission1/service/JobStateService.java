package com.xcy.elasticjob.elasticjobmission1.service;

import com.xcy.elasticjob.elasticjobmission1.model.CheckJobDTO;
import com.xcy.elasticjob.elasticjobmission1.model.DivideJobDTO;
import com.xcy.elasticjob.elasticjobmission1.model.ImportJobDTO;
import com.xcy.elasticjob.elasticjobmission1.model.SortJobDTO;

public interface JobStateService {

    /**
     * 更新ImportJob状态
     * @param job
     * @return
     */
    Integer updateImportJobState(ImportJobDTO job);

    /**
     * 更新DivideJob状态
     * @param job
     * @return
     */
    Integer updateDivideJobState(DivideJobDTO job);

    /**
     * 更新SortJob状态
     * @param job
     * @return
     */
    Integer updateSortJobState(SortJobDTO job);

    /**
     * 更新CheckJob状态
     * @param job
     * @return
     */
    Integer updateCheckJobState(CheckJobDTO job);

    /**
     * 判断FileImportJob是否ready
     * @return
     */
    boolean isImportJobReady();

    /**
     * 判断FileImportJob是否finish
     * @return
     */
    boolean isImportJobFinish();

    /**
     * 判断FileDivideJob是否ready
     * @return
     */
    boolean isDivideJobReady();

    /**
     * 判断FileDivideJob是否finish
     * @return
     */
    boolean isDivideJobFinish();

    /**
     * 判断FileDivideJob是否ready
     * @return
     */
    boolean isSortJobReady();

    /**
     * 判断FileSortJob是否ready
     * @return
     */
    boolean isSortJobFinish();

    /**
     * 判断FileCheckJob是否ready
     * @return
     */
    boolean isCheckJobReady();

    /**
     * 判断FileCheckJob是否finish
     * @return
     */
    boolean isCheckJobFinish();

    /**
     * 修改FileImportJob为Ready
     * @return
     */
    boolean updImportJobReady();

    /**
     * 修改FileImportJob为Finish
     * @return
     */
    boolean updImportJobFinish();

    /**
     * 修改FileDivideJob为Ready
     * @return
     */
    boolean updDivideJobReady();

    /**
     * 修改FileDivideJob为Finish
     * @return
     */
    boolean updDivideJobFinish();

    /**
     * 修改FileSortJob为Ready
     * @return
     */
    boolean updSortJobReady();

    /**
     * 修改FileSortJob为Finish
     * @return
     */
    boolean updSortJobFinish();

    /**
     * 修改FileCheckJob为Ready
     * @return
     */
    boolean updCheckJobReady();

    /**
     * FileCheckJob为Finish
     * @return
     */
    boolean updCheckJobFinish();
}
