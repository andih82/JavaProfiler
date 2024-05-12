package cc.hofstadler;


import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JavaProfilerTest {

    @Test
    void shouldSetFileNameWhenOnlyFileNameProvided() {
        JavaProfiler.parseArgs(new String[]{"file.java"});
        assertEquals("file.java", JavaProfiler.mainFileName);
    }

    @Test
    void shouldSetOutPathWhenOptionOProvided() {
        JavaProfiler.parseArgs(new String[]{"-o", "output", "file.java"});
        assertEquals("output", JavaProfiler.outDir);
    }

    @Test
    void shouldSetSrcPathWhenOptionSProvided() {
        JavaProfiler.parseArgs(new String[]{"-s", "src", "file.java"});
        assertEquals(Paths.get("src"), JavaProfiler.srcDirPath);
    }

    @Test
    void shouldSetFileNameWhenMultipleOptionsProvided() {
        JavaProfiler.parseArgs(new String[]{"-o", "output", "-s", "src", "file.java"});
        assertEquals("file.java", JavaProfiler.mainFileName);
    }

    @Test
    void shouldIgnoreExtraArguments() {
        JavaProfiler.parseArgs(new String[]{"-o", "output", "-s", "src", "file.java", "extra", "-extra", "more" });
        assertEquals("file.java", JavaProfiler.mainFileName);
        assertEquals("output", JavaProfiler.outDir);
        assertEquals(Paths.get("src"), JavaProfiler.srcDirPath);
        assertEquals("extra -extra more", JavaProfiler.passArgs);
    }





}