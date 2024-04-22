package `in`.bitcode.googlemapsdemo

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

//AIzaSyDoyIqarzjNYyXKZeI3NZpmxlOwM-Kw_mg

class MainActivity : AppCompatActivity() {

    private lateinit var mapsFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var markerPune : Marker
    private lateinit var markerMum : Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapsFragment =
            supportFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment
        /*mapsFragment.getMapAsync(
            object : OnMapReadyCallback {
                override fun onMapReady(map: GoogleMap) {
                    this@MainActivity.map = map
                    mt("Map is ready to used...")
                }
            }
        )*/
        mapsFragment.getMapAsync { map ->
            this@MainActivity.map = map
            mt("Map is ready to used...")
            initMapSettings()
            addMarkers()
            addListeners()
            drawShapes()
            customInfoWindows()
        }

    }

    private fun customInfoWindows() {
        map.setInfoWindowAdapter(MyInfoWindowAdapter())
    }

    inner class MyInfoWindowAdapter : InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null;
        }

        override fun getInfoContents(marker: Marker): View? {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.info_window, null)
            view.findViewById<ImageView>(R.id.imgInfoWindow).setImageResource(R.mipmap.ic_launcher)
            view.findViewById<TextView>(R.id.txtInfoWindow).text = marker.title
            return view
        }
    }

    private fun drawShapes() {
        val circleOptions = CircleOptions()
        circleOptions.center( markerPune.position )
        circleOptions.radius(5000.0)
        circleOptions.strokeColor(Color.RED)
        circleOptions.fillColor( Color.argb(80, 255, 0, 0))

        val circle = map.addCircle(circleOptions)
        map.setOnCircleClickListener {

        }
        //circle.remove()
        //circle.isVisible = false

        val polygon = map.addPolygon(
            PolygonOptions()
                .add(LatLng(13.0827, 80.2707))
                .add(LatLng(8.5241, 76.9366))
                .add(LatLng(12.9716, 77.5946))
                .add(LatLng(17.4065, 78.4772))
                .strokeColor(Color.BLACK)
                .fillColor(Color.argb(90, 0, 0, 255))
        )
    }

    private fun addListeners() {
        map.setOnMarkerClickListener(
            object : OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {
                    mt("Marker clicked: ${marker.title}")
                    return false
                }
            }
        )

        map.setOnInfoWindowClickListener(
            object : OnInfoWindowClickListener {
                override fun onInfoWindowClick(marker: Marker) {
                    mt("Info window clicked: ${marker.title}")
                }
            }
        )

        map.setOnMapClickListener(
            object : GoogleMap.OnMapClickListener {
                override fun onMapClick(position: LatLng) {
                    map.addMarker(
                        MarkerOptions()
                            .title("Dummy marker: ${position.hashCode()}")
                            .snippet("Dummy marker snippet")
                            .position(position)
                    )

                    /*val cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerPune.position, 18F)
                    map.moveCamera(cameraUpdate)
                    map.animateCamera(cameraUpdate)*/

                    val cameraPosition = CameraPosition.builder()
                        .bearing(65F)
                        .tilt(90F)
                        .zoom(20F)
                        .target(markerPune.position)
                        .build()

                    val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                    map.animateCamera(
                        cameraUpdate,
                        5000,
                        object : CancelableCallback {
                            override fun onCancel() {
                                mt("Animation cancelled...")
                            }

                            override fun onFinish() {
                                mt("Animation finished....")
                            }
                        }
                    )

                }
            }
        )

        map.setOnMarkerDragListener(
            object : OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {
                    mt("Drag Started: ${marker.title}")
                }

                override fun onMarkerDrag(marker: Marker) {
                    Log.e("tag", "${marker.position}")
                }

                override fun onMarkerDragEnd(marker: Marker) {
                    mt("Drag end: ${marker.title}")
                }
            }
        )
    }

    private fun addMarkers() {
        val markerOptionsPune = MarkerOptions()
        markerOptionsPune.position(LatLng(18.5204, 73.8567))
        markerOptionsPune.title("Pune")
        markerOptionsPune.snippet("This is Pune!")
        markerOptionsPune.draggable(true)
        markerOptionsPune.rotation(45.0f)
        val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
        markerOptionsPune.icon(icon)
        markerPune = map.addMarker(markerOptionsPune)!!

        val iconMumbai = BitmapDescriptorFactory.fromResource(R.drawable.home)

        map.addMarker(
            MarkerOptions()
                .position(LatLng(19.0760, 72.8777))
                .title("Mumbai")
                .snippet("This is Mumbai")
                //.icon(iconMumbai)
        )

    }

    @SuppressLint("MissingPermission")
    private fun initMapSettings() {
        val uiSettings = map.uiSettings

        map.isMyLocationEnabled = true

        uiSettings.isZoomGesturesEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
        uiSettings.isRotateGesturesEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isCompassEnabled = true
        //uiSettings.setAllGesturesEnabled(true)
        //map.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    private fun mt(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}