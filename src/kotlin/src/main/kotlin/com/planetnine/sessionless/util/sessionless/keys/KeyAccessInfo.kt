package com.planetnine.sessionless.util.sessionless.keys

/** Information about accessing a key in secure storage
 * @param alias Key alias (used in storage)
 * @param password password securing the key pair */
class KeyAccessInfo(val alias: String, val password: CharArray? = null)