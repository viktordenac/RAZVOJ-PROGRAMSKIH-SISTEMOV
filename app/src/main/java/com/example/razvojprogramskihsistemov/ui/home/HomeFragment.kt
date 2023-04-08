package com.example.razvojprogramskihsistemov.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.R

class HomeFragment : Fragment() {

    private lateinit var scheduleInput: EditText
    private lateinit var mondayTextView: TextView
    private lateinit var tuesdayTextView: TextView
    private lateinit var wednesdayTextView: TextView
    private lateinit var thursdayTextView: TextView
    private lateinit var fridayTextView: TextView
    private lateinit var submitButton: Button

    private var selectedDayTextView: TextView? = null

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
        submitButton = view.findViewById(R.id.submit_button)

        scheduleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // set an OnClickListener for the submit button to update the selected day's text
        submitButton.setOnClickListener {
            val selectedDay = selectedDayTextView
            if(!scheduleInput.text.toString().equals("")){
                if (selectedDay != null) {
                    val scheduleText = scheduleInput.text.toString()
                    selectedDay.text = "${selectedDay.text.toString().substringBefore("\n")}\n$scheduleText"
                    Toast.makeText(requireContext(), "Schedule updated!", Toast.LENGTH_SHORT).show()
                }
            }
             else {
                Toast.makeText(requireContext(), "Please select a day to update the schedule!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listeners for each day of the week
        // set an OnClickListener for each day to update the input field with the corresponding text
        mondayTextView.setOnClickListener {
            updateScheduleText(mondayTextView.text.toString().substringAfter("\n"))
            selectDay(mondayTextView)
        }
        tuesdayTextView.setOnClickListener {
            updateScheduleText(tuesdayTextView.text.toString().substringAfter("\n"))
            selectDay(tuesdayTextView)
        }
        wednesdayTextView.setOnClickListener {
            updateScheduleText(wednesdayTextView.text.toString().substringAfter("\n"))
            selectDay(wednesdayTextView)
        }
        thursdayTextView.setOnClickListener {
            updateScheduleText(thursdayTextView.text.toString().substringAfter("\n"))
            selectDay(thursdayTextView)
        }
        fridayTextView.setOnClickListener {
            updateScheduleText(fridayTextView.text.toString().substringAfter("\n"))
            selectDay(fridayTextView)
        }

        // Set long click listeners for each day of the week
        mondayTextView.setOnLongClickListener {
            showNoteDialogAndUpdateText(mondayTextView)
            true
        }
        tuesdayTextView.setOnLongClickListener {
            showNoteDialogAndUpdateText(tuesdayTextView)
            true
        }
        wednesdayTextView.setOnLongClickListener {
            showNoteDialogAndUpdateText(wednesdayTextView)
            true
        }
        thursdayTextView.setOnLongClickListener {
            showNoteDialogAndUpdateText(thursdayTextView)
            true
        }
        fridayTextView.setOnLongClickListener {
            showNoteDialogAndUpdateText(fridayTextView)
            true
        }

        return view
    }
    private fun showNoteDialogAndUpdateText(textView: TextView) {
        val noteInput = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            hint = "Enter your note"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add a Note")
            .setView(noteInput)
            .setPositiveButton("Add") { _, _ ->
                val note = noteInput.text.toString()
                if (note.isNotBlank()) {
                    textView.text = "${textView.text}\nNote: $note"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun selectDay(dayTextView: TextView) {
        // unselect the previously selected day, if any
        selectedDayTextView?.setBackgroundResource(R.drawable.day_background)

        // select the clicked day
        selectedDayTextView = dayTextView
        selectedDayTextView?.setBackgroundColor(resources.getColor(R.color.black))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set the schedule text for each day
        mondayTextView.text = "Monday:\n9:00 AM - 5:00 PM"
        tuesdayTextView.text = "Tuesday:\n9:00 AM - 5:00 PM"
        wednesdayTextView.text = "Wednesday:\n9:00 AM - 5:00 PM"
        thursdayTextView.text = "Thursday:\n9:00 AM - 5:00 PM"
        fridayTextView.text = "Friday:\n9:00 AM - 5:00 PM"
    }

    private fun updateScheduleText(text: String) {
        scheduleInput.setText(text)
    }
}

