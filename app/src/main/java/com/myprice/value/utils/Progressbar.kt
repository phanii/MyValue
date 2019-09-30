package com.myprice.value.utils


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myprice.value.R
import kotlinx.android.synthetic.main.progressbar.*

/**
 * A simple [Fragment] subclass.
 */
class Progressbar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.progressbar, container, false)
    }

    fun setVisibilityForProgressBar(visibility: Boolean) {
        when (visibility) {
            true -> progressBar.visibility = View.VISIBLE
            false -> progressBar.visibility = View.GONE

        }

    }


}
