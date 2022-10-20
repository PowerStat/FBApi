/*
 * Copyright (C) 2019-2020 Dipl.-Inform. Kai Hofmann. All rights reserved!
 */
module de.powerstat.fb
 {
  exports de.powerstat.fb;

  requires org.apache.logging.log4j;
  requires java.xml;
  requires jakarta.xml.bind;
  requires de.powerstat.fb.mini;
  requires de.powerstat.fb.generated;
  requires de.powerstat.fb.generated.tr64;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;

 }
