package matheuscabrini;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {
	
	private boolean[][] matrix;
	private int nodes;
	
	private ArrayList<Region> regions = null;
	private PriorityQueue<Region> queueMRV = null;
	private int coloredNodes = 0;
	private int attributionsCounter = 0;
	private boolean flagForwardCheck = false;
	private boolean flagMRV = false;
	private boolean flagDegree = false;
	
	/*
	 * Construtor do grafo de restrições. Usa-se uma matriz de adjacências.
	 */
	public Graph(int nodes) {
		this.nodes = nodes;
		
		matrix = new boolean[nodes][nodes];
		for (int i = 0; i < nodes; i++) 
			for (int j = 0; j < nodes; j++) 
				matrix[i][j] = false;
	}
	
	/*
	 * Adiciona ao grafo, simetricamente, uma aresta entre src e dst.
	 */
	public void addLink(int src, int dst) {
		matrix[src][dst] = true;
		matrix[dst][src] = true;
	}
	
	/*
	 * Verifica a validade da solução encontrada para o algoritmo de coloring.
	 * Se houver alguma invalidade, imprime-se na tela a região de cor conflitante.
	 */
	public void assertSolution() {
		boolean ok = true;
		for (int i = 0; i < matrix.length; i++) {
			if (!colorIsValid(i, regions.get(i).color)) {
				System.err.println("Color conflict with neighbor of " + regions.get(i).name);
				ok = false;
			}
		}
		if (ok) System.err.println("Colors are OK!!!");
	}
	
	/*
	 * Retorna o número de atribuições de cor feitas durante o algoritmo.
	 */
	public int getAttributions() {
		return attributionsCounter;
	}
	
	/*
	 * Chamada inicial para o algoritmo de coloração.
	 */
	public boolean coloring(ArrayList<Region> regions, 
			boolean forwardCheck, boolean MRV, boolean degree) {
		
		this.regions = regions;
		this.flagForwardCheck = forwardCheck;
		this.flagMRV = MRV;
		this.flagDegree = degree;
		
		int src = ((flagMRV) ? getMRVNode() : 0);		
		
		if (coloringUtil(src) == true) return true;
		else return false;
	}
	
	/*
	 * Retorna true se nenhum dos nós vizinhos a src 
	 * tiver a cor color; false, caso contrário.
	 */
	private boolean colorIsValid(int src, byte color) {
		for (int i = 0; i < nodes; i++) {
			if (matrix[src][i] && color == regions.get(i).color)
				return false;
		}
		return true;
	}
	
	/*
	 * Atualiza o dominio das regiões vizinhas a src, o qual acabou de 
	 * ter sido colorido ou descolorido. No primeiro caso, a flag remove
	 * deve ser true, pois srcColor será removido do dominio dos vizinhos.
	 * Caso contrário, remove deve ser false e a cor será validada de volta no
	 * dominio das variáveis vizinhas, desde que nenhum dos vizinhos dos vizinhos 
	 * tenha tal cor (isto é assegurado pelo método colorIsValid())
	 */
	private void updateNeighborDomains(int src, byte srcColor, boolean remove) {
		for (int i = 0; i < nodes; i++) {
			if (matrix[src][i] && regions.get(i).color == Color.UNCOLORED) {
				if (remove)
					regions.get(i).removeColorFromDomain(srcColor);
				else if (colorIsValid(i, srcColor))
					regions.get(i).restoreColorToDomain(srcColor);
				else 
					continue;
				
				// Retiramos a região atualizada e adicionamos ela de novo na
				// fila de prioridades, a fim de atualizar também a fila
				if (flagMRV) {
					if (queueMRV.remove(regions.get(i)) == true)
						queueMRV.add(regions.get(i));
				}
			}
		}
	}
	
	/*
	 * Para a verificação adiante: retorna true se todas as regiões
	 * ainda não coloridas possuem alguma cor possível; false, caso contrário.
	 */
	private boolean domainsAreValid() {
		for (Region reg : regions) {
			if (reg.color == Color.UNCOLORED && reg.domainIsEmpty())
				return false;
		}
		return true;
	}
	
	/*
	 * Retorna o índice da região com mínimos valores remanescentes em seu dominio.
	 * Utiliza-se uma fila de prioridades contendo as regiões. A fila
	 * retorna o elemento com menor número de valores possíveis em seu
	 * domínio. Para desempatar, pode-se usar o grau da região. Se o empate 
	 * permanecer, qualquer das duas regiões empatadas serve.
	 */
	private int getMRVNode() {
		// Inicializamos a fila, se ela ainda não existir, com seu algoritmo
		// de comparação e adicionamos todas as regiões nela.
		if (queueMRV == null) {
			queueMRV = new PriorityQueue<Region>(nodes, new Comparator<Region>() {
				@Override public int compare(Region r1, Region r2) {
					int domainDiff = r1.remainingDomain() - r2.remainingDomain();
					if (domainDiff < 0) return -1;
					if (domainDiff > 0) return 1;
					if (flagDegree && getDegree(r1) > getDegree(r2)) return -1;
					else return 1;
				}
			});
			
			for (Region reg : regions) queueMRV.add(reg);
		}
		
		return queueMRV.remove().index;
	}
	
	/*
	 * Retorna o grau (número de vizinhos) do vértice src.
	 * Se este valor já foi calculado antes, será obtido da
	 * region correspondente a src. Senão, ele é calculado
	 * e então armazenado na region correspondente.
	 */
	private int getDegree(Region srcReg) {
		if (srcReg.getDegree() != null)
			return srcReg.getDegree();
		
		int degree = 0;
		for (int i = 0; i < nodes; i++) {
			if (matrix[srcReg.index][i]) degree++;
		}
		srcReg.setDegree(degree);
		return degree;
	}
	
	/*
	 * Algoritmo recursivo com backtracking para coloração de regiões 
	 * representadas em um grafo. De acordo com as flags setadas,
	 * pode-se usar as heurísticas: verificação adiante, escolha de região por 
	 * mínimos valores remanescentes e desempate por grau.
	 */
	private boolean coloringUtil(int src) {
		// Testando as possíveis cores e vendo qual dá certo:
		for (byte c = 1; c <= Color.MAX_COLORS; c++) {
			if (colorIsValid(src, c)) {
				regions.get(src).color = c;
				attributionsCounter++;
				coloredNodes++;
				
				// Caso em que chegou-se na solução: todas as regiões foram coloridas.
				if (coloredNodes == nodes) 
					return true;
				
				// Atualizar o dominio dos vizinhos de src, de acordo com a cor atribuida a src
				if (flagForwardCheck || flagMRV) updateNeighborDomains(src, c, true);
				
				// Checando se alguma região não colorida ficou sem cor válida em seu dominio
				if (flagForwardCheck && !domainsAreValid()) {
					regions.get(src).color = Color.UNCOLORED;
					coloredNodes--;
					
					// Atualizar o dominio dos vizinhos, de acordo com a cor retirada de src
					if (flagForwardCheck || flagMRV) updateNeighborDomains(src, c, false);
					
					return false;
				}
				
				// A próxima região a colorir será a de MVR ou simplesmente a do próximo índice
				int nextSrc = ((flagMRV) ? getMRVNode() : src + 1);
				
				if (coloringUtil(nextSrc) == true)
					return true;
				else {
					regions.get(src).color = Color.UNCOLORED;
					coloredNodes--;
					
					// Atualizar o dominio dos vizinhos, de acordo com a cor retirada de src
					if (flagForwardCheck || flagMRV) updateNeighborDomains(src, c, false);
					
					// Recolocando nextSrc na fila de prioridades
					if (flagMRV) queueMRV.add(regions.get(nextSrc));
				}
			}	
		}
		return false;
	}
}
