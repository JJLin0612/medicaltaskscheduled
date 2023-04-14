package com.graduation.medicaltaskscheduled.service;

import com.graduation.medicaltaskscheduled.entity.Webaccesscount;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.medicaltaskscheduled.entity.vo.WebAccessCountQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 */
public interface WebaccesscountService extends IService<Webaccesscount> {

    List<Webaccesscount> getAccessCount(WebAccessCountQuery query);

}
