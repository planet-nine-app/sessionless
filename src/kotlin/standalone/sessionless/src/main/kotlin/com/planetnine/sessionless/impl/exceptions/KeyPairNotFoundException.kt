package com.planetnine.sessionless.impl.exceptions

import java.security.UnrecoverableKeyException

class KeyPairNotFoundException(extra: String? = null) :
    UnrecoverableKeyException("Key pair was not found" + (if (extra == null) "" else ": $extra"))
