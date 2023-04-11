package com.graduation.medicaltaskscheduled.utils;

import com.graduation.medicaltaskscheduled.entity.Appointment;

import javax.swing.*;
import java.util.List;

/**
 * @author RabbitFaFa
 */
public class ShowScreen extends JFrame {
    private static final long serialVersionUID = 1L;

    public ShowScreen(List<Appointment> list) {
        super("调度大屏");

        // 表头
        String[] columnNames = {"医疗车编号", "血压", "心率", "血糖", "呼吸率", "体温", "氧饱和度", "其他备注"};

        // 表格内容
        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            data[i][0] = list.get(i).getCarId();
            data[i][1] = list.get(i).getPressure();
            data[i][2] = list.get(i).getHeartRate();
            data[i][3] = list.get(i).getSugar();
            data[i][4] = list.get(i).getBreathRate();
            data[i][5] = list.get(i).getTemperature();
            data[i][6] = list.get(i).getOxygen();
            data[i][7] = list.get(i).getOther();
        }

        // 创建表格
        JTable table = new JTable(data, columnNames);

        // 将表格添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(table);

        // 将滚动面板添加到窗口中
        add(scrollPane);

        // 设置窗口属性
        setSize(500, 300);
        setLocationRelativeTo(null); // 居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
