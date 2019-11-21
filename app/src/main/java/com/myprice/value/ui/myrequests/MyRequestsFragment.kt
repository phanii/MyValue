package com.myprice.value.ui.myrequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.myprice.value.R
import com.myprice.value.SharedViewModel
import com.myprice.value.ui.myrequests.adapter.MyRequestsAdapter
import com.myprice.value.ui.myrequests.model.ProductBean
import com.myprice.value.utils.showLog
import com.myprice.value.utils.showSnack
import kotlinx.android.synthetic.main.fragment_myrequests.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class MyRequestsFragment : Fragment(), MyRequestsAdapter.UpdateDataClickListener {
    override fun onEditClick(position: Int) {
        requireActivity().showLog(Gson().toJson(productsList[position]))
        sharedViewModel.setProductHere(productsList[position])
        view?.findNavController()?.navigate(R.id.nav_request)
    }

    override fun onDeleteClick(position: Int, id: String?) {
        requireActivity().runOnUiThread {
            db.collection("products").document(id.toString()).delete()
                .addOnSuccessListener {
                    showSnack(ll, "Deleted")
                    productsList.removeAt(position)
                    reqAdapter?.notifyItemRemoved(position)

                }
                .addOnFailureListener { showSnack(ll, "Not deleted") }
        }
    }

    lateinit var db: FirebaseFirestore
    private lateinit var productsList: ArrayList<ProductBean>
    private lateinit var sharedViewModel: SharedViewModel
    private var reqAdapter: MyRequestsAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_myrequests, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        productsList = arrayListOf()
        reqAdapter = MyRequestsAdapter(requireActivity())
        myrequs_rcv.apply {
            hasFixedSize()
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = reqAdapter
        }
        reqAdapter!!.setOnItemClickListener(this)
        commonProgressBarLayout.visibility = View.VISIBLE
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productsList.add(
                        ProductBean(
                            document.id,
                            document["location"] as String?,
                            document.get("name") as String?,
                            document.get("quantity") as String?,
                            document.get("requestedPrice") as String?
                        )
                    )

                }
                reqAdapter!!.setData(productsList)

                commonProgressBarLayout.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        //getTheLocationsBasedOnTheRadius(20.0)
        getDocumentsNearByMe(17.486740, 78.313745, 5.0)

    }

    fun getTheLocationsBasedOnTheRadius(distance: Double) {

        val mylat: Double = 16.640173
        val mylong: Double = 81.959595
        val coefInMts = distance * 0.0000089
        val coefInKMs = distance * 0.0089
        val newLat = mylat + coefInKMs
        val newLong = mylong + coefInKMs / cos(mylat * (0.018))
        val productsRef = db.collection("products")

        println("old lat-lang $mylat - $mylong \n new lat-lang $newLat - $newLong")
        productsRef.get().addOnSuccessListener { result ->

            for (document in result) {

                val d = 3959 * acos(
                    cos(
                        Math.toRadians((17.414478)) * cos(Math.toRadians(document.get("latitude").toString().toDouble()))
                                * cos(
                            Math.toRadians(
                                (document.get("longitude").toString().toDouble()) - Math.toRadians((78.466646)) + sin(
                                    Math.toRadians((17.414478))
                                            * sin(Math.toRadians((document.get("latitude").toString().toDouble())))
                                )
                            )
                        )
                    )
                )
            }
            // productsRef.orderBy().endAt(distance)
        }

    }

    fun getDocumentsNearByMe(latitude: Double, longitude: Double, distance: Double) {
        val lat = 0.0144927536231884
        val lon = 0.0181818181818182
        val lowerLat = latitude - (lat * distance)
        val lowerLon = longitude - (lon * distance)

        val greaterLat = latitude + (lat * distance)
        val greaterLon = longitude + (lon * distance)

        val lesserGeopoint = GeoPoint(lowerLat, lowerLon)
        val greaterGeopoint = GeoPoint(greaterLat, greaterLon)

        val productsRef = db.collection("products")

        //val query = productsRef.whereField("location", isGreaterThan: lesserGeopoint).whereField("location", isLessThan: greaterGeopoint)
        val query = productsRef.orderBy("name").startAt(lesserGeopoint).endAt(greaterGeopoint)

        productsRef.whereGreaterThanOrEqualTo("loct", lesserGeopoint)
            .whereLessThanOrEqualTo("loct", greaterGeopoint).get().addOnSuccessListener {

            val d = it.documents
            d
        }

        /*.on("value", function (snapshot) {
            console.log("objects: " + snapshot.numChildren());
            snapshot.forEach(function(childSnapshot) {
                console.log(childSnapshot.key);
            });
        });*/
        /*query.get().addOnSuccessListener {
            Log.d("LOCATION ", Gson().toJson(it))
            val d=it
            val e= d.documents
            e

        }*/

    }
}