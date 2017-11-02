package com.jc.api.common.crypto

import com.jc.api.common.ConfigWithDefault
import com.typesafe.config.Config

trait CryptoConfig extends ConfigWithDefault {
  def rootConfig: Config

  lazy val iterations  = getInt("crypto.argon2.iterations", 2)
  lazy val memory      = getInt("crypto.argon2.memory", 16383)
  lazy val parallelism = getInt("crypto.argon2.parallelism", 4)
}
