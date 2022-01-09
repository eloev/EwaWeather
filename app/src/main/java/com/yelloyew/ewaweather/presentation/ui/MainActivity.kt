package com.yelloyew.ewaweather.presentation.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.yelloyew.ewaweather.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.location.LocationServices
import com.yelloyew.ewaweather.R
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.presentation.ui.first.MainViewModel
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_item_geo -> {
                getLocation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getLocation()
                } else {
                    Toast.makeText(this, getString(R.string.perm_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // if system don't used location services, return null.
                // Need to go in google maps or another app and get location.
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val language = Locale.getDefault().language
                    viewModel.setRequestParams(RequestParams(latitude, longitude, language))
                }
            }
        }
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1000
    }
}