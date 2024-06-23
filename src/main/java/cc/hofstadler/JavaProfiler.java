package cc.hofstadler;


import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JavaProfiler {

    protected static String mainFileName;
    protected static String outDir;
    protected static String srcDir;
    protected static String passArgs;
    protected static String javaPackage = "";
    protected static String extClassPath;

    protected static Path mainFilePath;
    protected static Path outDirPath;
    protected static Path srcDirPath;
    protected static boolean verbose = false;

    private static List<Path> fileList;

    public static void main(String[] args) throws IOException {
        parseArgs(args);
        initArgs();
        analyzeAndInstrument();
        compileAndRun();

        try {
            // open report in default browser
            Desktop.getDesktop().open(outDirPath.resolve("report.html").toFile());
        } catch (Exception ioe) {
            System.err.println("Could not open report in browser");
            System.out.println("Please open " + outDirPath.resolve("report.html") + " in your browser.");
        }
    }

    private static void analyzeAndInstrument() throws IOException {
        println("#############################################################");
        println("# Analyze ...                                               #");
        println("#############################################################");
        Locator loc = new Locator();

        if (srcDirPath != null) {
            fileList = Files.walk(srcDirPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(p ->
                            FileUtils.writeStringToFile(
                                    Instrumenter.instrument(FileUtils.readFileToString(p.toString()), analyzeFile(p, loc)),
                                    p.getFileName().toString(),
                                    outDirPath.toString() + File.separator + srcDirPath.relativize(p.getParent())
                            )).collect(Collectors.toList());
        } else {
            fileList = new ArrayList<>();
            Path p = Paths.get(mainFileName);
            fileList.add(FileUtils.writeStringToFile(
                    Instrumenter.instrument(FileUtils.readFileToString(p.toString()), analyzeFile(p, loc)),
                    p.getFileName().toString(),
                    outDirPath.toString()
            ));
        }
        fileList.add(init_M(loc.getClasses(), loc.getMethodes()));
    }

    private static void compileAndRun() {
        println("#############################################################");
        println("# Compile ...                                               #");
        println("#############################################################");


        Executor exec = new Executor(outDirPath.toString(), extClassPath);
        try {
            exec.compile(fileList, extClassPath);
            File mainFile = outDirPath.resolve(mainFileName).toFile();
            exec.run(mainFile.getName(), javaPackage, passArgs);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path init_M(List<String> classes, List<List<String>> methodes) {
        String resource = "_M.frame";
        String frame;

        try {
            InputStream input = JavaProfiler.class.getResourceAsStream(resource);
            if (input == null) {
                // this is how we load file within editor (eg eclipse), fallback for development
                input = JavaProfiler.class.getClassLoader().getResourceAsStream(resource);
            }
            frame = new String(input.readAllBytes(), StandardCharsets.ISO_8859_1);
            input.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not read resource " + resource);
        }

        String inited_M = Instrumenter.init_M(frame, classes, methodes, outDirPath);
        return FileUtils.writeStringToFile(inited_M, "_M.java", outDirPath.toString() + File.separator + "measurement");
    }

    protected static List<InsertPoint> analyzeFile(Path file, Locator loc) {
        loc.clear();
        println("Analyzing " + file);
        Scanner scanner = new Scanner(file.toString());
        Parser parser = new Parser(scanner);
        parser.loc = loc;
        parser.Parse();
        println(parser.errors.count + " errors detected");
        return loc.getInsertPoints();
    }

    private static void initArgs() throws IOException {
        mainFilePath = Paths.get(mainFileName).toAbsolutePath().getParent();

        if (srcDir != null) {
            srcDirPath = Paths.get(srcDir).toAbsolutePath();
            if (!Files.isDirectory(srcDirPath)) {
                throw new IllegalArgumentException("Not a directory: " + srcDir);
            }
            javaPackage = javaPackage.isEmpty() ? srcDirPath.relativize(mainFilePath).toString().replace(File.separator, ".") : javaPackage;
        }

        if (outDir != null) {
            outDirPath = Paths.get(outDir).toAbsolutePath().resolve(".profile");
        } else {
            outDirPath = srcDirPath != null ? srcDirPath.resolve(".profile") : mainFilePath.resolve(".profile");
        }

        if (outDirPath.toFile().exists()) {
//			System.out.println("Directory " + outDirPath +" already exists. Delete all content? (y/[n])");
//			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			String answer = reader.readLine();
//			if(answer.equals("y")){
            Files.walk(outDirPath)
                    .sorted((a, b) -> b.toString().length() - a.toString().length())
                    .map(Path::toFile)
                    .forEach(File::delete);
//			}else{
//				System.out.println("Aborted.");
//				System.exit(0);
//			}
        } else {
            Files.createDirectories(outDirPath);
        }

    }

    protected static void parseArgs(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: JavaProfiler [-v] [-o <output>] [-s <src>] [-cp <extClassPath>]<file> [passArgs]");
            System.out.println("""
                    -v                 : verbose
                    -o <output>        : output directory
                    -s <src>           : source directory, only needed when the program has multiple classes
                    -cp <extClassPath> : external class path passed to the compiler option -cp 
                    <file>             : main file 
                    passArgs           : arguments passed to main method
                    """);
            System.exit(0);
        } else {
            int i = 0;
            loop:
            while (i < args.length) {
                switch (args[i]) {
                    case "-o" -> outDir = args[++i];
                    case "-s" -> srcDir = args[++i];
                    case "-cp" -> extClassPath = args[++i];
                    case "-v" -> verbose = true;
                    default -> {
                        break loop;
                    }
                }
                i++;
            }
            mainFileName = args[i++];
            passArgs = Arrays.stream(args).skip(i).collect(Collectors.joining(" "));
        }
    }

    public static void println(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

}


