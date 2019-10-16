package com.myprice.value.ui.request

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.myprice.value.R
import com.myprice.value.utils.*
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.fragement_request.*
import kotlinx.android.synthetic.main.new_request_scroll_layout.*


class NewRequestFragment : Fragment() {
    var firsttime: Boolean = true
    var formFilled = true
    private var customPrice: String? = null
    private var requestedProduct: String? = null
    lateinit var db: FirebaseFirestore
    val PERMISSION_ID = 42
    private var mLastLocation: Location? = null
    private var mResultReceiver: AddressResultReceiver? = null
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragement_request, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mResultReceiver = AddressResultReceiver(Handler())
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.producttypes, R.layout.spinner_textview
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_textview)
            new_req_product_spinner.adapter = adapter
        }
        //new_req_product_spinner.prompt = "Choose"
        new_req_product_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    showToast(requireActivity(), "Nothing was selected!")
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (firsttime) firsttime = false
                    else {
                        val selectionValue = parent?.getItemAtPosition(position).toString()
                        if (position == 0) showToast(requireContext(), "Select one product!")
                        else
                            requestedProduct = selectionValue
                    }

                }

            }

        new_req_price_radio_group.setOnCheckedChangeListener { group, checkedId ->

            val text =
                if (R.id.new_req_marketprice_radiobtn == checkedId) Constants.MARKET_PRICE else Constants.CUSTOM_PRICE
            if (text == Constants.MARKET_PRICE) {
                if (new_req_myprice_value.isVisible) {
                    new_req_myprice_value.visibility = View.GONE
                    new_req_myprice_value.text.clear()
                }
            } else {
                new_req_myprice_value.visibility = View.VISIBLE
            }
        }

        new_req_send_request_btn.setOnClickListener {
            new_req_quantity.validator()
                .nonEmpty()
                .atleastOneNumber()
                .addErrorCallback {
                    new_req_quantity.error = it
                    formFilled = false
                }
                .addSuccessCallback {
                    formFilled = true
                }
                .check()
            if (new_req_myprice_value.isVisible) {
                new_req_myprice_value.validator()
                    .nonEmpty()
                    .atleastOneNumber()
                    .addErrorCallback {
                        new_req_myprice_value.error = it
                        formFilled = false
                    }
                    .addSuccessCallback {
                        formFilled = true
                    }
                    .check()
            }

            if (formFilled) {
                progressBar.visibility = View.VISIBLE

                customPrice =
                    if (new_req_myprice_value.isVisible) new_req_myprice_value.text.toString() else "MarketValuePrice"
                val product = hashMapOf(
                    "name" to requestedProduct,
                    "quantity" to new_req_quantity.text.toString(),
                    "requestedPrice" to customPrice,
                    "location" to new_req_location_value.text.toString()
                )
                db.collection("products")
                    .add(product)
                    .addOnSuccessListener {
                        showSnack(frg_requ_root_layout, "Data Saved Successfully!!")
                        clearTheViews(new_req_quantity, new_req_myprice_value)

                        progressBar.visibility = View.GONE
                        view?.findNavController()?.navigate(R.id.nav_home)

                    }
                    .addOnFailureListener {
                        showSnack(frg_requ_root_layout, "Data was not saved!!")
                        progressBar.visibility = View.GONE
                    }
            }
        }

        new_req_locateme_btn.setOnClickListener {
            getLastLocation()
        }
    }

    /**
     * location
     */

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        requestNewLocationData()
                        val lat = location.latitude
                        val lon = location.longitude
                        val d = "$lat and $lon"
                        mLastLocation = location

                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation1: Location = locationResult.lastLocation
            val lat = mLastLocation1.latitude
            val lon = mLastLocation1.longitude
            val d = "$lat and $lon"
            mLastLocation = mLastLocation1
            println("current location is $d")
            startIntentService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
                getLastLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private inner class AddressResultReceiver internal constructor(handler: Handler) :
        ResultReceiver(handler) {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            val mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY)
            println("mAddressOutput $mAddressOutput")
            //displayAddressOutput()
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                new_req_location_value.text = mAddressOutput
                showToast(requireContext(), "address_found")
                new_req_locateme_btn.isEnabled = false
            } else {
                new_req_location_value.text = "Try"
            }
        }
    }


    private fun startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(requireActivity(), FetchAddressIntentService::class.java)

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver)

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation)

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        requireActivity().startService(intent)
    }


}