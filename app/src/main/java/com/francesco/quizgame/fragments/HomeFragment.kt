package com.francesco.quizgame.fragments

import android.R.attr.bitmap
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.francesco.quizgame.R
import com.francesco.quizgame.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.checkerframework.checker.nullness.qual.NonNull
import java.io.ByteArrayOutputStream


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
    private val imageCapture = 100
    private lateinit var context: Context
    private lateinit var profilePicture: ImageView
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseStorageReference: StorageReference
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }


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
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseStorageReference = firebaseStorage.reference


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
            intent.type = "text/plain"
            val url = "https://github.com/FrancescoP1/QGame.git"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            val chooser = Intent.createChooser(intent , "Share using...")
            startActivity(chooser)
        }

        val cameraButton: Button = view.findViewById(R.id.camera_button)
        cameraButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, imageCapture)
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(this.context, "Error while starting camera app", Toast.LENGTH_SHORT).show()
            }
        }

        profilePicture = view.findViewById(R.id.profile_picture)
        downloadProfilePictureFromCloud()

        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = userEmail
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK) {
            val imageCaptured = data?.extras?.get("data") as Bitmap
            uploadPictureToCloud(imageCaptured)
            profilePicture.setImageBitmap(imageCaptured)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadPictureToCloud(image: Bitmap) {
        val uuid = firebaseAuth.currentUser?.uid
        val profilePictureRef = firebaseStorageReference.child("/profileImages/$uuid")

        // convert image to byteArray
        val imageByteOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, imageByteOutputStream)
        val imageBytes = imageByteOutputStream.toByteArray()

        val uploadTask = profilePictureRef.putBytes(imageBytes)
        uploadTask.addOnSuccessListener{
            Toast.makeText(this.context, "Image uploaded", Toast.LENGTH_SHORT).show()
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this.context, "There was an error uploading your image", Toast.LENGTH_SHORT).show()
        }

    }

    private fun downloadProfilePictureFromCloud() {
        val fileId = firebaseAuth.currentUser?.uid
        val pathRef = firebaseStorageReference.child("profileImages/$fileId")
        val imageBytes = pathRef.getBytes(1024*1024*10)
        imageBytes.addOnSuccessListener { image ->
            val bitmapImage =  BitmapFactory.decodeByteArray(image, 0, image.size)
            profilePicture.setImageBitmap(bitmapImage)
        }
        imageBytes.addOnFailureListener{
            Toast.makeText(this.context, "There was an error retrieving your profile picture", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
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