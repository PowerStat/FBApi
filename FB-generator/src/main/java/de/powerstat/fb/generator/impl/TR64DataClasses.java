/**
 * Copyright (C) 2020 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
package de.powerstat.fb.generator.impl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.powerstat.phplib.templateengine.TemplateEngine;


/**
 * TR64 data classes.
 */
public class TR64DataClasses
 {
  /**
   * Logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(TR64DataClasses.class);

  /**
   * OUT text.
   */
  private static final String OUT = "out"; //$NON-NLS-1$

  /**
   * RETCONVB.
   */
  private static final String RETCONVB = "RETCONVB"; //$NON-NLS-1$

  /**
   * RETCONVE.
   */
  private static final String RETCONVE = "RETCONVE"; //$NON-NLS-1$

  /**
   * Output path.
   */
  private final transient String outputPath;


  /**
   * Constructor.
   *
   * @param outputPath Output path for generated code
   */
  public TR64DataClasses(final String outputPath)
   {
    Objects.requireNonNull(outputPath, "outputPath"); //$NON-NLS-1$
    this.outputPath = outputPath;
   }


  /**
   * Return values.
   *
   * @param classname Class name
   * @param variableTypes Variable types
   * @param templ Template
   * @param allArgsOut All arguments out
   * @param methodName Method name
   * @param arguments Arguments
   * @param argDirections Argument directions
   * @param argRelated Argument related
   * @param argsOut Arguments out
   * @return All arguments out
   * @throws IOException IO exception
   * @throws FileNotFoundException File not found
   */
  public int generateDataClass(final String classname, final Map<String, String> variableTypes, final TemplateEngine templ, final int allArgsOut, final String methodName, final List<String> arguments, final List<String> argDirections, final List<String> argRelated, final int argsOut) throws IOException
   {
    if (argsOut == 0)
     {
      templ.setVar("METHODTYPE", "void"); //$NON-NLS-1$ //$NON-NLS-2$
      return allArgsOut;
     }
    else if (argsOut == 1)
     {
      final String mType = TR64ServiceClasses.getFromTypeMap(variableTypes.get(argRelated.get(argDirections.indexOf(OUT))));
      templ.setVar("METHODTYPE", mType); //$NON-NLS-1$
      templ.setVar("RETURNDESC", TR64ServiceTemplates.toSentence(arguments.get(argDirections.indexOf(OUT)))); //$NON-NLS-1$
      templ.setVar("RETURNPARAMNAME", arguments.get(argDirections.indexOf(OUT))); //$NON-NLS-1$
      if ("String".equals(mType)) //$NON-NLS-1$
       {
        templ.setVar(RETCONVB, ""); //$NON-NLS-1$
        templ.setVar(RETCONVE, ""); //$NON-NLS-1$
        templ.setVar("RETPARCONVB", ""); //$NON-NLS-1$ //$NON-NLS-2$
        templ.setVar("RETPARCONVE", ""); //$NON-NLS-1$ //$NON-NLS-2$
       }
      else
       {
        templ.setVar("RETPARCONVB", ""); //$NON-NLS-1$ //$NON-NLS-2$
        templ.setVar("RETPARCONVE", ""); //$NON-NLS-1$ //$NON-NLS-2$
        if ("long".equals(mType)) //$NON-NLS-1$
         {
          templ.setVar(RETCONVB, "Long.parseLong("); //$NON-NLS-1$
          templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
         }
        else if ("int".equals(mType)) //$NON-NLS-1$
         {
          templ.setVar(RETCONVB, "Integer.parseInt("); //$NON-NLS-1$
          templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
         }
        else if ("boolean".equals(mType)) //$NON-NLS-1$
         {
          templ.setVar(RETCONVB, "Boolean.parseBoolean("); //$NON-NLS-1$
          templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
         }
        else if ("short".equals(mType)) //$NON-NLS-1$
         {
          templ.setVar(RETCONVB, "Short.parseShort("); //$NON-NLS-1$
          templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
         }
        else if ("Date".equals(mType)) //$NON-NLS-1$
         {
          templ.setVar(RETCONVB, "DateFormat.getDateInstance().parse("); //$NON-NLS-1$
          templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
         }
        else
         {
          templ.setVar(RETCONVB, ""); //$NON-NLS-1$
          templ.setVar(RETCONVE, ""); //$NON-NLS-1$
         }
       }
      return allArgsOut;
     }
    else // if (argsOut > 1)
     {
      final String clName = (classname + methodName).replace('-', '_');
      templ.setVar("METHODTYPE", TR64ServiceTemplates.convertUnderline2CamelCase(clName, true)); //$NON-NLS-1$
      templ.setVar("RETURNDESC", TR64ServiceTemplates.toSentence(clName)); //$NON-NLS-1$
      templ.setVar(RETCONVB, "new " + TR64ServiceTemplates.convertUnderline2CamelCase(clName, true) + "("); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setVar(RETCONVE, ")"); //$NON-NLS-1$
      templ.setVar("RETURNPARAMNAME", arguments.get(argDirections.indexOf(OUT))); //$NON-NLS-1$

      templ.setVar(clName.toUpperCase(Locale.getDefault()), templ.getVar("DATACLASS")); //$NON-NLS-1$
      templ.setVar("CLASSNAME", TR64ServiceTemplates.convertUnderline2CamelCase(clName, true)); //$NON-NLS-1$
      templ.setVar("CLASSDESC", TR64ServiceTemplates.toSentence(TR64ServiceTemplates.convertUnderline2CamelCase(clName, true))); //$NON-NLS-1$
      templ.setVar("FIELDS_BLK"); //$NON-NLS-1$
      templ.setVar("DOCS_BLK"); //$NON-NLS-1$
      templ.setVar("ASSIGNMENTS_BLK"); //$NON-NLS-1$
      templ.setVar("CPARAMS_BLK"); //$NON-NLS-1$
      templ.setVar("GETTERS_BLK"); //$NON-NLS-1$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "DC_IMPORTS_DATE", "DC_IMPORTS_DATE_BLK"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "FIELDS", "FIELDS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "DOCS", "DOCS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "ASSIGNMENTS", "ASSIGNMENTS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "CPARAMS", "CPARAMS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$
      templ.setBlock(clName.toUpperCase(Locale.getDefault()), "GETTERS", "GETTERS_BLK"); //$NON-NLS-1$ //$NON-NLS-2$

      int commaCounter = 0;
      for (int m = 0; m < arguments.size(); ++m)
       {
        if ("in".equals(argDirections.get(m))) //$NON-NLS-1$
         {
          continue;
         }
        ++commaCounter;
        final String argumentName = arguments.get(m);

        templ.setVar("FIELDDESC", TR64ServiceTemplates.toSentence(argumentName)); //$NON-NLS-1$
        templ.setVar("FIELDTYPE", TR64ServiceClasses.getFromTypeMap(variableTypes.get(argRelated.get(m)))); //$NON-NLS-1$
        templ.setVar("FIELDNAME", TR64ServiceTemplates.convertUnderline2CamelCase(argumentName.replace('-', '_'), false)); //$NON-NLS-1$
        templ.setVar("UFIELDNAME", TR64ServiceTemplates.convertUnderline2CamelCase(argumentName.replace('-', '_'), true)); //$NON-NLS-1$
        templ.setVar("COMMA", (commaCounter == argsOut) ? "" : ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (!variableTypes.containsValue("dateTime")) //$NON-NLS-1$
         {
          templ.setVar("DC_IMPORTS_DATE"); //$NON-NLS-1$
         }
        templ.parse("DC_IMPORTS_DATE_BLK", "DC_IMPORTS_DATE", false); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("FIELDS_BLK", "FIELDS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("DOCS_BLK", "DOCS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("ASSIGNMENTS_BLK", "ASSIGNMENTS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("CPARAMS_BLK", "CPARAMS", true); //$NON-NLS-1$ //$NON-NLS-2$
        templ.parse("GETTERS_BLK", "GETTERS", true); //$NON-NLS-1$ //$NON-NLS-2$
       }
      templ.parse(clName.toUpperCase(Locale.getDefault()) + "final", clName.toUpperCase(Locale.getDefault())); //$NON-NLS-1$
      final File dir = new File(this.outputPath + "/generated-sources/de/powerstat/fb/generated/tr64/"); //$NON-NLS-1$
      /* final boolean success = */ dir.mkdirs();
      if (LOGGER.isDebugEnabled())
       {
        LOGGER.debug("Write2: " + dir.getAbsolutePath() + File.separator + clName + ".java"); //$NON-NLS-1$ //$NON-NLS-2$
       }
      try (PrintWriter out = new PrintWriter(dir.getAbsolutePath() + File.separator + TR64ServiceTemplates.convertUnderline2CamelCase(clName, true) + ".java", StandardCharsets.UTF_8)) //$NON-NLS-1$
       {
        out.println(templ.get(clName.toUpperCase(Locale.getDefault()) + "final")); //$NON-NLS-1$
       }
      return allArgsOut + 1;
     }
   }

 }
