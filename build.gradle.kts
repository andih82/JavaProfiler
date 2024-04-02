


plugins {
    id("java")
}

group = "cc.hofstadler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly ( files("coco/Coco.jar"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runCoco"){
    classpath = files("coco/Coco.jar")
    mainClass = "Coco/Coco"
    args = listOf("${project.rootDir}\\coco\\JavaWithBlockDepth.atg", "-package", "cc.hofstadler", "-o", "${project.rootDir}\\src/main/java/cc/hofstadler/")
}