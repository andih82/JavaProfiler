package cc.hofstadler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaProfiler {


  	public static void main (String[] args) {
		if(args.length == 0){
        	System.out.println("Usage: Run <fileName>   - Filename of a Javafile");
        	System.exit(-1);
    	}

		String fileName = "";
		String profileDir = ".profile"+System.currentTimeMillis();
		Path absolutePath;


		try {
			fileName = FileUtils.getFileName(args[0]);
		}catch (IllegalArgumentException iae){
			System.err.println(iae.getMessage());
		}

		absolutePath = FileUtils.getAbsolutePath(args[0]);

		System.out.println();
		System.out.println("#############################################################");
		System.out.println("# Analyze ...                                               #");
		System.out.println("#############################################################");
		System.out.println();
		Scanner scanner = new Scanner(args[0]);
		Parser parser = new Parser(scanner);
		parser.loc = new Locator();
		parser.Parse();

		System.out.println();
		System.out.println("#############################################################");
		System.out.println("# Instrumment ...                                           #");
		System.out.println("#############################################################");
		System.out.println();
		Instrumenter instrumenter = new Instrumenter(parser.loc.getClasses(), parser.loc.getMethodes(), parser.loc.getInsertPoints());

		String instrumented = instrumenter.instrument(FileUtils.readFileToString(args[0]));
		String inited_M = instrumenter.init_M(FileUtils.readFileToString("src\\main\\java\\cc\\hofstadler\\_M.frame") );

		FileUtils.writeStringToFile(instrumented, absolutePath,fileName, profileDir);
		FileUtils.writeStringToFile(inited_M,absolutePath,"_M.java", profileDir);

		System.out.println();
		System.out.println("#############################################################");
		System.out.println("# Compile ...                                               #");
		System.out.println("#############################################################");
		System.out.println();
		try {
			Executor.compile(absolutePath + File.separator + profileDir);
			Executor.run(absolutePath + File.separator + profileDir , fileName.substring(0,fileName.lastIndexOf(".")));

		}catch (IOException ioe){
			System.out.println(ioe.getMessage());
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(parser.errors.count + " errors detected");
	}
}

class FileUtils{

	public static String readFileToString(String path){
		Path src = Paths.get(path);
		String fileString = "";
		try{
			fileString = Files.readString(src);
		}catch (IOException ioe){
			System.err.println("Could not read file " + path);
		}
		return fileString;
	}

	public static Path getAbsolutePath(String path){
		Path p = Paths.get(path);
		if(!p.isAbsolute()){
			p = p.toAbsolutePath();
		}
		if(Files.isDirectory(p)){
			return p;
		}else{
			return p.getParent();
		}
	}

	public static void writeStringToFile(String srcString, Path absolutePath, String fileName, String profileDir){
		Path target = Paths.get(absolutePath.toString(), profileDir, fileName);
		try {
			if(Files.notExists(target.getParent())){
				Files.createDirectories(target.getParent());
			}
			Files.writeString(target, srcString);
		}catch (IOException ioe){
			System.err.println("Could not write file " + target);
		}
	}


	public static String getFileName(String arg) {
		Path p = Paths.get(arg);
		if(Files.isRegularFile(p)){
			return p.getFileName().toString();
		}else throw new IllegalArgumentException("Not a file: " + arg);
	}
}

class Executor {

	public static void compile(String path) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> compilationUnits1 =
				fileManager.getJavaFileObjectsFromPaths(Files.list(Path.of(path)).toList());
		compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

		fileManager.close();
	}

	public static void run(String path, String mainFile) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec("java -cp " + path + " " + mainFile);

		p.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println("...  " + line);
		}
		BufferedReader ein = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = ein.readLine()) != null) {
			System.err.println("...  " + line);
		}
		p.waitFor();
	}
}