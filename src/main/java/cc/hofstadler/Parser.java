package cc.hofstadler;

import java.util.List;
import java.util.ArrayList;

class InsertPoint{

    static final int BEGINN = 0;
	static final int END = 1;
	static final int RETURN = 2;
	static final int START = 3;
	static final int EXIT = 4;
	static final int UNROLL = 5;

    int nClass;
    int nMethod;
    int typ;
    int charPos;
    boolean isBlock;

    public InsertPoint(int nClass, int nMethod, int typ, int charPos, boolean isBlock){
        this.nClass = nClass;
        this.nMethod = nMethod;
        this.typ = typ;
        this.charPos = charPos;
        this.isBlock = isBlock;
    }
}



public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _number = 2;
	public static final int _string = 3;
	public static final int _charCon = 4;
	public static final int _void_ = 5;
	public static final int _if_ = 6;
	public static final int _while_ = 7;
	public static final int _switch_ = 8;
	public static final int _else_ = 9;
	public static final int _throws_ = 10;
	public static final int _lpar = 11;
	public static final int _rpar = 12;
	public static final int _lbrace = 13;
	public static final int _rbrace = 14;
	public static final int maxT = 23;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	private static final int SIZE = 64;
	private Token[] buf = new Token[SIZE];
	private int top = 0;
	private int bufLen = 0;

	String curMethod = "";
	String curClass = "";
	boolean isVoidMethode = false;
	int nClass = -1;
	int nMethod = -1;
	int blockDepth = 0;
	List<String> classes = new ArrayList<String>();
	List<List<String>> methodes = new ArrayList<List<String>>();
	List<InsertPoint> insertPoints = new ArrayList();

	int pos(int i) {
		if (i >= 0) return i; else return i + SIZE;
	}

	boolean isMethodBlock() {
		if ((la.kind == _lbrace && buf[pos(top-2)].kind == _rpar) || la.kind == _throws_) {
			int level = 1;
			int i = pos(top-3);
			while (i != top &&  i <= bufLen ) {
				if (buf[i].kind == _rpar) level++;
				else if (buf[i].kind == _lpar) {
					level--;
					if (level == 0) {
						if (buf[pos(i-1)].kind == _ident) {
							curMethod = buf[pos(i-1)].val;
                            isVoidMethode = buf[pos(i-2)].kind == _void_ || curClass.equals(curMethod);
							return true;
						} else return false;
					}
				}
				i = pos(i-1);
			}
		}
		return false;
	}

    Token findMethodBegin() {
        if(la.kind == _ident && ("super".equals(la.val) || "this".equals(la.val) )){
			Token peekToken = scanner.Peek();
            if(peekToken.kind == _lpar){
                for (;;){
                    if (";".equals(peekToken.val)){
                        return peekToken;
                    }
                    peekToken = scanner.Peek();
                }
            }
        }
        return t;
    }

	boolean isReturnInBlock(){
		if (buf[pos(top-3)].kind == _rpar 
			|| buf[pos(top-3)].kind == _else_) {
			return false;
		}
		return true;
	}
	

/*-------------------------------------------------------------------------*/



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
		buf[top] = la;
		bufLen += bufLen < SIZE ? 1 : 0;
		top = (top + 1) % SIZE;
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void Java() {
		while (StartOf(1)) {
			if (la.kind == 15) {
				ClassDeclaration(false);
				while (StartOf(2)) {
					Get();
				}
				Expect(13);
				ClassBody(false);
			} else {
				Get();
			}
		}
	}

	void ClassDeclaration(boolean innerClass) {
		Expect(15);
		Expect(1);
		if(innerClass){
		 System.out.println("----- " + nClass + " Iner  class " + t.val);
		 }else{
		nClass++;
		System.out.println("----- " + nClass + " class " + t.val);
		nMethod = -1;
		curClass  = t.val;
		classes.add(curClass);
		methodes.add(new ArrayList<String>());
		}
		
	}

	void ClassBody(boolean innerClass) {
		while (StartOf(3)) {
			if (isMethodBlock()) {
				if (la.kind == 10) {
					Get();
					Expect(1);
					while (la.kind == 16) {
						Get();
						Expect(1);
					}
				}
				Expect(13);
				nMethod++; blockDepth = 1;
				     Token bTok = findMethodBegin();
				System.out.printf("%" + (blockDepth * 2)+ "s %s \n", "", "beg " + nMethod + " " + curMethod + ": line " + bTok.line + ", col " + bTok.col);
				methodes.get(methodes.size() - 1).add(curMethod);
				insertPoints.add(new InsertPoint( nClass, nMethod, "main".equals(curMethod) ? InsertPoint.START :  InsertPoint.BEGINN, bTok.charPos + 1, true ));
				
				Block(false, true);
			} else if (la.kind == 13) {
				Get();
				blockDepth++; curMethod = "";
				System.out.printf("%" + (blockDepth * 2)+ "s %s \n", "", "beg " + nMethod + " NOMETHODE line " + t.line + ", col " + t.col);
				
				Block(false, false);
			} else if (la.kind == 15) {
				ClassDeclaration(true);
				while (StartOf(2)) {
					Get();
				}
				Expect(13);
				ClassBody(true);
			} else {
				Get();
			}
		}
		Expect(14);
		if(innerClass){
		                                                                                                 System.out.println("----- end: " + nClass + " inner  class " + t.val);
		                                                                                                 }else{
		                                                                   							   System.out.println("----- end: " + nClass + " " +curClass+" " + t.val);
		                                                                   							   }
		                                                                   							   
	}

	void Block(boolean unroll, boolean method) {
		if(unroll){
		 insertPoints.add(new InsertPoint( nClass, nMethod, InsertPoint.UNROLL, t.charPos + 1, true ));
		}
		
		while (StartOf(3)) {
			if (la.kind == 13) {
				Get();
				blockDepth++;
				System.out.printf("%" + (blockDepth * 2)+ "s %s \n", "", "beg " + nMethod +  " line " + t.line + ", col " + t.col + "unrolling: " + unroll);
				
				Block(false, false);
			} else if (la.kind == 17) {
				Get();
				System.out.printf("%" + (blockDepth * 2)+ "s %s \n", "", "return " + nMethod + " line " + t.line + ", col " + t.col + ", braces " + isReturnInBlock() );
				insertPoints.add(new InsertPoint( nClass, nMethod, InsertPoint.RETURN, t.charPos, isReturnInBlock() ));
				
				while (StartOf(4)) {
					Get();
				}
				Expect(18);
			} else if (la.kind == 19 || la.kind == 21) {
				if (la.kind == 19) {
					Get();
					Expect(11);
					QualIdent();
					while (la.kind == 20) {
						Get();
						QualIdent();
					}
					Expect(1);
					Expect(12);
					System.out.println("catch"); 
				} else {
					Get();
					System.out.println("finally"); 
				}
				Expect(13);
				Block(true, false);
				blockDepth++;
			} else {
				Get();
			}
		}
		Expect(14);
		System.out.printf("%" + (blockDepth * 2)+ "s %s \n", "", "end " + nMethod + ": line " + t.line + ", col " + t.col+ (unroll ? "unrollTo Block" :"") + (method ? " method end" : ""));
		blockDepth--;
		//if(blockDepth == 0 && isVoidMethode && !"".equals(curMethod) && !unroll){
		if( method && isVoidMethode){
		insertPoints.add(new InsertPoint( nClass, nMethod, "main".equals(curMethod) ? InsertPoint.EXIT :  InsertPoint.END, t.charPos, false ));
		}
		
	}

	void QualIdent() {
		Expect(1);
		while (la.kind == 22) {
			Get();
			Expect(1);
		}
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Java();
		Expect(0);

		scanner.buffer.Close();
	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "number expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "charCon expected"; break;
			case 5: s = "void_ expected"; break;
			case 6: s = "if_ expected"; break;
			case 7: s = "while_ expected"; break;
			case 8: s = "switch_ expected"; break;
			case 9: s = "else_ expected"; break;
			case 10: s = "throws_ expected"; break;
			case 11: s = "lpar expected"; break;
			case 12: s = "rpar expected"; break;
			case 13: s = "lbrace expected"; break;
			case 14: s = "rbrace expected"; break;
			case 15: s = "\"class\" expected"; break;
			case 16: s = "\",\" expected"; break;
			case 17: s = "\"return\" expected"; break;
			case 18: s = "\";\" expected"; break;
			case 19: s = "\"catch\" expected"; break;
			case 20: s = "\"|\" expected"; break;
			case 21: s = "\"finally\" expected"; break;
			case 22: s = "\".\" expected"; break;
			case 23: s = "??? expected"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
