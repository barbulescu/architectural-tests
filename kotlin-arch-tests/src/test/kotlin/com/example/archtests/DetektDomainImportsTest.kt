package com.example.archtests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

/** Runs the detekt CLI (separate JVM, see build.gradle.kts) with only the fragment as configuration. */
class DetektDomainImportsTest {

    private val fragment = File("src/main/resources/detekt/domain-imports.yml").absoluteFile

    @Test
    fun `domain importing only the JDK, and web importing Spring, pass`(@TempDir tmp: Path) {
        assertEquals(emptyList(), forbiddenImports("valid", tmp))
    }

    @Test
    fun `domain importing a framework or an outer layer fails`(@TempDir tmp: Path) {
        val findings = forbiddenImports("invalid", tmp)
        assertEquals(2, findings.size, findings.joinToString("\n"))
    }

    private fun forbiddenImports(variant: String, tmp: Path): List<String> {
        val report = tmp.resolve("report.txt").toFile()
        val java = ProcessHandle.current().info().command().get()
        val process = ProcessBuilder(
            java, "-cp", System.getProperty("detekt.cli.classpath"), "io.gitlab.arturbosch.detekt.cli.Main",
            "--input", Fixtures.dir("detekt-domain-imports", variant).path,
            "--config", fragment.path,
            "--report", "txt:${report.path}",
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        check(report.exists()) { "detekt produced no report:\n$output" }
        return report.readLines().filter { it.startsWith("ForbiddenImport") }
    }
}
