package ambit2.sln;

public class SLNConst
{	

	//Logical operations
	public static char LogOperationChars[] = {'!', '&', '|', ';'};
	public static final int LO_NOT = 0;
	public static final int LO_AND = 1;
	public static final int LO_OR = 2;
	public static final int LO_ANDLO = 3;
	public static final int LO = 1000;
	
	
	//Atom Attributes (predefined)   A_ATTR_*	
	public static final int A_ATTR_charge = 1;	 // specifies formal charge -/+n
	public static final int A_ATTR_I = 2;		 // specifies atom isotope
	public static final int A_ATTR_fcharge = 3;	// specifies a partial charge
	public static final int A_ATTR_s = 4; 		//specifies stereo-chemistry at tetrahedral atoms 
	public static final int A_ATTR_spin = 5;	//specifies that the atom is a radical 	
	public static final int A_ATTR_USER_DEFINED = 500; //  == other= !!!
	//boolean  - the attribute's name is specified, as in C[backbone]
	//valued - the name is specified, followed by an equal sign ant the value, as in C-[chemshift=7.2]
	
	public static String atomAttributeToNameString(int attr)
	{
		switch (attr)
		{
		case A_ATTR_charge:
			return "charge";
		case A_ATTR_I:
			return "I";
			//TODO
		default:
			return "";
		}
	}
	
	//Atom stereo-chemistry values (for attribute s)
	public static final int A_STEREO_R = 0; // CIP configurations
	public static final int A_STEREO_S = 1; // CIP configurations
	public static final int A_STEREO_N = 2; // normal with respect to atom ID sequence
	public static final int A_STEREO_I = 3; // inverted with respect to atom ID sequence
	public static final int A_STEREO_D = 4; // relative to glycer-aldehyde
	public static final int A_STEREO_L = 5; // relative to glyceraldehyde
	public static final int A_STEREO_U = 6; // unknown
	public static final int A_STEREO_E = 7; // explicit
	public static final int A_STEREO_Rel = 8; // relative == * !!!
	public static final int A_STEREO_M = 9; // mixture
	//TODO  Specifying Stereo-chemistry
	
	//Atom radical values (for attribute spin)
	public static final int A_spin_s = 0; // singlet
	public static final int A_spin_d = 1; // doublet == * !!!
	public static final int A_spin_t = 2; // triplet
	
	
	//Bond Attributes (predefined)   B_ATTR_*
	public static final int B_ATTR_type = 0; // overrides the type specified by the bond character
	public static final int B_ATTR_s = 1;	//indicates stereo-chemistry about a double bond
	
	public static final int B_ATTR_USER_DEFINED = 500;
	
	// Predefined values for type 
	public static final int B_TYPE_ANY = 0; // equal to -
	public static final int B_TYPE_1 = 1; // equal to -
	public static final int B_TYPE_2 = 2; // equal to =
	public static final int B_TYPE_3 = 3; // equal to #
	public static final int B_TYPE_aromatic = 4; // equal to :
	
	public static final int B_TYPE_USER_DEFINED = 100; //???
	
	
	//Bond stereo-chemistry values (for attribute s)
	public static final int B_STEREO_C = 0; // Cis
	public static final int B_STEREO_T = 1; // Trans
	public static final int B_STEREO_E = 2; // Entgegen
	public static final int B_STEREO_Z = 3; // Zusammen
	public static final int B_STEREO_N = 4; // Normal, similar to E
	public static final int B_STEREO_I = 5; // Inverted, similar to Z
	public static final int B_STEREO_U = 6; // Unknown, structure might represent single, mixture or both config.
	public static final int B_STEREO_U_= 7; // unknown, structure represent single configuration
	
	

	//Bond types
	public static char BondChars[] = {'~','-','=','#',':'};
	
	/*  //left-out from SMARTS parser could be removed
	public static final int BT_ANY = 0;
	public static final int BT_SINGLE = 1;
	public static final int BT_DOUBLE = 2;
	public static final int BT_TRIPLE = 3;
	public static final int BT_AROMATIC = 4;
	public static final int BT_UNDEFINED = 100;
	*/
	
	public static int getBondCharNumber (char ch)
	{
		for (int i=0; i < BondChars.length; i++)
		{
			if (ch == BondChars[i])
				return (i);
		}
		return(-1);
	}	
	
	//Atom and Bond attributes used only in query Q_ATTR_*    ???collect them to atom and bond attributes???
	public static final int Q_ATTR_mapNum = 100; // #1,#2,... map number to an atom "a"
	public static final int Q_ATTR_c = 101; // contol how atoms in the target are matched "convered"  by pattern atoms "a"
	public static final int Q_ATTR_f = 102; // boolean attribute indicating that the atom is field "a"
	public static final int Q_ATTR_hac = 103; // heavy atom count "b"
	public static final int Q_ATTR_hc = 104; // hydrogen count "b"
	public static final int Q_ATTR_htc = 105; // hetero atom count "b"
	public static final int Q_ATTR_is = 106; // Specifies patterns that the qualified atom in a query must match if it is to match the query atom "a"
	public static final int Q_ATTR_mw = 107; // The molecular weight attribute is used with group atoms (R, X, and Rx)to specify the cumulative atomic weights that atoms that match the group atom to match the query "b"
	public static final int Q_ATTR_n = 108; // atom that matches a query atom from being covered "a"
	public static final int Q_ATTR_not = 109; // Specifies patterns that the qualified atom in a query must not match "a"
	public static final int Q_ATTR_ntc = 110; // number of nonterminal atoms "b"
	public static final int Q_ATTR_r = 111; // Boolean attribute used to specify that a bond or atom is in a ring "a"
	public static final int Q_ATTR_rbc = 112; // ring bond count "b"
	public static final int Q_ATTR_src = 113; // the smallest ring count "b"
	public static final int Q_ATTR_tac = 114; // total number of atoms attached to the qualified atom "b"
	public static final int Q_ATTR_tbo = 115; // total bond order of an atom "b"
	public static final int Q_ATTR_type = 116; // bond type specified by the bond character !!! -,=,#,:,1,2,3,aromatic,.,~
	public static final int Q_ATTR_v = 117; // Conveys Markush and macro atom valence information
	
	//Values of converege attribute
	public static final int Q_CONVERED_n = 0; // atom must not have been matched previously
	public static final int Q_CONVERED_o = 1; // atom's coverage flags are ignored
	public static final int Q_CONVERED_y = 2; // atom must be covered by previous search
	
	
	//Matrix with the operation priorities {pij}
	//p[i][j] < 0 means that priority(i) < priority(j)
	//p[i][j] = 0 means that priority(i) = priority(j)
	//p[i][j] > 0 means that priority(i) > priority(j)
	public static int priority[][] = {
		//         !  &  |  ;
		/* ! */  {-1, 1, 1, 1},
		/* & */  {-1, 0, 1, 1},
		/* | */  {-1,-1, 0, 1},
		/* ; */  {-1,-1,-1, 0},
	};
	
	public static final int GlobDictOffseet = 1000;
	public static final int LocalDictOffseet = 1000000;
	
	
	public static final String elSymbols[] =
	{
			"",                                                  
			"H","He","Li","Be","B",
			"C","N","O","F","Ne",         
			"Na","Mg","Al","Si","P",
			"S","Cl","Ar","K","Ca",      
			"Sc","Ti","V","Cr","Mn",
			"Fe","Co","Ni","Cu","Zn",    
			"Ga","Ge","As","Se","Br",
			"Kr","Rb","Sr","Y","Zr",    
			"Nb","Mo","Tc","Ru","Rh",
			"Pd","Ag","Cd","In","Sn",   
			"Sb","Te","I","Xe","Cs",
			"Ba","La","Ce","Pr","Nd",    
			"Pm","Sm","Eu","Gd","Tb",
			"Dy","Ho","Er","Tm","Yb",   
			"Lu","Hf","Ta","W","Re",
			"Os","Ir","Pt","Au","Hg",    
			"Tl","Pb","Bi","Po","At",
			"Rn","Fr","Ra","Ac","Th",   
			"Pa","U","Np","Pu","Am",
			"Cm","Bk","Cf","Es","Fm",    
			"Md","No","Lr","Rf","Db",
			"Sg","Bh","Hs","Mt","Ds", 
			"Rg"  		//up to element #111
		};
}
