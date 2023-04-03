/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */

package com.verifone.dms.agent.lib.util

import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import java.io.*
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


object ZipUnZipUtils {
    private const val BUFFER_SIZE = 4096

    /* zipAll(directory: String, zipFile: String)
    This method will be used to zip all files
    by calling  the method
    zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String)
    i/p : directory - Target Directory
     zipFile - Destination
    o/p : zip files

    */
    @Nullable
    fun zipAll(directory: String, zipFile: String) {
        val sourceFile = File(directory)
        if (sourceFile.exists()) {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
                zipFiles(it, sourceFile, "")
            }
        } else {
            Log.e("Zip All", "File Not Found")
        }
    }

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
        val data = ByteArray(2048)
        var entry: ZipEntry
        sourceFile.listFiles()?.forEach { f ->
            if (f.isDirectory) {
                val path = if (parentDirPath == "") {
                    f.name
                } else {
                    parentDirPath + File.separator + f.name
                }
                entry = ZipEntry(path + File.separator)
                entry.time = f.lastModified()
                entry.isDirectory
                entry.size = f.length()
                zipOut.putNextEntry(entry)
                //Call recursively to add files within this directory
                zipFiles(zipOut, f, path)
            } else {
                FileInputStream(f).use { fi ->
                    BufferedInputStream(fi).use { origin ->
                        val path = parentDirPath + File.separator + f.name
                        entry = ZipEntry(path)
                        entry.time = f.lastModified()
                        entry.isDirectory
                        entry.size = f.length()
                        zipOut.putNextEntry(entry)
                        while (true) {
                            val readBytes = origin.read(data)
                            if (readBytes == -1) {
                                break
                            }
                            zipOut.write(data, 0, readBytes)
                        }
                    }
                }
            }
        }
    }
    /* unzip(zipFilePath: File, destDirectory: String
    This method will be used to unzip  all files by calling  the method
    extractFile(inputStream: InputStream, destFilePath: String)
    i/p : zipFilePath - Target Directory
     zipFile - Destination
    o/p : unzip files

    */

    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: String) {
        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }
        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val filePath = destDirectory + File.separator + entry.name
                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdirs()
                    }
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }
}