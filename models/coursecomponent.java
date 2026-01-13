package models;

public class coursecomponent {

    private int componentId;
    private int offeringId;
    private String componentName;
    private double weightage;
    private int createdBy;
    private double maxMarks;

    public coursecomponent(int componentId, int offeringId, String componentName,
                           double weightage, int createdBy, double maxMarks) {

        this.componentId = componentId;
        this.offeringId = offeringId;
        this.componentName = componentName;
        this.weightage = weightage;
        this.createdBy = createdBy;
        this.maxMarks = maxMarks;
    }

    public int getComponentId() { return componentId; }
    public int getOfferingId() { return offeringId; }
    public String getComponentName() { return componentName; }
    public double getWeightage() { return weightage; }
    public int getCreatedBy() { return createdBy; }
    public double getMaxMarks() { return maxMarks; }
}
