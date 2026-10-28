// From github.com/barbulescu/architectural-tests, rule group "kotlin-immutability". Copied into your project: edit freely.
package com.example.shop.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse

/**
 * Objects that are shared (between threads, caches, callers) must not change under their users' feet.
 * `val` vs `var` and property types are source concepts, which is why this is a Konsist rule.
 */
object ImmutabilityRules {

    private val MUTABLE_TYPES = Regex("""^(kotlin\.collections\.)?(Mutable\w*|ArrayList|HashMap|HashSet|LinkedHashMap|LinkedHashSet)\b""")
    private val MUTABLE_FACTORIES = Regex("""^(mutable\w*Of|arrayListOf|hashMapOf|hashSetOf|linkedMapOf|linkedSetOf)\b""")

    /**
     * No `var` and no mutable collection types (declared or inferred from a `mutableListOf()`-style initialiser)
     * in properties of files in `<rootPackage>.<subPackage>` and its sub-packages, e.g. `("com.acme.shop", "domain")`.
     * Leave [subPackage] empty for the whole root package.
     * Covers top-level properties, constructor properties and class, object and interface bodies.
     *
     * Existing offenders can be marked with `@Suppress("konsist.<name of the calling test>")`.
     */
    fun assertImmutable(scope: KoScope, rootPackage: String, subPackage: String = "", testName: String = callingTestName()) {
        val files = scope.files.withPackage(inside(rootPackage, subPackage))
        val properties = files.flatMap { file ->
            file.properties(includeNested = false) +
                (file.classes(includeNested = true) + file.objects(includeNested = true) + file.interfaces(includeNested = true))
                    .flatMap { it.properties(includeNested = false) }
        }
        properties.assertFalse(testName = testName) { it.hasVarModifier || it.isMutableCollection() }
    }

    private fun KoPropertyDeclaration.isMutableCollection(): Boolean =
        type?.text?.let { MUTABLE_TYPES.containsMatchIn(it) } == true ||
            value?.let { MUTABLE_FACTORIES.containsMatchIn(it) } == true

    private fun inside(rootPackage: String, subPackage: String) =
        if (subPackage.isEmpty()) "$rootPackage.." else "$rootPackage.$subPackage.."

    /** Konsist finds the test name at a fixed stack depth, which a helper function shifts; pass it on explicitly. */
    private fun callingTestName(): String = StackWalker.getInstance().walk { frames ->
        frames.map { it.className to it.methodName }
            .filter { (className, _) -> className != ImmutabilityRules::class.java.name && !className.startsWith(ImmutabilityRules::class.java.name + "$") }
            .findFirst().get().second
    }
}
