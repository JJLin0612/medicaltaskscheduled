package com.graduation.medicaltaskscheduled.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.medicaltaskscheduled.entity.Admin;
import com.graduation.medicaltaskscheduled.entity.Appointment;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.entity.vo.AdminQuery;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.mapper.AdminMapper;
import com.graduation.medicaltaskscheduled.pso.PSOUtil;
import com.graduation.medicaltaskscheduled.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.medicaltaskscheduled.service.AppointmentService;
import com.graduation.medicaltaskscheduled.utils.ClientInfo;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import com.graduation.medicaltaskscheduled.utils.ShowScreen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private static boolean flag = false;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ArrayList<Appointment> appointmentList;

    @Autowired
    private AppointmentService appointmentService;


    @Override
    public String adminRegister(String mobile, String pwd) {

        //根据手机号码查询是否存在该手机号 且 该用户是否被删除
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer countByPhone = baseMapper.selectCount(wrapper);
        wrapper.eq("is_deleted", ResultCode.USER_IS_NOT_DELETED);
        Integer countByPhoAndIsDele = baseMapper.selectCount(wrapper);
        //手机号已存在(存在且未被删除 或 存在但删除字段为 1)
        if (countByPhone == 1) {
            if (countByPhoAndIsDele == 1) {
                //该手机号用户存在且未被删除
                throw new CustomException(ResultCode.USER_HAS_EXITS_ERROR, "admin has registered");
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
        Admin admin = new Admin();
        admin.setAcc(newAccNumber);
        admin.setMobile(mobile);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncode = encoder.encode(pwd);
        admin.setPwd(pwdEncode);
        int res = baseMapper.insert(admin);
        if (res == 0) throw new CustomException(ResultCode.ADD_RECORD_FAILED, "insert failed");
        return newAccNumber;
    }

    @Override
    public String adminLogin(String mobile, String pwd, HttpServletRequest request) {
        // 用户未注册则抛异常
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        log.warn("电话号码为=" + mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count == 0)
            throw new CustomException(ResultCode.USER_UNREGISTERED_ERROR, "unregistered");

        //根据手机号获取user
        Admin admin = baseMapper.selectOne(wrapper);

        // 比较手机号 密码是否正确
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(pwd, admin.getPwd()))//密码匹配
            throw new CustomException(ResultCode.INCORRECT_PHONE_OR_PWD_ERROR, "incorrect phone or pwd");

        //用户被禁用
        if (admin.getIsDisabled())
            throw new CustomException(ResultCode.USER_IS_DISABLED_ERROR, "admin has been disabled");

        //获取用户id
        String id = admin.getId();
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
            //将token缓存
            redisTemplate.opsForValue().set(id, token, 24 * 7, TimeUnit.HOURS);
        }

        return token;
    }

    @Override
    public List<Appointment> getTaskQueue() {
        if (!flag) {
            QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
            wrapper.eq("is_scheduled", ResultCode.APPOINTMENT_IS_NO_SCHEDULED);
            List<Appointment> list = appointmentService.list(wrapper);
            for (Appointment item : list) {
                appointmentList.add(item);
            }
            flag = true;
        }
        log.warn("appointment length = " + appointmentList.size());
        return appointmentList;
    }

//    @Async
    @Override
    public List<Integer> taskScheduled(List<String> carIdList, List<Appointment> appointmentList) throws ExecutionException, InterruptedException {
//        return CompletableFuture.supplyAsync(() -> {
            int carNum = carIdList.size();
            int serNum = appointmentList.size() - 1;

            //获取坐标
            double[][] location = new double[serNum + 1][2];
            for (int i = 0; i < location.length; i++) {
                Appointment appointment = appointmentList.get(i);
                location[i][0] = appointment.getLocatX();
                location[i][1] = appointment.getLocatY();
            }

            //规划路径
            int[] path = PSOUtil.getPath(location, carNum, serNum);

            //为appointment指定carId
            int count = 2 * carNum;//减到0代表可以将下一个carId赋给appointment
            int carIndex = 0;
            for (int i = 0; i < path.length; i++) {
                log.warn("path[i]="+path[i]);
                if (path[i] == 0) {
                    count--;
                    if (count % 2 != 0) carIndex = (2 * carNum - count) >> 1;
                    continue;
                }

                String carId = carIdList.get(carIndex);
                log.error(carIndex + "");
                log.error(carId);
                appointmentList.get(path[i]).setCarId(carId);
//                appointmentList.get(path[i]).setIsScheduled(true);
            }

            //更新DB appointment
            for (Appointment appointment : appointmentList) {
                appointmentService.updateById(appointment);
            }

            //结果集
            List<Integer> res = new ArrayList<>(path.length);
            for (int point : path) {
                res.add(point);
            }
            log.warn("结果集=" + res.toString());
            return res;
//        }).get();
    }

    @Override
    public void ackScheduled(List<String> appointmentIdList) {
        //将appointment获取
        List<Appointment> appointmentList = new ArrayList<>(appointmentIdList.size());
        for (String id : appointmentIdList) {
            appointmentList.add(appointmentService.getById(id));
        }
        //TODO 下发至大屏
//        new ShowScreen(appointmentList);

        //TODO 路径下发至医疗车
    }

    /***
     * 条件+分页 组合查询
     * @param page
     * @param adminQuery
     */
    @Override
    public void adminListQuery(Page<Admin> page, AdminQuery adminQuery) {
        if (StringUtils.isEmpty(adminQuery)) {
            baseMapper.selectPage(page, null);
            return;
        }
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(adminQuery.getName())) {
            wrapper.like("name", adminQuery.getName());
        }
        if (!StringUtils.isEmpty(adminQuery.getIsDisabled())) {
            wrapper.eq("is_disabled", adminQuery.getIsDisabled());
        }
        if (!StringUtils.isEmpty(adminQuery.getBeginTime())) {
            wrapper.ge("gmt_create", adminQuery.getBeginTime());
        }
        if (!StringUtils.isEmpty(adminQuery.getEndTime())) {
            wrapper.le("gmt_create", adminQuery.getEndTime());
        }
        baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Admin getAdminByToken(String token) {
        String id = JwtUtils.getDataIdByTokenStr(token);
        return baseMapper.selectOne(new QueryWrapper<Admin>().eq("id", id));
    }
}
