package com.francesco.quizgame.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.francesco.quizgame.R
import com.francesco.quizgame.databinding.ActivityMainBinding
import com.francesco.quizgame.fragments.HomeFragment
import com.francesco.quizgame.fragments.PlayFragment
import com.francesco.quizgame.fragments.RankingFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity: AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentPage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        currentPage = getString(R.string.home)

        val extras = intent.extras
        var email = extras!!.getString("email")
        if(email == null) {
            email = firebaseAuth.currentUser?.email
        }
        if(email != null) {
            replaceFragment(HomeFragment.newInstance(email))
        }


        /*
        binding.signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            redirectToLogin()
        }
         */
        binding.bottomNavigation.setOnItemSelectedListener{ menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_home -> {
                    currentPage = getString(R.string.home)
                    binding.Page.text = currentPage
                    if(email != null) {
                        replaceFragment(HomeFragment.newInstance(email))
                    } else {
                        replaceFragment(HomeFragment.newInstance(""))
                    }
                    true
                }

                R.id.navigation_play -> {
                    currentPage = getString(R.string.quiz)
                    binding.Page.text = currentPage
                    replaceFragment(PlayFragment())
                    true
                }

                R.id.navigation_ranking -> {
                    currentPage = getString(R.string.rankingPage)
                    binding.Page.text = currentPage
                    replaceFragment(RankingFragment())
                    true
                }

                else -> false
            }
        }

    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_display, fragment)
            .commit()
    }

}