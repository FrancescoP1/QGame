package com.francesco.quizgame.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.francesco.quizgame.R
import com.francesco.quizgame.databinding.ActivityLoginBinding
import com.francesco.quizgame.validation.EmailValidation
import com.francesco.quizgame.validation.PasswordValidation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result -> if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignIn(task) }
        }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            if(EmailValidation.isEmailValid(email)
                && PasswordValidation.isPasswordValid(pass)) {
                firebaseAuth
                    .signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            redirectToMainActivity(email)
                        } else {
                            Toast
                                .makeText(this, "Something went wrong! Please try again", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            } else {
                Toast
                    .makeText(this, "Email/Password is invalid!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // google sign in button
        binding.googleButton.setOnClickListener {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignIn()
        }

        // redirect to sign up
        binding.signUpLink.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null) {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleGoogleSignIn(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val signedInAccount: GoogleSignInAccount? = task.result
            if(signedInAccount != null) {
                val accountCredentials = GoogleAuthProvider.getCredential(signedInAccount.idToken, null)
                firebaseAuth
                    .signInWithCredential(accountCredentials)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            redirectToMainActivity(signedInAccount.email)
                        } else {
                            googleSignInFailed()
                        }
                    }
            }
        } else {
            googleSignInFailed()
        }
    }

    private fun redirectToMainActivity(email: String?) {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun googleSignInFailed() {
        Toast.makeText(
            this,
            "Unexpected error, google sign in failed!",
            Toast.LENGTH_SHORT)
            .show()
    }


}