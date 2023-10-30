/*
 * Copyright (C) 2015-2023 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb;


import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.w3c.dom.Node;

import de.powerstat.fb.generated.Devicelist;
import de.powerstat.fb.mini.AHASessionMini;
import de.powerstat.validation.values.Hostname;
import de.powerstat.validation.values.Password;
import de.powerstat.validation.values.Port;
import de.powerstat.validation.values.Username;
import de.powerstat.validation.values.strategies.UsernameConfigurableStrategy;
import de.powerstat.validation.values.strategies.UsernameConfigurableStrategy.HandleEMail;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;


/**
 * FB AHA session.
 *
 * @author Kai Hofmann
 */
public final class AHASession extends AHASessionMini
 {
  /* *
   * Logger.
   */
  // private static final Logger LOGGER = LogManager.getLogger(AHASession.class);

  /**
   * Replacement regexp.
   */
  private static final Pattern REPLACE_REGEXP = Pattern.compile("AHASessionMini"); //$NON-NLS-1$



  /**
   * Constructor.
   *
   * @param httpclient CloseableHttpClient
   * @param docBuilder DocumentBuilder
   * @param hostname FB hostname
   * @param port FB AHA-Api port
   * @param username FB username
   * @param password FB Password
   * @throws NullPointerException If hostname or password is null
   */
  private AHASession(final CloseableHttpClient httpclient, final DocumentBuilder docBuilder, final Hostname hostname, final Port port, final Username username, final Password password)
   {
    super(httpclient, docBuilder, hostname, port, username, password);
   }


  /**
   * Get new instance for a FB hostname with password.
   *
   * @param httpclient CloseableHttpClient
   * @param docBuilder DocumentBuilder
   * @param hostname FB hostname
   * @param port FB AHA-Api port
   * @param username FB username
   * @param password FB password
   * @return A new AHASession instance.
   * @throws NullPointerException If hostname or password is null
   */
  public static AHASession newInstance(final CloseableHttpClient httpclient, final DocumentBuilder docBuilder, final Hostname hostname, final Port port, final Username username, final Password password)
   {
    return new AHASession(httpclient, docBuilder, hostname, port, username, password);
   }


  /**
   * Get new instance for a FB hostname with password.
   *
   * @param hostname FB hostname
   * @param port FB AHA-Api port
   * @param username FB username
   * @param password FB password
   * @return A new AHASession instance.
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyStoreException Key store exception
   * @throws KeyManagementException Key management exception
   * @throws NullPointerException If hostname or password is null
   */
  public static AHASession newInstance(final Hostname hostname, final Port port, final Username username, final Password password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    final CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build())).build();

    final var factory = DocumentBuilderFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //$NON-NLS-1$
    final var docBuilder = factory.newDocumentBuilder();

    return newInstance(httpclient, docBuilder, hostname, port, username, password);
   }


  /**
   * Get new instance for a FB default hostname fritz.box, default port 443 with username and password.
   *
   * @param username FB username
   * @param password FB password
   * @return A new AHASession instance.
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyStoreException Key store exception
   * @throws KeyManagementException Key management exception
   * @throws NullPointerException If hostname or password is null
   *
   * TODO username
   */
  public static AHASession newInstance(final String username, final String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    return newInstance(Hostname.of("fritz.box"), Port.of(443), Username.of(UsernameConfigurableStrategy.of(0, 32, "^[@./_0-9a-zA-Z-]*$", HandleEMail.EMAIL_POSSIBLE), username), Password.of(password)); //$NON-NLS-1$
   }


  /**
   * Get new instance for a FB default hostname fritz.box, empty username with password.
   *
   * @param password FB password
   * @return A new AHASession instance.
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyStoreException Key store exception
   * @throws KeyManagementException Key management exception
   * @throws NullPointerException If hostname or password is null
   *
   * TODO username
   */
  public static AHASession newInstance(final String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    return newInstance("", password); //$NON-NLS-1$
   }


  /**
   * Parse device list infos to java objects.
   *
   * @param doc XML document
   * @return Devicelist
   * @throws JAXBException JAXB exception
   */
  public static Devicelist parseDeviceListInfos(final Node doc) throws JAXBException
   {
    return (Devicelist)JAXBContext.newInstance(Devicelist.class).createUnmarshaller().unmarshal(doc.getFirstChild());
   }


  /**
   * Returns the string representation of this AHASession.
   *
   * The exact details of this representation are unspecified and subject to change, but the following may be regarded as typical:
   *
   * "AHASession[hostname=fritz.box, ...]"
   *
   * @return String representation of this AHASession.
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
   {
    return REPLACE_REGEXP.matcher(super.toString()).replaceFirst("AHASession"); //$NON-NLS-1$
   }

 }
