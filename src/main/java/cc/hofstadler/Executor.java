package cc.hofstadler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class Executor {

    public void compile(List<Path> files) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromPaths(files);
        compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

        fileManager.close();
    }

    public void run(String path, String mainFile, String javaPackage , String args) throws IOException, InterruptedException {
        String execName =mainFile.substring(0, mainFile.lastIndexOf(".") );

        if (javaPackage != null && !javaPackage.isEmpty()) {
            execName = javaPackage + "." + execName;
        }

        System.out.println("java -cp " + path + " " + execName + " " + args);
        Process p = Runtime.getRuntime().exec("java -cp " + path + " " + execName + " " + args);

        p.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("..> " + line);
        }
        BufferedReader ein = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = ein.readLine()) != null) {
            System.err.println(".!> " + line);
        }
        p.waitFor();
    }
}