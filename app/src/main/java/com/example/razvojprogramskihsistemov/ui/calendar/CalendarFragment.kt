package com.example.razvojprogramskihsistemov.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.R
import com.example.razvojprogramskihsistemov.ui.user.SubjectProvider
import com.example.razvojprogramskihsistemov.ui.user.UserFragment

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var subjectDropdown: Spinner
    private lateinit var subjectAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize the CalendarView
        calendarView = rootView.findViewById(R.id.calendarView)

        // Retrieve the list of subjects from the UserFragment
        val subjectProvider = activity as? SubjectProvider
        val subjects = subjectProvider?.getSubjects() ?: emptyList<String>()

        // Initialize the subject dropdown
        subjectDropdown = Spinner(requireContext())
        subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subjectDropdown.adapter = subjectAdapter

        // Add the subjectDropdown to the RelativeLayout
        val relativeLayout = rootView.findViewById<RelativeLayout>(R.id.calendarRelativeLayout)
        relativeLayout.addView(subjectDropdown)

        // Set an event listener to handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle the selected date and subject
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            val selectedSubject = subjectDropdown.selectedItem?.toString()
            // TODO: Perform your desired action with the selected date and subject
        }

        return rootView
    }
}

