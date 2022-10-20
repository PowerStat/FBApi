/*
 * Copyright (C) 2015-2020 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb.generator.impl;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.powerstat.fb.mini.TR64SessionMini;


/**
 * FB TR64 description.
 */
public final class TR64Description
 {
  /**
   * Logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(TR64Description.class);

  /**
   * TR64 session.
   */
  private final transient TR64SessionMini session;

  /**
   * Output path.
   */
  private final transient String outputPath;


  /**
   * Constructor.
   *
   * @param session TR64 session
   * @param outputPath Output path for generated code
   */
  public TR64Description(final TR64SessionMini session, final String outputPath)
   {
    Objects.requireNonNull(session, "session"); //$NON-NLS-1$
    Objects.requireNonNull(outputPath, "outputPath"); //$NON-NLS-1$
    this.session = session;
    this.outputPath = outputPath;
   }


  /**
   * Write xml document to file.
   *
   * @param document DOM document
   * @param filename Filename
   */
  public static void writeXml2File(final Document document, final String filename)
   {
    try
     {
      final TransformerFactory factory = TransformerFactory.newInstance();
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "all"); //$NON-NLS-1$
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "all"); //$NON-NLS-1$
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      final Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //$NON-NLS-1$
      transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
      final File file = new File(filename);
      /* final boolean success = */ file.getParentFile().mkdirs();
      transformer.transform(new DOMSource(document), new StreamResult(file));
     }
    catch (final TransformerException e)
     {
      LOGGER.debug("", e); //$NON-NLS-1$
     }
   }


  /**
   * Get TR64 description and loop over all service nodes.
   *
   * @throws NoSuchAlgorithmException No such algorithm exception
   * @throws KeyStoreException Key store exception
   * @throws KeyManagementException Key management exception
   * @throws ParserConfigurationException Parser configuration exception
   * @throws IOException IO exception
   * @throws ClientProtocolException Client protocol exception
   * @throws SAXException SAX exception
   * @throws UnsupportedEncodingException Unsupported encoding
   * @throws XPathExpressionException XPath expression exception
   */
  public void getTR64desc() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParserConfigurationException, IOException, SAXException, XPathExpressionException
   {
    final Document tr64desc = this.session.getDoc("/tr64desc.xml"); //$NON-NLS-1$
    if (LOGGER.isDebugEnabled())
     {
      writeXml2File(tr64desc, this.outputPath + "/tr64desc.xml"); //$NON-NLS-1$
     }
    // TODO /root/systemVersion/Minor   /root/systemVersion/Patch     7.12
    final NodeList serviceNL = tr64desc.getElementsByTagName("service"); //$NON-NLS-1$
    final TR64ServiceTemplates services = new TR64ServiceTemplates(this.session, this.outputPath);
    for (int service = 0; service < serviceNL.getLength(); ++service)
     {
      services.generateServiceTemplate(serviceNL, service);
     }
   }

 }
