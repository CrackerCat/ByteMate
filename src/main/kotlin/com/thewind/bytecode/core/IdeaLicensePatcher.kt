package com.thewind.bytecode.core

import com.thewind.bytecode.editor.ByteCodeAssist
import com.thewind.bytecode.model.PatchClass
import com.thewind.bytecode.model.PatchStaticCode
import com.thewind.bytecode.model.PatchedClass
import javassist.ClassPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

suspend fun patchIntellijLicenseDialog() = withContext(Dispatchers.IO) {
    val jarPath = "/Users/read/Desktop/lib/util.jar"

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
    val className = "com.intellij.util.UrlImpl"
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
        "/Users/read/IdeaProjects/ByteMate/src/main/java"

    val className = "com/thewind/bytecode/intellij/def/IdeaLicensePatcher.java".replace("/", ".")
    return ByteCodeAssist.compileNewClass(sourceDir = sourceDir, className = className)
}