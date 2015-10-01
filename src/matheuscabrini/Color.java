package matheuscabrini;

public class Color {
	
	static final byte UNCOLORED = 0;
	static final byte RED = 1;
	static final byte GREEN = 2;
	static final byte BLUE = 3;
	static final byte YELLOW = 4;

	static final byte MAX_COLORS = 4;
	
	public static String output(byte color) {
		if (color == Color.UNCOLORED) return "Sem cor";
		if (color == Color.RED) return "Vermelho";
		if (color == Color.GREEN) return "Verde";
		if (color == Color.BLUE) return "Azul";
		if (color == Color.YELLOW) return "Amarelo";
		return null;
	}
}
