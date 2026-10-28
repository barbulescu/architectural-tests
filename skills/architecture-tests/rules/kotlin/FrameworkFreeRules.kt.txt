// From github.com/barbulescu/architectural-tests, rule group "kotlin-framework-free". Copied into your project: edit freely.
package com.example.archtests

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse

/** Code that is meant to outlive frameworks (typically the domain) must not import them. */
object FrameworkFreeRules {

    val COMMON_FRAMEWORKS = listOf("org.springframework.", "jakarta.", "javax.persistence.", "io.micronaut.", "io.quarkus.")

    /**
     * No import in files of `<rootPackage>.<subPackage>` and its sub-packages, e.g. `("com.acme.shop", "domain")`,
     * starts with any of [forbiddenPrefixes]. Leave [subPackage] empty for the whole root package.
     * Reported per import, with file and line.
     */
    fun assertNoImports(
        scope: KoScope,
        rootPackage: String,
        subPackage: String = "",
        forbiddenPrefixes: List<String> = COMMON_FRAMEWORKS,
        testName: String = callingTestName(),
    ) {
        scope.files.withPackage(inside(rootPackage, subPackage)).flatMap { it.imports }
            .assertFalse(testName = testName) { import -> forbiddenPrefixes.any { import.name.startsWith(it) } }
    }

    private fun inside(rootPackage: String, subPackage: String) =
        if (subPackage.isEmpty()) "$rootPackage.." else "$rootPackage.$subPackage.."

    /** Konsist finds the test name at a fixed stack depth, which a helper function shifts; pass it on explicitly. */
    private fun callingTestName(): String = StackWalker.getInstance().walk { frames ->
        frames.map { it.className to it.methodName }
            .filter { (className, _) -> className != FrameworkFreeRules::class.java.name && !className.startsWith(FrameworkFreeRules::class.java.name + "$") }
            .findFirst().get().second
    }
}
