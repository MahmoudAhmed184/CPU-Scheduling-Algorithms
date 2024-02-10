package Scheduling;

class ProcessWrapper implements Comparable<ProcessWrapper> {
    private static long counter = 0;
    private final long order;
    private final Process process;

    public ProcessWrapper(Process process) {
        this.process = process;
        this.order = counter++;
    }

    public Process getProcess() {
        return this.process;
    }

    public int getProcessNumber() {
        return this.process.getProcessNumber();
    }

    public int getArrivalTime() {
        return this.process.getArrivalTime();
    }

    public int getBurstTime() {
        return this.process.getBurstTime();
    }

    public int getPriority() {
        return this.process.getPriority();
    }

    public long getOrder() {
        return this.order;
    }

    @Override
    public int compareTo(ProcessWrapper other) {
        int res = process.compareTo(other.getProcess());
        return res != 0 ? res : Long.compare(order, other.order);
    }
}