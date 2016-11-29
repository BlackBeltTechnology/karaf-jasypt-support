package hu.blackbelt.karaf.jasypt.commands;

import java.util.List;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.jasypt.registry.AlgorithmRegistry;

/**
 * Get PBE algorithms supported by Jasypt.
 */
@Service
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class PBEAlgorithmCompleter implements Completer {

    @Override
    public int complete(final Session session, final CommandLine commandLine, final List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        for (final Object algorithm : AlgorithmRegistry.getAllPBEAlgorithms()) {
            delegate.getStrings().add(algorithm.toString());
        }
        return delegate.complete(session, commandLine, candidates);
    }
}
