package io.micronaut.acme.challenge.dns;

import io.micronaut.context.annotation.DefaultImplementation;

@DefaultImplementation(RenderedTextDnsChallengeResolver.class)
public interface DnsChallengeResolver {
    void createRecord(String domain, String digest);
    void destroyRecord(String domain);
}
