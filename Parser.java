package net.mips.compiler;

import java.io.IOException;

public class Parser {
	private Scanner scan;

	public Scanner getScan() {
		return scan;
	}

	public void setScan(Scanner scan) {
		this.scan = scan;
	}
	
	public Parser(String nomFich) 
		throws IOException, ErreurCompilation {
		scan=new Scanner(nomFich);
	}
	
	public void testAccept(Tokens t, CodesErr c) 
				throws IOException, ErreurCompilation {
		if (scan.getSymbCour().getToken()==t)
			scan.symbSuiv();
		else
			throw new ErreurSyntaxique(c);
	}
	
	public void program() throws IOException, ErreurCompilation {
		testAccept(Tokens.PROGRAM_TOKEN, CodesErr.PROGRAM_ERR);
		testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
		block();
		testAccept(Tokens.PNT_TOKEN, CodesErr.PNT_ERR);
	}
	
	public void block() throws IOException, ErreurCompilation {
		consts();
		vars();
		insts();
	}
	
	//CONSTS 	::=	const ID = NUM ; { ID = NUM ; } | eps
	public void consts() throws IOException, ErreurCompilation {
		switch(scan.getSymbCour().getToken()) {
		case CONST_TOKEN:
			scan.symbSuiv();
			testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
			testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
			testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
			testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			while (scan.getSymbCour().getToken()==Tokens.ID_TOKEN) {
				scan.symbSuiv();
				testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
				testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
				testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			}
			break;
		case VAR_TOKEN:
			break;
		case BEGIN_TOKEN:
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.CONSTS_ERR);
		}
	}
	
	//VARS 	::= 	var ID { , ID } ; | epsilon
	public void vars() throws IOException, ErreurCompilation {
		switch (scan.getSymbCour().getToken()) {
		case VAR_TOKEN:
			//var ID { , ID } ;
			scan.symbSuiv();
			testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
			while (scan.getSymbCour().getToken()==Tokens.VIR_TOKEN){
				scan.symbSuiv();
				testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
			}
			testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			break;
		case BEGIN_TOKEN:
			//epsilon
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.VARS_ERR);
		}
	}
	
	//INSTS 	::=	begin INST { ; INST } end
	public void insts() throws IOException, ErreurCompilation {
		testAccept(Tokens.BEGIN_TOKEN, CodesErr.BEGIN_ERR);
		inst();
		while (scan.getSymbCour().getToken()==Tokens.PVIR_TOKEN){
			scan.symbSuiv();
			inst();
		}
		testAccept(Tokens.END_TOKEN, CodesErr.END_ERR);
	}
	
		
	public void inst() throws ErreurCompilation, IOException {
		switch(scan.getSymbCour().getToken()) {
		case BEGIN_TOKEN :
			insts();
			break;
		case ID_TOKEN:
			affec();
			break;
		case IF_TOKEN:
			si();
			break;
		case WHILE_TOKEN:
			tantque();
			break;
		case WRITE_TOKEN:
			ecrire();
			break;
		case READ_TOKEN:
			lire();
			break;
		case PVIR_TOKEN:
			break;
		case END_TOKEN:
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.INST_ERR);
		}
	}
	
	public void affec() throws ErreurCompilation, IOException {
		testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		testAccept(Tokens.AFFEC_TOKEN, CodesErr.AFFEC_ERR);
		expr();
	}
	
	public void si() throws ErreurCompilation, IOException {
		testAccept(Tokens.IF_TOKEN, CodesErr.IF_ERR);
		cond();
		testAccept(Tokens.THEN_TOKEN, CodesErr.THEN_ERR);
		inst();
	}
	
	public void tantque() throws ErreurCompilation, IOException {
		testAccept(Tokens.WHILE_TOKEN, CodesErr.WHILE_ERR);
		cond();
		testAccept(Tokens.DO_TOKEN, CodesErr.DO_ERR);
		inst();
	}
	
	public void ecrire() throws ErreurCompilation, IOException {
		testAccept(Tokens.WRITE_TOKEN, CodesErr.WRITE_ERR);
		testAccept(Tokens.PARG_TOKEN, CodesErr.PARG_ERR);
		expr();
		while(scan.getSymbCour().getToken()==Tokens.VIR_TOKEN) {
			scan.symbSuiv();
			expr();
		}
		testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
	}
	
	public void lire() throws ErreurCompilation, IOException {
		testAccept(Tokens.READ_TOKEN, CodesErr.READ_ERR);
		testAccept(Tokens.PARG_TOKEN, CodesErr.PARG_ERR);
		testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		while(scan.getSymbCour().getToken()==Tokens.VIR_TOKEN) {
			scan.symbSuiv();
			testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		}
		testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
	}
	
	public void cond() throws IOException, ErreurCompilation {
		expr();
		switch(scan.getSymbCour().getToken()) {
		case EG_TOKEN:
			scan.symbSuiv();
			break;
		case DIFF_TOKEN:
			scan.symbSuiv();
			break;
		case INF_TOKEN:
			scan.symbSuiv();
			break;
		case SUP_TOKEN:
			scan.symbSuiv();
			break;
		case INFEG_TOKEN:
			scan.symbSuiv();
			break;
		case SUPEG_TOKEN:
			scan.symbSuiv();
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.RELOP_ERR);
		}
		expr();
	}
	
	public void expr() throws IOException, ErreurCompilation {
		term();
		while(scan.getSymbCour().getToken()==Tokens.PLUS_TOKEN || scan.getSymbCour().getToken()==Tokens.MOINS_TOKEN) {
			scan.symbSuiv();
			term();
		}
	}
	
	public void term() throws IOException, ErreurCompilation {
		fact();
		while(scan.getSymbCour().getToken()==Tokens.MUL_TOKEN || scan.getSymbCour().getToken()==Tokens.DIV_TOKEN) {
			scan.symbSuiv();
			fact();
		}
	}
	
	public void fact() throws IOException, ErreurCompilation {
		switch(scan.getSymbCour().getToken()) {
		case ID_TOKEN:
			scan.symbSuiv();
			break;
		case NUM_TOKEN:
			scan.symbSuiv();
			break;
		case PARG_TOKEN:
			scan.symbSuiv();
			expr();
			testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
		default:
			throw new ErreurSyntaxique(CodesErr.FACT_ERR);
		}
	}
		
		
	public static void main(String [] args) 
			 throws IOException, ErreurCompilation {
		Parser parse=new Parser("test.p");
		parse.getScan().initMotsCles();
		parse.getScan().lireCar();
		parse.getScan().symbSuiv();
		parse.program();
		if (parse.getScan().getSymbCour().getToken()==Tokens.EOF_TOKEN) 
			System.out.println("Analyse syntaxique reussie");
		else
			throw new ErreurSyntaxique(CodesErr.EOF_ERR);
	}
}
