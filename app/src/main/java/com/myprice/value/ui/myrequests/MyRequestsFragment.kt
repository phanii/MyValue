package com.myprice.value.ui.myrequests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class MyRequestsFragment : Fragment(), MyRequestsAdapter.UpdateDataClickListener {
    override fun onEditClick(position: Int) {
        requireActivity().showLog(Gson().toJson(productsList[position]))
        sharedViewModel.setProductHere(productsList[position])
        view?.findNavController()?.navigate(R.id.nav_request)
    }

    override fun onAcceptOrRejectClicked(position: Int, button: Button, id: String?) {
        requireActivity().runOnUiThread {
            requireActivity().showLog("id is $id")
            val updatedRef = db.collection("products").document(id.toString())
            updatedRef.update(
                mapOf(
                    "ProductId" to id,
                    "userPhonenumber" to "123",
                    "requestStatus" to (button.text == getString(R.string.accept))
                )
            )
                .addOnSuccessListener {
                    if (button.text == getString(R.string.accept)) {
                        button.text = getString(R.string.reject)
                        reqAdapter?.changeReqStatus(position, flag = true)
                    } else {
                        button.text = getString(R.string.accept)
                        reqAdapter?.changeReqStatus(position, flag = false)
                    }

                }
                .addOnFailureListener { showSnack(ll, "Try again") }
        }
    }

    /**
     * Open Chat window to finalize the price...
     */

    override fun goToChatScreen(product: ProductBean) {
        try {
            val bundle = Bundle()
            bundle.putString(
                "productname",
                if (product.name.toString()
                        .isNotEmpty() || product.name != null
                ) product.name.toString() else "Product"
            )
            bundle.putParcelable("product", product)
            view?.findNavController()?.navigate(R.id.nav_chatScreen, bundle)
        } catch (e: Exception) {
        }
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
    private lateinit var productIdList: ArrayList<String>
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
        productsList.clear()
        productIdList = arrayListOf()
        reqAdapter = MyRequestsAdapter(requireActivity())
        myrequs_rcv.apply {
            hasFixedSize()
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = reqAdapter
        }
        reqAdapter!!.setOnItemClickListener(this)
        commonProgressBarLayout.visibility = View.VISIBLE
        db.collection("UsersProducts")
            .get()
            .addOnSuccessListener { result ->
                productsList.let {
                    it.clear()
                }
                for (document in result) {
                    try {
                        try {
                            val productId = document["ProductId"] as String?
                            println("product LIst size  product id is $productId")
                            if (productId != null) {
                                productIdList.add(productId)
                            }

                            val requestStatus = document["requestStatus"] as Boolean?
                            // doAsync {

                            // }
                        } catch (e: Exception) {
                            requireActivity().showLog(e.message!!, e)
                        }
                    } catch (e: Exception) {
                        requireActivity().showLog("Error ${e.message}", e)
                    }
                }
                // reqAdapter!!.setData(productsList)
                println("product LIst size  productIdList is ${Gson().toJson(productIdList)}")
                populateProductList(productIdList)

            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        //getTheLocationsBasedOnTheRadius(20.0)
        //getDocumentsNearByMe(17.486740, 78.313745, 5.0)
        //myRequestedProductsList("1231")
    }

    private fun populateProductList(productIdList: java.util.ArrayList<String>) {

        // doAsync {
        // Thread().run {
        doAsync {
            for (pid in 0..productIdList.size.minus(1)) {
                val d = db.collection("products").document(productIdList[pid]).get()
                    .addOnSuccessListener {
                        val product = ProductBean(
                            it.id,
                            it.get("location") as String?,
                            it.get("name") as String?,
                            it.get("quantity") as String?,
                            it.get("requestedPrice") as String?,
                            it.get("requestStatus") as Boolean?
                        )
                        println(
                            "product LIst size Location " + it.get("location") + " id  " + it.id + "Product is " + Gson().toJson(
                                product
                            )
                        )

                        productsList.add(product)
                        reqAdapter!!.notifyDataSetChanged()
                    }.addOnFailureListener {
                        println("product LIst size Failure " + it.message)
                    }
            }
            uiThread {

                reqAdapter!!.setData(productsList)
                commonProgressBarLayout.visibility = View.GONE

            }
        }

        //}
/*
            uiThread {

                reqAdapter!!.setData(productsList)
                commonProgressBarLayout.visibility = View.GONE
            }
        }*/
        println("product LIst size  productIdList is Here  ${Gson().toJson(productsList)}")

        /*reqAdapter!!.setData(productsList)
        commonProgressBarLayout.visibility = View.GONE
*/
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
                        Math.toRadians((17.414478)) * cos(
                            Math.toRadians(
                                document.get("latitude").toString().toDouble()
                            )
                        )
                                * cos(
                            Math.toRadians(
                                (document.get("longitude").toString()
                                    .toDouble()) - Math.toRadians((78.466646)) + sin(
                                    Math.toRadians((17.414478))
                                            * sin(
                                        Math.toRadians(
                                            (document.get("latitude").toString().toDouble())
                                        )
                                    )
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
                productsList.let { oldlLstExists ->
                    oldlLstExists.clear()
                }
                for (document in d) {
                    productsList.add(
                        ProductBean(
                            document.id,
                            document["location"] as String?,
                            document.get("name") as String?,
                            document.get("quantity") as String?,
                            document.get("requestedPrice") as String?,
                            document.get("requestStatus") as Boolean
                        )
                    )

                }

                reqAdapter!!.setData(productsList)


                commonProgressBarLayout.visibility = View.GONE
            }


    }

    fun myRequestedProductsList(userId: String) {

        db.collection("UsersProducts")
            .whereEqualTo("userPhonenumber", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("single", document.get("ProductId").toString())
                }

            }
            .addOnFailureListener {
                val d = it.localizedMessage
                d
                Log.d("single- Exception", it.message)
            }
    }
}