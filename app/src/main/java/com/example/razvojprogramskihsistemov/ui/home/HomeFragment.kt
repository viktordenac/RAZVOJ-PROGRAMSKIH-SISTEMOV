package com.example.razvojprogramskihsistemov.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.R

class HomeFragment : Fragment() {

    private lateinit var scheduleInput: EditText
    private lateinit var mondayTextView: TextView
    private lateinit var tuesdayTextView: TextView
    private lateinit var wednesdayTextView: TextView
    private lateinit var thursdayTextView: TextView
    private lateinit var fridayTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        scheduleInput = view.findViewById(R.id.schedule_input)
        mondayTextView = view.findViewById(R.id.monday_textview)
        tuesdayTextView = view.findViewById(R.id.tuesday_textview)
        wednesdayTextView = view.findViewById(R.id.wednesday_textview)
        thursdayTextView = view.findViewById(R.id.thursday_textview)
        fridayTextView = view.findViewById(R.id.friday_textview)

        // Set click listeners for each day of the week
        mondayTextView.setOnClickListener { updateScheduleText(mondayTextView.text.toString()) }
        tuesdayTextView.setOnClickListener { updateScheduleText(tuesdayTextView.text.toString()) }
        wednesdayTextView.setOnClickListener { updateScheduleText(wednesdayTextView.text.toString()) }
        thursdayTextView.setOnClickListener { updateScheduleText(thursdayTextView.text.toString()) }
        fridayTextView.setOnClickListener { updateScheduleText(fridayTextView.text.toString()) }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set the schedule text for each day
        mondayTextView.text = "Monday: 9:00 AM - 5:00 PM"
        tuesdayTextView.text = "Tuesday: 9:00 AM - 5:00 PM"
        wednesdayTextView.text = "Wednesday: 9:00 AM - 5:00 PM"
        thursdayTextView.text = "Thursday: 9:00 AM - 5:00 PM"
        fridayTextView.text = "Friday: 9:00 AM - 5:00 PM"
    }

    private fun updateScheduleText(text: String) {
        scheduleInput.setText(text)
    }
}

