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

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.jasypt.registry.AlgorithmRegistry;

/**
 * Apache Karaf command to show available algorithms.
 */
@Command(scope = "jasypt", name = "info", description = "Get Jasypt information for encryption/decryption.")
@Service
public class Info implements Action {

    @Override
    public Object execute() {
        System.out.println("Digest algorithms: " + AlgorithmRegistry.getAllDigestAlgorithms());
        System.out.println("PBE algorithms: " + AlgorithmRegistry.getAllPBEAlgorithms());

        return null;
    }
}
