package com.graduation.medicaltaskscheduled.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.RecordLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.medicaltaskscheduled.entity.vo.LogQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
public interface RecordLogService extends IService<RecordLog> {

    void getLogByQueryPage(Page<RecordLog> page, LogQuery logQuery);
}
