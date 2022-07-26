package generationPoints;
/**
 * CSMA接入，多个节点在一个时隙需要通信时随机选择一个
 */

import java.util.ArrayList;
import java.util.Random;

public class CsmaAccess {
    /**
     * @param nums: 输入，源节点对象数组，每个元素为一个源节点对象
     * @return ：输出，将输入中同一时隙同时发送的源节点随机选择一个发送，其他置为0
     */
    public static void csmaAccess(ArrayList<PossionNode> nums) {
        int len = nums.size();
        int size = nums.get(0).getPossionData().length;
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> resList = new ArrayList<>();
            for (int j = 0; j < len; j++) {
                if (nums.get(j).getPossionData()[i] == 1) {
                    resList.add(j);
                }
            }
            if (resList.size() <= 1) {
                continue;
            }
            int luckBoy = new Random().nextInt(resList.size());
            for (int j = 0; j < len; j++) {
                if (j != luckBoy) {
                    if (nums.get(j).getPossionData()[i] == 1) {
                        nums.get(j).setNumberOfPacket(nums.get(j).getNumberOfPacket() - 1);
                    }
                    nums.get(j).getPossionData()[i] = 0;
                }
            }
        }
    }
}
