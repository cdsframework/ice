package org.opencds.war.test;

import org.apache.maven.project.MavenProject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigGenerator {
    public static void run(MavenProject project) throws RuntimeException {
        var basePath = project.getBasedir().toPath();
        var classesPath = Paths.get(basePath.toString(),
                        "target",
                        "classes")
                .toFile()
                .getAbsolutePath();
        var krPath = Paths.get(classesPath,
                        "k-repo")
                .toFile()
                .getAbsolutePath();
        var doPath = Paths.get(classesPath,
                        "dot-opencds",
                        "sec.xml")
                .toFile()
                .getAbsolutePath();
        var props = new Properties();
        props.put("knowledge-repository.type", "SIMPLE_FILE");
        props.put("knowledge-repository.path", krPath);
        props.put("config.security", doPath);
        props.put("km.threads", "1");
        Path output = Paths.get(classesPath,
                        "dot-opencds",
                        "opencds-test.properties");
        output.getParent().toFile().mkdirs();
        try (var writer = new FileWriter(output.toFile())) {
            props.store(writer, "PROPERTIES FOR FUNCTIONAL TESTING");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
