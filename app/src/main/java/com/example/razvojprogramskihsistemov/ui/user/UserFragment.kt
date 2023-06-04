package com.example.razvojprogramskihsistemov.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razvojprogramskihsistemov.R
import com.example.razvojprogramskihsistemov.databinding.FragmentUserBinding
import com.example.razvojprogramskihsistemov.ui.subjects.SubjectModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var subjectEditText: EditText
    private lateinit var changeUser: Button
    private lateinit var addSubjectButton: Button
    private lateinit var subjectDisplayTextView: TextView

    private lateinit var subjectRecyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private val subjects = mutableListOf<String>()

    private lateinit var userManager: UserManager
    private lateinit var navHeaderView: View
    private lateinit var userNameTextView: TextView
    private lateinit var userSurnameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private lateinit var dbRefSubjects: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userManager = UserManager(requireContext())

        nameEditText = binding.editName
        surnameEditText = binding.editSurname
        emailEditText = binding.editEmail
        subjectEditText = binding.editSubject
        changeUser = binding.btnChangeUser
        addSubjectButton = binding.btnAddSubject
        subjectDisplayTextView = binding.textSubjectDisplay

        val currentUser = FirebaseAuth.getInstance().currentUser
        dbRefUser = FirebaseDatabase.getInstance().getReference("Users/${currentUser?.uid}")
        dbRefSubjects = FirebaseDatabase.getInstance().getReference("Users/${currentUser?.uid}/Subjects")

        subjectRecyclerView = binding.subjectRecyclerView
        subjectRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        subjectAdapter = SubjectAdapter(subjects)
        subjectRecyclerView.adapter = subjectAdapter

        subjects.addAll(userManager.getSubjects())

        // Inside onCreateView after binding the views
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        navHeaderView = navView.getHeaderView(0)
        userNameTextView = navHeaderView.findViewById(R.id.user_name)
        userSurnameTextView = navHeaderView.findViewById(R.id.user_surname)
        userEmailTextView = navHeaderView.findViewById(R.id.user_mail)

        // Set the hints in the input fields as the user information from the navigation header
        val userInfo = userManager.getUserInfo()
        val userName = userInfo.name
        val userSurname = userInfo.surname

        dbRefUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    if (userName != null) {
                        nameEditText.hint = if (userName.isNotBlank()) userName else "Enter your name"
                    }
                    if (userSurname != null) {
                        surnameEditText.hint = if (userSurname.isNotBlank()) userSurname else "Enter your surname"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        changeUser.setOnClickListener {
            submitUserDetails()
        }

        addSubjectButton.setOnClickListener {
            addSubject()
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        userManager.saveSubjects(subjects)
    }


    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email
        userEmailTextView.text = userEmail
        if (userEmail != null) {
            emailEditText.hint = userEmail
        }

        val userId = currentUser?.uid
        val dbRefUserInfo = FirebaseDatabase.getInstance().getReference("Users/$userId")
        dbRefUserInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").value.toString()
                val surname = dataSnapshot.child("surname").value.toString()

                userNameTextView.text = name
                nameEditText.hint = if (name.isNotBlank()) name else "Enter your name"

                userSurnameTextView.text = surname
                surnameEditText.hint = if (surname.isNotBlank()) surname else "Enter your surname"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                val errorMessage = "Database Error: ${databaseError.message}"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                // Perform any additional error handling actions if needed
            }
        })
    }

    private fun submitUserDetails() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()

        val originalName = userManager.getUserName()
        val originalSurname = userManager.getUserSurname()

        if (name == originalName && surname == originalSurname) {
            // No changes were made, so no need to update the details
            Toast.makeText(requireContext(), "No changes were made", Toast.LENGTH_SHORT).show()
            return
        }

        // Update the user name and email in the navigation header
        val updatedName = if (name.isNotBlank()) name else originalName
        val updatedSurname = if (surname.isNotBlank()) surname else originalSurname

        userNameTextView.text = updatedName
        userSurnameTextView.text = updatedSurname

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { userId ->
            // Save the updated user details to Firebase Realtime Database
            val userMap = HashMap<String, String?>()
            userMap["name"] = updatedName
            userMap["surname"] = updatedSurname

            dbRefUser.updateChildren(userMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "User details updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(requireContext(), "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Update the hints in the input fields based on the new user information
        nameEditText.hint = updatedName
        surnameEditText.hint = updatedSurname

        // Clear the input fields
        nameEditText.text.clear()
        surnameEditText.text.clear()
        emailEditText.text.clear()
    }


    private fun addSubject() {
        val subjectName = subjectEditText.text.toString()

        if (subjectName.isEmpty()) {
            subjectEditText.error = "Enter your subject."
        } else if (subjects.contains(subjectName)) {
            Toast.makeText(requireContext(), "Subject already exists.", Toast.LENGTH_SHORT).show()
        } else {
            subjects.add(subjectName)
            subjectAdapter.notifyItemInserted(subjects.size - 1)
            subjectEditText.text.clear()

            val subjectRef = dbRefSubjects.push()
            val subject = SubjectModel(subjectName = subjectName)
            subjectRef.setValue(subject)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Subject added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(requireContext(), "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private inner class SubjectAdapter(private val subjects: MutableList<String>) :
        RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectAdapter.SubjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_subject, parent, false)

            // Create an instance of SubjectViewHolder by providing the reference to the outer SubjectAdapter class
            val viewHolder = subjectAdapter.SubjectViewHolder(view)

            // Set a long click listener for the subject item
            view.setOnLongClickListener {
                val position = viewHolder.adapterPosition
                subjectAdapter.deleteSubject(position)
                true
            }

            return viewHolder
        }



        override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
            val subject = subjects[position]
            holder.bind(subject)

            // Set a long click listener for the subject item
            holder.itemView.setOnLongClickListener {
                deleteSubject(position)
                true
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }

        inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val subjectTextView: TextView = itemView.findViewById(R.id.subjectText)

            fun bind(subject: String) {
                subjectTextView.text = subject
            }
        }

        private fun deleteSubject(position: Int) {
            subjects.removeAt(position)
            notifyItemRemoved(position)

            // TODO: Remove the subject from Firebase Realtime Database using the subject's key
        }
    }

    class UserManager(private val context: Context) {
        private val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        private val subjectKey = "Subjects"
        private val userInfoKey = "UserInfo"

        fun getSubjects(): MutableList<String> {
            val subjectsJson = sharedPreferences.getString(subjectKey, null)
            return if (subjectsJson != null) {
                Gson().fromJson(subjectsJson, object : TypeToken<MutableList<String>>() {}.type)
            } else {
                mutableListOf()
            }
        }

        fun saveSubjects(subjects: MutableList<String>) {
            val subjectsJson = Gson().toJson(subjects)
            sharedPreferences.edit().putString(subjectKey, subjectsJson).apply()
        }

        fun saveUserDetails(name: String?, surname: String?) {
            val userInfo = getUserInfo()
            val updatedUserInfo = User(name ?: userInfo.name, surname ?: userInfo.surname)
            val userInfoJson = Gson().toJson(updatedUserInfo)
            sharedPreferences.edit().putString(userInfoKey, userInfoJson).apply()
        }

        fun getUserInfo(): User {
            val userInfoJson = sharedPreferences.getString(userInfoKey, null)
            return if (userInfoJson != null) {
                Gson().fromJson(userInfoJson, User::class.java)
            } else {
                User("", "")
            }
        }

        fun getUserName(): String? {
            return getUserInfo().name
        }

        fun getUserSurname(): String? {
            return getUserInfo().surname
        }
    }

    data class User(val name: String?, val surname: String?)
}
