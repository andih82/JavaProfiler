package cc.hofstadler;

import java.util.List;
import java.util.stream.Collectors;

public class Instrumenter {


    public static String instrument(String srcString, List<InsertPoint> insertPoints){
        System.out.println("Instrumenting... ");
        StringBuilder sb = new StringBuilder(srcString);
        insertPoints.stream()
                .sorted((a,b) -> Integer.compare(b.charPos,a.charPos))
                .forEach(insertPoint -> {
                    switch (insertPoint.typ){
                        case InsertPoint.BEGIN -> sb.insert(insertPoint.charPos ,
                                "_M.beg("+ insertPoint.nClass +", " + insertPoint.nMethod +");");
                        case InsertPoint.END -> sb.insert(insertPoint.charPos, "_M.end();");
                        case InsertPoint.RETURN -> {
                            if(insertPoint.isBlock) {sb.insert(insertPoint.charPos, "_M.end();");}
                            else  {
                                sb.insert(sb.indexOf(";", insertPoint.charPos ) +1,  "}");
                                sb.insert(insertPoint.charPos, "{_M.end();");
                            }
                        }
                        case InsertPoint.UNROLL -> sb.insert(insertPoint.charPos, "_M.unrollTo("+ insertPoint.nClass +", " + insertPoint.nMethod +");");
                        case InsertPoint.IMPORT -> sb.insert(insertPoint.charPos, "import measurement._M;");
                    }
                } );
        return sb.toString();
    }

    public static String init_M(String frameString, String javaPackage, List<String> classes, List<List<String>> methodes){
        javaPackage = "package measurement;";
        String classesInit = classes.stream()
                .collect(Collectors.joining("\", \"","\"", "\""));
        StringBuilder methodSb = new StringBuilder();
        for(List<String> m: methodes){
            methodSb.append(m.stream()
                    .collect(Collectors.joining("\", \"","{\"", "\"},")));
            methodSb.append("\n");
        }
        if (methodSb.toString().endsWith(",")) methodSb.deleteCharAt(methodSb.lastIndexOf(","));
        return  frameString.replace("%classArray%", classesInit)
                .replace("%methodArrays%", methodSb.toString())
                .replace("%javaPackage%", javaPackage);
    }

}
