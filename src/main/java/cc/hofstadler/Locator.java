package cc.hofstadler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Locator {

    public String getCurrClassName() {
        return currClassName;
    }

    private String currClassName = "";
    private int currClassNumber = -1;
    private int currMethodNumber = -1;
    private int maxClassNumber = -1;

    private int currAnonymousClass = 0;

    //Stacks for outer Classes
    private int[] outerClassStack = new int[64];
    private int[] outerMethodStack = new int[64];
    private int[] outerAnonymousClass = new int[64];

    private String[] outerNameStack = new String[64];

    private int outerStack = 0;

    public List<String> getClasses() {
        return classes;
    }

    public List<List<String>> getMethodes() {
        return methodes;
    }

    public List<InsertPoint> getInsertPoints() {
        return List.copyOf(insertPoints);
    }

    private String packageString = "";
    private List<String> classes = new ArrayList<>();
    private List<List<String>> methodes = new ArrayList<>();

    private List<InsertPoint> insertPoints = new ArrayList<>();

    public void clear(){
        insertPoints = new ArrayList<>();
        currClassName = "";
        currClassNumber = -1;
        currMethodNumber = -1;
        outerStack = 0;
    }

    public void registerPackage(Token token, String packageString) {
        System.out.println("Locator.registerPackage");
        this.packageString = packageString;
        addInsertPoint(InsertPoint.IMPORT, packageString.isEmpty()? token.charPos:token.charPos +1,  false);
        log(token);
    }

    public void registerClass(Token token) {
        System.out.println("Locator.registerClass");
        log(token);
        currClassName = token.val;
        currClassNumber = ++maxClassNumber;
        currMethodNumber = -1;
        currAnonymousClass = 0;
        classes.add(packageString.isEmpty()? currClassName : packageString + "." +  currClassName);
        methodes.add(new ArrayList<>());
    }

    public void registerInnerClass(Token token, boolean isAnonymous) {
        System.out.println("Locator.registerInnerClass");
        log(token);
        outerClassStack[outerStack] = currClassNumber;
        outerMethodStack[outerStack] = currMethodNumber;
        outerNameStack[outerStack] = currClassName;
        outerAnonymousClass[outerStack] = ++currAnonymousClass;
        outerStack++;

        currClassName = isAnonymous ? "" + currAnonymousClass : token.val;
        currClassNumber = ++maxClassNumber;
        currMethodNumber = -1;
        currAnonymousClass = 0;
        String qualifedName = packageString.isEmpty()? "" : packageString + "." ;
        qualifedName += Arrays.stream(outerNameStack).limit(outerStack).reduce((s1, s2) -> s1 + "$" + s2).orElse("") + "$" + currClassName;
        classes.add(qualifedName);
        methodes.add(new ArrayList<>());
    }

    public void leaveInnerClass(Token token) {
        System.out.println("Locator.leaveInnerClass");
        log(token);
        outerStack--;
        currClassName = outerNameStack[outerStack];
        currClassNumber = outerClassStack[outerStack];
        currMethodNumber = outerMethodStack[outerStack];
        currAnonymousClass = outerAnonymousClass[outerStack];
    }

    public void registerMethod(Token token, String methodName) {
        System.out.println("Locator.registerMethod");
        log(token);
        currMethodNumber++;
        methodes.get(currClassNumber).add(methodName);
        addInsertPoint(InsertPoint.BEGIN, token.charPos + 1, true);
    }

    public void registerReturn(Token token, boolean isInBlock) {
        System.out.println("Locator.registerReturn " + isInBlock);
        log(token);
        addInsertPoint(InsertPoint.RETURN, token.charPos, isInBlock);
    }

    public void leaveVoidMethod(Token token) {
        System.out.println("Locator.leaveVoidMethod");
        log(token);
        if(insertPoints.getLast().charPos == token.charPos) {
            insertPoints.removeLast();
        } else addInsertPoint(InsertPoint.END, token.charPos, false);
    }

    public void registerUnroll(Token token) {
        System.out.println("Locator.registerUnroll");
        log(token);
        addInsertPoint(InsertPoint.UNROLL, token.charPos + 1, true);

    }

    private void addInsertPoint(int type, int pos, boolean isBlock) {
        insertPoints.add(new InsertPoint(
                currClassNumber,
                currMethodNumber,
                type,
                pos,
                isBlock
        ));
    }

    private void log(Token token) {
        System.out.printf("line=%3d col=%3d charPos=%4d kind=%2d val=%s \n", token.line, token.col, token.charPos, token.kind, (token.val != null ? token.val : "--"));
    }
}

class InsertPoint{

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