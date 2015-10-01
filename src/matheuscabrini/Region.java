package matheuscabrini;

public class Region implements Comparable<Region> {
	
	public String name = null;
	public byte color;
	public int index;
	
	// Dom�nio (poss�veis cores v�lidas) da vari�vel (regi�o), para a 
	// verifica��o adiante e MRV. True = cor ainda � v�lida, false = cor inv�lida
	private boolean[] domain;
	
	// Grau, ou n�mero de vizinhos da regi�o, que pode vir a ser requisitado
	// e calculado durante o algoritmo de coloring.
	private Integer degree; 
	
	/*
	 * Construtor da regi�o. Inicializa-se com nome, sem cor e com 
	 * todos os valores do dom�nio como v�lidos.
	 */
	public Region(String name) {
		this.name = name;
		color = Color.UNCOLORED;
		
		domain = new boolean[Color.MAX_COLORS+1];
		for (int i = 1; i < domain.length; i++)
			domain[i] = true;
	}
	
	/*
	 * Retorna o n�mero de valores v�lidos no dominio de cores desta regi�o.
	 */
	public int remainingDomain() {
		int counter = 0;
		for (int i = 1; i < domain.length; i++) {
			if (domain[i] == true) counter++;
		}
		return counter;
	}
	
	/*
	 * Informa se n�o h� mais cores poss�veis para esta regi�o.
	 */
	public boolean domainIsEmpty() {
		if (this.remainingDomain() == 0) return true;
		else return false;
	}
	
	/*
	 * Marca, no dominio desta regi�o, que a cor color est� inv�lida.
	 */
	public void removeColorFromDomain(byte color) {
		domain[color] = false;
	}
	
	/*
	 * Marca, no dominio desta regi�o, que a cor color est� v�lida.
	 */
	public void restoreColorToDomain(byte color) {
		domain[color] = true;
	}
	
	public void setDegree(int degree) {
		this.degree = new Integer(degree);
	}
	
	public Integer getDegree() {
		return degree;
	}

	@Override
	public String toString() {
		return this.name + ": " + Color.output(this.color) + ".";
	}
	
	@Override
	public int compareTo(Region r) {
		return -(r.name.compareTo(this.name));
	}
	
	@Override
	public boolean equals(Object o) {
		Region r = (Region) o;
		return r.name.equals(this.name);
	}
}