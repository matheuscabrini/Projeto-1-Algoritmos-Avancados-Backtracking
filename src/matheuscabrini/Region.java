package matheuscabrini;

public class Region implements Comparable<Region> {
	
	public String name = null;
	public byte color;
	public int index;
	
	// Domínio (possíveis cores válidas) da variável (região), para a 
	// verificação adiante e MRV. True = cor ainda é válida, false = cor inválida
	private boolean[] domain;
	
	// Grau, ou número de vizinhos da região, que pode vir a ser requisitado
	// e calculado durante o algoritmo de coloring.
	private Integer degree; 
	
	/*
	 * Construtor da região. Inicializa-se com nome, sem cor e com 
	 * todos os valores do domínio como válidos.
	 */
	public Region(String name) {
		this.name = name;
		color = Color.UNCOLORED;
		
		domain = new boolean[Color.MAX_COLORS+1];
		for (int i = 1; i < domain.length; i++)
			domain[i] = true;
	}
	
	/*
	 * Retorna o número de valores válidos no dominio de cores desta região.
	 */
	public int remainingDomain() {
		int counter = 0;
		for (int i = 1; i < domain.length; i++) {
			if (domain[i] == true) counter++;
		}
		return counter;
	}
	
	/*
	 * Informa se não há mais cores possíveis para esta região.
	 */
	public boolean domainIsEmpty() {
		if (this.remainingDomain() == 0) return true;
		else return false;
	}
	
	/*
	 * Marca, no dominio desta região, que a cor color está inválida.
	 */
	public void removeColorFromDomain(byte color) {
		domain[color] = false;
	}
	
	/*
	 * Marca, no dominio desta região, que a cor color está válida.
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