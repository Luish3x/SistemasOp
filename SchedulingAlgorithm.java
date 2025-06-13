// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;
import java.util.Collections;

public class SchedulingAlgorithm {

	// funcion que encuentra el siguiente indice
	public static int getNextProcessIndex(Vector processVector, int currentIndex) {
		int size = processVector.size();
		for (int offset = 1; offset <= size; offset++) {
			int nextIndex = (currentIndex + offset) % size;
			sProcess nextProcess = (sProcess) processVector.elementAt(nextIndex);
			if (nextProcess.cpudone < nextProcess.cputime) {
				return nextIndex;
			}
		}
		return currentIndex;
	}

	// optiene la mitad del vector
	public static int[] obtenerIndicesCentrales(Vector<Integer> vector) {
        int size = vector.size();
        int mitad = size / 2;

        if (size % 2 == 0) {
            // Tamaño PAR: mitad-1 y mitad
            return new int[]{mitad - 1, mitad};
        } else {
            // Tamaño IMPAR: mitad y mitad+1
            return new int[]{mitad, mitad + 1};
        }
    }

	// funcion que calcula el promedio para despues ser usada como un quantum
	public static int quantumIO(Vector processVector) {
		int numProcesos = processVector.size();
		Vector <Integer> vectorOrdenado = new Vector <Integer>();

		for (int i = 0; i < numProcesos; i++) {
			sProcess process = (sProcess) processVector.elementAt(i);
			vectorOrdenado.add(process.ioblocking);
		}

		Collections.sort(vectorOrdenado);
		int[] centro = obtenerIndicesCentrales(vectorOrdenado);
		int resultado = vectorOrdenado.get(centro[0]) + vectorOrdenado.get(centro[1]);

		return resultado / 2;
	}


    public static Results Run(int runtime, Vector processVector, Results result) {
        int comptime = 0;
        int currentProcess = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";

		int quantum = quantumIO(processVector);

        result.schedulingType = "Preemptive";
        result.schedulingName = "Round Robin";
        result.setQuantum(quantum);

        try {
            // BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            // OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            sProcess process = (sProcess) processVector.elementAt(currentProcess);

			// Se registra el primer proceso
            out.println("Process: " + currentProcess + " registered... (" +
                        process.cputime + " " + process.ioblocking + " " +
                        process.cpudone + ")");

            while (comptime < runtime) {

				// Cuando el proceso termina
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + currentProcess + " completed... (" +
                                (process.cputime - process.cpudone) + " " + process.ioblocking + " " +
                                process.cpudone + ")");

                    if (completed == size) {
                        result.compuTime = comptime;
							out.println("Este es el fin");
                        out.close();
                        return result;
                    }

					currentProcess = getNextProcessIndex(processVector, currentProcess);
                    process = (sProcess) processVector.elementAt(currentProcess);
                    out.println("Process: " + currentProcess + " registered... (" +
                                (process.cputime - process.cpudone) + " " + process.ioblocking + " " +
                                process.cpudone + ")");
                }

				// El proceso se bloquea

				if (quantum > process.ioblocking) {
					// Se ejecuta con quantum
					out.println("Process: " + currentProcess + " ejecutando con QUANTUM de " + quantum + " unidades.");

					int steps = 0;
					while (steps < quantum && process.cpudone < process.cputime) {
						process.cpudone++;
						if (process.ioblocking > 0) {
							process.ionext++;
							if (process.ionext == quantum) {
								out.println("Process: " + currentProcess + " I/O blocked durante quantum... (" +
											(process.cputime - process.cpudone) + " " + process.ioblocking + " " +
											process.cpudone + ")");
								process.numblocked++;
								process.ionext = 0;

								currentProcess = getNextProcessIndex(processVector, currentProcess);
								process = (sProcess) processVector.elementAt(currentProcess);
								out.println("Process: " + currentProcess + " registered... (" +
											(process.cputime - process.cpudone) + " " + process.ioblocking + " " +
											process.cpudone + ")");
								break;
							}
						}
						steps++;
						comptime++;
					}
				} else {
					// Se mantiene la lógica original de bloqueo por I/O
					if (process.ioblocking == process.ionext) {
						out.println("Process: " + currentProcess + " I/O blocked... (" +
									(process.cputime - process.cpudone) + " " + process.ioblocking + " " +
									process.cpudone + ")");
						process.numblocked++;
						process.ionext = 0;

						currentProcess = getNextProcessIndex(processVector, currentProcess);
						process = (sProcess) processVector.elementAt(currentProcess);
						out.println("Process: " + currentProcess + " registered... (" +
									(process.cputime - process.cpudone) + " " + process.ioblocking + " " +
									process.cpudone + ")");
					}

					process.cpudone++;
					if (process.ioblocking > 0) {
						process.ionext++;
					}

					comptime++;
				}
			}


            out.close();

        } catch (IOException e) {
            // Handle exceptions
        }


        result.compuTime = comptime;
        return result;
    }
}
