package networkSetting;

public class DataPacket {
    private String sourceNode;
    private int timeSlot;
    private int sourceIndex;

    public DataPacket() {

    }

    public DataPacket(String sourceNode, int timeSlot) {
        this.sourceNode = sourceNode;
        this.timeSlot = timeSlot;
        this.sourceIndex = generateSourceIndex(this.sourceNode);
    }

    public int generateSourceIndex(String sourceNode) {
        return Integer.parseInt(sourceNode.substring(10));
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "sourceNode='" + sourceNode + '\'' +
                ", timeSlot=" + timeSlot +
                ", sourceIndex=" + sourceIndex +
                '}';
    }

    public static void main(String[] args) {
        DataPacket a = new DataPacket("sourceNode9", 1);
        System.out.println(a);
    }
}
