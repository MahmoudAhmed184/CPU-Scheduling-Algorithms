package Scheduling;

import java.util.Comparator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Queue;

public class Scheduler {
    // Table generator for creating the scheduling table
    private static final TableGenerator tableGenerator = new TableGenerator();

    // Headers for the scheduling table
    private static final String[] tableHeaders = {"Process Number", "Start Execution Time", "Completion Time", "Waiting Time", "Turn Around Time"};

    public static void runFirstComeFirstServe(Collection<Process> processes) {
        // FCFS does not need any specific comparator, so we pass naturalOrder
        runNonPreemptiveAlgorithm(processes, Comparator.naturalOrder());
    }

    public static void runShortestJobFirst(Collection<Process> processes) {
        // SJF uses burst time to decide the order of execution
        /*
            The queue is sorted by two criteria:
            1. Burst Time: Processes with lower burst time are placed before those with higher burst time.
            2. Order of Arrival: If two processes have the same burst time, the one that arrived earlier (has a lower order value) is placed first.
        */
        runNonPreemptiveAlgorithm(processes, Comparator.comparingInt(ProcessWrapper::getBurstTime).thenComparingLong(ProcessWrapper::getOrder));
    }

    public static void runNonPreemptivePriority(Collection<Process> processes) {
        // Priority scheduling uses process priority to decide the order of execution
        /*
            The queue is sorted by two criteria:
            1. Priority: Processes with higher priority (represented by lower integer values) are placed before those with lower priority.
            2. Order of Arrival: If two processes have the same priority, the one that arrived earlier (has a lower order value) is placed first.
        */
        runNonPreemptiveAlgorithm(processes, Comparator.comparingInt(ProcessWrapper::getPriority).thenComparingLong(ProcessWrapper::getOrder));
    }

    public static void runRoundRobin(Collection<Process> processes, int timeQuantum) {    
        // Convert the collection of processes into a list for easier manipulation.
        List<Process> processesList = new LinkedList<>(processes);

        // Sort the list by arrival time to ensure processes are handled in the order they arrive.
        Collections.sort(processesList);

        // Initialize a list of lists of string to store the rows of the scheduling table.
        List<List<String>> tableRows = new LinkedList<>();

        // Initialize a queue to represent the ready queue.
        Queue<Process> readyQueue = new LinkedList<>();

        // Initialize a map to store the remaining CPU time required for each process.
        HashMap<Integer, Integer> requiredCpuTime = new HashMap<>();

        // Populate the map with the burst time of each process.
        processesList.forEach(process -> requiredCpuTime.put(process.getProcessNumber(), process.getBurstTime()));

        // Initialize variables to calculate the average waiting time and turnaround time.
        double averageWaitingTime = 0;
        double averageTurnaroundTime = 0;

        int currentTime = 0;

        // Continue scheduling while there are still processes in the list or in the ready queue.
        while (!processesList.isEmpty() || !readyQueue.isEmpty()) {
            // Add all processes that have arrived by the current time to the ready queue.
            while (!processesList.isEmpty() && processesList.getFirst().getArrivalTime() <= currentTime) {
                readyQueue.add(processesList.removeFirst());
            }

            // If the ready queue is empty, advance the current time to the arrival time of the next process.
            if (readyQueue.isEmpty()) {
                currentTime = processesList.getFirst().getArrivalTime();
                continue;
            }

            // Dequeue the next process from the ready queue.
            Process runningProcess = readyQueue.poll();
            int runningProcessNumber = runningProcess.getProcessNumber();
            int startExecutionTime = currentTime;

            // Get the remaining CPU time required for the running process.
            int remainingTime = requiredCpuTime.get(runningProcessNumber);

            /*
                If the running process requires more CPU time than the time quantum,
                execute the process for the duration of the time quantum,
                update the remaining CPU time for the process,
                and re-add the process to the ready queue after adding any newly arrived processes.
            */
            if (remainingTime > timeQuantum) {
                currentTime += timeQuantum;

                remainingTime -= timeQuantum;
                requiredCpuTime.replace(runningProcessNumber, remainingTime);


                // Add any processes that have arrived during the execution of the current process to the ready queue.
                while (!processesList.isEmpty() && processesList.getFirst().getArrivalTime() <= currentTime) {
                    readyQueue.add(processesList.removeFirst());
                }

                // Re-add the current process to the ready queue.
                readyQueue.add(runningProcess);

                // Add a row to the scheduling table indicating that the process was preempted.
                String[] row = {Integer.toString(runningProcessNumber), Integer.toString(startExecutionTime),
                        currentTime + "(Preempted)", "(Preempted)", "(Preempted)"};
                tableRows.add(Arrays.asList(row));
            } else {
                /*
                    If the running process requires CPU time less than or equal to the time quantum,
                    execute the process until completion,
                    update the remaining CPU time for the process to zero,
                    and calculate the waiting time and turnaround time for the process.
                */
                currentTime += remainingTime;

                remainingTime = 0;
                requiredCpuTime.replace(runningProcessNumber, remainingTime);

                int turnaroundTime = currentTime - runningProcess.getArrivalTime();
                averageTurnaroundTime += turnaroundTime;

                int waitingTime = turnaroundTime - runningProcess.getBurstTime();
                averageWaitingTime += waitingTime;

                // Add a row to the scheduling table indicating that the process has completed.
                String[] row = {Integer.toString(runningProcessNumber), Integer.toString(startExecutionTime),
                        Integer.toString(currentTime), Integer.toString(waitingTime), Integer.toString(turnaroundTime)};

                tableRows.add(Arrays.asList(row));
            }
        }

        int numberOfProcesses = processes.size();
        averageWaitingTime /= numberOfProcesses;
        averageTurnaroundTime /= numberOfProcesses;

        // Print the scheduling table and the average times
        System.out.printf(tableGenerator.generateTable(Arrays.asList(tableHeaders), tableRows) +
                "\nAverage waiting time %f\nAverage turnaround time %f", averageWaitingTime, averageTurnaroundTime);
    }

    public static void runPreemptivePriority(Collection<Process> processes) {
        // Convert the collection of processes into a list for easier manipulation.
        List<Process> processesList = new LinkedList<>(processes);

        // Sort the list by arrival time to ensure processes are handled in the order they arrive.
        Collections.sort(processesList);

        // Initialize a list of lists of string to store the rows of the scheduling table.
        List<List<String>> tableRows = new LinkedList<>();

        /*
            Initialize a priority queue to represent the ready queue for the preemptive priority scheduling algorithm.
            The queue is sorted by two criteria:
                1. Priority: Processes with higher priority (represented by lower integer values) are placed before those with lower priority.
                2. Order of Arrival: If two processes have the same priority, the one that arrived earlier (has a lower order value) is placed first.

            The ProcessWrapper class is used to wrap the Process objects and add an order field to them.
            This order field is used to ensure the stability of the priority queue.

            The counter variable in the ProcessWrapper class is a static variable that increments each time a ProcessWrapper object is created.
            This ensures that each ProcessWrapper object has a unique order value.
        */

        var readyQueueComparator = Comparator.comparingInt(ProcessWrapper::getPriority).thenComparingLong(ProcessWrapper::getOrder);
        PriorityQueue<ProcessWrapper> readyQueue = new PriorityQueue<>(readyQueueComparator);

        // Initialize a map to store the remaining CPU time required for each process.
        Map<Integer, Integer> requiredCpuTime = new HashMap<>();

        // Populate the map with the burst time of each process.
        processesList.forEach(process -> requiredCpuTime.put(process.getProcessNumber(), process.getBurstTime()));

        // Initialize variables for calculating the average waiting time and turnaround time
        double averageWaitingTime = 0;
        double averageTurnAroundTime = 0;

        int currentTime = 0;

        // Initialize the currently running process, its process number, and its start execution time
        ProcessWrapper runningProcessWrapper = null;
        Process runningProcess = null;
        int runningProcessNumber = 0;
        int startExecutionTime = 0;

        // Continue scheduling while there are still processes in the list, in the ready queue, or currently running a process.
        while (!processesList.isEmpty() || !readyQueue.isEmpty() || runningProcess != null) {

            // Add all processes that have arrived by the current time to the ready queue.
            while (!processesList.isEmpty() && processesList.getFirst().getArrivalTime() <= currentTime) {
                readyQueue.add(new ProcessWrapper(processesList.removeFirst()));
            }

            // If the ready queue is empty and there is no running process, advance the current time to the arrival time of the next process.
            if (readyQueue.isEmpty() && runningProcess == null) {
                currentTime = processesList.getFirst().getArrivalTime();
                continue;
            }

            // If there is a running process, check if it should be preempted by a process in the ready queue with a higher priority.
            if (runningProcess != null && !readyQueue.isEmpty() && runningProcess.getPriority() > readyQueue.peek().getPriority()) {
                
                // Create a row for the scheduling table to indicate that the running process was preempted.
                String[] row = {Integer.toString(runningProcessNumber), Integer.toString(startExecutionTime),
                        currentTime + "(Preempted)", "(Preempted)", "(Preempted)"};

                tableRows.add(Arrays.asList(row));

                // Add the preempted process back to the ready queue for future execution.
                readyQueue.add(runningProcessWrapper);

                // Reset the running process to null since it was preempted and the next process with higher priority will be dequeued in the next block.
                runningProcess = null;
            }

            // If there is no running process, take one from the ready queue
            if (runningProcess == null && !readyQueue.isEmpty()) {
                runningProcessWrapper = readyQueue.poll();
                runningProcess = runningProcessWrapper.getProcess();
                runningProcessNumber = runningProcess.getProcessNumber();
                startExecutionTime = currentTime;
            }

            // Execute the running process for one time unit.
            currentTime++;

            // Update the remaining CPU time required for the running process.
            int remainingTime = requiredCpuTime.get(runningProcessNumber) - 1;

            requiredCpuTime.replace(runningProcessNumber, remainingTime);

            // If the running process has completed, calculate its waiting time and turnaround time
            if (remainingTime == 0) {
                int turnAroundTime = currentTime - runningProcess.getArrivalTime();
                averageTurnAroundTime += turnAroundTime;

                int waitingTime = turnAroundTime - runningProcess.getBurstTime();
                averageWaitingTime += waitingTime;

                // Add a row to the scheduling table indicating that the process has completed.
                String[] row = {Integer.toString(runningProcessNumber), Integer.toString(startExecutionTime),
                        Integer.toString(currentTime), Integer.toString(waitingTime), Integer.toString(turnAroundTime)};

                tableRows.add(Arrays.asList(row));

                // Reset the running process to null for the next iteration.
                runningProcess = null;
            }
        }

        int numberOfProcesses = processes.size();

        averageWaitingTime /= numberOfProcesses;
        averageTurnAroundTime /= numberOfProcesses;

        // Print the scheduling table and the average times
        System.out.printf(tableGenerator.generateTable(Arrays.asList(tableHeaders), tableRows) +
                "\nAverage waiting time %f\nAverage turnaround time %f", averageWaitingTime, averageTurnAroundTime);
    }

    private static void runNonPreemptiveAlgorithm(Collection<Process> processes, Comparator<ProcessWrapper> readyQueueComparator) {
        // Convert the collection of processes into a list for easier manipulation.
        List<Process> processesList = new LinkedList<>(processes);

        // Sort the list by arrival time to ensure processes are handled in the order they arrive.
        Collections.sort(processesList);

        // Initialize a list of lists of string to store the rows of the scheduling table.
        List<List<String>> tableRows = new LinkedList<>();

        // Create a priority queue for the ready queue, with the provided comparator
        var readyQueue = new PriorityQueue<>(readyQueueComparator);

        // Initialize variables for calculating the average waiting time and turnaround time
        double averageWaitingTime = 0;
        double averageTurnaroundTime = 0;

        int currentTime = 0;

        // Continue scheduling while there are still processes in the list or in the ready queue.
        while (!processesList.isEmpty() || !readyQueue.isEmpty()) {

            // Add all processes that have arrived by the current time to the ready queue.
            while (!processesList.isEmpty() && processesList.getFirst().getArrivalTime() <= currentTime) {
                readyQueue.add(new ProcessWrapper(processesList.removeFirst()));
            }

            // If the ready queue is empty, advance the current time to the arrival time of the next process.
            if (readyQueue.isEmpty()) {
                currentTime = processesList.getFirst().getArrivalTime();
                continue;
            }

            // Get the next process to run from the ready queue
            Process runningProcess = readyQueue.poll().getProcess();

            // Calculate start and completion times
            String startExecutionTime = Integer.toString(currentTime);
            currentTime += runningProcess.getBurstTime();
            String completionTime = Integer.toString(currentTime);

            // Calculate turnaround and waiting times
            int turnaroundTime = currentTime - runningProcess.getArrivalTime();
            averageTurnaroundTime += turnaroundTime;
            int waitingTime = turnaroundTime - runningProcess.getBurstTime();
            averageWaitingTime += waitingTime;

            // Add a row to the scheduling table
            String[] row = {Integer.toString(runningProcess.getProcessNumber()), startExecutionTime,
                    completionTime, Integer.toString(waitingTime), Integer.toString(turnaroundTime)};

            tableRows.add(Arrays.asList(row));
        }

        int numberOfProcesses = processes.size();

        // Calculate average waiting and turnaround times
        averageWaitingTime /= numberOfProcesses;
        averageTurnaroundTime /= numberOfProcesses;

        // Print the scheduling table and the average times
        System.out.printf(tableGenerator.generateTable(Arrays.asList(tableHeaders), tableRows) +
                "\nAverage waiting time %f\nAverage turnaround time %f", averageWaitingTime, averageTurnaroundTime);
    }
}