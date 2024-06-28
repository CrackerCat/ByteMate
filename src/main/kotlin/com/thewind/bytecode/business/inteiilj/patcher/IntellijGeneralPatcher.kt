package com.thewind.bytecode.business.inteiilj.patcher

import com.thewind.bytecode.editor.ByteCodeAssist
import com.thewind.bytecode.model.PatchClass
import com.thewind.bytecode.model.PatchMethod
import com.thewind.bytecode.model.PatchMethodType
import java.io.File

object IntellijGeneralPatcher {

    fun patchLicenseDialog(installDir: String, onEvent: (msg: String) -> Unit = {}) {
        val jarPath = findTargetFilePath(installDir, "app-client.jar").takeIf { it.isNotBlank() }
        if (jarPath == null) {
            onEvent("未发现文件，破解失败")
            return
        }
        onEvent("发现文件：jarPath = $jarPath")
        val className = "com.intellij.openapi.ui.impl.DialogWrapperPeerImpl"
        val patchedClass = ByteCodeAssist.modifyClass(
            jarPath = jarPath, className = className, patchClass = PatchClass(
                patchMethods = listOf(
                    PatchMethod(
                        name = "setTitle", body = """
                    if (title.equals("Licenses")) {
                        dispose();
                        return;
                    }
                """.trimIndent(), type = PatchMethodType.INSERT_TO_START
                    )
                )
            )
        )
        onEvent("正在应用补丁")
        val success =
            ByteCodeAssist.packageClassToJar(originalJarPath = jarPath, patchedList = listOf(patchedClass)) != null
        onEvent(if (success) "破解完成" else "破解失败")
    }

    private fun findTargetFilePath(path: String, targetFileName: String): String {
        val file = File(path).takeIf { it.exists() } ?: return ""
        if (file.isFile && file.name == targetFileName) return file.absolutePath
        if (file.isDirectory) {
            file.listFiles()?.forEach { item ->
                val targetPath = findTargetFilePath(item.absolutePath, targetFileName)
                if (targetPath.isNotBlank()) return targetPath
            }
        }
        return ""
    }

}