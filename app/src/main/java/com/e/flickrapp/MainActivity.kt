package com.e.flickrapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.e.flickrapp.databinding.ActivityMainBinding
import java.lang.ref.WeakReference


/*
* Question 12: the answer server is not really using JSON format because the data are enclosed into jsonFlickrFeed(..) that should
* not be here to be a JSON format
* */

/*
Using such a type allows to keep good performances when running the app
 */

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, Array<String>(1){ Manifest.permission.ACCESS_FINE_LOCATION}, 0)
        //ActivityCompat.requestPermissions(this, Array<String>(1){Manifest.permission.ACCESS_COARSE_LOCATION; }, 0)


        //lorsqu'on clique sur le bouton "get Image" on lance une async task
        binding.getImageButton.setOnClickListener(GetImageOnClickListener(WeakReference(binding.image)))

        binding.toListButton.setOnClickListener{v->
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }

        getPosition()



    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(i in 0 until grantResults.size)
            Log.i("JFL", permissions[i] +" granted")

    }

     fun getPosition(){



        val manager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //On vérifie si l'utilisateur a donné les permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val localisation: Location =  manager.getLastKnownLocation("gps")
            Log.i("JFL", "LON: " + localisation.longitude + " LAT: "+ localisation.latitude)
            PositionInfo.lat = localisation.longitude
            PositionInfo.lon = localisation.latitude

        }

    }

}


class GetImageOnClickListener(val image:WeakReference<ImageView>) : View.OnClickListener{
    override fun onClick(v: View?) {
        AsyncFlickrJSONData(image).execute("https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json")


    }



}