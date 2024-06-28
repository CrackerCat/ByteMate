package com.thewind.bytecode.business.inteiilj.patcher

import com.thewind.bytecode.core.FunctionPatcher
import com.thewind.bytecode.editor.ByteCodeAssist
import com.thewind.bytecode.model.PatchClass
import com.thewind.bytecode.model.PatchStaticCode
import com.thewind.bytecode.model.PatchedClass
import javassist.ClassPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

suspend fun patchIntellijLicenseDialog() = withContext(Dispatchers.IO) {

    val jarPath = "/home/read/.local/share/JetBrains/Toolbox/apps/webstorm/lib/util.jar"

    println("start generate class")

    ClassPool.getDefault()
        .insertClassPath(jarPath)


    val patchedList = compileClass().toMutableList().apply {
        add(addLicenseChecker(jarPath))
    }

    println("start pack to jar")
    ByteCodeAssist.packageClassToJar(originalJarPath = jarPath, patchedList = patchedList)
    println("end pack to jar")
}

private fun insertClass(jarPath: String, classFilePath: String): PatchedClass {
    val clazz = ClassPool.getDefault().makeClass(FileInputStream(classFilePath))
    return PatchedClass(
        className = clazz.name,
        classFilePath = classFilePath
    )
}


private fun addLicenseChecker(jarPath: String): PatchedClass {
    val className = "com.intellij.openapi.extensions.PluginId"
    val patchClass = "com.thewind.bytecode.intellij.def.IdeaLicensePatcher"
    return ByteCodeAssist.modifyClass(
        jarPath = jarPath,
        className = className,
        patchClass = PatchClass(
            patchStaticCode = PatchStaticCode(
                body = """
                     {
                       ${patchClass}.load();
                     }
                """.trimIndent()
            )
        )
    )
}

private fun compileClass(): List<PatchedClass> {
    val sourceDir =
        "/home/read/IdeaProjects/ByteMate/src/main/java"

    val className = "com/thewind/bytecode/intellij/def/IdeaLicensePatcher.java".replace("/", ".")
    return ByteCodeAssist.compileNewClass(sourceDir = sourceDir, className = className)
}


suspend fun patch() = withContext(Dispatchers.IO) {

    val jarPath = "E:\\core.jar"
    println("start generate class")
    ClassPool.getDefault()
        .insertClassPath("C:\\Users\\read\\AppData\\Local\\Programs\\IntelliJ IDEA Ultimate\\lib\\app-client.jar")
    ClassPool.getDefault()
        .insertClassPath("C:\\Users\\read\\AppData\\Local\\Programs\\IntelliJ IDEA Ultimate\\lib\\util-8.jar")
    val patchedList = listOf(
        FunctionPatcher.mockLoginStatus(jarPath),
        FunctionPatcher.mockLoginStatusParser(jarPath),
        FunctionPatcher.enableAllCopilotFeature(jarPath),
        FunctionPatcher.mockLoginTypeParser(jarPath),
        FunctionPatcher.mockAgentGitHubService(jarPath)
    )
    println("start pack to jar")
    ByteCodeAssist.packageClassToJar(originalJarPath = jarPath, patchedList = patchedList)
    println("end pack to jar")
}

