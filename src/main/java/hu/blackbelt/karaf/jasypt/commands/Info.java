package hu.blackbelt.karaf.jasypt.commands;

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
