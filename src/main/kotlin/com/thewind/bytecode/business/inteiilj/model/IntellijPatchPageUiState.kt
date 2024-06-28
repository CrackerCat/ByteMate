package com.thewind.bytecode.business.inteiilj.model

import androidx.compose.runtime.Immutable

@Immutable
data class IntellijPatchPageUiState(
    val installPath: String = "",
    val notices: List<String> = listOf("输入安装地址"),
    val pathHint:String = """
        点击此处输入安装地址，例如\n" + "/home/read/.local/share/JetBrains/Toolbox/apps/intellij-idea-ultimate/
    """.trimIndent(),
    val isPatching: Boolean = false
)
