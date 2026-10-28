// From github.com/barbulescu/architectural-tests, rule group "hexagonal". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures.OnionArchitecture;

import java.util.Map;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * Hexagonal (ports and adapters, a.k.a. onion) architecture: the domain depends on nothing, application services
 * depend on the domain, and adapters depend inwards but never on each other.
 */
public final class HexagonalArchitectureRules {

    private HexagonalArchitectureRules() {
    }

    /**
     * @param adapters adapter name to package pattern, e.g. {@code Map.of("persistence", "..adapter.persistence..")}
     */
    public static ArchRule hexagonal(
            String domainModelPackage, String domainServicePackage, String applicationPackage,
            Map<String, String> adapters) {
        OnionArchitecture rule = onionArchitecture()
                .domainModels(domainModelPackage)
                .domainServices(domainServicePackage)
                .applicationServices(applicationPackage);
        adapters.forEach(rule::adapter);
        // Empty layers (e.g. no domain services yet) are not a violation.
        return rule.withOptionalLayers(true);
    }
}
