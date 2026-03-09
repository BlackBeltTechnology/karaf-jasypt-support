# Karaf Commands Specification

## Purpose

Provides Apache Karaf console commands under the `jasypt:` scope for interactive encryption, decryption, digest computation, and algorithm discovery. Each command creates its own Jasypt encryptor/digester instance (independent of the OSGi service) and outputs results to the console.

## Architecture

- **`Encrypt`** — `jasypt:encrypt` command implementing `Action`
- **`Decrypt`** — `jasypt:decrypt` command implementing `Action`
- **`Digest`** — `jasypt:digest` command implementing `Action`
- **`Info`** — `jasypt:info` command implementing `Action`
- **`OutputTypeCompleter`** — `Completer` for output format options (`base64`, `hexadecimal`)
- **`PBEAlgorithmCompleter`** — `Completer` listing available PBE algorithms from `AlgorithmRegistry`
- **`DigestAlgorithmCompleter`** — `Completer` listing available digest algorithms from `AlgorithmRegistry`

All commands are annotated with `@Command(scope = "jasypt")` and `@Service` for auto-discovery by Karaf.

## Requirements

### Requirement: Encrypt command SHALL encrypt text using PBE

The `jasypt:encrypt` command SHALL accept a text argument and produce an encrypted value using `StandardPBEStringEncryptor`.

#### Scenario: Encrypt with default settings
- **GIVEN** the `ENCRYPTION_PASSWORD` environment variable is set
- **WHEN** `jasypt:encrypt "hello"` is executed
- **THEN** the command SHALL output `Encrypted value is: <encrypted_text>` using `PBEWithSHA1AndDESEDE` algorithm and `base64` output

#### Scenario: Encrypt with custom algorithm and password
- **WHEN** `jasypt:encrypt --algorithm PBEWithMD5AndTripleDES --password mypass "hello"` is executed
- **THEN** the command SHALL use the specified algorithm and password for encryption

#### Scenario: Encrypt with hexadecimal output
- **WHEN** `jasypt:encrypt --outputType hexadecimal "hello"` is executed
- **THEN** the output SHALL be in hexadecimal format

### Requirement: Decrypt command SHALL decrypt text using PBE

The `jasypt:decrypt` command SHALL accept encrypted text and produce the original plaintext.

#### Scenario: Decrypt with matching parameters
- **GIVEN** text was encrypted with algorithm `PBEWithSHA1AndDESEDE` and a known password
- **WHEN** `jasypt:decrypt --password <same_password> "<encrypted_text>"` is executed
- **THEN** the command SHALL output `Decrypted value is: <original_text>`

### Requirement: Digest command SHALL compute or validate digests

The `jasypt:digest` command SHALL compute a digest of input text, or validate text against a provided digest.

#### Scenario: Compute digest with defaults
- **WHEN** `jasypt:digest "hello"` is executed
- **THEN** the command SHALL output `Digest value is: <hex_digest>` using `SHA` algorithm and hexadecimal output

#### Scenario: Validate digest
- **GIVEN** a previously computed digest value
- **WHEN** `jasypt:digest --digest <digest_value> "hello"` is executed
- **THEN** the command SHALL output `Validation result: true` if the digest matches, or `false` otherwise

#### Scenario: Custom digest parameters
- **WHEN** `jasypt:digest --algorithm SHA-256 --saltSize 16 --iterations 1000 "hello"` is executed
- **THEN** the digester SHALL use the specified algorithm, salt size, and iteration count

### Requirement: Info command SHALL list available algorithms

The `jasypt:info` command SHALL print all available digest algorithms and PBE algorithms from the Jasypt `AlgorithmRegistry`.

#### Scenario: List algorithms
- **WHEN** `jasypt:info` is executed
- **THEN** the command SHALL output two lines: `Digest algorithms: [...]` and `PBE algorithms: [...]`

### Requirement: Commands SHALL fall back to environment variable for password

When no `--password` option is provided to `jasypt:encrypt` or `jasypt:decrypt`, the command SHALL read the password from the `ENCRYPTION_PASSWORD` environment variable.

#### Scenario: No password option
- **GIVEN** `ENCRYPTION_PASSWORD` is set to `secret`
- **WHEN** `jasypt:encrypt "hello"` is executed without `--password`
- **THEN** the command SHALL use the value of `ENCRYPTION_PASSWORD` as the encryption password

### Requirement: Tab completion SHALL be provided for options

Shell completers SHALL provide tab-completion for `--algorithm` and `--outputType` options.

#### Scenario: Output type completion
- **WHEN** a user presses Tab after `--outputType`
- **THEN** the completer SHALL suggest `base64` and `hexadecimal`

#### Scenario: PBE algorithm completion
- **WHEN** a user presses Tab after `--algorithm` in `jasypt:encrypt` or `jasypt:decrypt`
- **THEN** the completer SHALL suggest all PBE algorithms from `AlgorithmRegistry.getAllPBEAlgorithms()`

#### Scenario: Digest algorithm completion
- **WHEN** a user presses Tab after `--algorithm` in `jasypt:digest`
- **THEN** the completer SHALL suggest all digest algorithms from `AlgorithmRegistry.getAllDigestAlgorithms()`
