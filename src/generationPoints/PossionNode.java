package generationPoints;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 产生服从泊松分布的随机数对象，每个对象对应一个源节点
 * 源节点属性有：总的工作时隙数；lambda值；用于标记不同节点的userName；该节点对应的泊松流
 */

public class PossionNode {
    private String userName;
    private int slotLength;
    private double lambda;
    private int numberOfPacket;
    private int[] possionData;

    public PossionNode() {}

    public PossionNode(String userName, int slotLength, double lambda) {
        this.userName = userName;
        this.slotLength = slotLength;
        this.lambda = lambda;
        this.possionData = new int[slotLength];
        this.numberOfPacket = 0;
        this.possionData = generatePossionData(slotLength, lambda);
    }

    public int[] generatePossionData(int slotLength, double lambda) {
        for (int i = 0; i < slotLength; i++) {
            if (getPossionVariable(lambda) >= 1) {
                this.possionData[i] = 1;
                this.numberOfPacket += 1;
            } else {
                this.possionData[i] = 0;
            }
        }
        return this.possionData;
    }

    private int getPossionVariable(double lamda) {
        int x = 0;
        double y = Math.random(), cdf = getPossionProbability(x, lamda);
        while (cdf < y) {
            x++;
            cdf += getPossionProbability(x, lamda);
        }
        return x;
    }

    private double getPossionProbability(int k, double lamda) {
        double c = Math.exp(-lamda), sum = 1;
        for (int i = 1; i <= k; i++) {
            sum *= lamda / i;
        }
        return sum * c;
    }

    /**
     * @param number:源节点数目
     * @return 源节点对象列表
     */
    public static ArrayList<PossionNode> generateSourceNode(int number, int slotNum, double[] lambdas) {
        ArrayList<PossionNode> sourceNodeLists = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            PossionNode possionPoints = new PossionNode("sourceNode" + i, slotNum, lambdas[i]);
            sourceNodeLists.add(possionPoints);
        }
        try {
            CsmaAccess.csmaAccess(sourceNodeLists);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < slotNum; i++) {
            int countNum = 0;
            for (int j = 0; j < number; j++) {
                if (sourceNodeLists.get(j).getPossionData()[i] == 1) {
                    countNum++;
                }
            }
            if (countNum > 1) {
                System.out.println("CSMA did not work");
            }
        }
        return sourceNodeLists;
    }

    public int getSlotLength() {
        return slotLength;
    }

    public void setSlotLength(int slotLength) {
        this.slotLength = slotLength;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPossionData(int[] possionData) {
        this.possionData = possionData;
    }

    public int[] getPossionData() {
        return this.possionData;
    }

    public int getNumberOfPacket() {
        return numberOfPacket;
    }

    public void setNumberOfPacket(int numberOfPacket) {
        this.numberOfPacket = numberOfPacket;
    }

    @Override
    public String toString() {
        return "PossionNode{" +
                "userName='" + userName + '\'' +
                ", slotLength=" + slotLength +
                ", lambda=" + lambda +
                ", numberOfPacket=" + numberOfPacket +
                ", possionData=" + Arrays.toString(possionData) +
                '}';
    }
}
