package com.graduation.medicaltaskscheduled.pso;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author RabbitFaFa
 */
@Slf4j
public class PSOUtil {

    // PSO算法参数
    private static final double c1 = 0.8; // 学习因子1
    private static final double c2 = 0.8; // 学习因子2
    private static final double w = 1; // 惯性常量
    private static final int maxgen = 400; // 迭代次数
    private static final int sizepop = 500; // 粒子数量
    private static final double Vmax = 1; // 速度上限
    private static final double Vmin = -1; // 速度下限

    // 问题的参数
    private static final int carNum = 3; // 医疗车数量
    private static final int serNum = 11; // 服务点个数
    private static final int hosNum = 1; // 医疗中心数量 默认1

    // 坐标 默认第一个坐标为医疗中心
    private static final double[][] a = {{125, 167}, {131, 209}, {159, 199}, {98, 151}, {112, 142},
            {124, 132}, {161, 114}, {94, 231}, {93, 176}, {77, 166},
            {130, 76}, {201, 148}};


    public static int[] getPath() {
        // 距离矩阵
        double[][] D = distanceMatrix(a);

        // 初始化相关变量
        double[] fitness = new double[sizepop];
        int[][] pop = new int[sizepop][(serNum + hosNum + 1) * carNum];
        double[][] V = new double[sizepop][(serNum + hosNum + 1) * carNum];

        // 初始化种群中粒子的位置与初速度
        for (int i = 0; i < sizepop; i++) {
            // 初始化每个粒子的位置
            pop[i] = initPop(hosNum, serNum, carNum);
            // 约束判断
            while (!checkConstraint(pop[i], carNum, serNum, hosNum)) {
                pop[i] = initPop(hosNum, serNum, carNum);
            }
            // 初始化每个粒子的速度
            Random random = new Random();
            for (int j = 0; j < carNum * (serNum + hosNum + 1); j++) {
                V[i][j] = (Vmax - Vmin) * random.nextDouble() + Vmin;
            }
            // 每个粒子的适应度值
            fitness[i] = fun(D, pop[i], carNum);
        }

        // 记录fitness中最优值的值及最优粒子的索引
        int bestindex = getFitnessMinIndex(fitness);
        double bestfitness = fitness[bestindex];
        // 全局最优值
        int[] gbest = Arrays.copyOf(pop[bestindex], pop[bestindex].length);
        // 个体最优值 初始即为各自的值
        int[][] pbest = Arrays.copyOf(pop, pop.length);
        // 个体适应度值最优值
        double[] fitnesspbest = Arrays.copyOf(fitness, fitness.length);
        // 全局适应度值最优值
        double fitnessgbest = bestfitness;

        //开始迭代
        for (int i = 0; i < maxgen; i++) {
            // 更新每一个粒子
            for (int j = 0; j < sizepop; j++) {
                // 计算加速度
                double[] r1 = new double[carNum * (serNum + hosNum + 1)];
                double[] r2 = new double[carNum * (serNum + hosNum + 1)];
                Random rand = new Random();
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    r1[k] = rand.nextDouble();
                    r2[k] = rand.nextDouble();
                }

                // 更新速度
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    V[j][k] = w * V[j][k] + c1 * r1[k] * (pop[j][k] - pbest[j][k]) + c2 * r2[k] * (pop[j][k] - gbest[k]);
                    V[j][k] = Math.min(V[j][k], Vmax);
                    V[j][k] = Math.max(V[j][k], Vmin);
                }

                // 更新位置
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    pop[j][k] = (pop[j][k] + Math.round(V[j][k])) % 2 > 0 ? 1 : 0;
                }

                // 位置修正 固定每个path起点终点为医疗中心
                pop[j] = correctStartAndEnd(pop[j], carNum, serNum, hosNum);

                //不满足约束
                if (!checkConstraint(pop[j], carNum, serNum, hosNum)) {
                    // 粒子修正
                    pop[j] = correctPop(pop[j], carNum, serNum, hosNum);
                }

                // 计算适应度值
                fitness[j] = fun(D, pop[j], carNum);
            }

            // 更新pbest和gbest
            for (int j = 0; j < sizepop; j++) {
                if (fitness[j] < fitnesspbest[j]) {
                    pbest[j] = Arrays.copyOf(pop[j], pop[j].length);
                    fitnesspbest[j] = fitness[j];
                }
                if (fitness[j] < fitnessgbest) {
                    gbest = Arrays.copyOf(pop[j], pop[j].length);
                    fitnessgbest = fitness[j];
                }
            }
        }
        //实数编码的path
        return binToPath(gbest, carNum, serNum, hosNum);
    }

    /***
     * 测试PSO
     */
    public static void main(String[] args) {

        // 距离矩阵
        double[][] D = distanceMatrix(a);

        // 初始化相关变量
        double[] fitness = new double[sizepop];
        int[][] pop = new int[sizepop][(serNum + hosNum + 1) * carNum];
        double[][] V = new double[sizepop][(serNum + hosNum + 1) * carNum];

        // 初始化种群中粒子的位置与初速度
        for (int i = 0; i < sizepop; i++) {
            // 初始化每个粒子的位置
            pop[i] = initPop(hosNum, serNum, carNum);
            // 约束判断
            while (!checkConstraint(pop[i], carNum, serNum, hosNum)) {
                pop[i] = initPop(hosNum, serNum, carNum);
            }
            // 初始化每个粒子的速度
            Random random = new Random();
            for (int j = 0; j < carNum * (serNum + hosNum + 1); j++) {
                V[i][j] = (Vmax - Vmin) * random.nextDouble() + Vmin;
            }
            // 每个粒子的适应度值
            fitness[i] = fun(D, pop[i], carNum);
        }

        // 记录fitness中最优值的值及最优粒子的索引
        int bestindex = getFitnessMinIndex(fitness);
        double bestfitness = fitness[bestindex];
        // 全局最优值
        int[] gbest = Arrays.copyOf(pop[bestindex], pop[bestindex].length);
        // 个体最优值 初始即为各自的值
        int[][] pbest = Arrays.copyOf(pop, pop.length);
        // 个体适应度值最优值
        double[] fitnesspbest = Arrays.copyOf(fitness, fitness.length);
        // 全局适应度值最优值
        double fitnessgbest = bestfitness;

        //开始迭代
        for (int i = 0; i < maxgen; i++) {
            // 更新每一个粒子
            for (int j = 0; j < sizepop; j++) {
                // 计算加速度
                double[] r1 = new double[carNum * (serNum + hosNum + 1)];
                double[] r2 = new double[carNum * (serNum + hosNum + 1)];
                Random rand = new Random();
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    r1[k] = rand.nextDouble();
                    r2[k] = rand.nextDouble();
                }

                // 更新速度
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    V[j][k] = w * V[j][k] + c1 * r1[k] * (pop[j][k] - pbest[j][k]) + c2 * r2[k] * (pop[j][k] - gbest[k]);
                    V[j][k] = Math.min(V[j][k], Vmax);
                    V[j][k] = Math.max(V[j][k], Vmin);
                }

                // 更新位置
                for (int k = 0; k < carNum * (serNum + hosNum + 1); k++) {
                    pop[j][k] = (pop[j][k] + Math.round(V[j][k])) % 2 > 0 ? 1 : 0;
                }

                // 位置修正 固定每个path起点终点为医疗中心
                pop[j] = correctStartAndEnd(pop[j], carNum, serNum, hosNum);

                //不满足约束
                if (!checkConstraint(pop[j], carNum, serNum, hosNum)) {
                    // 粒子修正
                    pop[j] = correctPop(pop[j], carNum, serNum, hosNum);
                }

                // 计算适应度值
                fitness[j] = fun(D, pop[j], carNum);
            }

            // 更新pbest和gbest
            for (int j = 0; j < sizepop; j++) {
                if (fitness[j] < fitnesspbest[j]) {
                    pbest[j] = Arrays.copyOf(pop[j], pop[j].length);
                    fitnesspbest[j] = fitness[j];
                }
                if (fitness[j] < fitnessgbest) {
                    gbest = Arrays.copyOf(pop[j], pop[j].length);
                    fitnessgbest = fitness[j];
                }
            }
        }
        //实数编码的path
        int[] path = binToPath(gbest, carNum, serNum, hosNum);
        PermutationPlot permutationPlot = new PermutationPlot(path, a);
        permutationPlot.printPath();
    }

    private static int getFitnessMinIndex(double[] fitness) {
        int minIndex = 0;
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] < fitness[minIndex])
                minIndex = i;
        }
        return minIndex;
    }

    /***
     * 初始化单个粒子
     * @param hosNum 医疗中心数量
     * @param serNum 服务点数量
     * @param carNum 医疗车数量
     * @return 初始化后的粒子
     */
    public static int[] initPop(int hosNum, int serNum, int carNum) {
        // 一个粒子中一辆医疗车的二进制路径向量长度
        int singleCarPathLen = serNum + hosNum + 1;
        // 粒子长度 服务点+前后两个医疗点 乘以医疗车数量
        int len = singleCarPathLen * carNum;
        // 生成行矩阵
        int[] pop = new int[len];
        // 随机赋值
        for (int i = 0; i < len; i++) {
            pop[i] = (Math.random() > 0.5) ? 1 : 0;
        }
        // 固定每个 path 起点终点为医疗中心
        for (int i = 1; i <= carNum; i++) {
            pop[(i - 1) * singleCarPathLen] = 1;
            pop[(i - 1) * singleCarPathLen + (singleCarPathLen - 1)] = 1;
        }
        return pop;
    }

    /***
     * 将二进制编码的粒子转换为实数编码
     * @param pop 二进制编码的粒子
     * @param carNum 医疗车数量
     * @param serNum 服务点数量
     * @param hosNum 医疗中心数量
     * @return 实数编码的路径数组
     */
    private static int[] binToPath(int[] pop, int carNum, int serNum, int hosNum) {
        int popLen = pop.length; // 粒子长度
        int singleCarPathLen = serNum + hosNum + 1; // 一个粒子中一辆医疗车的二进制路径向量长度
        int[] path = new int[serNum + 2 * carNum];
        int m = 0;
        for (int i = 0; i < popLen; i++) {
            if (pop[i] == 1) {
                int index = i % singleCarPathLen;
                if (index == singleCarPathLen - 1) {
                    index = 0;
                }
                path[m++] = index;
            }
        }
        return path;
    }

    /***
     * 检查约束: 每个服务点被且仅被一辆医疗车服务
     * @param singlePop 单个粒子(一种路径)
     * @param carNum 医疗车数量
     * @param serNum 服务点数量
     * @param hosNum 医疗中心数量
     * @return true-粒子满足约束  false-粒子不满足约束
     */
    public static boolean checkConstraint(int[] singlePop, int carNum, int serNum, int hosNum) {
        boolean flag = true;
        int singleCarPathLen = serNum + hosNum + 1; // 一个粒子中一辆医疗车的二进制路径向量长度
        for (int i = 1; i < singleCarPathLen - 1; i++) { // 遍历每个服务点，不遍历医疗点
            int sum = 0;
            for (int j = 0; j < carNum; j++) {
                sum += singlePop[i + j * singleCarPathLen];
            }
            if (sum != 1) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /***
     * 修正粒子 使其满足 每个服务点被且仅被一辆医疗车服务
     * @param pop
     * @param carNum
     * @param serNum
     * @param hosNum
     * @return
     */
    public static int[] correctPop(int[] pop, int carNum, int serNum, int hosNum) {
        int singleCarPathLen = serNum + hosNum + 1; // 一个粒子中一辆医疗车的二进制路径向量长度
        for (int m = 1; m < singleCarPathLen - 1; m++) {
            int sum = 0;
            for (int j = 0; j < carNum; j++) {
                sum += pop[m + j * singleCarPathLen];
            }
            // 不满足约束
            if (sum != 1) {
                // 全0，该服务点无医疗车进行服务，随机选一辆医疗车进行服务
                if (sum == 0) {
                    int carNo = ThreadLocalRandom.current().nextInt(1, carNum + 1);
                    pop[m + (carNo - 1) * singleCarPathLen] = 1;
                }
                // 多个1，多辆医疗车服务该服务点
                else {
                    for (int j = 0; j < carNum; j++) {
                        pop[m + j * singleCarPathLen] = 0;
                    }
                    int carNo = ThreadLocalRandom.current().nextInt(1, carNum + 1);
                    pop[m + (carNo - 1) * singleCarPathLen] = 1;
                }
            }
        }
        return pop;
    }

    /***
     * 修正粒子 使得粒子中每辆医疗车路径的起点终点都为医疗中心
     * @param pop
     * @param carNum
     * @param serNum
     * @param hosNum
     * @return
     */
    public static int[] correctStartAndEnd(int[] pop, int carNum, int serNum, int hosNum) {
        int singleCarPathLen = serNum + hosNum + 1; // 一个粒子中一辆医疗车的二进制路径向量长度
        for (int k = 1; k <= carNum; k++) {
            pop[(k - 1) * singleCarPathLen] = 1; // 起点为医疗中心
            pop[(k - 1) * singleCarPathLen + singleCarPathLen - 1] = 1; // 终点为医疗中心
        }
        return pop;
    }

    /***
     * 求解距离矩阵
     * @param a 位置坐标
     * @return 距离矩阵 任意两个点之间的距离
     */
    public static double[][] distanceMatrix(double[][] a) {
        int dLen = a.length;
        double[][] D = new double[dLen][dLen];
        for (int i = 0; i < dLen; i++) {
            for (int j = i + 1; j < dLen; j++) {
                double distance = Math.sqrt(Math.pow(a[i][0] - a[j][0], 2) + Math.pow(a[i][1] - a[j][1], 2));
                D[i][j] = distance;
                D[j][i] = distance;
            }
        }
        return D;
    }

    /**
     * 目标函数 计算粒子路径对应的总距离
     *
     * @param D      距离矩阵
     * @param x      单个粒子
     * @param carNum 医疗车数量
     * @return 总距离
     */
    public static double fun(double[][] D, int[] x, int carNum) {
        int singleCarPathLen = x.length / carNum;
        int p1 = 0; // 慢指针
        int p2 = 1; // 快指针
        double distance = 0; // 路径距离
        while (p2 < singleCarPathLen * carNum) {
            // 找到下一个1
            while (x[p2] != 1) p2++;
            // 记录距离
            int nodeStart = p1 % singleCarPathLen;
            int nodeEnd = p2 % singleCarPathLen;
            if (nodeStart == singleCarPathLen - 1) {
                nodeStart = 0;
            }
            if (nodeEnd == singleCarPathLen - 1) {
                nodeEnd = 0;
            }
            distance = distance + D[nodeStart][nodeEnd];
            // 移动指针
            p1 = p2++;
        }

        // 【约束条件 每辆医疗车单次行驶最远距离为 120】
        for (int i = 1; i <= carNum; i++) {
            int p3 = (i - 1) * singleCarPathLen;
            int p4 = (i - 1) * singleCarPathLen + 1;
            int bound = i * singleCarPathLen;
            double singleCarDistance = 0;
            // 计算单辆医疗车行驶总距离
            while (p4 < bound) {
                //查找下一个服务点
                while (x[p4] != 1) p4++;
                // 记录距离
                int nodeStart = p3 % singleCarPathLen;
                int nodeEnd = p4 % singleCarPathLen;
                if (nodeStart == singleCarPathLen - 1) {
                    nodeStart = 0;
                }
                if (nodeEnd == singleCarPathLen - 1) {
                    nodeEnd = 0;
                }
                singleCarDistance = singleCarDistance + D[nodeStart][nodeEnd];
                // 移动指针
                p3 = p4++;
            }
            // 单医疗车行驶总距离大于260 加入惩罚项
            if (singleCarDistance > 260) {
                distance = distance + 100;
            }
        }
        return distance;
    }

}
