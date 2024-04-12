package cc.hofstadler;

import java.util.List;
import java.util.stream.Collectors;

public class Instrumenter {

    private final List<String> classes;
    private final List<List<String>> methodes;
    private final List<InsertPoint> insertPoints;

    public Instrumenter(List<String> classes, List<List<String>> methodes, List<InsertPoint> insertPoints ){
        this.classes = classes;
        this.methodes = methodes;
        this.insertPoints = insertPoints;
    }

    public String instrument(String srcString){
        StringBuilder sb = new StringBuilder(srcString);
        insertPoints.stream()
                .sorted((a,b) -> Integer.compare(b.charPos,a.charPos))
                .forEach(insertPoint -> {
                    switch (insertPoint.typ){
                        case InsertPoint.BEGINN -> sb.insert(insertPoint.charPos ,
                                "_M.beg("+ insertPoint.nClass +", " + insertPoint.nMethod +");");
                        case InsertPoint.END -> sb.insert(insertPoint.charPos, "_M.end();");
                        case InsertPoint.RETURN -> {
                            if(insertPoint.isBlock) sb.insert(insertPoint.charPos, "_M.end();");
                            else  {
                                sb.insert(sb.indexOf(";", insertPoint.charPos + 1),  "}");
                                sb.insert(insertPoint.charPos, "{_M.end();");
                            }
                        }
                        case InsertPoint.START -> sb.insert(insertPoint.charPos, "_M.init(); _M.beg("+ insertPoint.nClass +", " + insertPoint.nMethod +");");
                        case InsertPoint.EXIT -> sb.insert(insertPoint.charPos, "_M.end(); _M.printResults();");
                        case InsertPoint.UNROLL -> sb.insert(insertPoint.charPos, "_M.unrollTo("+ insertPoint.nClass +", " + insertPoint.nMethod +");");

                    }
                } );
        return sb.toString();
    }

    public String init_M(String frameString){
        String classesInit = classes.stream()
                .collect(Collectors.joining("\",\"","\"", "\""));
        StringBuilder methodSb = new StringBuilder();
        for(List<String> m: methodes){
            methodSb.append(m.stream()
                    .collect(Collectors.joining("\",\"","new String[]{\"", "\"},")));
            methodSb.append("\n");
        }
        methodSb.deleteCharAt(methodSb.lastIndexOf(","));
        return  frameString.replace("%classArray%", classesInit)
                .replace("%methodArrays%", methodSb.toString());
    }


}
