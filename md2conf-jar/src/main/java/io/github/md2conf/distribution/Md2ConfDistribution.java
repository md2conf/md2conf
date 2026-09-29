package io.github.md2conf.distribution;

/**
 * Metadata of the self-contained md2conf command-line distribution produced by this module.
 *
 * <p>This module has no business logic of its own: the {@code maven-shade-plugin} repackages the
 * {@code md2conf-command} CLI together with all of its runtime dependencies into a single executable
 * JAR. The resulting artifact is run as follows:</p>
 *
 * <pre>{@code java -jar md2conf.jar conpub --url=http://localhost:8090 --space-key=DOC}</pre>
 *
 * <p>The entry point of the shaded JAR is {@code io.github.md2conf.command.MainApp}.</p>
 *
 * <p>The class exists so that the module publishes valid {@code -sources} and {@code -javadoc}
 * artifacts, which the Sonatype Central Portal requires for every component.</p>
 */
public final class Md2ConfDistribution {

    /**
     * The name under which the shaded executable JAR is produced.
     */
    public static final String ARTIFACT_NAME = "md2conf";

    private Md2ConfDistribution() {
        throw new AssertionError("Utility class");
    }
}
