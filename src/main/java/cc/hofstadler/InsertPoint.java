package cc.hofstadler;


public class InsertPoint {

    static final int BEGIN = 0;
    static final int END = 1;
    static final int RETURN = 2;
    static final int UNROLL = 3;
    static final int IMPORT = 4;


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
