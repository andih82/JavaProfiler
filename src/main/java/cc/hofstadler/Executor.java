package cc.hofstadler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Executor {


    private String path;
    private String extClassPath;

    public String getClassPath() {
        return path+"/classes";
    }

    /**
     * Constructor
     * @param path the -classpath (-cp) for the profiled program
     */
    public Executor(String path, String extClassPath) {
        this.path = path.replace("\\", "/");
        if(extClassPath != null && !extClassPath.isEmpty()){
            this.extClassPath = extClassPath.replace("\\", "/");
        }
    }

    /**
     * Compiles the given files and stores the classes in the given path
     * @param files List of files to compile
     * @throws IOException
     */
    public void compile(List<Path> files, String extClassPath) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);


        List<String> options = new ArrayList<>();
        options.add("-d");
        options.add(getClassPath());
        if(extClassPath != null && !extClassPath.isEmpty()){
            options.add("-cp");
            options.add(extClassPath);
        }
        Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromPaths(files);
        compiler.getTask(null, fileManager, null, options, null, compilationUnits1).call();

        fileManager.close();
    }

    /**
     * Runs the profiled program in the given path with the given argument
     *
     * @param mainFile the main class
     * @param javaPackage if the main class is in a package
     * @param args passed to the profiled program
     * @throws IOException
     * @throws InterruptedException
     */
    public void run(String mainFile, String javaPackage , String args) throws IOException, InterruptedException {

        String execName =mainFile.substring(0, mainFile.lastIndexOf(".") );
        if (javaPackage != null && !javaPackage.isEmpty()) {
            execName = javaPackage + "." + execName;
        }

        List<String> commands = new ArrayList<>();
        commands.add("java");
        commands.add("-cp");
        if(extClassPath != null && !extClassPath.isEmpty()){
            commands.add(getClassPath() + File.pathSeparator+ extClassPath);
        }else{
            commands.add(getClassPath());
        }
        commands.add(execName);
        commands.addAll(Arrays.asList(args.split(" ")));
        System.out.println(commands.stream().collect(Collectors.joining(" ")));
        System.out.println("Running... the following output is from the profiled program:\n\n");

        Process p = new ProcessBuilder(commands)
                .inheritIO()
                .start();

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        BufferedReader ein = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = ein.readLine()) != null) {
            System.err.println(line);
        }
        p.waitFor();

        System.out.println("\n\nProgram exited with code " + p.exitValue());
    }
}