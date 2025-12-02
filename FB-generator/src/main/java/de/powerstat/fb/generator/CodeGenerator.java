/*
 * Copyright (C) 2015-2025 Dipl.-Inform. Kai Hofmann. All rights reserved!
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0.
 */
package de.powerstat.fb.generator;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import de.powerstat.fb.generator.impl.TR64Description;
import de.powerstat.fb.mini.TR64SessionMini;


/**
 * FB code generator.
 *
 * @author Kai Hofmann
 */
public final class CodeGenerator
 {
  /**
   * Hidden default constructor.
   */
  private CodeGenerator()
   {
    super();
   }


  /**
   * Main.
   *
   * @param args 0: FB hostname; 1 : FB port; 2 : FB username; 3 : FB password; 4: ouput path for generated code; [5: outtype: java|ansible]
   * @throws KeyManagementException Key management exception
   * @throws NumberFormatException Number format exception
   * @throws NoSuchAlgorithmException No such algorithm
   * @throws KeyStoreException Key store exception
   * @throws ClientProtocolException Client protocol exception
   * @throws UnsupportedEncodingException Unsupported encoding exception
   * @throws XPathExpressionException XPath expression exception
   * @throws ParserConfigurationException Parser configuration exception
   * @throws IOException IO exception
   * @throws SAXException SAX exception
   */
  public static void main(final String[] args) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, XPathExpressionException, ParserConfigurationException, IOException, SAXException
   {
    Objects.requireNonNull(args[0], "arg0"); //$NON-NLS-1$
    Objects.requireNonNull(args[1], "arg1"); //$NON-NLS-1$
    Objects.requireNonNull(args[2], "arg2"); //$NON-NLS-1$
    Objects.requireNonNull(args[3], "arg3"); //$NON-NLS-1$
    Objects.requireNonNull(args[4], "arg4"); //$NON-NLS-1$
    String outType = "java"; //$NON-NLS-1$
    if (args.length == 6)
     {
      outType = args[5];
     }
    new TR64Description(TR64SessionMini.newInstance(args[0], Integer.parseInt(args[1]), args[2], args[3]), args[4], outType).fetchTR64desc();
   }

 }
