package com.e.flickrapp

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class AsyncFlickrJSONData(val image: WeakReference<ImageView>): AsyncTask<String, Void, JSONObject>() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun doInBackground(vararg params: String?): JSONObject? {
        lateinit var url: URL
        var json: JSONObject? = null



        try {
            url = URL(params[0])
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

            try {
                val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                val s: String = readStream(input)
                Log.i("JFL", s)
                json = JSONObject(s.substringAfter('(').substringBeforeLast(')'))



            }finally {
                urlConnection.disconnect()
            }
        }catch (e: MalformedURLException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return json
    }


    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        Log.i("JFL", result.toString())
        val imageLink:String = result?.getJSONArray("items")?.getJSONObject(1)?.getJSONObject("media")?.get("m").toString()
        AsyncBitmapDownloader(image).execute(imageLink)

    }




    @RequiresApi(Build.VERSION_CODES.N)
    private fun readStream(input:InputStream): String{
        return BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
            .collect(Collectors.joining("\n"))
    }
}

