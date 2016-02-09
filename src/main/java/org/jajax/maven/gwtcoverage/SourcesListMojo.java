package org.jajax.maven.gwtcoverage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This Mojo creates a list of all source files of a Maven project for GWT coverage.
 */
@Mojo(name = "sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SourcesListMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}/gwt-coverage-sources.txt", required = true)
    private File targetFile;

    @Component
    private MavenProject project;

    /**
     * Defines files in the source directories to include (all .java files by default).
     */
    @Parameter
    private String[] includes = {"**/*.java"};

    /**
     * Defines which of the included files in the source directories to exclude (non by default).
     */
    @Parameter
    private String[] excludes;


    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException {
        final Log log = getLog();

        File usedTargetFile = new File(targetFile.getAbsolutePath());
        log.info("creating source list file '" + usedTargetFile.getAbsolutePath() + "'");

        try {
            if (!usedTargetFile.getParentFile().exists() && !usedTargetFile.getParentFile().mkdirs())
                throw new MojoExecutionException("cannot create targetdir: " + usedTargetFile.getParentFile().getAbsolutePath());
            BufferedWriter writer = new BufferedWriter(new FileWriter(usedTargetFile));

            scanDirectories(project.getCompileSourceRoots(), writer);
            scanDirectories(project.getTestCompileSourceRoots(), writer);
            IOUtil.close(writer);
        } catch (IOException e) {
            throw new MojoExecutionException("IO-Error while generating source list file '" + usedTargetFile + "'", e);
        }
    }

    private void scanDirectories(List<String> sourceRoots, BufferedWriter writer) throws IOException {
        for (String sourceRoot : sourceRoots) {
            scanDirectory(new File(sourceRoot), writer);
        }
    }

    private void scanDirectory(File directory, BufferedWriter writer) throws IOException {
        if (!directory.exists())
            return;

        final Log log = getLog();
        log.info("scanning source directory '" + directory.getAbsolutePath() + "'");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.setBasedir(directory);
        scanner.scan();

        for (String fileName : scanner.getIncludedFiles()) {
            writer.write(fileName);
            writer.newLine();
        }
    }


}
