package hu.blackbelt.karaf.jasypt.commands;

/*-
 * #%L
 * Jasypt Karaf support
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import hu.blackbelt.karaf.jasypt.services.DefaultStringEncryptorConfig;
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
    private String algorithm = DefaultStringEncryptorConfig.DEFAULT_ENCRYPTION_ALGORITHM;

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
            config.setPasswordEnvName(DefaultStringEncryptorConfig.DEFAULT_ENCRYPTION_PASSWORD_ENV_NAME);
        }
        config.setStringOutputType(outputType);
        encryptor.setConfig(config);

        System.out.println("Encrypted value is: " + encryptor.encrypt(text));

        return null;
    }
}
