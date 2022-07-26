package swiptSystem;

import generationPoints.CsmaAccess;
import generationPoints.PossionNode;
import networkSetting.DataPacket;
import networkSetting.NetworkStart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class AgeBasedScheduling {
    public static void main(String[] args) {
        int timeSlot = 10000;
        double[] resultAge = new double[timeSlot];
        double[] resultEnergy = new double[timeSlot];
        for (int timeIndex = 0; timeIndex < 30; timeIndex++) {
            int sourceNumber = 20;
            double[] lambdas = new double[sourceNumber];
            for (int i = 0; i < sourceNumber; i++) {
                lambdas[i] = 0.008;
            }
            ArrayList<PossionNode> sourceNodes = PossionNode.generateSourceNode(sourceNumber, timeSlot, lambdas);
            CsmaAccess.csmaAccess(sourceNodes);
            for (PossionNode possionNode : sourceNodes) {
                System.out.println(possionNode);
            }
            double alpha = 0.1;
            double initEnergy = 20;
            NetworkStart networkStart = new NetworkStart(timeSlot, sourceNumber, alpha);
            networkStart.setEnergyQueueCap(initEnergy);
            double[][] age = new double[sourceNumber][timeSlot];
            for (int i = 0; i < sourceNumber; i++) {
                age[i][0] = 5;
            }
            LinkedList<DataPacket> dataQueue = new LinkedList<>();
            int numberOfFail = 0;
            int numberOfSuccess = 0;
            for (int t = 0; t < timeSlot; t++) {
                System.out.println(t);
                networkStart.updateEnergyQueue(t);
                ageUpdate(age, t);
                if (dataQueue.size() != 0) {
                    DataPacket dataPacket = dataQueue.pollFirst();
                    if (networkStart.canSuccessDecoding(dataPacket.getSourceIndex(), t)) {
                        age[dataPacket.getSourceIndex()][t] = 2;
                        networkStart.relayPacket(t);
                        numberOfSuccess++;
                    } else {
                        numberOfFail++;
                    }
                }
                for (PossionNode possionNode : sourceNodes) {
                    if (possionNode.getPossionData()[t] == 1) {
                        DataPacket dataPacket = new DataPacket(possionNode.getUserName(), t);
                        dataQueue.add(dataPacket);
                        networkStart.arrivalPacket(dataPacket.getSourceIndex(), t);
                    }
                }
            }
            double[] energyLevel = networkStart.getEnergyQueue();
            for (double d: energyLevel) {
                System.out.print(d + " ");
            }
            System.out.println();
            double[] ageAverage = new double[timeSlot];
            for (int q = 0; q < timeSlot; q++) {
                for (int m = 0; m < sourceNumber; m++) {
                    ageAverage[q] += age[m][q];
                }
                ageAverage[q] = ageAverage[q] / sourceNumber;
            }
            System.out.println("numberOfSuccess:" + numberOfSuccess);
            System.out.println("numberOfFail:" + numberOfFail);
            for (int ti = 0; ti < timeSlot; ti++) {
                resultAge[ti] += ageAverage[ti];
                resultEnergy[ti] += energyLevel[ti];
            }
        }
        for (int ti = 0; ti < timeSlot; ti++) {
            resultAge[ti] = resultAge[ti] / 30;
            resultEnergy[ti] = resultEnergy[ti] / 30;
        }
        String file1 = "D://swipt//averAge.txt";
        String file2 = "D://swipt//averEnergy.txt";
        try {
            FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
            FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
            for (double d : resultAge) {
                fileOutputStream1.write((d + "\n").getBytes());
            }
            for (double d : resultEnergy) {
                fileOutputStream2.write((d + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ageUpdate(double[][] age, int t) {
        int sourceNumber = age.length;
        if (t == 0) {
            return;
        }
        for (int i = 0; i < sourceNumber; i++) {
            age[i][t] = age[i][t - 1] + 1;
        }
    }
}
