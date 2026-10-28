// From github.com/barbulescu/architectural-tests, rule group "kotlin-framework-free". Copied into your project: edit freely.
package com.example.shop.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse

/** Code that is meant to outlive frameworks (typically the domain) must not import them. */
object FrameworkFreeRules {

    val COMMON_FRAMEWORKS = listOf("org.springframework.", "jakarta.", "javax.persistence.", "io.micronaut.", "io.quarkus.")

    /**
     * No import in files of [packagePattern] (Konsist syntax, e.g. `"com.acme.domain.."`) starts with any of
     * [forbiddenPrefixes]. Reported per import, with file and line.
     */
    fun assertNoImports(
        scope: KoScope,
        packagePattern: String,
        forbiddenPrefixes: List<String> = COMMON_FRAMEWORKS,
        testName: String = callingTestName(),
    ) {
        scope.files.withPackage(packagePattern).flatMap { it.imports }
            .assertFalse(testName = testName) { import -> forbiddenPrefixes.any { import.name.startsWith(it) } }
    }

    /** Konsist finds the test name at a fixed stack depth, which a helper function shifts; pass it on explicitly. */
    private fun callingTestName(): String = StackWalker.getInstance().walk { frames ->
        frames.map { it.className to it.methodName }
            .filter { (className, _) -> className != FrameworkFreeRules::class.java.name && !className.startsWith(FrameworkFreeRules::class.java.name + "$") }
            .findFirst().get().second
    }
}
