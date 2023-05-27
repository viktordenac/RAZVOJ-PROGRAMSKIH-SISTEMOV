package com.example.razvojprogramskihsistemov.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razvojprogramskihsistemov.R
import com.example.razvojprogramskihsistemov.databinding.FragmentUserBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        val userEmail = userInfo.email

        if (userName != null) {
            nameEditText.hint = if (userName.isNotBlank()) userName else "Enter your name"
        }
        if (userSurname != null) {
            surnameEditText.hint = if (userSurname.isNotBlank()) userSurname else "Enter your surname"
        }
        if (userEmail != null) {
            emailEditText.hint = if (userEmail.isNotBlank()) userEmail else "Enter your email"
        }

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

        // Update the user information in the navigation header when the fragment starts
        val savedUserName = userManager.getUserName()
        val savedUserEmail = userManager.getUserEmail()
        userNameTextView.text = savedUserName
        userEmailTextView.text = savedUserEmail
        if (savedUserName != null) {
            nameEditText.hint = if (savedUserName.isNotBlank()) savedUserName.split(" ")[0] else "Enter your name"
            //surnameEditText.hint = if (savedUserName.isNotBlank()) savedUserName.split(" ")[1] else "Enter your surname"

        }

        emailEditText.hint = savedUserEmail
    }

    private fun submitUserDetails() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()

        // Update the user name and email in the navigation header
        val updatedName =
            if (name.isNotBlank()) name else userNameTextView.text.toString().split(" ")[0]
        val updatedSurname =
            if (surname.isNotBlank()) surname else userNameTextView.text.toString().split(" ")[1]
        val updatedEmail =
            if (email.isNotBlank()) email else userManager.getUserEmail()

        userNameTextView.text = updatedName
        userSurnameTextView.text = updatedSurname
        userEmailTextView.text = updatedEmail


        // Save the updated user details
        if (updatedEmail != null) {
            userManager.saveUserDetails(updatedName, updatedSurname, updatedEmail)
        }

        // Update the hints in the input fields based on the new user information
        nameEditText.hint = updatedName
        surnameEditText.hint = updatedSurname
        emailEditText.hint = updatedEmail

        // Clear the input fields
        nameEditText.text.clear()
        surnameEditText.text.clear()
        emailEditText.text.clear()
    }

    private fun addSubject() {
        val subject = subjectEditText.text.toString()
        if (subject.isNotBlank()) {
            subjects.add(subject)
            subjectAdapter.notifyItemInserted(subjects.size - 1)
            subjectEditText.text.clear()
        }
    }

    private inner class SubjectAdapter(private val subjects: List<String>) :
        RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_subject, parent, false)
            return SubjectViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
            val subject = subjects[position]
            holder.bind(subject)
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
    }

    class UserManager(private val context: Context) {
        private val sharedPreferences =
            context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
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

        fun saveUserDetails(name: String, surname: String, email: String) {
            val userInfo = UserInfo(name, surname, email)
            val userInfoJson = Gson().toJson(userInfo)
            sharedPreferences.edit().putString(userInfoKey, userInfoJson).apply()
        }

        fun getUserInfo(): UserInfo {
            val userInfoJson = sharedPreferences.getString(userInfoKey, null)
            return if (userInfoJson != null) {
                Gson().fromJson(userInfoJson, UserInfo::class.java)
            } else {
                UserInfo("", "", "")
            }
        }

        fun getUserName(): String? {
            return getUserInfo().name
        }

        fun getUserEmail(): String? {
            return getUserInfo().email
        }

        fun getUserSurname(): String? {
            return getUserInfo().surname
        }
    }

    data class UserInfo(val name: String?, val surname: String?, val email: String?)
}
