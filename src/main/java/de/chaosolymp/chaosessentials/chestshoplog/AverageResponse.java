package de.chaosolymp.chaosessentials.chestshoplog;

public class AverageResponse {
    private double average;
    private int count;

    public AverageResponse(double average, int count) {
        this.average = average;
        this.count = count;
    }

    public AverageResponse() {
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTotalAverage(int multiplier) {
        return Math.round(average * multiplier * 100) / 100.0;
    }
}
