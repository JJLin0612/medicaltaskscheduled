package com.graduation.medicaltaskscheduled.entity.vo;

import com.graduation.medicaltaskscheduled.entity.Appointment;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author RabbitFaFa
 */
@Data
public class ScheduleVo {
    private List<String> carIdList;
    private List<Appointment> appointmentList;

}
