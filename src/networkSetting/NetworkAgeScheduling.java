package networkSetting;

import java.util.Random;

public class NetworkAgeScheduling {
    /**
     * 设置网络参数
     * monitor与relay之间：距离；信道
     * relay与destination之间：距离；信道
     * monitor发射功率
     * relay发射功率
     * relay处能量分割因子
     */
    private double transmitPS;
    private double transmitPR;
    private double alpha;
    private double beta;
    private double pathLossPara;
    private double noisePowerSR;
    private double[] distanceSR;
    private double[][] channelSR;
    private double noisePowerRD;
    private double distanceRD;
    private double[] channelRD;

    private double[][] energyHarvest;
    private double[][] signalPowerRelay;

    private double[] energyQueue;
    private double energyQueueCap;

    private double[] lambdas;
    private double[] thresholdAge;

    public void arrivalPacket(int sourceNodeIndex, int slotIndex) {
        System.out.println("energyHarvesting:" + energyHarvest[sourceNodeIndex][slotIndex]);
        energyQueue[slotIndex + 1] = Math.min(energyQueue[slotIndex + 1] + energyHarvest[sourceNodeIndex][slotIndex], energyQueueCap);
    }

    public boolean ifRelayPacket(int sourceNodeIndex, double ageOfNode) {
        if (ageOfNode > thresholdAge[sourceNodeIndex]) {
            double temp = (-1) * Math.pow(ageOfNode, 2) +
                    (2 * thresholdAge[sourceNodeIndex] - 2) * ageOfNode
                    + 3 - 2 * thresholdAge[sourceNodeIndex];
            if (temp <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean canSuccessDecoding(int sourceNodeIndex, int slotIndex) {
        if (energyQueue[slotIndex] < 1) {
            return false;
        }
        double temp1 = (1 - alpha) * transmitPS * transmitPR
                * Math.pow(channelSR[sourceNodeIndex][slotIndex - 1], 2)
                * Math.pow(channelRD[slotIndex], 2);
        double temp2 = transmitPR * Math.pow(channelRD[slotIndex], 2) * noisePowerSR
                * Math.pow(distanceSR[sourceNodeIndex], pathLossPara);
        double temp3 = Math.pow(distanceSR[sourceNodeIndex], pathLossPara)
                * Math.pow(distanceRD, pathLossPara) * noisePowerRD;
        double snr = temp1 / (temp2 + temp3);
        System.out.println("snr = " + snr);
        return snr >= 1;
    }

    public void relayPacket(int slotIndex) {
        energyQueue[slotIndex + 1] = energyQueue[slotIndex + 1] - 1;
    }

    public double[] getEnergyQueue() {
        return energyQueue;
    }

    public void setEnergyQueue(double[] energyQueue) {
        this.energyQueue = energyQueue;
    }

    public NetworkAgeScheduling() {
    }

    public NetworkAgeScheduling(int timeSlot, int sourceNumber, double alpha,
                                double[] lambdas, double beta, double initEnergyCap) {
        transmitPowerSetting();
        noisePowerSetting();
        this.distanceSR = new double[sourceNumber];
        distanceSRSetting();
        this.pathLossPara = 2.7;
        this.alpha = alpha;
        this.beta = beta;
        this.energyQueueCap = initEnergyCap;
        channelSR = new double[sourceNumber][timeSlot];
        channelRD = new double[timeSlot];
        channelSetting();
        energyHarvest = new double[sourceNumber][timeSlot];
        signalPowerRelay = new double[sourceNumber][timeSlot];
        receivePowerCalculate();
        energyQueue = new double[timeSlot + 1];
        this.lambdas = lambdas;
        this.thresholdAge = new double[sourceNumber];
        calculateTa(timeSlot, beta);
    }

    public void calculateTa(int timeSlot, double beta) {
        for (int i = 0; i < thresholdAge.length; i++) {
            double temp = (transmitPS * Math.sqrt(0.5)) / (Math.pow(distanceSR[i], pathLossPara));
            System.out.println("receivePower:" + temp);
            double te = 1 / ((alpha * temp) * lambdas[i]);
            thresholdAge[i] = (transmitPR * timeSlot) / ((alpha * temp) * lambdas[i] * timeSlot + beta * energyQueueCap);
            System.out.println("ta:" + thresholdAge[i]);
        }
    }

    public void receivePowerCalculate() {
        int sourceNumber = signalPowerRelay.length;
        int timeSlot = signalPowerRelay[0].length;
        for (int i = 0; i < sourceNumber; i++) {
            for (int j = 0; j < timeSlot; j++) {
                /**
                 * 计算源节点i在时隙j发送信号在relay处对应接收信号功率
                 * 计算源节点i在时隙j发送信号relay处能收集的能量
                 */
                double temp1 = transmitPS * Math.pow(channelSR[i][j], 2);
                double temp2 = Math.pow(distanceSR[i], pathLossPara);
                double receivePower = temp1 / temp2;
                signalPowerRelay[i][j] = receivePower;
                energyHarvest[i][j] = receivePower * alpha;
            }
        }
    }

    public void updateEnergyQueue(int timeSlot) {
        energyQueue[timeSlot + 1] = energyQueue[timeSlot];
    }

    public void transmitPowerSetting() {
        transmitPS = 1;
        transmitPR = 1;
    }

    public void noisePowerSetting() {
        noisePowerSR = Math.pow(10, -3);
        noisePowerRD = Math.pow(10, -3);
    }

    public void distanceSRSetting() {
        Random r = new Random();
        for (int i = 0; i < distanceSR.length; i++) {
            distanceSR[i] = r.nextDouble() + 0.5;
        }
        distanceRD = 3;
    }

    public void channelSetting() {
        int sourceNumber = channelSR.length;
        int timeSlot = channelSR[0].length;
        Random random = new Random();
        for (int i = 0; i < sourceNumber; i++) {
            for (int j = 0; j < timeSlot; j++) {
                channelSR[i][j] = Math.sqrt(0.5) * random.nextGaussian();
            }
        }
        for (int i = 0; i < timeSlot; i++) {
            channelRD[i] = Math.sqrt(0.5) * random.nextGaussian();
        }
    }

    public double getTransmitPS() {
        return transmitPS;
    }

    public void setTransmitPS(double transmitPS) {
        this.transmitPS = transmitPS;
    }

    public double getTransmitPR() {
        return transmitPR;
    }

    public void setTransmitPR(double transmitPR) {
        this.transmitPR = transmitPR;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getPathLossPara() {
        return pathLossPara;
    }

    public void setPathLossPara(double pathLossPara) {
        this.pathLossPara = pathLossPara;
    }

    public double getNoisePowerSR() {
        return noisePowerSR;
    }

    public void setNoisePowerSR(double noisePowerSR) {
        this.noisePowerSR = noisePowerSR;
    }

    public double[] getDistanceSR() {
        return distanceSR;
    }

    public void setDistanceSR(double[] distanceSR) {
        this.distanceSR = distanceSR;
    }

    public double[][] getChannelSR() {
        return channelSR;
    }

    public void setChannelSR(double[][] channelSR) {
        this.channelSR = channelSR;
    }

    public double getNoisePowerRD() {
        return noisePowerRD;
    }

    public void setNoisePowerRD(double noisePowerRD) {
        this.noisePowerRD = noisePowerRD;
    }

    public double getDistanceRD() {
        return distanceRD;
    }

    public void setDistanceRD(double distanceRD) {
        this.distanceRD = distanceRD;
    }

    public double[] getChannelRD() {
        return channelRD;
    }

    public void setChannelRD(double[] channelRD) {
        this.channelRD = channelRD;
    }

    public double[][] getEnergyHarvest() {
        return energyHarvest;
    }

    public void setEnergyHarvest(double[][] energyHarvest) {
        this.energyHarvest = energyHarvest;
    }

    public double[][] getSignalPowerRelay() {
        return signalPowerRelay;
    }

    public void setSignalPowerRelay(double[][] signalPowerRelay) {
        this.signalPowerRelay = signalPowerRelay;
    }

    public double getEnergyQueueCap() {
        return energyQueueCap;
    }

    public void setEnergyQueueCap(double energyQueueCap) {
        this.energyQueueCap = energyQueueCap;
        this.energyQueue[0] = energyQueueCap;
    }

    public double[] getLambdas() {
        return lambdas;
    }

    public void setLambdas(double[] lambdas) {
        this.lambdas = lambdas;
    }

    public double[] getThresholdAge() {
        return thresholdAge;
    }

    public void setThresholdAge(double[] thresholdAge) {
        this.thresholdAge = thresholdAge;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
}
