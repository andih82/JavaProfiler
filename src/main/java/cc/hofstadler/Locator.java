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
    //Stackpointer
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

    /**
     * Clears the Locator
     * to be used for the next file
     */
    public void clear(){
        insertPoints = new ArrayList<>();
        currClassName = "";
        currClassNumber = -1;
        currMethodNumber = -1;
        outerStack = 0;
    }

    /**
     * Registers a package
     * @param token token passed from Parser
     * @param packageString the package
     */
    public void registerPackage(Token token, String packageString) {
        JavaProfiler.println("Locator.registerPackage");
        this.packageString = packageString;
        addInsertPoint(InsertPoint.IMPORT, packageString.isEmpty()? token.charPos:token.charPos +1,  false);
        log(token);
    }

    /**
     * Registers a class
     * @param token token passed from Parser
     */
    public void registerClass(Token token) {
        JavaProfiler.println("Locator.registerClass");
        log(token);
        currClassName = token.val;
        currClassNumber = ++maxClassNumber;
        currMethodNumber = -1;
        currAnonymousClass = 0;
        classes.add(packageString.isEmpty()? currClassName : packageString + "." +  currClassName);
        methodes.add(new ArrayList<>());
    }

    /**
     * Registers an inner/nested/anonymous class
     * Current class and method are stored on a stack
     * @param token token passed from Parser
     * @param isAnonymous is the class anonymous
     */
    public void registerInnerClass(Token token, boolean isAnonymous) {
        JavaProfiler.println("Locator.registerInnerClass");
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

    /**
     * Leaves an inner class
     * Current classes and methods are restored from the stack
     * @param token
     */
    public void leaveInnerClass(Token token) {
        JavaProfiler.println("Locator.leaveInnerClass");
        log(token);
        outerStack--;
        currClassName = outerNameStack[outerStack];
        currClassNumber = outerClassStack[outerStack];
        currMethodNumber = outerMethodStack[outerStack];
        currAnonymousClass = outerAnonymousClass[outerStack];
    }

    /**
     * Registers a method
     * @param token token passed from Parser
     * @param methodName the method name
     */
    public void registerMethod(Token token, String methodName) {
        JavaProfiler.println("Locator.registerMethod");
        log(token);
        currMethodNumber++;
        methodes.get(currClassNumber).add(methodName);
        addInsertPoint(InsertPoint.BEGIN, token.charPos + 1, true);
    }

    /**
     * Registers a return statement
     * @param token token passed from Parser
     * @param isInBlock is the return statement in a block
     */
    public void registerReturn(Token token, boolean isInBlock) {
        JavaProfiler.println("Locator.registerReturn " + isInBlock);
        log(token);
        addInsertPoint(InsertPoint.RETURN, token.charPos, isInBlock);
    }

    /**
     * Leaves a method
     * Only called for void methods, saves InsertPoint.END at the end of method
     * @param token token passed from Parser
     */
    public void leaveVoidMethod(Token token) {
        JavaProfiler.println("Locator.leaveVoidMethod");
        log(token);
        if(insertPoints.getLast().charPos == token.charPos) {
            insertPoints.removeLast();
        } else addInsertPoint(InsertPoint.END, token.charPos, false);
    }

    /**
     * Registers an unroll statement
     * @param token token passed from Parser
     */
    public void registerUnroll(Token token) {
        JavaProfiler.println("Locator.registerUnroll");
        log(token);
        addInsertPoint(InsertPoint.UNROLL, token.charPos + 1, true);

    }

    /**
     * Adds an InsertPoint to the list of insertPoints
     * @param type of InsertPoint
     * @param pos insert position in the source code
     * @param isBlock is the InsertPoint in a block, used for return statements and unroll statements to determine if a block is needed
     */
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
        JavaProfiler.println(String.format("line=%3d col=%3d charPos=%4d kind=%2d val=%s \n", token.line, token.col, token.charPos, token.kind, (token.val != null ? token.val : "--")));
    }
}

