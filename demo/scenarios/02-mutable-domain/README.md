# 02 - The convenient `var`

A discount that "needs to be adjusted later", so its percentage became a `var`. Domain objects quietly stop being
safe to share between threads and caches.

Caught by: **Konsist** (`DomainModelKonsistTest.domain objects are immutable`). `val` vs `var` is a Kotlin
source concept; that is exactly the kind of rule Konsist exists for.
