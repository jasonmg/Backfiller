package com.jason.utils

import org.slf4j.{Logger, LoggerFactory}


trait Log {
  val log = LoggerFactory.getLogger(this.getClass)
//  LoggerFactory.getLogger()
}
