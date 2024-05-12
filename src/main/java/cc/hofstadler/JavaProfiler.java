package cc.hofstadler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaProfiler {

	protected static String mainFileName;
	protected static String outDir;
	protected static String srcDir;
	protected static String passArgs;
	private static String javaPackage = "";

	protected static Path mainFilePath;
	protected static Path outDirPath;
	protected static Path srcDirPath;

	private static List<Path> fileList;

	private static Map<Path,List<InsertPoint>> metaData = new HashMap<>();


	public static void main (String[] args) throws IOException {
		parseArgs(args);
		initArgs();

		analyizeAndInstrument();


		compileAndRun();
		System.out.println("done.");
	}

	private static void analyizeAndInstrument() throws IOException {
		Locator loc = new Locator();

		if(srcDirPath != null) {
			fileList = Files.walk(srcDirPath).filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".java"))
					.map(p ->
							FileUtils.writeStringToFile(
									Instrumenter.instrument(FileUtils.readFileToString(p.toString()), analyzeFile(p, loc)),
									p.getFileName().toString(),
									outDirPath.toString() + File.separator + srcDirPath.relativize(p.getParent())
							)).collect(Collectors.toList());
		}else{
			fileList = new ArrayList<>();
			Path p = Paths.get(mainFileName);
			fileList.add(FileUtils.writeStringToFile(
					Instrumenter.instrument(FileUtils.readFileToString(p.toString()), analyzeFile(p, loc)),
					p.getFileName().toString(),
					outDirPath.toString()
			));
		}
		fileList.add(
				init_M(loc.getClasses(), loc.getMethodes(), javaPackage)
		);
	}

	private static void compileAndRun() {
		System.out.println();
		System.out.println("#############################################################");
		System.out.println("# Compile ...                                               #");
		System.out.println("#############################################################");
		System.out.println();
		Executor exec = new Executor();
		File mainFile = outDirPath.resolve(mainFileName).toFile();
		try {
			exec.compile(fileList);
			exec.run(outDirPath.toString(), mainFile.getName(), javaPackage, passArgs);
		}catch (IOException ioe){
			System.out.println(ioe.getMessage());
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
	}

	private static Path init_M(List<String> classes, List<List<String>> methodes, String javaPackage) {
		String inited_M =Instrumenter.init_M(FileUtils.readFileToString("src\\main\\java\\cc\\hofstadler\\_M.frame"), javaPackage, classes, methodes);
		return FileUtils.writeStringToFile(inited_M,"_M.java", outDirPath.toString() +File.separator+"measurement");
	}

	private static List<InsertPoint> analyzeFile(Path file, Locator loc) {
		loc.clear();
		System.out.println("Analyzing " + file);
		Scanner scanner = new Scanner(file.toString());
		Parser parser = new Parser(scanner);
		parser.loc = loc;
		parser.Parse();
		System.out.println(parser.errors.count + " errors detected");
		metaData.put(file, loc.getInsertPoints());
		return loc.getInsertPoints();
	}

	private static void initArgs() throws IOException {
		mainFilePath = Paths.get(mainFileName).toAbsolutePath().getParent();

		if(srcDir != null) {
			srcDirPath = Paths.get(srcDir).toAbsolutePath();
			if (!Files.isDirectory(srcDirPath)) {
				throw new IllegalArgumentException("Not a directory: " + srcDir);
			}
		}

		if(outDir != null){
			outDirPath = Paths.get(outDir).toAbsolutePath().resolve(".profile");
		}else {
			outDirPath = srcDirPath != null ? srcDirPath.resolve(".profile") : mainFilePath.resolve(".profile");
		}

		if(outDirPath.toFile().exists()){
			System.out.println("Directory " + outDirPath +" already exists. Delete all content? (y/[n])");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String answer = reader.readLine();
			if(answer.equals("y")){
				Files.walk(outDirPath)
						.sorted((a, b) -> b.toString().length() - a.toString().length())
						.map(Path::toFile)
						.forEach(File::delete);
			}else{
				System.out.println("Aborted.");
				System.exit(0);
			}
		}else {
			Files.createDirectories(outDirPath);
		}

	}

	protected static void parseArgs(String[] args) {
		if(args.length < 1){
			System.out.println("Usage: JavaProfiler [-p <package>] [-o <output>] [-s <src>] <file>");
    	} else {
			int i = 0;
			loop: while (i < args.length) {
				switch (args[i]) {
					case "-o" -> outDir = args[++i];
					case "-s" -> srcDir = args[++i];
					case "-p" ->  javaPackage = args[++i];
					default -> {break loop;}
				}
				i++;
			}
			mainFileName = args[i++];
			passArgs = Arrays.stream(args).skip(i).collect(Collectors.joining(" "));
		}
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

	public static Path getAbsoluteDir(String path){
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

	public static Path writeStringToFile(String srcString, String fileName, String destDir){
		System.out.println("Writing " + fileName + " to " + destDir);
		Path target = Paths.get( destDir, fileName);
		try {
			if(Files.notExists(target.getParent())){
				Files.createDirectories(target.getParent());
			}
			return Files.writeString(target, srcString);
		}catch (IOException ioe){
			System.err.println("Could not write file " + target);
			throw new RuntimeException(ioe);
		}
	}


	public static String getFileName(String arg) {
		Path p = Paths.get(arg);
		if(Files.isRegularFile(p)){
			return p.getFileName().toString();
		}else throw new IllegalArgumentException("Not a file: " + arg);
	}

	public static List<Path> getAllJavaFiles(Path path) throws IOException {

		List<Path> result;
		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".java")).peek(p->toOutpath(p)).toList();
		}
		return result;
	}

	private static void toOutpath(Path p){
		Path out = p.getParent().resolve(".profile"+System.currentTimeMillis()).resolve(p.getFileName());
		System.out.println(out);
	}
}
