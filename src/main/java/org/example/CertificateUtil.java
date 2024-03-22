package org.example;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateUtil {


    public static X509Certificate generateCACertificate(String dn, KeyPair keyPair, Date startDate, Date expiryDate) throws Exception {

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                new X500Name(dn), // issuer
                BigInteger.ONE, // serial number
                startDate,
                expiryDate,
                new X500Name(dn), // subject
                keyPair.getPublic()
        );


        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        // Add Key Usage extension to specify the purposes for which the key can be used
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));

        // Create a content signer
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());

        return new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
    }
}
