package com.example.razvojprogramskihsistemov

import android.content.Intent
import com.example.razvojprogramskihsistemov.ui.user.UserFragment
import com.example.razvojprogramskihsistemov.ui.calendar.CalendarFragment
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.databinding.ActivityMainBinding
import com.example.razvojprogramskihsistemov.ui.dashboard.DashboardFragment
import com.example.razvojprogramskihsistemov.ui.home.HomeFragment
import com.example.razvojprogramskihsistemov.ui.login_register.LoginActivity
import com.example.razvojprogramskihsistemov.ui.login_register.RegistrationActivity
import com.example.razvojprogramskihsistemov.ui.notifications.NotificationsFragment

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    private lateinit var userManager: UserFragment.UserManager
    private lateinit var navHeaderView: View
    private lateinit var userNameTextView: TextView
    private lateinit var userSurnameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            var intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userManager = UserFragment.UserManager(this)

        navHeaderView = navView.getHeaderView(0)
        userNameTextView = navHeaderView.findViewById(R.id.user_name)
        userSurnameTextView = navHeaderView.findViewById(R.id.user_surname)
        userEmailTextView = navHeaderView.findViewById(R.id.user_mail)

        // Get the saved user details
        val savedUserName = userManager.getUserName()
        val savedUserEmail = userManager.getUserEmail()

        // Update the user information in the navigation header
        userNameTextView.text = savedUserName
        userEmailTextView.text = savedUserEmail

        // Set the hints in the input fields as the user information from the navigation header
        val userInfo = userManager.getUserInfo()
        val userName = userInfo.name
        val userSurname = userInfo.surname
        val userEmail = userInfo.email

        if (userName != null) {
            userNameTextView.hint = if (userName.isNotBlank()) userName else ""
        }

        if (userSurname != null) {

            userSurnameTextView.hint = if (userSurname.isNotBlank()) userSurname else "Not logged in"
        }

        if (userEmail != null) {
            userEmailTextView.hint = if (userEmail.isNotBlank()) userEmail else ""
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(), menuItem.title.toString())
                R.id.nav_dashboard -> replaceFragment(DashboardFragment(), menuItem.title.toString())
                R.id.nav_notifications -> replaceFragment(NotificationsFragment(), menuItem.title.toString())
                R.id.nav_user -> replaceFragment(UserFragment(), menuItem.title.toString())
                R.id.nav_calendar -> replaceFragment(CalendarFragment(), menuItem.title.toString())
                R.id.nav_logout -> {
                    Toast.makeText(applicationContext, "Clicked Logout", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_share -> Toast.makeText(applicationContext, "Clicked Share", Toast.LENGTH_SHORT).show()
                R.id.nav_feedback -> Toast.makeText(applicationContext, "Clicked Feedback", Toast.LENGTH_SHORT).show()
            }
            true
        }



    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commitNow()
        drawerLayout.closeDrawers()
        setTitle(title)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return toggle.onOptionsItemSelected(item)
    }

}