/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.acme.background;

import io.micronaut.acme.AcmeConfiguration;
import io.micronaut.acme.services.AcmeService;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.shredzone.acme4j.exception.AcmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Background task to automatically refresh the certificates from an ACME server on a configurable interval.
 */
@Singleton
public final class AcmeCertRefresherTask {

    private static final Logger LOG = LoggerFactory.getLogger(AcmeCertRefresherTask.class);

    private AcmeService acmeService;
    private final AcmeConfiguration acmeConfiguration;

    /**
     * Constructs a new Acme cert refresher background task.
     *
     * @param acmeService       Acme service
     * @param acmeConfiguration Acme configuration
     */
    public AcmeCertRefresherTask(AcmeService acmeService, AcmeConfiguration acmeConfiguration) {
        this.acmeService = acmeService;
        this.acmeConfiguration = acmeConfiguration;
    }

    /**
     * Scheduled task to refresh certs from ACME server.
     *
     * @throws AcmeException if any issues occur during certificate renewal
     */
    @Scheduled(
            fixedDelay = "${acme.refresh.frequency:24h}",
            initialDelay = "${acme.refresh.delay:24h}")
    void backgroundRenewal() throws AcmeException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running background/scheduled renewal process");
        }
        renewCertIfNeeded();
    }

    /**
     * Checks to see if certificate needs renewed on app startup.
     *
     * @param startupEvent Startup event
     */
    @EventListener
    void onStartup(ApplicationStartupEvent startupEvent) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Running startup renewal process");
            }
            renewCertIfNeeded();
        } catch (Exception e) {
            LOG.error("Failed to initialize certificate for SSL no requests would be secure. Stopping application", e);
            throw new ApplicationStartupException("Failed to start due to SSL configuration issue.", e);
        }
    }

    /**
     * Does the work to actually renew the certificate if it needs to be done.
     * @throws AcmeException if any issues occur during certificate renewal
     */
    protected void renewCertIfNeeded() throws AcmeException {
        if (!acmeConfiguration.isTosAgree()) {
            throw new IllegalStateException(String.format("Cannot refresh certificates until terms of service is accepted. Please review the TOS for Let's Encrypt and set \"%s\" to \"%s\" in configuration once complete", "acme.tos-agree", "true"));
        }

        List<String> domains = new ArrayList<>();
        for (String domain : acmeConfiguration.getDomains()) {
            domains.add(domain);
            if (domain.startsWith("*.")) {
                String baseDomain = domain.substring(2);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Configured domain is a wildcard, including the base domain [{}] in addition", baseDomain);
                }
                domains.add(baseDomain);
            }
        }

        X509Certificate currentCertificate = acmeService.getCurrentCertificate();
        if (currentCertificate != null) {
            long daysTillExpiration = ChronoUnit.SECONDS.between(Instant.now(), currentCertificate.getNotAfter().toInstant());

            if (daysTillExpiration <= acmeConfiguration.getRenewWitin().getSeconds()) {
                acmeService.orderCertificate(domains);
            } else {
                acmeService.setupCurrentCertificate();
            }
        } else {
            acmeService.orderCertificate(domains);
        }
    }
}
