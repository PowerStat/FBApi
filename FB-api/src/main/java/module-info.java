/*
 * Copyright (C) 2019-2026 Dipl.-Inform. Kai Hofmann. All rights reserved!
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0.
 */


/**
 * Module descriptor.
 */
module de.powerstat.fb
 {
  exports de.powerstat.fb;

  requires org.apache.logging.log4j;
  requires transitive java.xml;
  requires jakarta.xml.bind;
  requires de.powerstat.fb.mini;
  requires transitive de.powerstat.fb.generated;
  requires de.powerstat.fb.generated.tr64;
  requires de.powerstat.ddd;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;
  requires org.checkerframework.checker.qual;
  requires org.jmolecules.ddd;

 }
