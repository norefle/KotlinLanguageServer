package org.javacs.kt.util

import org.javacs.kt.LOG
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture

fun execAndReadStdout(shellCommand: String, directory: Path): String {
    val process = Runtime.getRuntime().exec(shellCommand, null, directory.toFile())
    val stdout = process.inputStream
    var result = ""
    stdout.bufferedReader().use {
        result = it.readText()
    }
    return result
}

inline fun withCustomStdout(delegateOut: PrintStream, task: () -> Unit) {
    val actualOut = System.out
    System.setOut(delegateOut)
    task()
    System.setOut(actualOut)
}

fun winCompatiblePathOf(path: String): Path {
    if (path.get(2) == ':' && path.get(0) == '/') {
        // Strip leading '/' when dealing with paths on Windows
        return Paths.get(path.substring(1))
    } else {
        return Paths.get(path)
    }
}

fun Path.replaceExtensionWith(newExtension: String): Path {
	val oldName = fileName.toString()
	val newName = oldName.substring(0, oldName.lastIndexOf(".")) + newExtension
	return resolveSibling(newName)
}

inline fun <T, C : Iterable<T>> C.onEachIndexed(transform: (index: Int, T) -> Unit): C = apply {
    var i = 0
    for (element in this) {
        transform(i, element)
        i++
    }
}

fun <T> noResult(message: String, result: T): T {
    LOG.info(message)
    return result
}

fun <T> noFuture(message: String, contents: T): CompletableFuture<T> = noResult(message, CompletableFuture.completedFuture(contents))

fun <T> emptyResult(message: String): List<T> = noResult(message, emptyList())

fun <T> nullResult(message: String): T? = noResult(message, null)

fun <T> firstNonNull(vararg optionals: () -> T?): T? {
    for (optional in optionals) {
        val result = optional()
        if (result != null) {
            return result
        }
    }
    return null
}

fun <T> nonNull(item: T?, errorMsgIfNull: String): T =
    if (item == null) {
        throw NullPointerException(errorMsgIfNull)
    } else item

inline fun <T> tryResolving(what: String, resolver: () -> T?): T? {
    try {
        val resolved = resolver()
        if (resolved != null) {
            LOG.info("Successfully resolved {}", what)
            return resolved
        } else {
            LOG.info("Could not resolve {} as it is null", what)
        }
    } catch (e: Exception) {
        LOG.info("Could not resolve {}: {}", what, e.message)
    }
    return null
}
