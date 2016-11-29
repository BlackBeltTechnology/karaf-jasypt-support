package hu.blackbelt.karaf.jasypt.commands;

import java.util.List;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;

/**
 * Get list of output formats.
 */
@Service
public class OutputTypeCompleter implements Completer {

    public static final String BASE64 = "bas64";
    public static final String HEXADECIMAL = "hexadecimal";

    @Override
    public int complete(final Session session, final CommandLine commandLine, final List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        delegate.getStrings().add(HEXADECIMAL);
        delegate.getStrings().add(BASE64);
        return delegate.complete(session, commandLine, candidates);
    }
}
