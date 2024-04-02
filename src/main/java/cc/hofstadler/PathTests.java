package cc.hofstadler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTests {

    public static void main(String[] args){
        Path p = Paths.get("JavaProfiler.java");
        System.out.println(p);
        System.out.println(p.toAbsolutePath());
        System.out.println(p.toAbsolutePath().getParent());
    }
}
