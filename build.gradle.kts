plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.ktlint)
}

group = "es.unizar.webeng"
version = "2026-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

val mockitoAgent =
    configurations.create("mockitoAgent") {
        isCanBeConsumed = false
    }

val byteBuddyAgent =
    configurations.create("byteBuddyAgent") {
        isCanBeConsumed = false
    }

dependencies {
    val springBootVersion = libs.versions.springBoot.get()
    val springBootBom = platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    implementation(springBootBom)
    mockitoAgent(springBootBom)
    byteBuddyAgent(springBootBom)

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.jackson.module.kotlin)
    runtimeOnly(libs.kotlin.reflect)
    runtimeOnly(libs.h2)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.springmockk)
    mockitoAgent("org.mockito:mockito-core") {
        isTransitive = false
    }
    byteBuddyAgent("net.bytebuddy:byte-buddy-agent") {
        isTransitive = false
    }
}

// Attach Mockito at JVM startup so the inline mock maker does not self-attach.
// See https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
abstract class MockitoAgentProvider : CommandLineArgumentProvider {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val agentJar: ConfigurableFileCollection

    override fun asArguments(): Iterable<String> =
        listOf(
            "-javaagent:${agentJar.asPath}",
            // The agent appends to the bootstrap classpath, which disables CDS sharing.
            "-Xshare:off",
        )
}

// MockK instruments classes through Byte Buddy. Load that agent at startup too.
abstract class ByteBuddyAgentProvider : CommandLineArgumentProvider {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val agentJar: ConfigurableFileCollection

    override fun asArguments(): Iterable<String> = listOf("-javaagent:${agentJar.asPath}")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgumentProviders.add(
        objects.newInstance<MockitoAgentProvider>().apply {
            agentJar.from(mockitoAgent)
        },
    )
    jvmArgumentProviders.add(
        objects.newInstance<ByteBuddyAgentProvider>().apply {
            agentJar.from(byteBuddyAgent)
        },
    )
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
}
