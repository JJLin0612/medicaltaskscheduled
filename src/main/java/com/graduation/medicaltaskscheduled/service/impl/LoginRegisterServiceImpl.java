package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.medicaltaskscheduled.entity.Admin;
import com.graduation.medicaltaskscheduled.entity.Doctor;
import com.graduation.medicaltaskscheduled.entity.Patient;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.entity.dto.UserLabel;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.service.AdminService;
import com.graduation.medicaltaskscheduled.service.DoctorService;
import com.graduation.medicaltaskscheduled.service.LoginRegisterService;
import com.graduation.medicaltaskscheduled.service.PatientService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * 统一登录登出注册实现逻辑
 *
 * @author RabbitFaFa
 */
@Service(value = "loginRegisterService")
public class LoginRegisterServiceImpl implements LoginRegisterService {

    @Resource
    private AdminService adminService;

    @Resource
    private DoctorService doctorService;

    @Resource
    private PatientService patientService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String unifyUserRegister(String mobile, String pwd, String verifyCode, String userLabel) {
        //解密手机号
//        mobile = decryptData(mobile);
        //从Redis中获取验证码并验证
        String code = redisTemplate.opsForValue().get(mobile);
        if (!StringUtils.isEmpty(code) && !code.equals(verifyCode))
            throw new CustomException(ResultCode.INCORRECT_VERIFY_CODE_ERROR, "incorrect verifCode");
        //缓存中删除验证码
        redisTemplate.delete(mobile);

        //解密密码
//        pwd = decryptData(pwd);
        //根据手机号码查询是否存在该手机号 且 该用户是否被删除
        QueryWrapper<?> wrapper = new QueryWrapper<>();
        Integer countByPhone = 0;
        Integer countByPhoAndIsDele = 0;
        if (userLabel.equals(UserLabel.LABEL_ADMIN)) {
            wrapper.eq("mobile", mobile);
            countByPhone = adminService.count(wrapper);
            wrapper.eq("is_deleted", ResultCode.USER_IS_NOT_DELETED);
            countByPhoAndIsDele = adminService.count(wrapper);
        }

        if (userLabel.equals(UserLabel.LABEL_DOCTOR)) {
            QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
            wrapper.eq("mobile", mobile);
            countByPhone = doctorService.count(wrapper);
            wrapper.eq("is_deleted", ResultCode.USER_IS_NOT_DELETED);
            countByPhoAndIsDele = doctorService.count(wrapper);
        }

        if (userLabel.equals(UserLabel.LABEL_PATIENT)) {
            QueryWrapper<Patient> wrapper = new QueryWrapper<>();
            wrapper.eq("mobile", mobile);
            countByPhone = patientService.count(wrapper);
            wrapper.eq("is_deleted", ResultCode.USER_IS_NOT_DELETED);
            countByPhoAndIsDele = patientService.count(wrapper);
        }

        //手机号已存在(存在且未被删除 或 存在但删除字段为 1)
        if (countByPhone == 1) {
            if (countByPhoAndIsDele == 1) {
                //该手机号用户存在且未被删除
                throw new CustomException(ResultCode.USER_HAS_EXITS_ERROR, "user has registered");
            } else {
                //该手机号用户存在但已为删除状态 将记录删除
                QueryWrapper wrapper = new QueryWrapper<>();
                wrapper.eq("mobile", mobile);
                baseMapper.delete(wrapper);
            }
        }

        //生成账号acc
        Calendar calendar = Calendar.getInstance();
        String newAccNumber = "d" + calendar.get(Calendar.DAY_OF_YEAR) + "_" + Long.toHexString(System.currentTimeMillis());

        //BCrypt加密密码 新增user
        User user = new User();
        user.setAcc(newAccNumber);
        user.setMobile(mobile);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncode = encoder.encode(pwd);
        user.setPwd(pwdEncode);
        int res = baseMapper.insert(user);
        if (res == 0) throw new CustomException(ResultCode.ADD_RECORD_FAILED, "insert failed");
        return newAccNumber;
    }

    @Override
    public String unifyUserLogin(String mobile, String pwd, String userLabel) {
        return null;
    }

    @Override
    public int logout(String id) {
        return 0;
    }
}
