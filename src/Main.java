import java.util.Arrays;

import Scheduling.Process;
import Scheduling.Scheduler;

public class Main {
    public static void main(String[] args) {
        Process[][] testcases = {
                // Test cases with processes that have the same arrival time.
                {
                        new Process(1, 0, 24, 0),
                        new Process(2, 0, 3, 0),
                        new Process(3, 0, 3, 0),
                },
                // Same example as above but with a different order of processes to check the stability of the scheduling algorithm.
                {
                        new Process(2, 0, 3, 0),
                        new Process(3, 0, 3, 0),
                        new Process(1, 0, 24, 0),
                },
                {
                        new Process(1, 0, 6, 0),
                        new Process(2, 0, 8, 0),
                        new Process(3, 0, 7, 0),
                        new Process(4, 0, 3, 0),
                },
                {
                        new Process(1, 0, 10, 3),
                        new Process(2, 0, 1, 1),
                        new Process(3, 0, 2, 4),
                        new Process(4, 0, 1, 5),
                        new Process(5, 0, 5, 2),
                },
                {
                        new Process(1, 0, 53, 0),
                        new Process(2, 0, 17, 0),
                        new Process(3, 0, 68, 0),
                        new Process(4, 0, 24, 0),
                },
                // Test cases with processes that have different arrival times.
                {
                        new Process(1, 0, 8, 0),
                        new Process(2, 1, 4, 0),
                        new Process(3, 2, 9, 0),
                        new Process(4, 3, 5, 0),
                },
                {
                        new Process(1, 0, 7, 0),
                        new Process(2, 2, 4, 0),
                        new Process(3, 4, 1, 0),
                        new Process(4, 5, 4, 0),
                },
                {
                        new Process(1, 2, 1, 0),
                        new Process(2, 1, 5, 0),
                        new Process(3, 4, 1, 0),
                        new Process(4, 0, 6, 0),
                        new Process(5, 2, 3, 0),
                },
                {
                        new Process(1, 0, 8, 3),
                        new Process(2, 1, 2, 4),
                        new Process(3, 3, 4, 4),
                        new Process(4, 4, 1, 5),
                        new Process(5, 5, 6, 2),
                        new Process(6, 6, 5, 6),
                        new Process(7, 10, 1, 1),
                },
                {
                        new Process(1, 0, 8, 0),
                        new Process(2, 5, 2, 0),
                        new Process(3, 1, 7, 0),
                        new Process(4, 6, 3, 0),
                        new Process(5, 8, 5, 0),
                },
                {
                        new Process(1, 0, 3, 5),
                        new Process(2, 1, 7, 3),
                        new Process(3, 4, 2, 4),
                        new Process(4, 2, 3, 3),
                        new Process(5, 6, 4, 1),
                },
                /*  
                    Test case where the CPU has idle times between process scheduling.
                    This can occur when the arrival times of the processes are not continuous,
                    causing the CPU to be idle until the next process arrives.
                */
                {
                        new Process(1, 2, 2, 0),
                        new Process(2, 0, 1, 0),
                        new Process(3, 2, 3, 0),
                        new Process(4, 3, 5, 0),
                        new Process(5, 4, 4, 0),
                },
        };
        Scheduler.runRoundRobin(Arrays.asList(testcases[11]), 3);
    }
}