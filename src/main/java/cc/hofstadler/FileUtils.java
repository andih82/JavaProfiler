package cc.hofstadler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileUtils{

    /**
     * Reads a file to a string
     * @param path
     * @return
     */
    public static String readFileToString(String path){
        Path src = Paths.get(path).toAbsolutePath();
        String fileString = "";
        try{
            fileString = Files.readString(src, StandardCharsets.ISO_8859_1);
        }catch (IOException ioe){
            System.err.println("Could not read file " + path);
        }
        return fileString;
    }

    /**
     * Writes a string to a file
     * @param srcString
     * @param fileName
     * @param destDir
     * @return
     */
    public static Path writeStringToFile(String srcString, String fileName, String destDir){
        JavaProfiler.println("Writing " + fileName + " to " + destDir);
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

}