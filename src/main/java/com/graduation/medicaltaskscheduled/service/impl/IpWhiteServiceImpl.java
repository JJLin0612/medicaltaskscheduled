package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.medicaltaskscheduled.entity.IpWhite;
import com.graduation.medicaltaskscheduled.mapper.IpWhiteMapper;
import com.graduation.medicaltaskscheduled.service.IpWhiteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.utils.IpUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-04-13
 */
@Service
public class IpWhiteServiceImpl extends ServiceImpl<IpWhiteMapper, IpWhite> implements IpWhiteService {


    /***
     * 添加 ip 至白名单
     * @param ipAddr
     * @return
     */
    @Override
    public boolean addUserWhiteList(String ipAddr) {
        IpWhite ipWhite = new IpWhite();
        ipWhite.setIpAddr(ipAddr);
        int res = baseMapper.insert(ipWhite);
        return res == 0 ? false : true;
    }

    /***
     * 删除 白名单中的 ip
     * @param ipAddr
     * @return
     */
    @Override
    public boolean deleteUserWhiteList(String ipAddr) {
        QueryWrapper<IpWhite> wrapper = new QueryWrapper<>();
        wrapper.eq("ip_addr", ipAddr);
        int res = baseMapper.delete(wrapper);
        return res == 0 ? false : true;
    }
}
