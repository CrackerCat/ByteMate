package com.thewind.bytecode.model

data class PatchMethod(
    val name: String = "",
    val body: String = "{}",
    val type: PatchMethodType = PatchMethodType.REPLACE_BODY
)