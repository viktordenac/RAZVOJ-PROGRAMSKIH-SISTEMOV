package com.example.razvojprogramskihsistemov.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class HomeFragment : Fragment() {

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

        mondayTextView = view.findViewById(R.id.monday_textview)
        tuesdayTextView = view.findViewById(R.id.tuesday_textview)
        wednesdayTextView = view.findViewById(R.id.wednesday_textview)
        thursdayTextView = view.findViewById(R.id.thursday_textview)
        fridayTextView = view.findViewById(R.id.friday_textview)
        submitButton = view.findViewById(R.id.submit_button)



        // set an OnClickListener for the submit button to update the selected day's text
        submitButton.setOnClickListener {
            saveScheduleToFile()
        }

        // Set click listeners for each day of the week
        // set an OnClickListener for each day to update the input field with the corresponding text
        mondayTextView.setOnClickListener {
            selectDay(mondayTextView)
            showNoteDialogAndUpdateText()
        }
        tuesdayTextView.setOnClickListener {
            selectDay(tuesdayTextView)
            showNoteDialogAndUpdateText()

        }
        wednesdayTextView.setOnClickListener {
            selectDay(wednesdayTextView)
            showNoteDialogAndUpdateText()
        }
        thursdayTextView.setOnClickListener {
            selectDay(thursdayTextView)
            showNoteDialogAndUpdateText()
        }
        fridayTextView.setOnClickListener {
            showNoteDialogAndUpdateText()
            selectDay(fridayTextView)
        }

        return view
    }

    private fun selectDay(dayTextView: TextView) {
        // unselect the previously selected day, if any
        selectedDayTextView?.setBackgroundResource(R.drawable.day_background)

        // select the clicked day
        selectedDayTextView = dayTextView
        selectedDayTextView?.setBackgroundColor(resources.getColor(R.color.black))
    }

    private fun saveScheduleToFile() {
        val scheduleArray = JSONArray()
        scheduleArray.put(JSONObject().apply {
            put("day", "Monday")
            val string: String = mondayTextView.text.toString().substringAfter("\n")
            put("time", string.substringBefore("\n").trim())
            var note: String = string.substringAfter("PM")
            if( note != ""){
                note = note.substringAfter("Note:")
            }else{
                note = ""
            }
            put("notes",note.trim())
        })
        scheduleArray.put(JSONObject().apply {
            put("day", "Tuesday")
            val string: String = tuesdayTextView.text.toString().substringAfter("\n")
            put("time", string.substringBefore("\n").trim())
            var note: String = string.substringAfter("PM")
            if( note != ""){
                note = note.substringAfter("Note:")
            }else{
                note = ""
            }
            put("notes", note.trim())
        })
        scheduleArray.put(JSONObject().apply {
            put("day", "Wednesday")
            val string: String = wednesdayTextView.text.toString().substringAfter("\n")
            put("time", string.substringBefore("\n").trim())
            var note: String = string.substringAfter("PM")
            if( note != ""){
                note = note.substringAfter("Note:")
            }else{
                note = ""
            }
            put("notes", note.trim())
        })
        scheduleArray.put(JSONObject().apply {
            put("day", "Thursday")
            val string: String = thursdayTextView.text.toString().substringAfter("\n")
            put("time", string.substringBefore("\n").trim())
            var note: String = string.substringAfter("PM")
            if( note != ""){
                note = note.substringAfter("Note:")
            }else{
                note = ""
            }
            put("notes", note.trim())
        })
        scheduleArray.put(JSONObject().apply {
            put("day", "Friday")
            val string: String = fridayTextView.text.toString().substringAfter("\n")
            put("time", string.substringBefore("\n").trim())
            var note: String = string.substringAfter("PM")
            if( note != ""){
                note = note.substringAfter("Note:")
            }else{
                note = ""
            }
            put("notes", note.trim())
        })

        val scheduleObject = JSONObject().apply {
            put("schedule", scheduleArray)
        }

        val filename = "schedule.json"
        val file = File(requireContext().filesDir, filename)

        // Check if the file already exists and contains schedule data
        if (file.exists()) {
            val inputStream = FileInputStream(file)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()

            // Compare the existing data to the new data
            val existingObject = JSONObject(inputString)
            val existingArray = existingObject.getJSONArray("schedule")

            var hasChanges = false
            for (i in 0 until existingArray.length()) {
                val existingDay = existingArray.getJSONObject(i)
                val newDay = scheduleArray.getJSONObject(i)

                if (existingDay.getString("time") != newDay.getString("time")
                    || existingDay.getString("notes") != newDay.getString("notes")
                ) {
                    hasChanges = true
                    break
                }
            }

            // Write the new data to the file if there are changes
            if (hasChanges) {
                val outputStream = FileOutputStream(file)
                outputStream.write(scheduleObject.toString().toByteArray())
                outputStream.close()
                Toast.makeText(
                    requireContext(),
                    "Schedule updated and saved to file! ${file.path}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Schedule already up-to-date in file!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Write the new data to the file if it doesn't exist
            val outputStream = FileOutputStream(file)
            outputStream.write(scheduleObject.toString().toByteArray())
            outputStream.close()
            Toast.makeText(
                requireContext(),
                "Schedule saved to file! ${file.path}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            loadScheduleFromFile()
        } catch (e: JSONException) {
            mondayTextView.text = "Monday:\n9:00 AM - 5:00 PM"
            tuesdayTextView.text = "Tuesday:\n9:00 AM - 5:00 PM"
            wednesdayTextView.text = "Wednesday:\n9:00 AM - 5:00 PM"
            thursdayTextView.text = "Thursday:\n9:00 AM - 5:00 PM"
            fridayTextView.text = "Friday:\n9:00 AM - 5:00 PM"
        }catch (e:NoSuchFileException){
            mondayTextView.text = "Monday:\n9:00 AM - 5:00 PM"
            tuesdayTextView.text = "Tuesday:\n9:00 AM - 5:00 PM"
            wednesdayTextView.text = "Wednesday:\n9:00 AM - 5:00 PM"
            thursdayTextView.text = "Thursday:\n9:00 AM - 5:00 PM"
            fridayTextView.text = "Friday:\n9:00 AM - 5:00 PM"
        }
    }

    private fun loadScheduleFromFile() {
        val filename = "schedule.json"
        val file = File(requireContext().filesDir, filename)
        if (file.exists()) {
            val inputStream = FileInputStream(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val json = JSONObject(jsonString)
            val scheduleArray = json.getJSONArray("schedule")
            if (scheduleArray.length() == 5) {
                mondayTextView.text = "${scheduleArray.getJSONObject(0).getString("day")}: \n${scheduleArray.getJSONObject(0).getString("time")} " +
                        if(scheduleArray.getJSONObject(0).getString("notes")!= ""){
                            "\nNote: ${scheduleArray.getJSONObject(0).getString("notes")}"
                        }else{
                            "${scheduleArray.getJSONObject(0).getString("notes")}"
                        }
                tuesdayTextView.text = "${scheduleArray.getJSONObject(1).getString("day")}: \n${scheduleArray.getJSONObject(1).getString("time")} " +
                        if(scheduleArray.getJSONObject(1).getString("notes")!= ""){
                            "\nNote: ${scheduleArray.getJSONObject(1).getString("notes")}"
                        }else{
                            "${scheduleArray.getJSONObject(1).getString("notes")}"
                        }
                wednesdayTextView.text = "${scheduleArray.getJSONObject(2).getString("day")}: \n${scheduleArray.getJSONObject(2).getString("time")} " +
                        if(scheduleArray.getJSONObject(2).getString("notes")!= ""){
                            "\nNote: ${scheduleArray.getJSONObject(2).getString("notes")}"
                        }else{
                            "${scheduleArray.getJSONObject(2).getString("notes")}"
                        }
                thursdayTextView.text = "${scheduleArray.getJSONObject(3).getString("day")}: \n${scheduleArray.getJSONObject(3).getString("time")} " +
                        if(scheduleArray.getJSONObject(3).getString("notes")!= ""){
                            "\nNote: ${scheduleArray.getJSONObject(3).getString("notes")}"
                        }else{
                            "${scheduleArray.getJSONObject(3).getString("notes")}"
                        }
                fridayTextView.text = "${scheduleArray.getJSONObject(4).getString("day")}: \n${scheduleArray.getJSONObject(4).getString("time")} " +
                        if(scheduleArray.getJSONObject(4).getString("notes")!= ""){
                            "\nNote: ${scheduleArray.getJSONObject(4).getString("notes")}"
                        }else{
                            "${scheduleArray.getJSONObject(4).getString("notes")}"
                        }
                Toast.makeText(requireContext(), "Schedule loaded from file!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error: Invalid file format!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Error: File not found!", Toast.LENGTH_SHORT).show()
            throw NoSuchFileException(file)
        }
    }

    private fun showNoteDialogAndUpdateText() {
        val view = layoutInflater.inflate(R.layout.fragment_home, null)
        val dayTextViewList = mutableListOf<TextView>()
        dayTextViewList.add(view.findViewById(R.id.monday_textview))
        dayTextViewList.add(view.findViewById(R.id.tuesday_textview))
        dayTextViewList.add(view.findViewById(R.id.wednesday_textview))
        dayTextViewList.add(view.findViewById(R.id.thursday_textview))
        dayTextViewList.add(view.findViewById(R.id.friday_textview))

        val filename = "schedule.json"
        val file = File(requireContext().filesDir, filename)
        if (file.exists()) {
            val inputStream = FileInputStream(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val json = JSONObject(jsonString)
            val scheduleArray = json.getJSONArray("schedule")
            // create a view for the dialog
            val view = layoutInflater.inflate(R.layout.edit_dialog, null)

            // get the views in the dialog
            val dayTextView = view.findViewById<TextView>(R.id.day_textview)
            val timeOfDayEditText = view.findViewById<TextView>(R.id.time_textview)
            val noteEditText = view.findViewById<EditText>(R.id.note_edittext)


            var index = -1
            for (i in 0 until dayTextViewList.size) {
                if (dayTextViewList[i].id == selectedDayTextView?.id) {
                    index = i
                    break
                }
            }
            // set the text in the views
            dayTextView.text = "${scheduleArray.getJSONObject(index).getString("day")}"
            timeOfDayEditText.setText("${scheduleArray.getJSONObject(index).getString("time")}")
            noteEditText.setText("${scheduleArray.getJSONObject(index).getString("notes")}")

            // create the dialog builder
            val builder = AlertDialog.Builder(requireContext())

            // set the view for the dialog
            builder.setView(view)

            // set the positive button click listener to update the text in the TextView
            builder.setPositiveButton("Save") { _, _ ->
                // get the text from the EditTexts
                val timeOfDay = timeOfDayEditText.text.toString()
                val note = noteEditText.text.toString()

                // update the text in the TextView
                selectedDayTextView?.text =
                    "${dayTextView.text}\n$timeOfDay" + if (note.isNotEmpty()) "\nNote: $note" else ""
                saveScheduleToFile()
            }

            // set the negative button click listener to cancel the dialog
            builder.setNegativeButton("Cancel") { _, _ -> }

            // create and show the dialog
            builder.create().show()
        }
    }

}
