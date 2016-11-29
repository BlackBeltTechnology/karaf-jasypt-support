package hu.blackbelt.karaf.jasypt.commands;

import hu.blackbelt.karaf.jasypt.Activator;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 * Apache Karaf command to encrypt a string.
 */
@Command(scope = "jasypt", name = "encrypt", description = "Encrypt String value.")
@Service
public class Encrypt implements Action {

    @Argument(index = 0, name = "text", description = "Text to encrypt", required = true, multiValued = false)
    private String text;

    @Option(name = "--algorithm", description = "Algorithm of encryption", required = false, multiValued = false)
    @Completion(PBEAlgorithmCompleter.class)
    private String algorithm = Activator.ENCRYPTION_ALGORITHM;

    @Option(name = "--password", description = "Password for encryption", required = false, multiValued = false)
    private String password;
    
    @Option(name = "--outputType", description = "Output type", required = false, multiValued = false)
    @Completion(OutputTypeCompleter.class)
    private String outputType = OutputTypeCompleter.BASE64;

    @Override
    public Object execute() {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        final EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(algorithm);
        if (password != null) {
            config.setPassword(password);
        } else {
            config.setPasswordEnvName(Activator.ENCRYPTION_PASSWORD_ENV_NAME);
        }
        config.setStringOutputType(outputType);
        encryptor.setConfig(config);
        
        System.out.println("Encrypted value is: " + encryptor.encrypt(text));

        return null;
    }
}
