package io.micronaut.acme.challenge.dns;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class RenderedTextDnsChallengeResolver implements DnsChallengeResolver {
    private static final String TXT_RECORD_NAME = "_acme-challenge";
    private static final Logger LOG = LoggerFactory.getLogger(RenderedTextDnsChallengeResolver.class);

    @Override
    public void createRecord(String domain, String digest) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("\t\t\t\t\t\t\tCREATE DNS `TXT` ENTRY AS FOLLOWS");
        System.out.println("\t\t\t\t" + TXT_RECORD_NAME + "." + domain + " with value " + digest);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void destroyRecord(String domain) {
        // To maintain backwards compatibility with <=v3.0.1, do not print text

        if (LOG.isDebugEnabled()) {
            LOG.debug("The 'TXT' record for " + TXT_RECORD_NAME + "." + domain + " can be removed");
        }
    }
}
