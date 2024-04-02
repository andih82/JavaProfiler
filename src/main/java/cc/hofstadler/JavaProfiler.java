package cc.hofstadler;

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


		Scanner scanner = new Scanner(args[0]);
		Parser parser = new Parser(scanner);
		parser.Parse();
		Instrumenter instrumenter = new Instrumenter(parser.classes, parser.methodes, parser.insertPoints);

		String instrumented = instrumenter.instrument(FileUtils.readFileToString(args[0]));
		String inited_M = instrumenter.init_M(FileUtils.readFileToString("C:\\workspace\\JavaProfiler\\Project\\JavaProfiler\\src\\main\\java\\cc\\hofstadler\\_M.frame") );

		FileUtils.writeStringToFile(instrumented, absolutePath,fileName, profileDir);
		FileUtils.writeStringToFile(inited_M,absolutePath,"_M.java", profileDir);

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
