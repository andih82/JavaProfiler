plugins {
    id("java")
}

group = "cc.hofstadler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:4.2.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runCoco"){
    classpath = files("coco/Coco.jar")
    mainClass = "Coco/Coco"
    args = listOf("${project.rootDir}/coco/Java.atg", "-package", "cc.hofstadler", "-o", "${project.rootDir}/src/main/java/cc/hofstadler/")
}

tasks.withType(Jar::class) {

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "cc.hofstadler.JavaProfiler"
    }
}