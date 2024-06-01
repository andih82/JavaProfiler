package cc.hofstadler;


public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _char = 2;
	public static final int _string = 3;
	public static final int _void_ = 4;
	public static final int _if_ = 5;
	public static final int _for_ = 6;
	public static final int _while_ = 7;
	public static final int _switch_ = 8;
	public static final int _else_ = 9;
	public static final int _throws_ = 10;
	public static final int _lpar = 11;
	public static final int _rpar = 12;
	public static final int _lbrace = 13;
	public static final int _rbrace = 14;
	public static final int _ldiamond = 15;
	public static final int _rdiamond = 16;
	public static final int _comma = 17;
	public static final int _arrow = 18;
	public static final int _new_ = 19;
	public static final int maxT = 32;

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
	String packageString = "";
	boolean isVoidMethode = false;
    Locator loc;

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
                            isVoidMethode = buf[pos(i-2)].kind == _void_ || loc.getCurrClassName().equals(curMethod);
							return true;
						} else return false;
					}
				}
				i = pos(i-1);
			}
		}
		return false;
	}

	boolean isLambdaBlock() {
        if ((la.kind == _lbrace && buf[pos(top-2)].kind == _arrow)) {
            return true;
        }
        return false;
    }

	boolean isAnonymClassDeclariation() {
        if ((la.kind == _lbrace && buf[pos(top-2)].kind == _rpar)) {
            int level = 1;
    	    int i = pos(top-3);
    	    while (i != top &&  i <= bufLen ) {
            	if (buf[i].kind == _rpar) level++;
            	else if (buf[i].kind == _lpar) {
            		level--;
            		if (level == 0) {
            		   i = pos(i-1);
                       while (i != top &&  i <= bufLen ) {
                          if(buf[i].kind == _new_) {
                             return true;
                          } else if ( !(buf[i].kind == _ident || buf[i].kind == _ldiamond || buf[i].kind == _rdiamond || buf[i].kind == _comma ) ) {
        	                 return false;
        	              }
                          i = pos(i-1);
                      }
                    }
                }
                i = pos(i-1);
            }
        }
        return false;
    }

    Token findMethodBegin() {
        scanner.ResetPeek();
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

	boolean isDirectAfterThorw(){
        if (";".equals(buf[pos(top-3)].val)) {
        			int level = 0;
        			int i = pos(top-4);
        			while (i != top &&  i <= bufLen ) {
        				if (buf[i].kind == _rpar) level++;
        				else if (buf[i].kind == _lpar) {
        					level--;
        					if (level == 0) {
        						if (buf[pos(i-2)].kind == _new_ && buf[pos(i-3)].kind == _ident && "throw".equals(buf[pos(i-3)].val))  {
        						   if (buf[pos(i-4)].kind == _rpar || buf[pos(i-4)].kind == _else_) {
                                       return false;
                                   }
                                   return true;
        						} else return false;
        					}
        				}
        				i = pos(i-1);
        			}
        		}
        		return false;
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
		if (la.kind == 31) {
			PackageDeclaration();
		}
		loc.registerPackage(t, packageString); 
		while (StartOf(1)) {
			if (StartOf(2)) {
				ClassDeclaration(false);
			} else {
				Get();
			}
		}
	}

	void PackageDeclaration() {
		Expect(31);
		Expect(1);
		packageString = t.val; 
		while (la.kind == 27) {
			Get();
			Expect(1);
			packageString += "." + t.val; 
		}
		Expect(24);
	}

	void ClassDeclaration(boolean innerClass) {
		if (la.kind == 20) {
			Get();
		} else if (la.kind == 21) {
			Get();
		} else if (la.kind == 22) {
			Get();
		} else if (la.kind == 23) {
			Get();
		} else SynErr(33);
		if (la.kind == 12 || la.kind == 17 || la.kind == 24) {
			if (la.kind == 24) {
				Get();
			} else if (la.kind == 17) {
				Get();
			} else {
				Get();
			}
		} else if (la.kind == 1) {
			Get();
			if(innerClass) loc.registerInnerClass(t, false); 
			else loc.registerClass(t); 
			while (StartOf(3)) {
				Get();
			}
			Expect(13);
			ClassBody(innerClass);
		} else SynErr(34);
	}

	void ClassBody(boolean innerClass) {
		while (StartOf(4)) {
			if (isMethodBlock()) {
				if (la.kind == 10) {
					Get();
					Expect(1);
					while (la.kind == 17) {
						Get();
						Expect(1);
					}
				}
				if (la.kind == 24) {
					Get();
				} else if (la.kind == 13) {
					Get();
					loc.registerMethod(findMethodBegin(), curMethod); 
					Block(false, isVoidMethode, false);
				} else SynErr(35);
			} else if (isAnonymClassDeclariation()) {
				Expect(13);
				loc.registerInnerClass(t, true); 
				ClassBody(true);
			} else if (isLambdaBlock()) {
				Expect(13);
				Block(false, false, true);
			} else if (StartOf(2)) {
				ClassDeclaration(true);
			} else if (la.kind == 13) {
				Get();
				Block(false, false, false);
			} else {
				Get();
			}
		}
		Expect(14);
		if(innerClass) loc.leaveInnerClass(t); 
	}

	void Block(boolean unroll, boolean methodEnd, boolean ignoreReturn) {
		if(unroll) loc.registerUnroll(t); 
		while (StartOf(4)) {
			if (isAnonymClassDeclariation()) {
				Expect(13);
				loc.registerInnerClass(t, true); 
				ClassBody(true);
			} else if (isLambdaBlock()) {
				Expect(13);
				Block(false, false, true);
			} else if (la.kind == 13) {
				Get();
				Block(false, false, false);
			} else if (la.kind == 25) {
				ReturnStatement(ignoreReturn);
			} else if (la.kind == 26) {
				ExitStatement();
			} else if (la.kind == 28 || la.kind == 30) {
				UnrollBlock();
			} else {
				Get();
			}
		}
		Expect(14);
		if(methodEnd && !isDirectAfterThorw()) loc.leaveVoidMethod(t); 
	}

	void ReturnStatement(boolean ignore) {
		Expect(25);
		if(!ignore) loc.registerReturn(t, isReturnInBlock());
		while (StartOf(5)) {
			Get();
		}
		Expect(24);
	}

	void ExitStatement() {
		Expect(26);
		Token temp = t; boolean isBlock = isReturnInBlock(); 
		Expect(27);
		Expect(1);
		if(t.val.equals("exit")) loc.registerReturn(temp, isBlock); 
	}

	void UnrollBlock() {
		if (la.kind == 28) {
			Get();
			Expect(11);
			QualIdent();
			while (la.kind == 29) {
				Get();
				QualIdent();
			}
			Expect(1);
			Expect(12);
		} else if (la.kind == 30) {
			Get();
		} else SynErr(36);
		Expect(13);
		Block(true, false, false);
	}

	void QualIdent() {
		Expect(1);
		while (la.kind == 27) {
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
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _T,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_x}

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
			case 2: s = "char expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "void_ expected"; break;
			case 5: s = "if_ expected"; break;
			case 6: s = "for_ expected"; break;
			case 7: s = "while_ expected"; break;
			case 8: s = "switch_ expected"; break;
			case 9: s = "else_ expected"; break;
			case 10: s = "throws_ expected"; break;
			case 11: s = "lpar expected"; break;
			case 12: s = "rpar expected"; break;
			case 13: s = "lbrace expected"; break;
			case 14: s = "rbrace expected"; break;
			case 15: s = "ldiamond expected"; break;
			case 16: s = "rdiamond expected"; break;
			case 17: s = "comma expected"; break;
			case 18: s = "arrow expected"; break;
			case 19: s = "new_ expected"; break;
			case 20: s = "\"class\" expected"; break;
			case 21: s = "\"enum\" expected"; break;
			case 22: s = "\"interface\" expected"; break;
			case 23: s = "\"record\" expected"; break;
			case 24: s = "\";\" expected"; break;
			case 25: s = "\"return\" expected"; break;
			case 26: s = "\"System\" expected"; break;
			case 27: s = "\".\" expected"; break;
			case 28: s = "\"catch\" expected"; break;
			case 29: s = "\"|\" expected"; break;
			case 30: s = "\"finally\" expected"; break;
			case 31: s = "\"package\" expected"; break;
			case 32: s = "??? expected"; break;
			case 33: s = "invalid ClassDeclaration"; break;
			case 34: s = "invalid ClassDeclaration"; break;
			case 35: s = "invalid ClassBody"; break;
			case 36: s = "invalid UnrollBlock"; break;
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
