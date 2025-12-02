/*
 * Copyright (C) 2019-2025 Dipl.-Inform. Kai Hofmann. All rights reserved!
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0.
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
  requires org.checkerframework.checker.qual;
  requires org.jmolecules.ddd;

 }
