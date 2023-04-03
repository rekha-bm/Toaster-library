package com.verifone.dms.agent.json.sevice

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.net.toUri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OsLogsUploads {
    private var imageData: ByteArray? = null
    private var getUri: Uri? = null

    /* uploadOSLogs(postURL: String, getUri: Uri, applicationContext: Context):Boolean
   This method will be used to upload file using volley
   i/p : postURL: String -> URL to upload
   getUri: Uri -> uri of file to upload
   applicationContext: Context -> Application Context
    o/p : -> Success result true or false
*/
  suspend fun uploadOSLogs(postURL: String, getUri: Uri, applicationContext: Context):Boolean?= suspendCoroutine { result ->
      createImageData(getUri ,applicationContext)
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {

                println("response is: $it")
                println("data is: ${String(it.data)}")
                Toast.makeText(applicationContext, "$it", Toast.LENGTH_SHORT).show()
                result.resume(true)

            },
            Response.ErrorListener {
                println("error is: $it")
                result.resume(false)
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params.put("file" , FileDataPart(getUri?.pathSegments?.last(), imageData!!, "*"))
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(applicationContext).add(request)

    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri, applicationContext: Context) {
        val inputStream = applicationContext.contentResolver.openInputStream(uri)
        getUri = uri
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }



}