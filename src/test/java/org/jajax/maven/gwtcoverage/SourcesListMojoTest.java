package org.jajax.maven.gwtcoverage;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class SourcesListMojoTest {

    @Tested
    private SourcesListMojo mojo;

    @Injectable
    private MavenProject project;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Injectable
    private File targetFile;

    @Before
    public void setUp() throws Exception {
        targetFile = temporaryFolder.newFile();
    }

    @Test
    public void test_Execute() throws Exception {
        final File mainsrc = temporaryFolder.newFolder("mainsrc");
        File sourceFile = new File(mainsrc, "org/jajax/maven/test/Source.java");
        assertThat(sourceFile.getParentFile().mkdirs()).describedAs("create main source directory").isTrue();
        new FileOutputStream(sourceFile).close();
        new Expectations() {{
            project.getCompileSourceRoots();
            result = Collections.singletonList(mainsrc.getAbsolutePath());
        }};

        mojo.execute();

        List<String> content = getContent(targetFile);
        assertThat(content).containsExactly("org/jajax/maven/test/Source.java");
    }

    @Test
    public void test_Execute_inexistent_directories() throws Exception {
        new Expectations() {{
            project.getCompileSourceRoots();
            result = Collections.singletonList("/tmp/nonexistent/directory");
        }};

        mojo.execute();

        List<String> content = getContent(targetFile);
        assertThat(content).isEmpty();
    }

    @Test
    public void test_Execute_invalid_targetfile() throws Exception {
        assertThat(targetFile.delete()).describedAs("delete targetfile").isTrue();
        assertThat(targetFile.mkdirs()).describedAs("create targetfile as directory").isTrue();

        try {
            mojo.execute();
            fail("expected excetion");
        } catch (MojoExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(FileNotFoundException.class);
        }
    }

    private List<String> getContent(File file) throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        List<String> result = new ArrayList<String>();
        while ((line = read.readLine()) != null) {
            result.add(line);
        }
        read.close();
        return result;
    }
}