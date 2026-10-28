// From github.com/barbulescu/architectural-tests, rule group "hexagonal". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures.OnionArchitecture;

import java.util.Map;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * Hexagonal (ports and adapters, a.k.a. onion) architecture: the domain depends on nothing, application services
 * depend on the domain, and adapters depend inwards but never on each other.
 *
 * <p>Packages are given relative to {@code rootPackage}: {@code "domain.model"} means {@code <rootPackage>.domain.model}
 * and its sub-packages. Only classes below {@code rootPackage} are checked.
 */
public final class HexagonalArchitectureRules {

    private HexagonalArchitectureRules() {
    }

    /**
     * @param adapters adapter name to package, e.g. {@code Map.of("persistence", "adapter.persistence")}
     */
    public static ArchRule hexagonal(
            String rootPackage, String domainModelPackage, String domainServicePackage, String applicationPackage,
            Map<String, String> adapters) {
        OnionArchitecture rule = onionArchitecture()
                .domainModels(inside(rootPackage, domainModelPackage))
                .domainServices(inside(rootPackage, domainServicePackage))
                .applicationServices(inside(rootPackage, applicationPackage));
        adapters.forEach((name, subPackage) -> rule.adapter(name, inside(rootPackage, subPackage)));
        // Empty layers (e.g. no domain services yet) are not a violation.
        return rule.withOptionalLayers(true);
    }

    private static String inside(String rootPackage, String subPackage) {
        return subPackage.isEmpty() ? rootPackage + ".." : rootPackage + "." + subPackage + "..";
    }
}
