package cc.hofstadler;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Instrumenter {

    /**
     * Instruments the source code with the given insert points
     * @param srcString
     * @param insertPoints
     * @return
     */
    public static String instrument(String srcString, List<InsertPoint> insertPoints){
        JavaProfiler.println("Instrumenting... ");
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

    /**
     * Initializes the measurement class with the given classes and methodes
     * @param frameString
     * @param classes
     * @param methodes
     * @param outDirPath
     * @return
     */
    public static String init_M(String frameString, List<String> classes, List<List<String>> methodes , Path outDirPath){
        String javaPackage = "package measurement;";
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
                .replace("%javaPackage%", javaPackage)
                .replace("%outPathDir%", outDirPath.toString().replace("\\","/") );
    }

}
