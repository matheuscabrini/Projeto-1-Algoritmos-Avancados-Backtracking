package matheuscabrini;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	private static int nodes;
	
	private static char algorithm;
	private static boolean flagForwardChecking = false;
	private static boolean flagMRV = false;
	private static boolean flagDegree = false;
	
	private static ArrayList<Region> regions;
	private static Graph graph;
	
	// Habilitando-se o testMode, escreve-se na tela o n�mero de atribui��es e tempo 
	// passado durante a colora��o, al�m da verifica��o de corretude da solu��o encontrada.
	private static boolean testMode = false;
	
	public static void main(String[] args) {
		
		//testMode = true;
		
		parseInput(new Scanner(System.in));
		
		long startTime = 0, endTime = 0;
		if (testMode) startTime = System.nanoTime();
		
		graph.coloring(regions, flagForwardChecking, flagMRV, flagDegree);
		
		if (testMode) endTime = System.nanoTime();
		if (testMode) System.err.println("Coloring time: " + (double)(endTime-startTime)/1000000 + " ms");
		if (testMode) System.err.println("Number of color attributions: " + graph.getAttributions());
		
		printOutput();
	}
	
	/*
	 * L� o input de stdin da forma especificada no projeto. Recebe o n�mero de regi�es,
	 * modo de heur�stica escolhido e as vizinhan�as entre as regi�es. Logo, constr�i o grafo.
	 */
	private static void parseInput(Scanner input) {
		Scanner line = new Scanner(input.nextLine());
		
		nodes = line.nextInt();
		regions = new ArrayList<Region>(nodes);
		graph = new Graph(nodes);
		
		algorithm = (char) line.next().charAt(0);
		
		if (algorithm == 'b' || algorithm == 'c' || algorithm == 'd')
			flagForwardChecking = true;
		if (algorithm == 'c' || algorithm == 'd')
			flagMRV = true;
		if (algorithm == 'd')
			flagDegree = true;
		
		for (int i = 0; i < nodes; i++) {
			line = new Scanner(input.nextLine());
			line.useDelimiter("[:,.]+");
			
			String regionName = line.next();
			Region reg = new Region(regionName);
			if (!regions.contains(reg)) {
				regions.add(reg);
				reg.index = regions.indexOf(reg);
			}
							
			while (line.hasNext()) {
				String neighborName = line.next().substring(1); // substring retira o espa�o antes do nome
				Region neighbor = new Region(neighborName);
				if (!regions.contains(neighbor)) {
					regions.add(neighbor);
					neighbor.index = regions.indexOf(neighbor);
				}				
				graph.addLink(regions.indexOf(reg), regions.indexOf(neighbor));
			}
		}
		
		line.close();
		input.close();
	}
	
	/*
	 * Imprime na tela a solu��o encontrada, de acordo com a 
	 * forma estabelecida no projeto (<regi�o>: <cor pintada>.)
	 */
	private static void printOutput() {		
		if (testMode) graph.assertSolution();
		
		regions.sort(null);
		for (Region r : regions) {
			System.out.println(r);
		}
	}
}
