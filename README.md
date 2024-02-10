# CPU-Scheduling-Algorithms

## Description
- This repository contains an implementation of various CPU scheduling algorithms in Java. The algorithms implemented include First Come First Serve (FCFS), Shortest Job First (SJF), Non-Preemptive Priority, Round Robin, and Preemptive Priority. These algorithms are fundamental to understanding how process scheduling works in operating systems. The program calculates and displays key metrics like waiting time and turn-around time for each process, providing a clear comparison of the efficiency of each algorithm. The results are displayed in a table format, with each row representing a process and its execution details. The columns in the table represent the "Process Number", "Start Execution Time", "Completion Time", "Waiting Time", and "Turn Around Time". This tabular representation provides a clear and concise view of the execution order and timing details of the processes.

## Components

### Process
- The `Process` class represents a process with attributes such as process number, arrival time, burst time, and priority. It implements the Comparable interface to allow processes to be sorted based on their arrival time.

### ProcessWrapper
- The `ProcessWrapper` class is a wrapper for the `Process` class that includes an order attribute. This attribute is used to preserve the order of processes with the same priority or burst time when they are sorted.

### Scheduler
- The `Scheduler` class provides static methods to run different scheduling algorithms including First Come First Serve (FCFS), Shortest Job First (SJF), Non-Preemptive Priority, Round Robin, and Preemptive Priority. Each algorithm is implemented with a focus on calculating and displaying key metrics such as start execution time, completion time, waiting time, and turnaround time. This class is where the main logic of the scheduling algorithms is implemented.

### TableGenerator
- The `TableGenerator` class is a utility class that generates a formatted table string from a list of headers and rows. This is used to display the scheduling results in a neat, tabular format.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or later

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/MahmoudAhmed184/Java-Scheduling-Algorithms.git
2. Open the project in your preferred Java IDE

### Usage
- To run a scheduling algorithm, create a collection of Process objects and pass it to the corresponding method in the Scheduler class. For example, to run the FCFS algorithm:

  ```java
  Collection<Process> processes = new ArrayList<>();
  processes.add(new Process(1, 0, 5, 1));
  processes.add(new Process(2, 1, 3, 2));
  Scheduler.runFirstComeFirstServe(processes);