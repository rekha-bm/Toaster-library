package com.verifone.dms.agent.lib

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.verifone.dms.agent.lib.json.model.response.CommonResponse
import com.verifone.dms.agent.lib.util.AppUtil
import com.verifone.dms.agent.lib.util.ZipUnZipUtils
import com.verifone.dms.agent.lib.validator.ResponseValidatorImpl
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToLong
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DownloadFileManager {
  private lateinit var fileUrl: String
  private lateinit var fileName: String
  var appUtil: AppUtil = AppUtil()
  lateinit var alertDialog: AlertDialog
  private var counter = 0

  fun downloadFile(jsonObject: CommonResponse, context: Context): String {
    Log.e("Download json", jsonObject.toString())
    fileUrl =
        jsonObject.serverMessage.content.opSets
            .opSet
            ?.get(0)
            ?.ops
            ?.get(0)
            ?.parameters
            ?.url
            .toString()
    Log.e("Download Url", fileUrl)
    return fileUrl
  }

  fun downloadFileName(jsonObject: CommonResponse, context: Context): String {
    fileName =
        jsonObject.serverMessage.content.opSets.opSet
            ?.get(0)
            ?.ops
            ?.get(0)
            ?.parameters
            ?.fileName
            .toString()
    Log.e("Download file Name", fileName)
    return fileName
  }

  @OptIn(DelicateCoroutinesApi::class)
  suspend fun downloadFileStatus(
      context: Context,
      fileUrl: String,
      fileName: String,
      mpName: String,
      mpJSONObject: CommonResponse
  ): Flow<String> = flow {
    alertDialog = appUtil.getCustomAlertDialog(context)
    if (alertDialog.isShowing) alertDialog.dismiss()

    val storageDirectory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +
            "/" +
            fileName
    var file =
        File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString())
    if (!file.exists()) {
      file.mkdirs()
    }
    var downloadedFileSize = File(storageDirectory).length()
    val receivedFileSize =
        mpJSONObject.serverMessage.content.opSets.opSet?.get(0)?.ops?.get(0)?.parameters?.fileSize
            ?: 0

    GlobalScope.launch(Dispatchers.IO) {
      try {
        val url = URL(fileUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept-Encoding", "identity")
        file = File(storageDirectory)
        var bytesCopied: Long = 0
        if (file.exists() && (receivedFileSize > downloadedFileSize))
            run {
              Log.e("Download status", "File exists")
              bytesCopied = file.length()
            }
        else if (file.exists() && receivedFileSize.toLong() == downloadedFileSize) {
          Log.e("Download status", "File already exists")
          emit(installAPKFileFromZip(context, storageDirectory, mpName, mpJSONObject))
          // it.resume(installAPKFileFromZip(context, storageDirectory,mpName,mpJSONObject))
          alertDialog.dismiss()
          emit("success")
          // it.resume("success")
          return@launch
        } else {
          Log.e("Download status", "File not exists")
        }
        connection.setRequestProperty("Range", "bytes=$bytesCopied-")
        connection.connectTimeout = 2000
        connection.connect()

        if (connection.responseCode in 200..299) {
          val fileSize = connection.contentLength
          val inputStream = BufferedInputStream(connection.inputStream)
          val outputStream =
              (if (bytesCopied == 0L) FileOutputStream(storageDirectory)
              else FileOutputStream(storageDirectory, true))
          Log.e("Download status ", "File Size $fileSize")
          Log.e("Download status ", "Byte Copied $bytesCopied")
          Log.e("Download status", "filePath $storageDirectory")
          val buffer = ByteArray(1024)
          var bytes = inputStream.read(buffer, 0, 1024)
          val byteCounter = bytesCopied
          withContext(Dispatchers.Main) {
            /* it.resume("downloadStarted")
            it.resume("DownloadInProgress")*/
            emit("downloadStarted")
            emit("DownloadInProgress")
            /*publishStatusReport(context,dmsAgent,"downloadStarted",mpName, mpJSONObject)
            publishStatusReport(context,dmsAgent,"DownloadInProgress",mpName, mpJSONObject)*/
            alertDialog.show()
          }

          while (bytes >= 0) {
            bytesCopied += bytes
            val downloadProgress =
                (bytesCopied.toFloat() / (fileSize.toFloat() + byteCounter) * 100).toInt()
            // Log.e("Download status","Bytes copied ${bytesCopied.toFloat()} /
            // ${fileSize.toFloat()+ byteCounter }")
            withContext(Dispatchers.Main) {
              appUtil.progressBarCustom.progress = downloadProgress
              appUtil.percentTextView.text =
                  appUtil.getProgressDisplayLine(
                      bytesCopied, ((fileSize.toFloat() + byteCounter).roundToLong()))
            }
            //  Log.e("Download status", "Percentage: $downloadProgress")
            outputStream.write(buffer, 0, bytes)
            bytes = inputStream.read(buffer, 0, 1024)
          }
          downloadedFileSize = File(storageDirectory).length()
          Log.e("Download status: ", "Download completed")
          Log.e("Download status", "DownloadedFileSize: $downloadedFileSize")
          Log.e("Download status", "received file size: $receivedFileSize")
          // publishStatusReport(context,dmsAgent,"DownloadCompleted",mpName, mpJSONObject)
          // it.resume("DownloadCompleted")
          emit("DownloadCompleted")
          if (ResponseValidatorImpl()
              .verifyDownloadedFileSize(receivedFileSize.toLong(), downloadedFileSize)) {
            emit(installAPKFileFromZip(context, storageDirectory, mpName, mpJSONObject))
            alertDialog.dismiss()
            emit("success")
            return@launch
          } else {
            Log.e("Download status", "File size didn't match ")

            if (counter < 1) {
              counter++
              GlobalScope.launch {
                delay(5000)
                withContext(Dispatchers.Main) {
                  File(storageDirectory).delete()
                  downloadFileStatus(context, fileUrl, fileName, mpName, mpJSONObject)
                }
              }
            } else {
              withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                Toast.makeText(context, "File size didn't Match", Toast.LENGTH_LONG).show()
                Log.e("Download status", "File size didn't match")
                emit("failed")
              }
              return@launch
            }
          }
        } else {
          Log.e("Download status: ", "Connection failed ---->reconnecting...")
          if (counter < 3) {
            counter++
            GlobalScope.launch {
              delay(5000)
              withContext(Dispatchers.Main) {
                downloadFileStatus(context, fileUrl, fileName, mpName, mpJSONObject)
              }
            }
          } else {
            withContext(Dispatchers.Main) {
              Toast.makeText(context, "Connection failed", Toast.LENGTH_LONG).show()
              emit("failed")
            }
            return@launch
          }
        }
      } catch (e: Exception) {
        Log.e("Download status: ", "Download failed $e")
        if (counter < 3) {
          counter++
          GlobalScope.launch {
            delay(5000)
            withContext(Dispatchers.Main) {
              alertDialog.dismiss()
              downloadFileStatus(context, fileUrl, fileName, mpName, mpJSONObject)
            }
          }
        } else {
          withContext(Dispatchers.Main) {
            alertDialog.dismiss()
            Toast.makeText(context, "error in downloading file", Toast.LENGTH_LONG).show()
            // publishStatusReport(context,dmsAgent,"Failed call",mpName, mpJSONObject)
            emit("failed")
          }
        }
      }
    }
  }

  private suspend fun installAPKFileFromZip(
      context: Context,
      sourcePath: String,
      mpName: String,
      mpJSONObject: CommonResponse
  ): String = suspendCoroutine {
    Log.e("Install ZIp Path", sourcePath)
    if (sourcePath.substring(sourcePath.lastIndexOf(".")).contains("zip")) {
      val apkFileDirectory = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "/"
      ZipUnZipUtils.unzip(File(sourcePath), apkFileDirectory)
      val dir = File(apkFileDirectory)
      Log.e("apk path", apkFileDirectory)
      val names = dir.list { dir, name -> name.endsWith(".apk") }
      Log.e("apk name", names[0].toString())
      if (File(apkFileDirectory + (names[0])).exists()) {
        // publishStatusReport(context,dmsAgent,"InstallStarted",mpName,mpJSONObject)
        it.resume("InstallStarted")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                BuildConfig.VHQ_APPLICATION_ID + ".provider",
                File(apkFileDirectory + names[0])),
            "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
      } else {
        Log.e("FileInstall Status", "Unable to find Apk File")
      }
    } else {
      Log.e("FIleInstallStatus", "Downloaded file is not ZIP file")
    }
  }

  /* private suspend fun publishStatusReport(context: Context,dmsAgent: MqttApi.BoundMqttClient, result: String, msgName: String?, mpJSONObject: CommonResponse): Boolean {
  val networkHelper = NetworkHelper(context)
   if (!dmsAgent.isAbandoned()) {
       Log.i("TAG", " onclick :")

       val msg = networkHelper.publishStatus(mpJSONObject, result)
       dmsAgent.onPublish(appUtil.STATUS_REPORT_TOPIC, msg.toByteArray(), 0, true)

       Toast.makeText(
           context, "published :$msg got $result to $msgName", Toast.LENGTH_LONG)
           .show()
      */
  /* var serverMessage =
  networkHelper.mqttJsonSubscribe(
      context, appUtil.SERVER_RESPONSE_TOPIC, mpJSONObject.toString())*/
  /*
              withContext(Dispatchers.Main) {
                  var serverStatus = mpJSONObject?.serverMessage?.content?.status
                  if (serverStatus.equals("success")) {
                      return@withContext true
                  }
                  Toast.makeText(context, serverStatus, Toast.LENGTH_LONG).show()
              }
          } else{
              Log.e("Download status","Unable to publish")
          }
          return false
      }
  */
}
