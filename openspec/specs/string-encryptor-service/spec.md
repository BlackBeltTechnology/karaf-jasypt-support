# String Encryptor Service Specification

## Purpose

Registers a default `StringEncryptor` OSGi service using Jasypt's `StandardPBEStringEncryptor`, configured via OSGi ConfigAdmin. The service is used by PAX-JDBC and other consumers that need transparent password-based encryption within the Karaf container.

## Architecture

- **`DefaultStringEncryptorConfig`** — OSGi Declarative Services component (`@Component`, `immediate = true`, `configurationPolicy = REQUIRE`)
- **`DefaultStringEncryptorConfig.Config`** — `@ObjectClassDefinition` annotation interface defining configurable properties
- Uses **composition** with `StandardPBEStringEncryptor` and `EnvironmentStringPBEConfig` (Jasypt classes are `final`)
- Registers `StringEncryptor` service with `Integer.MAX_VALUE` ranking via `BundleContext.registerService()`

## Requirements

### Requirement: Service SHALL require configuration to activate

The component uses `ConfigurationPolicy.REQUIRE`, so it SHALL NOT activate without a ConfigAdmin configuration entry.

#### Scenario: No configuration present
- **GIVEN** the bundle is installed in a Karaf container
- **WHEN** no configuration with PID `hu.blackbelt.karaf.jasypt.services.DefaultStringEncryptorConfig` exists
- **THEN** the `DefaultStringEncryptorConfig` component SHALL NOT be activated and no `StringEncryptor` service SHALL be registered

#### Scenario: Configuration present
- **GIVEN** the bundle is installed in a Karaf container
- **WHEN** a configuration with PID `hu.blackbelt.karaf.jasypt.services.DefaultStringEncryptorConfig` is created
- **THEN** the `DefaultStringEncryptorConfig` component SHALL activate and register a `StringEncryptor` service

### Requirement: Service SHALL be registered with highest priority

The `StringEncryptor` service SHALL be registered with `Constants.SERVICE_RANKING` set to `Integer.MAX_VALUE` to ensure it takes precedence over other encryptor implementations.

#### Scenario: Multiple encryptors present
- **GIVEN** other `StringEncryptor` services are registered in the OSGi registry
- **WHEN** `DefaultStringEncryptorConfig` activates
- **THEN** its `StringEncryptor` service SHALL have the highest service ranking

### Requirement: Password SHALL be resolved from multiple sources

The service SHALL support three password sources, evaluated in priority order: direct password, password file, environment variable.

#### Scenario: Direct password configured
- **GIVEN** `encryption_password` is set in the configuration
- **WHEN** the component activates
- **THEN** the encryptor SHALL use the direct password value

#### Scenario: Password file configured
- **GIVEN** `encryption_password` is not set but `encryption_passwordFile` points to a valid file
- **WHEN** the component activates
- **THEN** the encryptor SHALL read the password from the specified file

#### Scenario: Environment variable configured
- **GIVEN** neither `encryption_password` nor `encryption_passwordFile` is set, but `encryption_passwordEnvName` is set
- **WHEN** the component activates
- **THEN** the encryptor SHALL read the password from the named environment variable

#### Scenario: Default environment variable
- **GIVEN** no password source is explicitly configured
- **WHEN** the component activates
- **THEN** the encryptor SHALL fall back to the `ENCRYPTION_PASSWORD` environment variable

### Requirement: Encryption algorithm SHALL be configurable

The `encryption_algorithm` property SHALL default to `PBEWithSHA1AndDESEDE` and be overridable via configuration.

#### Scenario: Default algorithm
- **WHEN** no `encryption_algorithm` is specified in configuration
- **THEN** the encryptor SHALL use `PBEWithSHA1AndDESEDE`

#### Scenario: Custom algorithm
- **GIVEN** `encryption_algorithm` is set to `PBEWithMD5AndTripleDES`
- **WHEN** the component activates
- **THEN** the encryptor SHALL use the specified algorithm

### Requirement: Service SHALL support alias registration

When `encryptor_alias` is configured, the service SHALL include an `alias` property in its service registration.

#### Scenario: Alias configured
- **GIVEN** `encryptor_alias` is set to `myEncryptor`
- **WHEN** the component activates
- **THEN** the `StringEncryptor` service registration SHALL include property `alias=myEncryptor`

### Requirement: Service SHALL be unregistered on deactivation

When the component is deactivated, the `StringEncryptor` service SHALL be unregistered from the OSGi service registry.

#### Scenario: Component deactivation
- **GIVEN** the component is active with a registered `StringEncryptor` service
- **WHEN** the component's `@Deactivate` method is called
- **THEN** the `StringEncryptor` service SHALL be unregistered
