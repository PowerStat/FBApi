/*
 * Copyright (C) 2020-2023 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb.generator.impl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.powerstat.fb.mini.TR64SessionMini;
import de.powerstat.phplib.templateengine.HandleUndefined;
import de.powerstat.phplib.templateengine.TemplateEngine;


/**
 * TR64 services.
 */
public final class TR64ServiceTemplates
 {
  /**
   * Logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(TR64ServiceTemplates.class);

  /**
   * Method name regexp.
   */
  private static final Pattern METHODNAME_REGEXP = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"); //$NON-NLS-1$

  /**
   * TR64 session.
   */
  private final TR64SessionMini session;

  /**
   * Output path.
   */
  private final String outputPath;


  /**
   * Constructor.
   *
   * @param session TR64 session
   * @param outputPath Output path for generated code
   */
  public TR64ServiceTemplates(final TR64SessionMini session, final String outputPath)
   {
    Objects.requireNonNull(session, "session"); //$NON-NLS-1$
    Objects.requireNonNull(outputPath, "outputPath"); //$NON-NLS-1$
    this.session = session;
    this.outputPath = outputPath;
   }


  /**
   * Convert underline name to camel case name.
   *
   * @param name Name with undelrines
   * @param firstUpper First character becomes uppercase
   * @return Name in camel case
   */
  public static String convertUnderline2CamelCase(final String name, final boolean firstUpper)
   {
    final String[] parts = name.split("_"); //$NON-NLS-1$
    final var result = new StringBuilder();
    boolean first = true;
    for (final String part : parts)
     {
      if (first)
       {
        result.append(firstUpper ? part.substring(0, 1).toUpperCase(Locale.getDefault()) : part.substring(0, 1).toLowerCase(Locale.getDefault()));
        first = false;
       }
      else
       {
        result.append(part.substring(0, 1).toUpperCase(Locale.getDefault()));
       }
      result.append(part.substring(1));
     }
    return result.toString();
   }


  /**
   * To sentence.
   *
   * @param methodName Method/Parameter name
   * @return Name split up into sentence
   */
  public static String toSentence(final String methodName)
   {
    final var methodDesc = new StringBuilder();
    for (final String word : METHODNAME_REGEXP.split(methodName))
     {
      methodDesc.append((methodDesc.length() > 0) ? word.toLowerCase(Locale.getDefault()) : word);
      methodDesc.append(' ');
     }
    methodDesc.deleteCharAt(methodDesc.length() - 1);
    return methodDesc.toString();
   }


  /**
   * Generate services.
   *
   * @param serviceNL Service list
   * @param serviceNr Service nr
   * @throws IOException IO exception
   * @throws FileNotFoundException File not found
   * @throws ClientProtocolException Client protocol exception
   * @throws UnsupportedEncodingException Unsupported encoding
   * @throws SAXException SAX exception
   */
  public void generateServiceTemplate(final NodeList serviceNL, final int serviceNr) throws IOException, SAXException
   {
    var classname = "dummy"; //$NON-NLS-1$
    var scpdUrl = ""; //$NON-NLS-1$
    final var templ = new TemplateEngine(HandleUndefined.KEEP);
    templ.setFile("SCPD", new File("src/main/resources", "SCPD.tmpl")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.subst("SCPD"); //$NON-NLS-1$
    final NodeList serviceChildsNL = serviceNL.item(serviceNr).getChildNodes();
    for (int j = 0; j < serviceChildsNL.getLength(); ++j)
     {
      final Node serviceChild = serviceChildsNL.item(j);
      if (serviceChild.getNodeType() == Node.ELEMENT_NODE)
       {
        final String serviceChildName = serviceChild.getNodeName().toUpperCase(Locale.getDefault());
        final String serviceChildValue = serviceChild.getTextContent();
        // LOGGER.debug(serviceChildName + " = " + serviceChildValue);
        templ.setVar(serviceChildName, serviceChildValue);
        if ("SCPDURL".equals(serviceChildName)) //$NON-NLS-1$
         {
          scpdUrl = serviceChildValue;
          classname = serviceChildValue.substring(1, 2).toUpperCase(Locale.getDefault()) + serviceChildValue.substring(2, serviceChildValue.length() - 8); // "SCPD.xml"
          templ.setVar("CLASSNAME", convertUnderline2CamelCase(classname, true)); //$NON-NLS-1$
          templ.setVar("CLASSDESC", toSentence(convertUnderline2CamelCase(classname, true))); //$NON-NLS-1$
         }
       }
     }
    templ.parse("SCPDfinal", "SCPD"); //$NON-NLS-1$ //$NON-NLS-2$
    final var dir = new File(this.outputPath + "/generated-sources/fb/tmpl/"); //$NON-NLS-1$
    /* final boolean success = */ dir.mkdirs();
    if (LOGGER.isDebugEnabled())
     {
      LOGGER.debug("Write1: {}{}{}.tmpl", dir.getAbsolutePath(), File.separator, classname); //$NON-NLS-1$
     }
    try (var out = new PrintWriter(dir.getAbsolutePath() + File.separator + convertUnderline2CamelCase(classname, true) + ".tmpl", StandardCharsets.UTF_8)) //$NON-NLS-1$
     {
      out.println(templ.get("SCPDfinal")); //$NON-NLS-1$
     }
    final var classes = new TR64ServiceClasses(this.session, this.outputPath);
    classes.generateServiceClass(scpdUrl, classname);
   }

 }
