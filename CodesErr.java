package net.mips.compiler;

public enum CodesErr {
	CAR_INC_ERR("Symbole inconnu"),
	FIC_VID_ERR("Erreur d'ouverture de fichier"),
	
	PROGRAM_ERR("Mot clé program attendu"),
	ID_ERR("Identificateur attendu"),
	PVIR_ERR("Symbole ; attendu"),
	PNT_ERR("Symbole . attendu"),
	EG_ERR("Symbole = attendu"),
	NUM_ERR("Nombre entier attendu"),
	CONSTS_ERR("Mot clé const, var ou begin attendu"),
	VARS_ERR("Mot clé var ou begin attendu"),
	BEGIN_ERR("Mot clé begin attendu"),
	END_ERR("Mot clé end attendu"),
	INST_ERR ("Un mot clé begin, if, while, read ou end, un identificateur, ou le symbole ; sont attendus!"),
	AFFEC_ERR ("Symbole := attendu!"),
	IF_ERR ("Mot clé if attendu!"),
	THEN_ERR ("Mot clé then attendu!"),
	WHILE ("Mot clé while attendu!"),
	DO_ERR ("Mot clé end attendu!"),
	WRITE_ERR ("Mot clé write attendu!"),
	PARG_ERR ("Symbole ( attendu!"),
	PARD_ERR ("Symbole ) attendu!"),
	READ_ERR ("Mot clé read attendu!"),
	WHILE_ERR ("Mot clé while attendu!"),
	RELOP_ERR ("Opérateur relationnel <, >, =, <>, <= ou >= attendu !"),
	FACT_ERR ("Identificateur, nombre ou symbole ( attendu !"),
	EOF_ERR ("End of file attendu !"),
	
	DBL_DECL_ERR("Erreur double déclaration"),
	NON_DECL_ERR("Erreur symbole non déclaré"),
	CONST_MODIF_ERR("Erreur constante ne peut changer de valeur"),
	PROG_USE_ERR("Erreur nom de programme non autorisé");
	
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	private CodesErr(String message) {
		this.message=message;
	}
}



