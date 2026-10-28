// From github.com/barbulescu/architectural-tests, rule group "kotlin-file-layout". Copied into your project: edit freely.
package com.example.archtests

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.verify.assertTrue

/** Kotlin coding conventions: a file that contains a single class, interface or object is named after it. */
object FileLayoutRules {

    fun assertSingleDeclarationFilesAreNamedAfterIt(scope: KoScope, testName: String = callingTestName()) {
        scope.files
            .filter { it.topLevelTypeNames().size == 1 }
            .assertTrue(testName = testName) { it.name == it.topLevelTypeNames().single() }
    }

    private fun com.lemonappdev.konsist.api.declaration.KoFileDeclaration.topLevelTypeNames(): List<String> =
        (classes(includeNested = false) + interfaces(includeNested = false) + objects(includeNested = false)).map { it.name }

    /** Konsist finds the test name at a fixed stack depth, which a helper function shifts; pass it on explicitly. */
    private fun callingTestName(): String = StackWalker.getInstance().walk { frames ->
        frames.map { it.className to it.methodName }
            .filter { (className, _) -> className != FileLayoutRules::class.java.name && !className.startsWith(FileLayoutRules::class.java.name + "$") }
            .findFirst().get().second
    }
}
