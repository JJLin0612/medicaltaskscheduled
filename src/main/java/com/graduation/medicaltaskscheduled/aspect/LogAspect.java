package com.graduation.medicaltaskscheduled.aspect;


import com.graduation.medicaltaskscheduled.annotation.LogRecord;
import com.graduation.medicaltaskscheduled.entity.RecordLog;
import com.graduation.medicaltaskscheduled.entity.dto.OperateType;
import com.graduation.medicaltaskscheduled.entity.dto.Result;
import com.graduation.medicaltaskscheduled.entity.dto.ResultCode;
import com.graduation.medicaltaskscheduled.exception.CustomException;
import com.graduation.medicaltaskscheduled.service.AdminService;
import com.graduation.medicaltaskscheduled.service.PatientService;
import com.graduation.medicaltaskscheduled.service.RecordLogService;
import com.graduation.medicaltaskscheduled.utils.IpUtil;
import com.graduation.medicaltaskscheduled.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author RabbitFaFa
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    //TODO
    @Resource
    private AdminService adminService;

    @Resource
    private PatientService patientService;

    @Resource
    private RecordLogService logService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.graduation.medicaltaskscheduled.annotation.LogRecord)")
    public void logRecord() {
    }

    /***
     * 增强(通知)
     * @param joinPoint 连接点
     * @return 原被增强方法的执行结果
     */
    @Around("logRecord()")
    public Result aroundEnhance(ProceedingJoinPoint joinPoint) {
        //获取request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //获取方法上的LogRecord注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        LogRecord logRecordAnnotation = method.getAnnotation(LogRecord.class);

        //获取LogRecord注解的属性
        String operateType = logRecordAnnotation.operateType();
        String operateDesc = logRecordAnnotation.operateDesc();
        String contentId = logRecordAnnotation.contentId();
        String userType = logRecordAnnotation.userType();

        //获取ip 根据token获取用户id再查询DB得用户账号
        String opIp = IpUtil.getIpAddr(request);
        String opAcc = "";
        //非登陆注册
        if (!operateType.equals(OperateType.LOGIN) && !operateType.equals(OperateType.REGISTER)) {
            String userId = JwtUtils.getDataIdByJwtToken(request);//操作者(用户)id
            if (StringUtils.isEmpty(userId))
                throw new CustomException(ResultCode.TOKEN_NULL_OR_ERROR, "无登录凭证或登录凭证无效");
            if (userType.equals(OperateType.USER_TYPE_ADMIN)) {
                opAcc = adminService.getById(userId).getAcc();
            }
            if (userType.equals(OperateType.USER_TYPE_PATIENT)) {
                opAcc = patientService.getById(userId).getAcc();
            }
        }

        //TODO 用户进行更新操作 从DB根据id读取旧纪录
//        if (operateType.equals(OperateType.MODIFY)) {
//
//        }

        Result result = null;

        //操作结果 错误信息 耗时
        String status = "";
        String errorMsg = "";
        long timeCost = 0;

        try {
            long startTime = System.currentTimeMillis();
            //执行目标方法
            result = (Result) joinPoint.proceed();
            //获取操作耗时
            timeCost = System.currentTimeMillis() - startTime;
            //用户注册操作 获取注册后的acc更新opAcc
            if (operateType.equals(OperateType.REGISTER)) {
                opAcc = (String) result.getData().get("acc");
            }
            //用户登录操作
            if (operateType.equals(OperateType.LOGIN)) {
                String token = (String)result.getData().get("token");
                String id = JwtUtils.getDataIdByTokenStr(token);
                if (userType.equals(OperateType.USER_TYPE_ADMIN)) {
                    opAcc = adminService.getById(id).getAcc();
                }
                if (userType.equals(OperateType.USER_TYPE_PATIENT)) {
                    opAcc = patientService.getById(id).getAcc();
                }
            }
            //操作状态
            status = result.getIsSuccess() ? "success" : "failed";
        } catch (Throwable throwable) {
            status = "failed";
            //记录错误信息
            log.error("目标方法执行异常" + throwable.getCause());
            log.error("目标方法执行异常" + throwable.getMessage());
            errorMsg = throwable.getMessage();
            errorMsg = errorMsg.substring(0, Math.min(errorMsg.length(), 250));

        }

        //创建日志对象
        RecordLog recordLog = new RecordLog();
        recordLog.setUserType(userType);
        recordLog.setOpType(operateType);
        recordLog.setOpAcc(opAcc);
        recordLog.setOpDesc(operateDesc);
        recordLog.setOpIp(opIp);
        recordLog.setOpStatus(status);
        recordLog.setErrMsg(errorMsg);
        recordLog.setOpCostMs((int)timeCost);

        //将日志记录 异步 刷盘
        threadPoolExecutor.submit(() -> {
            try {
                boolean res = logService.save(recordLog);
            }catch (Exception e) {
                log.error("异步任务发生错误", e);
            }
        });

        return result;
    }

}
