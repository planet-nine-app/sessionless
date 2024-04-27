package com.planetnine.sessionless

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Timer
import kotlin.concurrent.timerTask

object IOUtils {
    /** Create new directories if they don't exist
     * @return this [File]
     * @see File.mkdirs */
    fun File.orMkdirs(): File {
        if (!exists()) mkdirs()
        return this
    }

    /** Create a new directory if it doesn't exist
     * @return this [File]
     * @see File.mkdir */
    fun File.orMkdir(): File {
        if (!exists()) mkdir()
        return this
    }

    /** Create a new file if it doesn't exist
     * @return this [File]
     * @see File.createNewFile */
    fun File.orNewFile(): File {
        if (!exists()) createNewFile()
        return this
    }

    /** Get the last child starting from this [File] down to the last of [children]
     * - ⚠️ Warning: this does NOT create any file or directory in your filesystem, but only returns a [File] object
     *
     * Example:
     * - File("x").child("a", "b", "c")
     *   - => x/a/b/c */
    fun File.child(vararg children: String): File =
        children.fold(this) { result, child ->
            File(result, child)
        }

    /** Get all the children starting from the first child of this [File] down to the last of [children]
     * - ⚠️ Warning: this does NOT create any file or directory in your filesystem, but only returns [File] objects
     *
     * Example:
     * - File("x").children("a", "b", "c")
     *   - => all of: x/a ... x/a/b ... x/a/b/c */
    fun File.children(vararg children: String): List<File> {
        var last = this
        return children.map {
            last = File(last, it)
            last
        }
    }

    /** Copy this [InputStream] content to [destination] [File]
     * @param overwrite overwrite [destination] if it exists
     * @return the number of bytes copied or null if not copied at all
     * @throws java.io.IOException */
    fun InputStream.copyToFile(destination: File, overwrite: Boolean = false): Long? {
        println("Copying ${this::class.java.simpleName} to $destination")
        use { i ->
            if (destination.exists()) {
                if (overwrite) destination.delete()
                else return null
            }
            destination.createNewFile()
            destination.outputStream().use { o ->
                return i.copyTo(o)
            }
        }
    }

    /** Copy this [File] to [destination]
     * @param overwrite overwrite [destination] if it exists
     * @return the number of bytes copied or null if not copied at all
     * @throws java.io.IOException */
    fun File.copyToFile(destination: File, overwrite: Boolean = false): Long? {
        println("Copying $this to $destination")
        if (!exists()) return null
        return FileInputStream(this).copyToFile(destination, overwrite)
    }

    /** Copy this [File] to [destination] then delete the original
     * @param overwrite overwrite [destination] if it exists
     * @return the number of bytes copied or null if not copied at all
     * @throws java.io.IOException */
    fun File.moveToFile(destination: File, overwrite: Boolean = false): Long? {
        println("Moving $this to $destination")
        val copied = copyToFile(destination, overwrite) ?: return null
        delete()
        return copied
    }

    /** Delete this [File] after [milliseconds] have elapsed
     * - Will check for existence before deleting just in case
     * @throws java.io.IOException */
    fun File.deleteAfter(milliseconds: Long) {
        println("Deleting $this after $milliseconds ms")
        Timer().schedule(timerTask {
            if (exists()) delete()
        }, milliseconds)
    }

//    /** Treat this [String] as a file extension and get its mime type using [MimeTypeMap] */
//    val String.mimeFromExtension: String?
//        get() = MimeTypeMap.getSingleton().getMimeTypeFromExtension(this)
//
//    /** Treat this [String] as a mime type and get its file extension using [MimeTypeMap] */
//    val String.extensionFromMime: String?
//        get() = MimeTypeMap.getSingleton().getExtensionFromMimeType(this)

    /** Get the mime type of this [File] */
    val File.mime: String?
        get() {
            return try {
                toURI()
                    .toURL()
                    .openConnection()
                    .apply { connect() }
                    .contentType
            } catch (e: Exception) {
                null
            }
        }

    fun File.recreateFile(): File = apply {
        delete()
        createNewFile()
    }

    fun File.recreateDir(): File = apply {
        deleteRecursively()
        mkdir()
    }

    /** Delete and recreate this [File] (or directory)
     * @throws java.io.IOException */
    fun File.recreate(): File = apply {
        if (isFile) recreateFile() else recreateDir()
    }

    /** Delete all children while keeping this directory
     * @param recreate Simply recreates the directory (quicker) if true. Otherwise it will delete children one by one (might be slightly slower)
     * @throws IllegalStateException if this is not a directory
     * @throws java.io.IOException */
    fun File.deleteChildrenRecursively(recreate: Boolean = false): File = apply {
        if (!isDirectory) throw IllegalStateException("Not a directory: $this")
        if (recreate) recreateDir()
        else listFiles()?.forEach { it.deleteRecursively() }
    }

    /** Empty this [File]
     * @param recreate Simply recreates the file (quicker) if true. Otherwise it will clear the content by writing an empty byte array into it (might be slightly slower)
     * @throws IllegalStateException if this is not a file
     * @throws java.io.IOException */
    fun File.clearFileContent(recreate: Boolean = false) {
        if (!isFile) throw IllegalStateException("Not a file: $this")
        if (recreate) recreateFile()
        else outputStream().use { it.write(ByteArray(0)) }
    }

//    fun File.loadImageBitmap(): ImageBitmap? {
//        return inputStream().use {
//            BitmapFactory.decodeStream(it)?.asImageBitmap()
//        }
//    }
}