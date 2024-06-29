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
        val tempCrackJarPath =
            ByteCodeAssist.packageClassToJar(originalJarPath = jarPath, patchedList = listOf(patchedClass))
        onEvent(if (tempCrackJarPath != null) "成功生成补丁文件!" else "破解失败")
        tempCrackJarPath ?: return
        onEvent("开始备份原文件")
        val backUpState = File(jarPath).renameTo(File("$jarPath.bak"))
        onEvent("备份原文件${if (backUpState) "成功" else "失败"}:${jarPath}.bak")
        onEvent("开始替换原文件")
        val replaceState = File(tempCrackJarPath).renameTo(File(jarPath))
        onEvent("破解${if (replaceState) "成功！" else "失败， 请手动替换"}")



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