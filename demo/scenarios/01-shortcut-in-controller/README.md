# 01 - The harmless-looking shortcut

A "read-only report" endpoint that goes straight to the repository implementation instead of through a use case.
It is small, it works, and it looks fine in a diff. It is also how layered code bases erode.

Caught by: **ArchUnit** (`LayerRulesTest`). The file is Java, so Konsist does not look at it.
