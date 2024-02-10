package Scheduling;

public final class Process implements Comparable<Process> {
    private final int processNumber;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority;

    public Process(int processNumber, int arrivalTime, int burstTime, int priority) {
        this.processNumber = processNumber;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    public int getProcessNumber() {
        return this.processNumber;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getBurstTime() {
        return this.burstTime;
    }

    public int getPriority() {
        return this.priority;
    }

    public int compareTo(Process that) {
        return Integer.compare(this.getArrivalTime(), that.getArrivalTime());
    }
}