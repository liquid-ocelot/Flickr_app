package com.e.flickrapp


import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.e.flickrapp.PositionInfo.Companion.lat
import com.e.flickrapp.PositionInfo.Companion.lon
import com.e.flickrapp.databinding.ActivityListBinding
import java.util.*

class ListActivity : AppCompatActivity() {

    private lateinit var adapter: MyAdapter
    private lateinit var binding: ActivityListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MyAdapter(applicationContext)
        binding.list.adapter = adapter

        //val url: String =
         //   "https://api.flickr.com/services/rest/?method=flickr.photos.search&license=4&api_key=xxxxxxxxxx&has_geo=1&lat=$lat&lon=$lon&per_page=1&format=json"

        val url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json"

        AsyncFlickrJSONDataForList(adapter).execute(url) //lors de la création de l'activité une lance une async task pour récupérer les images
    }
}


class MyAdapter(context: Context) : BaseAdapter(){


    private var vector:Vector<String> = Vector()
    private val mInflater: LayoutInflater

    init{
        this.mInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        if(convertView == null){
            view = this.mInflater.inflate(R.layout.bitmaplayout, parent, false)
        }
        else{
            view = convertView
        }

        val responseListener: Response.Listener<Bitmap> = Response.Listener { response ->  view!!.findViewById<ImageView>(R.id.imageView).setImageBitmap(response)}
        val error = Response.ErrorListener { error ->  view!!.findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.ic_launcher_foreground)}

        val imageRequest: ImageRequest = ImageRequest(vector[position], responseListener, 0, 0, null, error)

        var queue: RequestQueue = MySingleton.getInstance(view!!.context).addToRequestQueue(imageRequest)

        return view
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        return vector.size
    }

    fun add ( url:String){
        vector.addElement(url)
        Log.i("JFL", "Adding to adapter url : " + url)
    }

}


class MySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>): RequestQueue {
        requestQueue.add(req)
        return requestQueue
    }
}
