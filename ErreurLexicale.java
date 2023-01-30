package net.mips.compiler;

public class ErreurLexicale extends ErreurCompilation {
	
	private static final long serialVersionUID = 3906094829812460246L;

	public ErreurLexicale(CodesErr code) {
		super(code.getMessage());
	}
}
