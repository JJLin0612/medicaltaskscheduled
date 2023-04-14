package com.graduation.medicaltaskscheduled.service;

import com.graduation.medicaltaskscheduled.entity.IpWhite;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-13
 */
public interface IpWhiteService extends IService<IpWhite> {

    boolean addUserWhiteList(String ipAddr);

    boolean deleteUserWhiteList(String ipAddr);
}
