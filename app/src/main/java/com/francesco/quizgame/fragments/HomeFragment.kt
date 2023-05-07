package com.francesco.quizgame.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.francesco.quizgame.R
import com.francesco.quizgame.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val EMAIL = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userEmail: String? = null
    private var param2: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString(EMAIL)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        // sign out
        val signOutButton: Button = view.findViewById(R.id.signOut_button)
        signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            redirectToLogin()
        }

        // share
        val shareButton: Button = view.findViewById(R.id.share_button)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type =git
        }

        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = userEmail
        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(EMAIL, param1)
                }
            }
    }

    private fun redirectToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }
}