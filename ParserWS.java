package net.mips.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.mips.interpreter.Instruction;
import net.mips.interpreter.Mnemonique;

public class ParserWS extends Parser {
	
	private ArrayList<Instruction> pcode;
	private PrintWriter fluxCible;

	public ArrayList<Instruction> getPcode() {
		return pcode;
	}

	public void setPcode(ArrayList<Instruction> pcode) {
		this.pcode = pcode;
	}

	public PrintWriter getFluxCible() {
		return fluxCible;
	}

	public void setFluxCible(PrintWriter fluxCible) {
		this.fluxCible = fluxCible;
	}

	public ParserWS(String nomFich) 
			throws IOException, ErreurCompilation {
		super(nomFich);
		// TODO Auto-generated constructor stub
		setScan(new ScannerWS(nomFich));
		pcode=new ArrayList<Instruction>();
	}
	
	public void generer1(Mnemonique m) {
		Instruction ins=new Instruction();
		ins.setMne(m);
		pcode.add(ins);
	}
	
	public void generer2(Mnemonique m, int a) {
		Instruction ins=new Instruction(m,a);
		pcode.add(ins);
	}
	
	public void savePcode() throws IOException {
		fluxCible=new PrintWriter("sortie.pc");
		for (Instruction ins:pcode) {
			Mnemonique m=ins.getMne();
			if (m==Mnemonique.INT || m==Mnemonique.LDA ||
					m==Mnemonique.LDI || m==Mnemonique.BRN ||
					m==Mnemonique.BZE) 
				fluxCible.println(m+"\t"+ins.getSuite());
			else
				fluxCible.println(m);
		}
		fluxCible.close();
	}
	
	public void testInsere(Tokens t, ClasseIdf cls, CodesErr c) 
			throws IOException, ErreurCompilation {
		if (getScan().getSymbCour().getToken()==t) {
			((ScannerWS)getScan()).chercherSymb();
			if (((ScannerWS)getScan()).getPlaceSymb()!=-1)
				throw new ErreurSemantique(CodesErr.DBL_DECL_ERR);
			((ScannerWS)getScan()).entrerSymb(cls);
			getScan().symbSuiv();
		}
		else
			throw new ErreurSyntaxique(c);
	}
	
	public void testCherche(Tokens t, CodesErr c) 
			throws IOException, ErreurCompilation {
		if (getScan().getSymbCour().getToken()==t) {
			((ScannerWS)getScan()).chercherSymb();
			int p=((ScannerWS)getScan()).getPlaceSymb();
			if (p==-1)
				throw new ErreurSemantique(CodesErr.NON_DECL_ERR);
			Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
			if (s.getClasse()==ClasseIdf.PROGRAMME)
				throw new ErreurSemantique(CodesErr.PROG_USE_ERR);
			
			getScan().symbSuiv();
		}
		else
			throw new ErreurSyntaxique(c);
	}
	
	public void program() throws IOException, ErreurCompilation {
		testAccept(Tokens.PROGRAM_TOKEN, CodesErr.PROGRAM_ERR);
		
		//testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		testInsere(Tokens.ID_TOKEN, ClasseIdf.PROGRAMME, CodesErr.ID_ERR);
		
		testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
		block();
		testAccept(Tokens.PNT_TOKEN, CodesErr.PNT_ERR);
	}
	
	public void block() throws IOException, ErreurCompilation {
		consts();
		vars();
		//(a)
		int a=((ScannerWS)getScan()).getOffset();
		pcode.add(0,new Instruction(Mnemonique.INT,a));
		
		insts();
	}
	
	public void consts() throws IOException, ErreurCompilation {
		switch(getScan().getSymbCour().getToken()) {
		case CONST_TOKEN:
			getScan().symbSuiv();
			testInsere(Tokens.ID_TOKEN,ClasseIdf.CONSTANTE,  CodesErr.ID_ERR);
			//(a)
			int p=((ScannerWS)getScan()).getTableSymb().size()-1;
			Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
			int a=s.getAdresse();
			generer2(Mnemonique.LDA, a);
			
			testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
			testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
			//(b)
			a=((ScannerWS)getScan()).getVal();
			generer2(Mnemonique.LDI, a);
			//(c)
			generer1(Mnemonique.STO);
			
			testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			while (getScan().getSymbCour().getToken()==Tokens.ID_TOKEN) {
				testInsere(Tokens.ID_TOKEN,ClasseIdf.CONSTANTE,  CodesErr.ID_ERR);
				//(a)
				p=((ScannerWS)getScan()).getTableSymb().size()-1;
				s=((ScannerWS)getScan()).getTableSymb().get(p);
				a=s.getAdresse();
				generer2(Mnemonique.LDA, a);
				
				testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
				testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
				//(b)
				a=((ScannerWS)getScan()).getVal();
				generer2(Mnemonique.LDI, a);
				//(c)
				generer1(Mnemonique.STO);
				
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

	public void vars() throws IOException, ErreurCompilation {
		switch (getScan().getSymbCour().getToken()) {
		case VAR_TOKEN:
			//var ID { , ID } ;
			getScan().symbSuiv();
			testInsere(Tokens.ID_TOKEN,ClasseIdf.VARIABLE,  CodesErr.ID_ERR);
			while (getScan().getSymbCour().getToken()==Tokens.VIR_TOKEN){
				getScan().symbSuiv();
				testInsere(Tokens.ID_TOKEN,ClasseIdf.VARIABLE,  CodesErr.ID_ERR);
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
	
	public void affec() throws ErreurCompilation, IOException {
		testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		int p=((ScannerWS)getScan()).getPlaceSymb();
		Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
		if (s.getClasse()==ClasseIdf.CONSTANTE)
			throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
		
		testAccept(Tokens.AFFEC_TOKEN, CodesErr.AFFEC_ERR);
		expr();
	}

	public void lire() throws ErreurCompilation, IOException {
		testAccept(Tokens.READ_TOKEN, CodesErr.READ_ERR);
		testAccept(Tokens.PARG_TOKEN, CodesErr.PARG_ERR);
		testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		
		int p=((ScannerWS)getScan()).getPlaceSymb();
		Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
		if (s.getClasse()==ClasseIdf.CONSTANTE)
			throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
		
		while(getScan().getSymbCour().getToken()==Tokens.VIR_TOKEN) {
			getScan().symbSuiv();
			testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
			
			p=((ScannerWS)getScan()).getPlaceSymb();
			s=((ScannerWS)getScan()).getTableSymb().get(p);
			if (s.getClasse()==ClasseIdf.CONSTANTE)
				throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
		}
		testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
	}
	
	public void fact() throws IOException, ErreurCompilation {
		switch(getScan().getSymbCour().getToken()) {
		case ID_TOKEN:
			testCherche(Tokens.ID_TOKEN,  CodesErr.ID_ERR);
			break;
		case NUM_TOKEN:
			getScan().symbSuiv();
			break;
		case PARG_TOKEN:
			getScan().symbSuiv();
			expr();
			testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
		default:
			throw new ErreurSyntaxique(CodesErr.FACT_ERR);
		}
	}
	
	public static void main(String [] args) 
			 throws IOException, ErreurCompilation {
		ParserWS parse=new ParserWS("exemp6.p");
		parse.getScan().initMotsCles();
		parse.getScan().lireCar();
		parse.getScan().symbSuiv();
		parse.program();
		if (parse.getScan().getSymbCour().getToken()==Tokens.EOF_TOKEN) { 
			System.out.println("Analyse syntaxique reussie");
			parse.savePcode();
		}
		else
			throw new ErreurSyntaxique(CodesErr.EOF_ERR);
	}

}






