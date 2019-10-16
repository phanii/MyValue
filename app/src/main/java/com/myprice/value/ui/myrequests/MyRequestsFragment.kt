package com.myprice.value.ui.myrequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.myprice.value.R
import com.myprice.value.ui.myrequests.adapter.MyRequestsAdapter
import com.myprice.value.ui.myrequests.model.ProductBean
import com.myprice.value.utils.showSnack
import kotlinx.android.synthetic.main.fragment_myrequests.*

class MyRequestsFragment : Fragment(), MyRequestsAdapter.UpdateDataClickListener {
    override fun onDeleteClick(position: Int, id: String?) {
        requireActivity().runOnUiThread {
            db.collection("products").document(id.toString()).delete()
                .addOnSuccessListener {
                    showSnack(ll, "Deleted")
                    reqAdapter?.notifyItemRemoved(position)
                }
                .addOnFailureListener { showSnack(ll, "Not deleted") }
        }
    }

    lateinit var db: FirebaseFirestore
    private lateinit var productsList: ArrayList<ProductBean>
    private lateinit var myRequestsViewModel: MyRequestsViewModel
    private var reqAdapter: MyRequestsAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myRequestsViewModel =
            ViewModelProviders.of(this).get(MyRequestsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_myrequests, container, false)
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


    }
}