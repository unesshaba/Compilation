package net.mips.compiler;

public class ErreurSyntaxique extends ErreurCompilation {

	private static final long serialVersionUID = 1L;

	public ErreurSyntaxique(CodesErr code) {
		super(code.getMessage());
	}
}
