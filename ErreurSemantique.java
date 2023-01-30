package net.mips.compiler;

public class ErreurSemantique extends ErreurCompilation {
	
	private static final long serialVersionUID = -1123943036272378034L;

	public ErreurSemantique(CodesErr c) {
		super(c.getMessage());
	}
}
