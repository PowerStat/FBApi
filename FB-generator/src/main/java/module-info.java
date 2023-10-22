/*
 * Copyright (C) 2019-2023 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */


/**
 * Module descriptor.
 */
module de.powerstat.fb.generator
 {
  exports de.powerstat.fb.generator;

  requires org.apache.logging.log4j;
  requires java.xml;
  requires de.powerstat.phplib.templateengine;
  requires de.powerstat.fb.mini;
  requires org.apache.httpcomponents.httpclient;
  requires org.apache.httpcomponents.httpcore;

 }
