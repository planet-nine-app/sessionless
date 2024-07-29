package com.planetnine.sessionless.impl.exceptions

class HexFormatRequiredException(target: String? = null) :
    RuntimeException("Hex format is required" + (if (target == null) "" else ": $target"))
