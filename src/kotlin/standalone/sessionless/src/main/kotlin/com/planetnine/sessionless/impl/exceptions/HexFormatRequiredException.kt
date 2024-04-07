package com.planetnine.sessionless.impl.exceptions

class HexFormatRequiredException(target: String) : Exception("Hex format is required: $target")
