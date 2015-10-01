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
	 * Construtor do grafo de restri��es. Usa-se uma matriz de adjac�ncias.
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
	 * Verifica a validade da solu��o encontrada para o algoritmo de coloring.
	 * Se houver alguma invalidade, imprime-se na tela a regi�o de cor conflitante.
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
	 * Retorna o n�mero de atribui��es de cor feitas durante o algoritmo.
	 */
	public int getAttributions() {
		return attributionsCounter;
	}
	
	/*
	 * Chamada inicial para o algoritmo de colora��o.
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
	 * Retorna true se nenhum dos n�s vizinhos a src 
	 * tiver a cor color; false, caso contr�rio.
	 */
	private boolean colorIsValid(int src, byte color) {
		for (int i = 0; i < nodes; i++) {
			if (matrix[src][i] && color == regions.get(i).color)
				return false;
		}
		return true;
	}
	
	/*
	 * Atualiza o dominio das regi�es vizinhas a src, o qual acabou de 
	 * ter sido colorido ou descolorido. No primeiro caso, a flag remove
	 * deve ser true, pois srcColor ser� removido do dominio dos vizinhos.
	 * Caso contr�rio, remove deve ser false e a cor ser� validada de volta no
	 * dominio das vari�veis vizinhas, desde que nenhum dos vizinhos dos vizinhos 
	 * tenha tal cor (isto � assegurado pelo m�todo colorIsValid())
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
				
				// Retiramos a regi�o atualizada e adicionamos ela de novo na
				// fila de prioridades, a fim de atualizar tamb�m a fila
				if (flagMRV) {
					if (queueMRV.remove(regions.get(i)) == true)
						queueMRV.add(regions.get(i));
				}
			}
		}
	}
	
	/*
	 * Para a verifica��o adiante: retorna true se todas as regi�es
	 * ainda n�o coloridas possuem alguma cor poss�vel; false, caso contr�rio.
	 */
	private boolean domainsAreValid() {
		for (Region reg : regions) {
			if (reg.color == Color.UNCOLORED && reg.domainIsEmpty())
				return false;
		}
		return true;
	}
	
	/*
	 * Retorna o �ndice da regi�o com m�nimos valores remanescentes em seu dominio.
	 * Utiliza-se uma fila de prioridades contendo as regi�es. A fila
	 * retorna o elemento com menor n�mero de valores poss�veis em seu
	 * dom�nio. Para desempatar, pode-se usar o grau da regi�o. Se o empate 
	 * permanecer, qualquer das duas regi�es empatadas serve.
	 */
	private int getMRVNode() {
		// Inicializamos a fila, se ela ainda n�o existir, com seu algoritmo
		// de compara��o e adicionamos todas as regi�es nela.
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
	 * Retorna o grau (n�mero de vizinhos) do v�rtice src.
	 * Se este valor j� foi calculado antes, ser� obtido da
	 * region correspondente a src. Sen�o, ele � calculado
	 * e ent�o armazenado na region correspondente.
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
	 * Algoritmo recursivo com backtracking para colora��o de regi�es 
	 * representadas em um grafo. De acordo com as flags setadas,
	 * pode-se usar as heur�sticas: verifica��o adiante, escolha de regi�o por 
	 * m�nimos valores remanescentes e desempate por grau.
	 */
	private boolean coloringUtil(int src) {
		// Testando as poss�veis cores e vendo qual d� certo:
		for (byte c = 1; c <= Color.MAX_COLORS; c++) {
			if (colorIsValid(src, c)) {
				regions.get(src).color = c;
				attributionsCounter++;
				coloredNodes++;
				
				// Caso em que chegou-se na solu��o: todas as regi�es foram coloridas.
				if (coloredNodes == nodes) 
					return true;
				
				// Atualizar o dominio dos vizinhos de src, de acordo com a cor atribuida a src
				if (flagForwardCheck || flagMRV) updateNeighborDomains(src, c, true);
				
				// Checando se alguma regi�o n�o colorida ficou sem cor v�lida em seu dominio
				if (flagForwardCheck && !domainsAreValid()) {
					regions.get(src).color = Color.UNCOLORED;
					coloredNodes--;
					
					// Atualizar o dominio dos vizinhos, de acordo com a cor retirada de src
					if (flagForwardCheck || flagMRV) updateNeighborDomains(src, c, false);
					
					return false;
				}
				
				// A pr�xima regi�o a colorir ser� a de MVR ou simplesmente a do pr�ximo �ndice
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
