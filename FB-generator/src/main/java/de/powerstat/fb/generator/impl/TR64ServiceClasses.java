/*
 * Copyright (C) 2020 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb.generator.impl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.powerstat.fb.mini.TR64SessionMini;
import de.powerstat.phplib.templateengine.TemplateEngine;
import de.powerstat.phplib.templateengine.TemplateEngine.HandleUndefined;


/**
 * TR64 services.
 */
public final class TR64ServiceClasses
 {
  /**
   * Logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(TR64ServiceClasses.class);

  /**
   *Type map.
   */
  private static final transient Map<String, String> TYPE_MAP = new ConcurrentHashMap<>();

  /**
   * TR64 session.
   */
  private final transient TR64SessionMini session;

  /**
   * Output path.
   */
  private final transient String outputPath;


  /**
   * Static initialization.
   */
  static
   {
    TYPE_MAP.put("string", "String"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("uuid", "long"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("ui2", "int"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("ui4", "long"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("boolean", "boolean"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("i4", "long"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("dateTime", "Date"); //$NON-NLS-1$ //$NON-NLS-2$
    TYPE_MAP.put("ui1", "short"); //$NON-NLS-1$ //$NON-NLS-2$
   }


  /**
   * Constructor.
   *
   * @param session TR64 session
   * @param outputPath Output path for generated code
   */
  public TR64ServiceClasses(final TR64SessionMini session, final String outputPath)
   {
    Objects.requireNonNull(session, "session"); //$NON-NLS-1$
    Objects.requireNonNull(outputPath, "outputPath"); //$NON-NLS-1$
    this.session = session;
    this.outputPath = outputPath;
   }


  /**
   * Get from type map.
   *
   * @param key Key
   * @return Value
   */
  public static String getFromTypeMap(final String key)
   {
    return TYPE_MAP.get(key);
   }


  /**
   * Initializes variables.
   *
   * @param variablesNL NodeList with variables
   * @param variableTypes Variable types
   */
  private static void initVariables(final NodeList variablesNL, final Map<String, String> variableTypes)
   {
    for (int i = 0; i < variablesNL.getLength(); ++i) // Variable types
     {
      final NodeList variablesChildsNL = variablesNL.item(i).getChildNodes();
      String name = ""; //$NON-NLS-1$
      String datatype = ""; //$NON-NLS-1$
      String defaultvalue = ""; //$NON-NLS-1$
      for (int j = 0; j < variablesChildsNL.getLength(); ++j) // childs NAME, DATATYPE, DEFAULTVALUE
       {
        final Node variablesChild = variablesChildsNL.item(j);
        if (variablesChild.getNodeType() == 1)
         {
          final String variablesChildName = variablesChild.getNodeName().toUpperCase(Locale.getDefault());
          final String variablesChildValue = variablesChild.getTextContent();
          // LOGGER.info(variablesChildName + " = " + variablesChildValue);
          if ("NAME".equals(variablesChildName)) //$NON-NLS-1$
           {
            name = variablesChildValue;
           }
          else if ("DATATYPE".equals(variablesChildName)) //$NON-NLS-1$
           {
            datatype = variablesChildValue;
           }
          else if ("DEFAULTVALUE".equals(variablesChildName)) //$NON-NLS-1$
           {
            defaultvalue = variablesChildValue;
           }
         }
       }
      variableTypes.put(name, datatype);
     }
   }


  /**
   * Return values.
   *
   * @param variableTypes Variable types
   * @param templ Template
   * @param arguments Arguments
   * @param argDirections Argument directions
   * @param argRelated Argument relagted
   * @param argsOut Arguments out
   * @throws IOException IO exception
   */
  private void generateReturnValues(final Map<String, String> variableTypes, final TemplateEngine templ, final List<String> arguments, final List<String> argDirections, final List<String> argRelated, final int argsOut) throws IOException
   {
    if ("void".equals(templ.getVar("METHODTYPE")) || (argsOut == 0)) //$NON-NLS-1$ //$NON-NLS-2$
     {
      templ.setVar("METHODTYPE", "void"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setVar("RESULT"); //$NON-NLS-1$
      templ.setVar("RETURN"); //$NON-NLS-1$
      templ.setVar("RETURNDESCS"); //$NON-NLS-1$
     }
    else
     {
      if (argsOut == 1)
       {
        templ.setVar("RTCOMMA"); //$NON-NLS-1$
        templ.parse("RETFIELDS_BLK", "RETFIELDS"); //$NON-NLS-1$ //$NON-NLS-2$
        templ.setVar("RETNLSNR", String.valueOf(1)); //$NON-NLS-1$
        templ.parse("RETNLS_BLK", "RETNLS"); //$NON-NLS-1$ //$NON-NLS-2$
       }
      else
       {
        int k = 0;
        int ke = 0;
        for (int j = 0; j < arguments.size(); ++j)
         {
          if ("in".equals(argDirections.get(j))) //$NON-NLS-1$
           {
            continue;
           }
          ++k;
          templ.setVar("RTCOMMA", (k == argsOut) ? "" : ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          templ.setVar("RETURNPARAMNAME", arguments.get(j)); //$NON-NLS-1$

          final String mType = TYPE_MAP.get(variableTypes.get(argRelated.get(j)));
          if ("String".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", ""); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ""); //$NON-NLS-1$ //$NON-NLS-2$
           }
          else if ("long".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", "Long.parseLong("); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ")"); //$NON-NLS-1$ //$NON-NLS-2$
           }
          else if ("int".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", "Integer.parseInt("); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ")"); //$NON-NLS-1$ //$NON-NLS-2$
           }
          else if ("boolean".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", "\"1\".equals("); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ")"); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETNLSNR", String.valueOf(k + ke)); //$NON-NLS-1$
            templ.parse("RETNLS_BLK", "RETNLS", true); //$NON-NLS-1$ //$NON-NLS-2$
            ++ke;
           }
          else if ("short".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", "Short.parseShort("); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ")"); //$NON-NLS-1$ //$NON-NLS-2$
           }
          else if ("Date".equals(mType)) //$NON-NLS-1$
           {
            templ.setVar("RETPARCONVB", "DateFormat.getDateInstance().parse("); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ")"); //$NON-NLS-1$ //$NON-NLS-2$
           }
          else
           {
            templ.setVar("RETPARCONVB", ""); //$NON-NLS-1$ //$NON-NLS-2$
            templ.setVar("RETPARCONVE", ""); //$NON-NLS-1$ //$NON-NLS-2$
           }
          templ.parse("RETFIELDS_BLK", "RETFIELDS", true); //$NON-NLS-1$ //$NON-NLS-2$
          templ.setVar("RETNLSNR", String.valueOf(k + ke)); //$NON-NLS-1$
          templ.parse("RETNLS_BLK", "RETNLS", true); //$NON-NLS-1$ //$NON-NLS-2$
         }
        // templ.parse("RETFIELDS_BLK", "RETFIELDS", false);
        // templ.parse("RETNLS_BLK", "RETNLS", false);
        // templ.parse("RETURN_BLK", "RETURN");
       }
     }
   }


  /**
   * Generate arguments.
   *
   * @param variableTypes variable types
   * @param templ Template
   * @param arguments Arguments
   * @param argDirections Argument directions
   * @param argRelated Argument related
   * @param argsIn Arguments in
   * @throws IOException IO exception
   */
  private void generateArguments(final Map<String, String> variableTypes, final TemplateEngine templ, final List<String> arguments, final List<String> argDirections, final List<String> argRelated, final int argsIn) throws IOException
   {
    if (argsIn == 0)
     {
      templ.setVar("JPARAMDESCS"); //$NON-NLS-1$
      templ.setVar("JPARAMS"); //$NON-NLS-1$
      templ.setVar("PARAMS"); //$NON-NLS-1$
      templ.parse("JPARAMDESCS_BLK", "JPARAMDESCS"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.parse("JPARAMS_BLK", "JPARAMS"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.parse("PARAMS_BLK", "PARAMS"); //$NON-NLS-1$ //$NON-NLS-2$
     }
    else
     {
      int k = 0;
      for (int j = 0; j < arguments.size(); ++j)
       {
        if ("out".equals(argDirections.get(j))) //$NON-NLS-1$
         {
          continue;
         }
        ++k;
        final String argumentName = arguments.get(j);
        // LOGGER.info("argumentName=" + argumentName);
        final String varType = variableTypes.get(argRelated.get(j));
        final boolean isString = "string".equals(varType); //$NON-NLS-1$
        templ.setVar("PARAMNAME", argumentName); //$NON-NLS-1$
        templ.setVar("JPARAMNAME", TR64ServiceTemplates.convertUnderline2CamelCase(argumentName.replace('-', '_'), false)); //$NON-NLS-1$
        templ.setVar("JPARAMNAMEVAL", (isString ? "" : "String.valueOf(") + TR64ServiceTemplates.convertUnderline2CamelCase(argumentName.replace('-', '_'), false) + (isString ? "" : ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        templ.setVar("JPARAMDESC", TR64ServiceTemplates.toSentence(argumentName)); //$NON-NLS-1$
        templ.setVar("PARAMTYPE", TYPE_MAP.get(varType)); //$NON-NLS-1$
        templ.setVar("COMMA", (k == argsIn) ? "" : ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        templ.parse("PARAMS_BLK", "PARAMS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("JPARAMDESCS_BLK", "JPARAMDESCS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("JPARAMS_BLK", "JPARAMS", true); //$NON-NLS-1$ //$NON-NLS-2$
       }
     }
   }


  /**
   * Generate service action.
   *
   * @param scpdUrl scpd url
   * @param classname Classname
   * @param variableTypes Variable types
   * @param templ Template
   * @param actionNL Action node list
   * @param allArgsOut All arguments out
   * @param i All arguments out
   * @return All args out
   * @throws IOException IO exception
   * @throws FileNotFoundException File not found
   */
  private int generateServiceAction(final String scpdUrl, final String classname, final Map<String, String> variableTypes, final TemplateEngine templ, final NodeList actionNL, final int allArgsOut, final int i) throws IOException
   {
    String methodName = ""; //$NON-NLS-1$
    final List<String> arguments = new ArrayList<>();
    final List<String> argDirections = new ArrayList<>();
    final List<String> argRelated = new ArrayList<>();
    int argsIn = 0;
    int argsOut = 0;
    int allArgsOutIntern = allArgsOut;
    final NodeList actionChildsNL = actionNL.item(i).getChildNodes();
    // Analyze
    for (int j = 0; j < actionChildsNL.getLength(); ++j) // action childs name, argumentList
     {
      final Node actionChild = actionChildsNL.item(j);
      if ("name".equals(actionChild.getNodeName())) //$NON-NLS-1$
       {
        methodName = actionChild.getTextContent();
        templ.setVar("METHODNAME", methodName); //$NON-NLS-1$
        templ.setVar("JMETHODNAME", TR64ServiceTemplates.convertUnderline2CamelCase(methodName.replace('-', '_'), false)); //$NON-NLS-1$
        templ.setVar("METHODDESC", TR64ServiceTemplates.toSentence(methodName)); //$NON-NLS-1$
       }
      else if ("argumentList".equals(actionChild.getNodeName())) //$NON-NLS-1$
       {
        final NodeList argumentsNL = actionChild.getChildNodes();
        for (int k = 0; k < argumentsNL.getLength(); ++k) // arguments
         {
          final Node argument = argumentsNL.item(k);
          if (argument.getNodeType() == 1)
           {
            final NodeList argChilds = argument.getChildNodes();
            for (int l = 0; l < argChilds.getLength(); ++l) // argument childs name, direction
             {
              final Node arg = argChilds.item(l);
              if (arg.getNodeType() == 1)
               {
                if ("name".equals(arg.getNodeName())) //$NON-NLS-1$
                 {
                  if (arguments.contains(arg.getTextContent()))
                   {
                    if (LOGGER.isWarnEnabled())
                     {
                      LOGGER.warn("Argument already exist: " + scpdUrl + " / " + methodName + " / " + arg.getTextContent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                     }
                    break;
                   }
                  arguments.add(arg.getTextContent());
                 }
                else if ("direction".equals(arg.getNodeName())) //$NON-NLS-1$
                 {
                  final String direction = arg.getTextContent();
                  argDirections.add(direction);
                  if ("in".equals(direction)) //$NON-NLS-1$
                   {
                    ++argsIn;
                   }
                  else if ("out".equals(direction)) //$NON-NLS-1$
                   {
                    ++argsOut;
                   }
                 }
                else if ("relatedStateVariable".equals(arg.getNodeName())) //$NON-NLS-1$
                 {
                  argRelated.add(arg.getTextContent());
                 }
               }
             }
           }
         }
        final TR64DataClasses dataClass = new TR64DataClasses(this.outputPath);
        allArgsOutIntern = dataClass.generateDataClass(classname, variableTypes, templ, allArgsOutIntern, methodName, arguments, argDirections, argRelated, argsOut);
       } // argumentList
     }

    // Generate
    templ.setVar("METHODS", templ.getVar("METHOD")); //$NON-NLS-1$ //$NON-NLS-2$
    templ.setBlock("METHODS", "RESULT", "RESULT_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setBlock("METHODS", "RETURN", "RETURN_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setVar("RETFIELDS_BLK"); //$NON-NLS-1$
    templ.setVar("RETNLS_BLK"); //$NON-NLS-1$
    templ.setBlock("RETURN", "RETFIELDS", "RETFIELDS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setBlock("RETURN", "RETNLS", "RETNLS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setBlock("METHODS", "RETURNDESCS", "RETURNDESCS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setVar("JPARAMDESCS_BLK"); //$NON-NLS-1$
    templ.setVar("JPARAMS_BLK"); //$NON-NLS-1$
    templ.setVar("PARAMS_BLK"); //$NON-NLS-1$
    templ.setBlock("METHODS", "JPARAMDESCS", "JPARAMDESCS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setBlock("METHODS", "JPARAMS", "JPARAMS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    templ.setBlock("METHODS", "PARAMS", "PARAMS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    generateReturnValues(variableTypes, templ, arguments, argDirections, argRelated, argsOut);
    generateArguments(variableTypes, templ, arguments, argDirections, argRelated, argsIn);

    templ.parse("RESULT_BLK", "RESULT"); //$NON-NLS-1$ //$NON-NLS-2$
    templ.parse("RETURN_BLK", "RETURN"); //$NON-NLS-1$ //$NON-NLS-2$
    templ.parse("RETURNDESCS_BLK", "RETURNDESCS"); //$NON-NLS-1$ //$NON-NLS-2$
    templ.parse("METHODS_BLK", "METHODS", true); //$NON-NLS-1$ //$NON-NLS-2$
    return allArgsOutIntern;
   }


  /**
   * Generate SCPD class.
   *
   * @param scpdUrl SCPD url path
   * @param classname Template name
   * @throws SAXException SAX exception
   * @throws IOException IO exception
   * @throws UnsupportedEncodingException Unsupported encoding
   * @throws ClientProtocolException Client protocol exception
   */
  public void generateServiceClass(final String scpdUrl, final String classname) throws IOException, SAXException
   {
    // LOGGER.info("scpdUrl=" + scpdUrl + " ------------------------------");
    final Document scpd = this.session.getDoc(scpdUrl);
    if (LOGGER.isDebugEnabled())
     {
      TR64Description.writeXml2File(scpd, this.outputPath + scpdUrl.substring(scpdUrl.lastIndexOf('/')));
     }
    final NodeList variablesNL = scpd.getElementsByTagName("stateVariable"); //$NON-NLS-1$
    final Map<String, String> variableTypes = new ConcurrentHashMap<>();
    initVariables(variablesNL, variableTypes);

    final TemplateEngine templ = new TemplateEngine(HandleUndefined.KEEP);
    templ.setFile(classname.toUpperCase(Locale.getDefault()), new File(this.outputPath + "/generated-sources/fb/tmpl", TR64ServiceTemplates.convertUnderline2CamelCase(classname, true) + ".tmpl")); //$NON-NLS-1$ //$NON-NLS-2$
    try
     {
      templ.subst(classname.toUpperCase(Locale.getDefault()));

      templ.setFile("METHOD", new File("src/main/resources", "method.tmpl")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      templ.subst("METHOD"); //$NON-NLS-1$

      templ.setFile("DATACLASS", new File("src/main/resources", "dataClass.tmpl")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      templ.subst("DATACLASS"); //$NON-NLS-1$

      if (!templ.setBlock(classname.toUpperCase(Locale.getDefault()), "METHODS", "METHODS_BLK") && LOGGER.isWarnEnabled()) //$NON-NLS-1$ //$NON-NLS-2$
       {
        LOGGER.warn("METHODS not found in " + classname.toUpperCase(Locale.getDefault())); //$NON-NLS-1$
       }
      if (!templ.setBlock(classname.toUpperCase(Locale.getDefault()), "IMPORTS_DATE", "IMPORTS_DATE_BLK") && LOGGER.isWarnEnabled()) //$NON-NLS-1$ //$NON-NLS-2$
       {
        LOGGER.warn("IMPORTS_DATE not found in " + classname.toUpperCase(Locale.getDefault())); //$NON-NLS-1$
       }

      final NodeList actionNL = scpd.getElementsByTagName("action"); //$NON-NLS-1$
      int allArgsOut = 0;
      for (int i = 0; i < actionNL.getLength(); ++i) // actions
       {
        allArgsOut = generateServiceAction(scpdUrl, classname, variableTypes, templ, actionNL, allArgsOut, i);
       }
      if (!variableTypes.containsValue("dateTime")) //$NON-NLS-1$
       {
        templ.setVar("IMPORTS_DATE"); //$NON-NLS-1$
       }
      templ.parse("IMPORTS_DATE_BLK", "IMPORTS_DATE", false); //$NON-NLS-1$ //$NON-NLS-2$
      templ.parse(classname.toUpperCase(Locale.getDefault()) + "final", classname.toUpperCase(Locale.getDefault())); //$NON-NLS-1$
      final File dir = new File(this.outputPath + "/generated-sources/de/powerstat/fb/generated/tr64/"); //$NON-NLS-1$
      /* final boolean success = */ dir.mkdirs();
      if (LOGGER.isDebugEnabled())
       {
        LOGGER.debug("Write3: " + dir.getAbsolutePath() + File.separator + classname + ".java"); //$NON-NLS-1$ //$NON-NLS-2$
       }
      try (PrintWriter out = new PrintWriter(dir.getAbsolutePath() + File.separator + TR64ServiceTemplates.convertUnderline2CamelCase(classname, true) + ".java", StandardCharsets.UTF_8)) //$NON-NLS-1$
       {
        out.println(templ.get(classname.toUpperCase(Locale.getDefault()) + "final")); //$NON-NLS-1$
       }
     }
    catch (final Exception e)
     {
      if (LOGGER.isDebugEnabled())
       {
        LOGGER.debug("", e); //$NON-NLS-1$
       }
     }
   }

 }
