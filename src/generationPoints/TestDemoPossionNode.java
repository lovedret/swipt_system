package generationPoints;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TestDemoPossionNode {
    public static void main(String[] args) {
        int sourceNum = 50;
        int timeSlot = 1000;
        double[] averData = new double[99];
        for (int k = 0; k < 100; k++) {
            int n = 0;
            for (double lambda = 0.001; lambda <= 0.1; lambda += 0.001) {
                double[] lambdas = new double[sourceNum];
                int numberOfPacket = 0;
                for (int i = 0; i < sourceNum; i++) {
                    lambdas[i] = lambda / sourceNum;
                }
                ArrayList<PossionNode> arrayList = PossionNode.generateSourceNode(sourceNum, timeSlot, lambdas);
                CsmaAccess.csmaAccess(arrayList);
                for (int i = 0; i < timeSlot; i++) {
                    for (PossionNode possionNode : arrayList) {
                        numberOfPacket += possionNode.getPossionData()[i];
                    }
                }
                averData[n] += numberOfPacket;
                n++;
            }
        }
        for (int i = 0; i < averData.length; i++) {
            averData[i] = averData[i] / 100;
        }
        String file3 = "D://SwiptCode//csma50.txt";
        try {
            FileOutputStream fileOutputStream1 = new FileOutputStream(file3);
            for (double d : averData) {
                fileOutputStream1.write((d + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
