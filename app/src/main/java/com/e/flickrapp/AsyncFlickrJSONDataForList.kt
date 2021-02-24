package com.e.flickrapp

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class AsyncFlickrJSONDataForList(val adapter: MyAdapter): AsyncTask<String, Void, JSONObject>() {

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
        var imageLink:String = result?.getJSONArray("items")?.getJSONObject(1)?.getJSONObject("media")?.get("m").toString()
        val len = result?.getJSONArray("items")?.length()

        for(i in 1 until len!!){
            adapter.add(result.getJSONArray("items").getJSONObject(i)?.getJSONObject("media")?.get("m").toString())
        }

        adapter.notifyDataSetChanged()

    }




    @RequiresApi(Build.VERSION_CODES.N)
    private fun readStream(input: InputStream): String{
        return BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
            .collect(Collectors.joining("\n"))
    }

}