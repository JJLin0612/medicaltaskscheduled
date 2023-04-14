package com.graduation.medicaltaskscheduled.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.RecordLog;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.vo.LogQuery;
import com.graduation.medicaltaskscheduled.service.RecordLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/recordLog")
public class RecordLogController {

    @Resource
    private RecordLogService logService;

    @PostMapping("getLogByQueryPage")
    @LogRecord(operateType = OperateType.READ, operateDesc = "多组合分页查询")
    public Result getLogByQueryPage(
            @RequestBody LogQuery logQuery,
            @RequestParam(value = "current") Long current,
            @RequestParam(value = "pageSize") Long pageSize
    ) {
        Page<RecordLog> page = new Page<>(current, pageSize);
        logService.getLogByQueryPage(page, logQuery);
        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("current", page.getCurrent());
        map.put("logList", page.getRecords());
        log.error(map.toString());
        log.error(map.get("pageSize").toString());
        return Result.ok().data("map", map);
    }
}

