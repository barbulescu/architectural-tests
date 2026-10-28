package com.example.archtests

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import java.io.File

/** Fixtures are uncompiled sources in src/test/fixtures/<rule group>/<variant>/. */
object Fixtures {

    fun dir(group: String, variant: String): File = File("src/test/fixtures/$group/$variant").absoluteFile

    fun scope(group: String, variant: String): KoScope = Konsist.scopeFromExternalDirectory(dir(group, variant).path)
}
