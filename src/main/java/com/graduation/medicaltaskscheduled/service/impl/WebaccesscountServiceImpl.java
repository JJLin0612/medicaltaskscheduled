package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.medicaltaskscheduled.entity.Webaccesscount;
import com.graduation.medicaltaskscheduled.entity.vo.WebAccessCountQuery;
import com.graduation.medicaltaskscheduled.mapper.WebaccesscountMapper;
import com.graduation.medicaltaskscheduled.service.WebaccesscountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author RabbitFaFa
 */
@Slf4j
@Service
public class WebaccesscountServiceImpl extends ServiceImpl<WebaccesscountMapper, Webaccesscount> implements WebaccesscountService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Webaccesscount> getAccessCount(WebAccessCountQuery query) {
        //无条件查询
        if (StringUtils.isEmpty(query)) {
            return baseMapper.selectList(null);
        }
        QueryWrapper<Webaccesscount> wrapper = new QueryWrapper<>();
        log.error(query.toString());
        if (!StringUtils.isEmpty(query.getBegin())) {
            wrapper.ge("gmt_create", query.getBegin());
        }
        if (!StringUtils.isEmpty(query.getEnd())) {
            wrapper.le("gmt_create", query.getEnd());
        }

        List<Webaccesscount> dataList = baseMapper.selectList(wrapper);

        //截止日期为空  Redis中查询今日当前 IP UV PV 数据 加上今日的数据
//        if (StringUtils.isEmpty(query.getEnd())) {
//            Long ipSize = redisTemplate.opsForHyperLogLog().size("website:ip");
//            Long uvSize = redisTemplate.opsForHyperLogLog().size("website:uv");
//            String pvSize = redisTemplate.opsForValue().get("website:pv");
//            Webaccesscount currData = new Webaccesscount();
//            currData.setIp(ipSize.intValue());
//            currData.setUv(uvSize.intValue());
//            currData.setPv(Integer.parseInt(pvSize));
//            dataList.add(currData);
//        }

        return dataList;
    }
}
