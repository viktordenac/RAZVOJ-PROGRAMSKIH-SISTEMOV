package com.example.razvojprogramskihsistemov.ui.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razvojprogramskihsistemov.R
import com.example.razvojprogramskihsistemov.databinding.FragmentUserBinding
import com.google.android.material.navigation.NavigationView

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

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



        // Inside onCreateView after binding the views
        val navHeader = requireActivity().findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val userNameTextView = navHeader.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = navHeader.findViewById<TextView>(R.id.user_mail)

        // Set the hints in the input fields as the user information from the navigation header
        val userName = userNameTextView.text.toString().split(" ")
        nameEditText.hint = if (userName.isNotEmpty()) userName[0] else "Enter your name"
        surnameEditText.hint = if (userName.size > 1) userName[1] else "Enter your surname"
        emailEditText.hint = userEmailTextView.text.toString()


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
    }

    private fun submitUserDetails() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()

        // Inside onCreateView after binding the views
        val navHeader = requireActivity().findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val userNameTextView = navHeader.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = navHeader.findViewById<TextView>(R.id.user_mail)

        // Update the user name and email in the navigation header
        val updatedName = if (name.isNotEmpty()) name else userNameTextView.text.toString().split(" ")[0]
        val updatedSurname = if (surname.isNotEmpty()) surname else userNameTextView.text.toString().split(" ")[1]
        val updatedEmail = if (email.isNotEmpty()) email else userEmailTextView.text.toString()

        userNameTextView.text = "$updatedName $updatedSurname"
        userEmailTextView.text = updatedEmail

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
        if (subject.isNotEmpty()) {
            subjects.add(subject)
            subjectAdapter.notifyItemInserted(subjects.size - 1)
            subjectEditText.text.clear()
        }
    }

    private inner class SubjectAdapter(private val subjects: List<String>) :
        RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject, parent, false)
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
            private val subjectTextView: TextView = itemView.findViewById(R.id.nameTextView)

            fun bind(subject: String) {
                subjectTextView.text = subject
            }
        }
    }
}
