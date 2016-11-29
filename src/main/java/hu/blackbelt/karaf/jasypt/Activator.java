package hu.blackbelt.karaf.jasypt;

import java.util.Dictionary;
import java.util.Hashtable;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi bundle activator. It is used to register/unregister default StringEncryptor (service ranking is set to highest value). StringEncryptor service is used
 * also by PAX-JDBC config.
 */
@Slf4j
public class Activator implements BundleActivator {

    /**
     * Algorithm of default StringEncryptor.
     */
    public static final String ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESEDE";
    
    /**
     * Environment variable of password for default StringEncryptor.
     */
    public static final String ENCRYPTION_PASSWORD_ENV_NAME = "ENCRYPTION_PASSWORD";

    private ServiceRegistration<StringEncryptor> stringEncryptor;

    /**
     * Register StringEncryptor service instance.
     *
     * @param context bundle context
     */
    @Override
    public void start(final BundleContext context) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        final EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(ENCRYPTION_ALGORITHM);
        config.setPasswordEnvName(ENCRYPTION_PASSWORD_ENV_NAME);
        encryptor.setConfig(config);

        final Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        stringEncryptor = context.registerService(StringEncryptor.class, encryptor, dict);
    }

    /**
     * unregister StringEncryptor service instance.
     *
     * @param context bundle context
     */
    @Override
    public void stop(final BundleContext context) {
        try {
            if (stringEncryptor != null) {
                stringEncryptor.unregister();
            }
        } finally {
            stringEncryptor = null;
        }
    }
}
