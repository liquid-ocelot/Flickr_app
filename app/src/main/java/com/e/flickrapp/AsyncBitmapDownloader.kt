package com.e.flickrapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class AsyncBitmapDownloader(val image:WeakReference<ImageView>):  AsyncTask<String, Void, Bitmap>(){
    override fun doInBackground(vararg params: String?): Bitmap {
        lateinit var url: URL
        lateinit var bitmap: Bitmap




        try {
            url = URL(params[0])
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

            try {
                val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                bitmap = BitmapFactory.decodeStream(input)




            }finally {
                urlConnection.disconnect()
            }
        }catch (e: MalformedURLException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return bitmap
    }


    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        image.get()?.setImageBitmap(result)
    }
}