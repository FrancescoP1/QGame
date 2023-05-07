package com.francesco.quizgame.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.francesco.quizgame.databinding.ActivitySignupBinding
import com.francesco.quizgame.validation.EmailValidation
import com.francesco.quizgame.validation.PasswordValidation
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (EmailValidation.isEmailValid(email) && PasswordValidation.isPasswordValid(password)) {
                if (PasswordValidation.passwordsMatch(password, confirmPassword)) {
                    firebaseAuth
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast
                                    .makeText(this, "Something went wrong!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid password or email", Toast.LENGTH_SHORT).show()
            }
        }

        // sign in link
        binding.signInLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}