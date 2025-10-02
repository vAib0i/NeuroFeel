package com.example.neurofeel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var professionInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: SignInButton
    private lateinit var logoutButton: Button

    private lateinit var userCard: CardView
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userAgeText: TextView
    private lateinit var userProfessionText: TextView

    private lateinit var orText: TextView
    private lateinit var alreadyText: TextView
    private lateinit var inputCard: CardView

    private val RC_SIGN_IN = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Google SignIn setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your actual client id
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Bind views
        usernameInput = view.findViewById(R.id.usernameInput)
        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        ageInput = view.findViewById(R.id.ageInput)
        professionInput = view.findViewById(R.id.professionInput)
        signUpButton = view.findViewById(R.id.signUpButton)
        loginButton = view.findViewById(R.id.loginButton)
        googleSignInButton = view.findViewById(R.id.googleSignInButton)
        logoutButton = view.findViewById(R.id.logoutButton)

        userCard = view.findViewById(R.id.userCard)
        userNameText = view.findViewById(R.id.userNameText)
        userEmailText = view.findViewById(R.id.userEmailText)
        userAgeText = view.findViewById(R.id.userAgeText)
        userProfessionText = view.findViewById(R.id.userProfessionText)

        orText = view.findViewById(R.id.orText)
        alreadyText = view.findViewById(R.id.alreadyAccountText)
        inputCard = view.findViewById(R.id.inputCard)

        signUpButton.setOnClickListener {
            signUpUser()
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // If user already logged in, show user info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserDataAndDisplay(currentUser.uid)
        } else {
            resetUI()
        }
    }

    private fun signUpUser() {
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val profession = professionInput.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || profession.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid

                    // Save extra info to Firebase Realtime Database
                    val userMap = mapOf(
                        "username" to username,
                        "email" to email,
                        "age" to age,
                        "profession" to profession
                    )
                    database.reference.child("users").child(uid).setValue(userMap)
                        .addOnSuccessListener {
                            fetchUserDataAndDisplay(uid)
                            Toast.makeText(requireContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to save user info", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    fetchUserDataAndDisplay(uid)
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logoutUser() {
        auth.signOut()
        googleSignInClient.signOut()
        resetUI()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle Google Sign-In result here (You can add Firebase Authentication with Google)
                // For simplicity, just show a Toast here
                Toast.makeText(requireContext(), "Google Sign-In Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserDataAndDisplay(uid: String) {
        database.reference.child("users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.child("username").value as? String ?: "N/A"
                    val email = snapshot.child("email").value as? String ?: "N/A"
                    val age = snapshot.child("age").value as? String ?: "N/A"
                    val profession = snapshot.child("profession").value as? String ?: "N/A"

                    updateUIForLoggedIn(username, age, profession, email)
                } else {
                    Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    resetUI()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                resetUI()
            }
    }

    private fun updateUIForLoggedIn(name: String, age: String, profession: String, email: String) {
        // Fill userCard's TextViews with user data (assumed you have text views inside userCard)
        val userNameText: TextView = requireView().findViewById(R.id.userNameText)
        val userEmailText: TextView = requireView().findViewById(R.id.userEmailText)
        val userAgeText: TextView = requireView().findViewById(R.id.userAgeText)
        val userProfessionText: TextView = requireView().findViewById(R.id.userProfessionText)

        userNameText.text = " Name: $name"
        userEmailText.text = "🔸 Email: $email"
        userAgeText.text = "🔸 Age: $age"
        userProfessionText.text = "🔸 Profession: $profession"

        // Show the userCard
        val userCard: View = requireView().findViewById(R.id.userCard)
        userCard.visibility = View.VISIBLE

        // Hide all input fields and buttons
        usernameInput.visibility = View.GONE
        emailInput.visibility = View.GONE
        passwordInput.visibility = View.GONE
        ageInput.visibility = View.GONE
        professionInput.visibility = View.GONE
        signUpButton.visibility = View.GONE
        loginButton.visibility = View.GONE
        googleSignInButton.visibility = View.GONE

        logoutButton.visibility = View.VISIBLE
        orText.visibility = View.GONE
        alreadyText.visibility = View.GONE

        inputCard.visibility = View.GONE

    }

    private fun resetUI() {
        userCard.visibility = View.GONE
        logoutButton.visibility = View.GONE

        usernameInput.visibility = View.VISIBLE
        emailInput.visibility = View.VISIBLE
        passwordInput.visibility = View.VISIBLE
        ageInput.visibility = View.VISIBLE
        professionInput.visibility = View.VISIBLE

        signUpButton.visibility = View.VISIBLE
        loginButton.visibility = View.VISIBLE
        googleSignInButton.visibility = View.VISIBLE

        orText.visibility = View.VISIBLE
        alreadyText.visibility = View.VISIBLE

        inputCard.visibility = View.VISIBLE

        // Clear user info texts
        userNameText.text = ""
        userEmailText.text = ""
        userAgeText.text = ""
        userProfessionText.text = ""
    }


}
