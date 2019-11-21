package com.myprice.value.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.myprice.value.R
import com.myprice.value.ui.home.ProfileViewModel
import com.myprice.value.utils.clearTheViews
import com.myprice.value.utils.showSnack
import kotlinx.android.synthetic.main.profile_home.*

class ProfileViewFragment : Fragment() {

    private lateinit var homeViewModel: ProfileViewModel
    lateinit var profilename: String
    lateinit var phonenumber: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.profile_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = FirebaseFirestore.getInstance()

        db.collection("Users")
            .whereEqualTo("PhoneNumber", "1234")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("PhoneNumber", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("PhoneNumber", "Error getting documents: ", exception)
            }
        profile_save_button.setOnClickListener {


            profilename = profile_name.text.toString()
            phonenumber = profile_phone.text.toString()
            if (profilename.isNotEmpty() && phonenumber.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                val user = hashMapOf(
                    "Name" to profile_name.text.toString(),
                    "PhoneNumber" to profile_phone.text.toString()
                )
                db.collection("Users")
                    .add(user)
                    .addOnSuccessListener {
                        showSnack(profilerootlayout, "Data Saved Successfully!!")
                        clearTheViews(profile_name, profile_phone)
                        progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        showSnack(profilerootlayout, "Data was not saved!!")
                        progressBar.visibility = View.GONE
                    }

            } else showSnack(profilerootlayout, "Enter the Data!")
        }


    }


}