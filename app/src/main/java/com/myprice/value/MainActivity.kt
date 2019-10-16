package com.myprice.value

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var navigationController: NavController
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationController = findNavController(R.id.nav_host_fragment)
        findViewById<NavigationView>(R.id.navigationView)
            .setupWithNavController(navigationController)

        val appBarConfiguration = AppBarConfiguration(navigationController.graph, drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
        //setupActionBarWithNavController(this, navigationController)

        setupNavigationMenu(navigationController)


    }

    private fun profileNavigation(view: View?) {

        view?.findNavController()?.navigate(R.id.nav_profileimage)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navigationController, null)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.navigationView)
        sideNavView?.setupWithNavController(navController)
    }


}
