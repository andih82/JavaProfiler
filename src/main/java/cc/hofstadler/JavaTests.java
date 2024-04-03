package cc.hofstadler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaTests {

    public static void main(String[] args){
        Path p = Paths.get("JavaProfiler.java");
        System.out.println(p);
        System.out.println(p.toAbsolutePath());
        System.out.println(p.toAbsolutePath().getParent());
    }

}
