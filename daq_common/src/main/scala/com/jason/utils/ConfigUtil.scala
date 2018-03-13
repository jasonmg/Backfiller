package com.jason.utils

import com.typesafe.config.ConfigFactory

object ConfigUtil extends App{

 lazy val config = ConfigFactory.load()

}
