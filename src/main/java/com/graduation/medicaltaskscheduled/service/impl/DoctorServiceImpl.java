package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.medicaltaskscheduled.entity.Doctor;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.mapper.DoctorMapper;
import com.graduation.medicaltaskscheduled.service.DoctorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.utils.ClientInfo;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public String doctorRegister(String workId, String pwd) {

        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("work_id", workId);
        if (baseMapper.selectCount(wrapper) != 0)
            throw new CustomException(ResultCode.USER_HAS_EXITS_ERROR, "用户已注册");
        //创建 doctor
        Doctor doctor = new Doctor();
        doctor.setWorkId(workId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncode = encoder.encode(pwd);
        doctor.setPwd(pwdEncode);
        int res = baseMapper.insert(doctor);
        if (res == 0) throw new CustomException(ResultCode.ADD_RECORD_FAILED, "insert failed");
        return String.valueOf(res);
    }

    @Override
    public String dcotorLogin(String workId, String pwd, HttpServletRequest request) {
        // 用户未注册则抛异常
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("work_id", workId);
        Integer count = baseMapper.selectCount(wrapper);
        if (count == 0)
            throw new CustomException(ResultCode.USER_UNREGISTERED_ERROR, "unregistered");

        //根据手机号获取user
        Doctor user = baseMapper.selectOne(wrapper);

        // 比较手机号 密码是否正确
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(pwd, user.getPwd()))//密码匹配
            throw new CustomException(ResultCode.INCORRECT_PHONE_OR_PWD_ERROR, "incorrect phone or pwd");

        //用户被禁用
        if (user.getIsDisabled())
            throw new CustomException(ResultCode.USER_IS_DISABLED_ERROR, "user has been disabled");

        //获取用户id
        String id = user.getId();
        //返回的token
        String token = JwtUtils.getTokenFromRequest(request);

        if (StringUtils.isEmpty(token)) {
            //当前请求的设备(新设备)的信息
            String deviceInfo = ClientInfo.getDeviceInfo(request);
            //生成token
            token = JwtUtils.getJwtToken(id, deviceInfo);
            //将token缓存
            redisTemplate.opsForValue().set(id, token, 24 * 7, TimeUnit.HOURS);
        }

        return token;
    }
}
