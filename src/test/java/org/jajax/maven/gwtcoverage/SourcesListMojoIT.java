package org.jajax.maven.gwtcoverage;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class SourcesListMojoIT {
    @Test
    public void test_sourceslist() throws Exception {
        File testDir = runMaven();
        File coverageSources = new File(testDir, "target/gwt-coverage-sources.txt");
        assertTrue(coverageSources.exists());
        assertThat(coverageSources).hasSameContentAs(new File(testDir, "gwt-coverage-sources-target.txt"));
    }

    private File runMaven() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/gwtcoverage-test");

        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("org.jajax.maven.test", "gwtcoverage-test", "1.0-SNAPSHOT", "jar");
        verifier.executeGoal("generate-sources");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        return testDir;
    }
}
