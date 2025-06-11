// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {


	public static int getNextProcessIndex(Vector processVector, int currentIndex) {
		int size = processVector.size();
		for (int offset = 1; offset <= size; offset++) {
			int nextIndex = (currentIndex + offset) % size;
			sProcess nextProcess = (sProcess) processVector.elementAt(nextIndex);
			if (nextProcess.cpudone < nextProcess.cputime) {
				return nextIndex;
			}
		}
		return currentIndex; // si todos terminaron (debería haberse detectado antes)
	}

	public static double quantumIO(Vector processVector) {
		double totalIO = 0.0;
		int numProcesos = processVector.size();

		for (int i = 0; i < numProcesos; i++) {
			sProcess process = (sProcess) processVector.elementAt(i);
			totalIO += process.numblocked;
		}
		return (double) totalIO / numProcesos;
	}


    public static Results Run(int runtime, Vector processVector, Results result) {
        int i = 0;
        int comptime = 0;
        int currentProcess = 0;
        int previousProcess = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";
		//int quantum = (int) Math.ceil(quantumIO(processVector));
		int quantum = 100;
		int quantumCounter = 0;

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "First-Come First-Served";

        try {
            // BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            // OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            sProcess process = (sProcess) processVector.elementAt(currentProcess);

            out.println("Process: " + currentProcess + " registered... (" +
                        process.cputime + " " + process.ioblocking + " " +
                        process.cpudone + " " + process.cpudone + ")");

            while (comptime < runtime) {

				// Cuando el proceso termina
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + currentProcess + " completed... (" +
                                process.cputime + " " + process.ioblocking + " " +
                                process.cpudone + " " + process.cpudone + ")");

                    if (completed == size) {
                        result.compuTime = comptime;
							out.println("Este es el fin");
                        out.close();
                        return result;
                    }

					currentProcess = getNextProcessIndex(processVector, currentProcess);
					quantumCounter = 0;
                    process = (sProcess) processVector.elementAt(currentProcess);
                    out.println("Process: " + currentProcess + " registered... (" +
                                process.cputime + " " + process.ioblocking + " " +
                                process.cpudone + " " + process.cpudone + ")");
                }

				// El proceso se bloquea
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + currentProcess + " I/O blocked... (" +
                                process.cputime + " " + process.ioblocking + " " +
                                process.cpudone + " " + process.cpudone + ")");
                    process.numblocked++;
                    process.ionext = 0;
                    previousProcess = currentProcess;

					currentProcess = getNextProcessIndex(processVector, currentProcess);
					quantumCounter = 0;
                    process = (sProcess) processVector.elementAt(currentProcess);
                    out.println("Process: " + currentProcess + " registered... (" +
                                process.cputime + " " + process.ioblocking + " " +
                                process.cpudone + " " + process.cpudone + ")");
                }

				quantumCounter++;

				if (quantumCounter == quantum) {
					previousProcess = currentProcess;
					currentProcess = getNextProcessIndex(processVector, currentProcess);
					process = (sProcess) processVector.elementAt(currentProcess);
					out.println("Process: " + currentProcess + " (por quantum) registrado... (" +
								process.cputime + " " + process.ioblocking + " " + 
								process.cpudone + " " + process.cpudone + ")");
					quantumCounter = 0;
				}

                process.cpudone++;
                if (process.ioblocking > 0) {
                    process.ionext++;
                }

                comptime++;
            }


            out.close();

        } catch (IOException e) {
            // Handle exceptions
        }


        result.compuTime = comptime;
        return result;
    }
}
