package org.kurodev.rp.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
public record ProjectSettings(
        String name,
        String version
) {
}
