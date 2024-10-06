/*
 * Copyright (C) 2015-2023 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import de.powerstat.fb.mini.TR64SessionMini;
import de.powerstat.validation.values.Hostname;
import de.powerstat.validation.values.Password;
import de.powerstat.validation.values.Port;
import de.powerstat.validation.values.Username;
import de.powerstat.validation.values.strategies.UsernameConfigurableStrategy;
import de.powerstat.validation.values.strategies.UsernameConfigurableStrategy.HandleEMail;


/**
 * FB TR64 session.
 *
 * @author Kai Hofmann
 */
public final class TR64Session extends TR64SessionMini
 {
  /* *
   * Logger.
   */
  // private static final Logger LOGGER = LogManager.getLogger(TR64Session.class);

  /**
   * Replacement regexp.
   */
  private static final Pattern REPLACE_REGEXP = Pattern.compile("TR64SessionMini"); //$NON-NLS-1$


  /**
   * Constructor.
   *
   * @param httpclient CloseableHttpClient
   * @param docBuilder DocumentBuilder
   * @param hostname FB hostname
   * @param port FB port number
   */
  private TR64Session(final CloseableHttpClient httpclient, final DocumentBuilder docBuilder, final Hostname hostname, final Port port)
   {
    super(httpclient, docBuilder, hostname, port);
   }


  /**
   * Get new instance for a TR64Session.
   *
   * @param httpclient CloseableHttpClient
   * @param docBuilder DocumentBuilder
   * @param hostname FB hostname
   * @param port FB port number
   * @return TR64SessionMini
   */
  public static TR64Session newInstance(final CloseableHttpClient httpclient, final DocumentBuilder docBuilder, final Hostname hostname, final Port port)
   {
    return new TR64Session(httpclient, docBuilder, hostname, port);
   }


  /**
   * Get new instance for a TR64Session.
   *
   * @param hostname FB hostname
   * @param port FB port number
   * @param username FB username
   * @param password FB password
   * @return TR64Session
   * @throws KeyStoreException Key store exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyManagementException Key management exception
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NullPointerException If hostname, username or password is null
   */
  public static TR64Session newInstance(final Hostname hostname, final Port port, final Username username, final Password password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    final CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(new AuthScope(hostname.stringValue(), port.intValue()), new UsernamePasswordCredentials(username.stringValue(), password.stringValue()));
    final CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build())).setDefaultCredentialsProvider(credsProvider).build();

    final var factory = DocumentBuilderFactory.newInstance();
    try
     {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //$NON-NLS-1$
      final var docBuilder = factory.newDocumentBuilder();
      return newInstance(httpclient, docBuilder, hostname, port);
     }
    catch (ParserConfigurationException e)
     {
      try
       {
        httpclient.close();
       }
      catch (IOException e1)
       {
        // ignore
       }
      throw e;
     }
   }


  /**
   * Get new instance for a TR64Session.
   *
   * @param hostName FB hostname
   * @param portNr FB port number
   * @param userName FB username
   * @param passWord FB password
   * @return TR64SessionMini
   * @throws KeyStoreException Key store exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyManagementException Key management exception
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NullPointerException If hostname, username or password is null
   */
  public static TR64Session newInstance(final String hostName, final int portNr, final String userName, final String passWord) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    return newInstance(Hostname.of(hostName), Port.of(portNr), Username.of(UsernameConfigurableStrategy.of(0, 32, "^[@./_0-9a-zA-Z-]*$", HandleEMail.EMAIL_POSSIBLE), userName), Password.of(passWord));
   }


  /**
   * Get new instance for default hostname fritz.box and default port 49443.
   *
   * @param username FB username
   * @param password FB password
   * @return TR64SessionMini
   * @throws KeyStoreException Key store exception
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyManagementException Key management exception
   * @throws ParserConfigurationException Parser configuration exception
   * @throws NullPointerException If hostname, username or password is null
   */
  public static TR64Session newInstance(final String username, final String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ParserConfigurationException
   {
    return TR64Session.newInstance(Hostname.of("fritz.box"), Port.of(49443), Username.of(UsernameConfigurableStrategy.of(0, 32, "^[@./_0-9a-zA-Z-]*$", HandleEMail.EMAIL_POSSIBLE), username), Password.of(password)); //$NON-NLS-1$
   }


  /**
   * Returns the string representation of this TR64Session.
   *
   * The exact details of this representation are unspecified and subject to change, but the following may be regarded as typical:
   *
   * "TR64Session[hostname=fritz.box, port=49443, ...]"
   *
   * @return String representation of this TR64Session.
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
   {
    return REPLACE_REGEXP.matcher(super.toString()).replaceFirst("TR64Session"); //$NON-NLS-1$
   }

 }
