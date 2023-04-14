package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.RecordLog;
import com.graduation.medicaltaskscheduled.entity.vo.LogQuery;
import com.graduation.medicaltaskscheduled.mapper.RecordLogMapper;
import com.graduation.medicaltaskscheduled.service.RecordLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Slf4j
@Service
public class RecordLogServiceImpl extends ServiceImpl<RecordLogMapper, RecordLog> implements RecordLogService {

    /***
     * 多条件组合分页查询
     * @param page
     * @param logQuery
     */
    @Override
    public void getLogByQueryPage(Page<RecordLog> page, LogQuery logQuery) {
        log.warn("查询前：" + logQuery.toString());
        if (StringUtils.isEmpty(logQuery)) {
            baseMapper.selectPage(page, null);
            return;
        }
        QueryWrapper<RecordLog> wrapper = new QueryWrapper<>();
        log.warn(logQuery.toString());
        if (!StringUtils.isEmpty(logQuery.getUserType())) {
            wrapper.eq("user_type", logQuery.getUserType());
        }
        if (!StringUtils.isEmpty(logQuery.getBeginTime())) {
            wrapper.ge("create_time", logQuery.getBeginTime());
        }
        if (!StringUtils.isEmpty(logQuery.getEndTime())) {
            wrapper.le("create_time", logQuery.getEndTime());
        }
        if (!StringUtils.isEmpty(logQuery.getOpAcc())) {
            wrapper.eq("op_acc", logQuery.getOpAcc());
        }
        if (!StringUtils.isEmpty(logQuery.getCostTimeLowerLimit())) {
            wrapper.le("op_cost_ms", logQuery.getCostTimeLowerLimit());
        }
        if (!StringUtils.isEmpty(logQuery.getCostTimeSuperLimit())) {
            wrapper.ge("op_cost_ms", logQuery.getCostTimeSuperLimit());
        }
        if (!StringUtils.isEmpty(logQuery.getOpType())) {
            wrapper.eq("op_type", logQuery.getOpType());
        }
        if (!StringUtils.isEmpty(logQuery.getStatus())) {
            wrapper.eq("op_status", logQuery.getStatus());
        }
        baseMapper.selectPage(page, wrapper);
    }
}
