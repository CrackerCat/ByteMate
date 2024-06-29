package com.thewind.bytecode.editor

import com.thewind.bytecode.model.PatchClass
import com.thewind.bytecode.model.PatchMethodType
import com.thewind.bytecode.model.PatchedClass
import com.thewind.bytecode.model.entryName
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewConstructor
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object ByteCodeAssist {

    fun modifyClass(
        jarPath: String,
        className: String,
        patchClass: PatchClass
    ): PatchedClass {
        ClassPool.getDefault().insertClassPath(jarPath)
        val classToModify = ClassPool.getDefault().getCtClass(className)
        patchInterface(patchClass = patchClass, classToModify = classToModify)
        patchConstructor(patchClass = patchClass, classToModify = classToModify)
        patchMethod(patchClass = patchClass, classToModify = classToModify)
        patchStaticCode(patchClass = patchClass, classToModify = classToModify)

        val exportClassFolder = File(jarPath).parent + File.separator + "crack"
        classToModify.writeFile(exportClassFolder)
        val path = exportClassFolder + File.separator + className.replace(".", File.separator) + ".class"
        return PatchedClass(className, path)
    }

    private fun patchConstructor(patchClass: PatchClass, classToModify: CtClass) {
        patchClass.patchConstructors.forEach { data ->
            val paramsTypes = data.parameterTypes.map {
                ClassPool.getDefault().get(it)
            }.toTypedArray()
            var cont = try {
                classToModify.getDeclaredConstructor(paramsTypes)
            } catch (_: Exception) {
                null
            }
            if (cont == null) {
                cont = CtNewConstructor.make(paramsTypes, null, "{ ${data.body} }", classToModify)
                classToModify.addConstructor(cont)
            } else {
                cont.setBody("{ ${data.body} }")
            }

        }
    }

    private fun patchMethod(patchClass: PatchClass, classToModify: CtClass) {
        patchClass.patchMethods.forEach { data ->
            when (data.type) {
                PatchMethodType.REPLACE_BODY -> {
                    val methodToModify = classToModify.getDeclaredMethod(data.name)
                    methodToModify.setBody(data.body)
                }

                PatchMethodType.INSERT_TO_START -> {
                    val methodToModify = classToModify.getDeclaredMethod(data.name)
                    methodToModify.insertBefore(data.body)
                }

                PatchMethodType.ADD_METHOD -> {
                    val newMethod = CtMethod.make(data.body, classToModify)
                    classToModify.addMethod(newMethod)
                }
            }

        }
    }

    private fun patchStaticCode(patchClass: PatchClass, classToModify: CtClass) {
        patchClass.patchStaticCode?.body?.let {
            val staticBlock = classToModify.makeClassInitializer()
            staticBlock.setBody(it)
        }

    }

    private fun patchInterface(patchClass: PatchClass, classToModify: CtClass) {
        patchClass.patchInterface?.let {
            classToModify.addInterface(ClassPool.getDefault().get(it.name))
        }
    }

    fun packageClassToJar(originalJarPath: String, patchedList: List<PatchedClass>): String? {
        val newJarPath = "$originalJarPath.crack.jar"
        try {
            val entrySet = patchedList.map { it.entryName }.toSet()
            // Create a new JAR file
            val newJar = JarOutputStream(File(newJarPath).outputStream())

            // Copy entries from the original JAR to the new JAR
            ZipInputStream(File(originalJarPath).inputStream()).use { originalZip ->
                while (true) {
                    val entry = originalZip.nextEntry ?: break
                    if (!entrySet.contains(entry.name)) {
                        newJar.putNextEntry(ZipEntry(entry.name))
                        newJar.write(originalZip.readBytes())
                        newJar.closeEntry()
                    }

                }
            }

            // Add the new class to the new JAR
            patchedList.forEach { data ->
                newJar.putNextEntry(ZipEntry(data.entryName))
                File(data.classFilePath).inputStream().use { input ->
                    newJar.write(input.readBytes())
                }
            }

            newJar.closeEntry()
            // Close the new JAR file
            newJar.close()
        } catch (e: Exception) {
            return null
        }
        return newJarPath
    }

    fun makeClassName(className: String, innerClassName: String) = "$className\$$innerClassName"


    fun compileNewClass(sourceDir: String, className: String): List<PatchedClass> {
        val javaFilePath = sourceDir + File.separator + className.classNameToPath
        val compileCommand =
            "javac -target 17 -source 17 $javaFilePath -d $sourceDir"
        val packageName = className.substring(0, className.replace(".java", "").lastIndexOf("."))
        val outDirPath = sourceDir + File.separator + packageName.replace(".", File.separator)
        val reader = BufferedReader(InputStreamReader(Runtime.getRuntime().exec(arrayOf(compileCommand)).inputStream))

        var line: String? = null

        while (reader.readLine()?.also {
                line = it
            } != null) {
            println(line)
        }

        reader.close()

        val files = File(outDirPath).takeIf { it.exists() }?.listFiles()?.filter { it.absolutePath.endsWith(".class") }
            ?: return emptyList()

        return files.map {
            PatchedClass(
                className = packageName + it.name.replace(".class", ""),
                classFilePath = it.absolutePath
            )
        }

    }

}


private val String.classNameToPath: String
    get() {
        val dotIndex = replace(".java", "").lastIndexOf(".")
        val packagePath = substring(0, dotIndex).replace(".", File.separator)
        val fileName = substring(dotIndex + 1)
        return packagePath + File.separator + fileName

    }