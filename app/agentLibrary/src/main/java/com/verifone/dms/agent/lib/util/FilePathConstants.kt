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

import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.verifone.dms.agent.lib.BuildConfig
import java.io.File
import java.util.*

object FilePathConstants {

    var downloadPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    var pathDirectory: Uri = FileProvider.getUriForFile(
        Objects.requireNonNull(DmsApplication.getAppContext())!!,
        BuildConfig.VHQ_APPLICATION_ID + ".provider", (File("${downloadPath}/output.zip"))
    )
    var logsPaths = "${downloadPath}/LOGS"
    var logsZipLocation = "${downloadPath}/output.zip"


}