package hu.blackbelt.karaf.jasypt.services;

import java.util.Dictionary;
import java.util.Hashtable;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * StringEncryptor configuration service.
 *
 * <p>
 * It is used to register/unregister default StringEncryptor (service ranking is set to highest value). StringEncryptor service is used also by PAX-JDBC
 * config.</p>
 *
 * <p>
 * Note: composition is used instead of inheritance because Jasypt classes are final.</p>
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, service = DefaultStringEncryptorConfig.class)
@Designate(ocd = DefaultStringEncryptorConfig.Config.class)
public class DefaultStringEncryptorConfig {

    @SuppressWarnings("checkstyle:JavadocMethod")
    @ObjectClassDefinition(name = "Default StringEncryptor configuration")
    public @interface Config {

        @AttributeDefinition(required = false, name = "Encryption algorithm")
        String encryption_algorithm() default DEFAULT_ENCRYPTION_ALGORITHM;

        @AttributeDefinition(required = false, name = "Password for encryption", type = AttributeType.PASSWORD)
        String encryption_password();

        @AttributeDefinition(required = false, name = "Environment variable holding encryption algorithm")
        String encryption_passwordEnvName();

        @AttributeDefinition(required = false, name = "Alias for encryptor")
        String encryptor_alias();
    }

    /**
     * Algorithm of default StringEncryptor.
     */
    public static final String DEFAULT_ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESEDE";

    /**
     * Environment variable of password for default StringEncryptor.
     */
    public static final String DEFAULT_ENCRYPTION_PASSWORD_ENV_NAME = "ENCRYPTION_PASSWORD";

    private ServiceRegistration<StringEncryptor> defaultStringEncryptor;

    /**
     * Register StringEncryptor service instance.
     *
     * @param context bundle context
     */
    @Activate
    public void start(final BundleContext context, final Config config) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        final EnvironmentStringPBEConfig encryptorConfig = new EnvironmentStringPBEConfig();
        encryptorConfig.setAlgorithm(config.encryption_algorithm());
        if (config.encryption_password() != null) {
            encryptorConfig.setPassword(config.encryption_password());
        } else if (config.encryption_passwordEnvName() != null) {
            encryptorConfig.setPasswordEnvName(config.encryption_passwordEnvName());
        }
        encryptor.setConfig(encryptorConfig);

        final Dictionary<String, Object> dict = new Hashtable<>();
        dict.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        dict.put("algorithm", config.encryption_algorithm());
        if (config.encryptor_alias() != null) {
            dict.put("alias", config.encryptor_alias());
        }
        defaultStringEncryptor = context.registerService(StringEncryptor.class, encryptor, dict);
    }

    /**
     * unregister StringEncryptor service instance.
     *
     * @param context bundle context
     */
    @Deactivate
    public void stop(final BundleContext context) {
        try {
            if (defaultStringEncryptor != null) {
                defaultStringEncryptor.unregister();
            }
        } finally {
            defaultStringEncryptor = null;
        }
    }
}
