/*
 * Copyright (C) 2019-2023 Dipl.-Inform. Kai Hofmann. All rights reserved!
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
  requires de.powerstat.validation;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;

 }
