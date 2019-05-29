/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.configuration.acme.events;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

/**
 * Event used to alert when a newACME certificate is ready for use.
 */
public class CertificateEvent {
    private final X509Certificate certificate;
    private final KeyPair domainKeyPair;

    /**
     * Creates a new CertificateEvent.
     * @param certificate X509 certificate file
     * @param domainKeyPair key pair used to encrypt the certificate
     */
    public CertificateEvent(X509Certificate certificate, KeyPair domainKeyPair) {
        this.certificate = certificate;
        this.domainKeyPair = domainKeyPair;
    }

    /**
     * @return Certificate created by ACME server
     */
    public X509Certificate getCert() {
        return certificate;
    }

    /**
     * @return KeyPair used to encrypt the certificate.
     */
    public KeyPair getDomainKeyPair() {
        return domainKeyPair;
    }
}
