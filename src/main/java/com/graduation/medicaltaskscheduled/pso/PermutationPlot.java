package com.graduation.medicaltaskscheduled.pso;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * @author RabbitFaFa
 */
public class PermutationPlot extends JPanel {
    private int[] permutation;
    private double[][] coordinates;
    private int n;
    private double center_x;
    private double center_y;
    private double[] x;
    private double[] y;

    public PermutationPlot(int[] permutation, double[][] coordinates) {
        this.permutation = permutation;
        this.coordinates = coordinates;
        this.n = permutation.length;
        this.x = new double[coordinates.length];
        this.y = new double[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            this.x[i] = coordinates[i][0];
            this.y[i] = coordinates[i][1];
        }
        this.center_x = coordinates[0][0];
        this.center_y = coordinates[0][1];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 绘制点
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < coordinates.length; i++) {
            g2d.fillOval((int) (x[i] - 3), (int) (y[i] - 3), 6, 6);
        }
        g2d.setColor(Color.RED);
        g2d.fillOval((int) (center_x - 3), (int) (center_y - 3), 6, 6);

        // 绘制相邻两个点之间的线段
        for (int i = 0; i < n - 1; i++) {
            int start_idx = permutation[i];
            int end_idx = permutation[i + 1];
            double start_x = x[start_idx];
            double start_y = y[start_idx];
            double end_x = x[end_idx];
            double end_y = y[end_idx];
            g2d.draw(new Line2D.Double(start_x, start_y, end_x, end_y));
        }

        // 绘制最后一个点和第一个点之间的线段
        int start_idx = permutation[n - 1];
        int end_idx = permutation[0];
        double start_x = x[start_idx];
        double start_y = y[start_idx];
        double end_x = x[end_idx];
        double end_y = y[end_idx];
        g2d.draw(new Line2D.Double(start_x, start_y, end_x, end_y));
    }


    public void printPath() {
        PermutationPlot plot = new PermutationPlot(permutation, coordinates);
        JFrame frame = new JFrame("路径规划结果");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(plot);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
