package com.example.archtests

import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

class FileLayoutRulesTest {

    @Test
    fun `single class files named after the class, multi class files, and files outside the root package, pass`() {
        FileLayoutRules.assertSingleDeclarationFilesAreNamedAfterIt(Fixtures.scope("kotlin-file-layout", "valid"), "fixtures")
    }

    @Test
    fun `single class file with another name fails`() {
        val failure = assertThrows<KoAssertionFailedException> {
            FileLayoutRules.assertSingleDeclarationFilesAreNamedAfterIt(Fixtures.scope("kotlin-file-layout", "invalid"), "fixtures")
        }
        assertContains(failure.message.orEmpty(), "Utils.kt")
    }
}
