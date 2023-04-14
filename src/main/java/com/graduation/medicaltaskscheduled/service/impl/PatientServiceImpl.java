package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.entity.vo.PatientQuery;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.mapper.PatientMapper;
import com.graduation.medicaltaskscheduled.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.utils.ClientInfo;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author RabbitFaFa
 * @since 2023-03-29
 */
@Slf4j
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public String patientRegister(String mobile, String pwd) {

        //根据手机号码查询是否存在该手机号 且 该用户是否被删除
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer countByPhone = baseMapper.selectCount(wrapper);
        wrapper.eq("is_deleted", ResultCode.USER_IS_NOT_DELETED);
        Integer countByPhoAndIsDele = baseMapper.selectCount(wrapper);
        //手机号已存在(存在且未被删除 或 存在但删除字段为 1)
        if (countByPhone == 1) {
            if (countByPhoAndIsDele == 1) {
                //该手机号用户存在且未被删除
                throw new CustomException(ResultCode.USER_HAS_EXITS_ERROR, "patient has registered");
            } else {
                //该手机号用户存在但已为删除状态 将记录删除
                wrapper = new QueryWrapper<>();
                wrapper.eq("mobile", mobile);
                baseMapper.delete(wrapper);
            }
        }

        //生成账号acc
        Calendar calendar = Calendar.getInstance();
        String newAccNumber = "d" + calendar.get(Calendar.DAY_OF_YEAR) + "_" + Long.toHexString(System.currentTimeMillis());

        //BCrypt加密密码 新增user
        Patient patient = new Patient();
        patient.setAcc(newAccNumber);
        patient.setMobile(mobile);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncode = encoder.encode(pwd);
        patient.setPwd(pwdEncode);
        int res = baseMapper.insert(patient);
        if (res == 0) throw new CustomException(ResultCode.ADD_RECORD_FAILED, "insert failed");
        return newAccNumber;
    }

    @Override
    public String patientLogin(String mobile, String pwd, HttpServletRequest request) {
        // 用户未注册则抛异常
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count == 0)
            throw new CustomException(ResultCode.USER_UNREGISTERED_ERROR, "unregistered");

        //根据手机号获取user
        Patient patient = baseMapper.selectOne(wrapper);

        // 比较手机号 密码是否正确
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(pwd, patient.getPwd()))//密码匹配
            throw new CustomException(ResultCode.INCORRECT_PHONE_OR_PWD_ERROR, "incorrect phone or pwd");

        //用户被禁用
        if (patient.getIsDisabled())
            throw new CustomException(ResultCode.USER_IS_DISABLED_ERROR, "patient has been disabled");

        //获取用户id
        String id = patient.getId();
        //返回的token
        String token = JwtUtils.getTokenFromRequest(request);

        //TODO 请求中携有Token(用户第二次登录)
        if (!StringUtils.isEmpty(token)) {
            //更新过期时间
            redisTemplate.expire(id, 24 * 7, TimeUnit.HOURS);
        } else {//无token 第一次登录
            //redis缓存(旧设备)中是否有 该 userId
            String oldToken = redisTemplate.opsForValue().get(id);
            //账号在新设备登录 旧设备下线
            if (!StringUtils.isEmpty(oldToken)) redisTemplate.delete(id);
            //当前请求的设备(新设备)的信息
            String deviceInfo = ClientInfo.getDeviceInfo(request);
            //生成token
            token = JwtUtils.getJwtToken(id, deviceInfo);
            log.error("token" + token);
            //将token缓存
            redisTemplate.opsForValue().set(id, token, 24 * 7, TimeUnit.HOURS);
        }

        return token;
    }

    /***
     * 根据token查询患者id 由id查询患者
     * @param token token
     * @return 患者对象
     */
    @Override
    public Patient getPatientInfo(String token) {
        String id = JwtUtils.getDataIdByTokenStr(token);
        return baseMapper.selectById(id);
    }

    /***
     * 多条件组合查询 patient 集合
     * @param patientQuery 查询体
     * @return 符合条件的 patient 集合
     */
    @Override
    public void getPatientListByQuery(Page<Patient> page, PatientQuery patientQuery) {
        //无条件查询
        if (StringUtils.isEmpty(patientQuery)) {
            baseMapper.selectPage(page, null);
            return;
        }
        //多条件组合查询
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        //昵称 条件限制
        if (!StringUtils.isEmpty(patientQuery.getNickname())) {
            wrapper.like("nickname", patientQuery.getNickname());
        }
        //禁用 条件限制
        if (!StringUtils.isEmpty(patientQuery.getIsDisabled())) {
            wrapper.eq("is_disabled", patientQuery.getIsDisabled());
        }
        //起始时间 条件限制
        if (!StringUtils.isEmpty(patientQuery.getBeginTime())) {
            wrapper.ge("gmt_create", patientQuery.getBeginTime());
        }
        //截止时间 条件限制
        if (!StringUtils.isEmpty(patientQuery.getEndTime())) {
            wrapper.le("gmt_create", patientQuery.getEndTime());
        }
        baseMapper.selectPage(page, wrapper);
    }
}
