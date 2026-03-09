# Jasypt Karaf Support - Project Documentation

## Project Overview


**Repository:** BlackBeltTechnology/karaf-jasypt-support
**License:** Apache License 2.0
**Java Version:** 11 (CI runs on JDK 21)
**Build System:** Maven 3.x with Maven Wrapper (`./mvnw`)

1. Provides Jasypt encryption/decryption support as an OSGi bundle for Apache Karaf
2. Registers a default `StringEncryptor` service (highest priority) used by PAX-JDBC for encrypted database passwords
3. Exposes four Karaf console commands (`jasypt:encrypt`, `jasypt:decrypt`, `jasypt:digest`, `jasypt:info`) for interactive cryptographic operations
4. Supports configurable password sources: direct value, file, or environment variable (`ENCRYPTION_PASSWORD` by default)

## Code Instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Directory Structure

```
karaf-jasypt-support/
├── src/main/java/hu/blackbelt/karaf/jasypt/
│   ├── services/          # OSGi service component (StringEncryptor registration)
│   └── commands/           # Karaf shell commands and tab completers
├── .github/
│   └── workflows/          # GitHub Actions CI/CD pipelines
├── .mvn/                   # Maven wrapper and JVM configuration
├── openspec/               # OpenSpec workflow configuration
├── pom.xml                 # Maven build configuration (single bundle module)
├── mvnw / mvnw.cmd         # Maven wrapper scripts
└── logback-test.xml        # Logging configuration for tests
```

## Core Modules

This is a single-module project (no Maven submodules). The code is organized into two packages:

### Service Layer

| Class | Purpose |
|-------|---------|
| `DefaultStringEncryptorConfig` | OSGi DS component (`@Component`) that creates and registers a `StringEncryptor` service. Uses composition over inheritance (Jasypt classes are final). Reads encryption config from OSGi ConfigAdmin properties. Registered with `Integer.MAX_VALUE` service ranking. |

### Command Layer

| Class | Purpose |
|-------|---------|
| `Encrypt` | `jasypt:encrypt` — encrypts text using PBE with configurable algorithm, password, and output format |
| `Decrypt` | `jasypt:decrypt` — decrypts text using the same parameters as encrypt |
| `Digest` | `jasypt:digest` — computes or validates a digest with configurable algorithm, salt size, and iterations |
| `Info` | `jasypt:info` — lists all available digest and PBE algorithms from the Jasypt registry |
| `OutputTypeCompleter` | Tab completion for output types (`base64`, `hexadecimal`) |
| `PBEAlgorithmCompleter` | Tab completion for PBE encryption algorithms |
| `DigestAlgorithmCompleter` | Tab completion for digest algorithms |

## Technology Stack

### Core Technologies
- **Apache Karaf 4.0.7** — OSGi container and shell framework
- **Jasypt 1.9.2** — Java simplified encryption library
- **OSGi 6.0.0** — Service platform (core, DS annotations 1.3.0, metatype annotations)
- **Lombok 1.18.34** — Boilerplate reduction (`@Slf4j`)
- **SLF4J 1.7.21** — Logging facade

### Build & Quality
- **Maven 3.x** with Maven Wrapper and CI-friendly versioning (`${revision}`)
- **Felix Maven Bundle Plugin 6.0.0** — OSGi bundle packaging
- **Flatten Maven Plugin 1.3.0** — CI-friendly POM flattening
- **JUnit 5.9.1** — Test framework
- **JaCoCo 0.8.12** — Code coverage
- **SonarQube** — Code quality analysis (hosted at `sonar.judo.technology`)

## Build Commands

```bash
# Run tests
./mvnw clean test

# Full build (compile, test, package, install)
./mvnw clean install

# Build with signing (CI)
./mvnw -B -Drevision=<version> -Psign-artifacts -Prelease-judong deploy

# Deploy to Maven Central (release branches only)
./mvnw -B -Drevision=<version> -P"release-central,sign-artifacts" -Dmaven.test.skip=true deploy
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `sign-artifacts` | GPG-signs build artifacts using `sign-maven-plugin` |
| `release-judong` | Deploys to JUDO NG Nexus snapshots repository |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH |
| `generate-github-asciidoc-diagrams` | Generates documentation diagrams with PlantUML/Asciidoctor |
| `update-source-code-license` | Updates Apache 2.0 license headers in source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build config — single bundle module, all dependencies `provided` scope |
| `.mvn/jvm.config` | JVM settings for Maven: `-Xms1024m -Xmx2048m -Dfile.encoding=UTF-8` |
| `.mvn/extensions.xml` | Maven extensions: wagon-file, wagon-webdav-jackrabbit, buildtime, profile-activator |
| `logback-test.xml` | Test-time logging: INFO level to console |
| `.github/workflows/build.yml` | Main CI pipeline: build, test, deploy, tag, release |

## OSGi Configuration

The bundle's service is activated by creating an OSGi ConfigAdmin configuration with PID:

```
hu.blackbelt.karaf.jasypt.services.DefaultStringEncryptorConfig
```

Configuration properties:

| Property | Default | Description |
|----------|---------|-------------|
| `encryption_algorithm` | `PBEWithSHA1AndDESEDE` | PBE algorithm name |
| `encryption_password` | — | Direct password (type: PASSWORD) |
| `encryption_passwordFile` | — | Path to file containing the password |
| `encryption_passwordEnvName` | `ENCRYPTION_PASSWORD` | Environment variable holding the password |
| `encryptor_alias` | — | Service alias for the registered encryptor |

## Development Environment

**Required:**
- Java 11+ JDK
- Maven 3.x (or use `./mvnw`)

**Optional:**
- Apache Karaf 4.0.7+ (for runtime testing)
- Environment variable `ENCRYPTION_PASSWORD` set for encryption commands

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** `1.1.0-SNAPSHOT` (CI-friendly via `${revision}`)
- **Branching:** GitFlow — `feature/JNG-XXXX_description`, `release/VERSION`, `bugfix/`, `hotfix/`
- **Every commit must reference a JIRA ticket** (`JNG-xxxx`)
- **CI Runner:** Custom `judong` runner with 30-minute timeout
- **Deployment targets:** JUDO NG Nexus (snapshots) and Maven Central (releases)

## Important Notes

1. All runtime dependencies are `provided` scope — they come from the Karaf container at runtime
2. Jasypt classes (`StandardPBEStringEncryptor`, etc.) are `final`, so the service uses composition instead of inheritance
3. The `StringEncryptor` service is registered with `Integer.MAX_VALUE` ranking to ensure it becomes the default encryptor
4. Configuration is **required** (`ConfigurationPolicy.REQUIRE`) — the service won't start without a `.cfg` file or ConfigAdmin entry
5. The bundle exports no packages (`<Export-Package/>`) — it only exposes functionality through OSGi services and Karaf commands

## Related Documentation

- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and contribution guidelines
- [README.md](README.md) — Usage and installation instructions
- [.github/CIFLOW.md](.github/CIFLOW.md) — Branching strategy and CI/CD pipeline documentation
