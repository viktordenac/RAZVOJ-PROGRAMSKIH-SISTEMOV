package com.example.razvojprogramskihsistemov.ui.calendar

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razvojprogramskihsistemov.R
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var subjectDropdown: Spinner
    private lateinit var subjectAdapter: ArrayAdapter<String>
    private lateinit var assignedSubjectText: TextView
    private lateinit var selectedSubjectsRecyclerView: RecyclerView
    private lateinit var selectedSubjectsAdapter: SelectedSubjectsAdapter
    private val selectedSubjects = mutableListOf<Subject>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("SelectedSubjects", Context.MODE_PRIVATE)

        // Initialize the CalendarView
        calendarView = rootView.findViewById(R.id.calendarView)
        assignedSubjectText = rootView.findViewById(R.id.assignedSubjectText)

        // Retrieve the list of subjects from the resources file
        val subjects = resources.getStringArray(R.array.array_of_subjects)

        // Initialize the subject dropdown
        subjectDropdown = rootView.findViewById(R.id.subjectDropdown)
        subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subjectDropdown.adapter = subjectAdapter

        // Set up the RecyclerView for selected subjects
        selectedSubjectsRecyclerView = rootView.findViewById(R.id.selectedSubjectsRecyclerView)
        selectedSubjectsAdapter = SelectedSubjectsAdapter(
            selectedSubjects,
            onDeleteSubject = { subject -> deleteSubject(subject) },
            onDeleteDate = { date -> deleteDate(date) }
        )
        selectedSubjectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        selectedSubjectsRecyclerView.adapter = selectedSubjectsAdapter


        // Restore selected subjects from SharedPreferences
        restoreSelectedSubjects()


        // Set an event listener to handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle the selected date and subject
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            val selectedSubject = subjectDropdown.selectedItem?.toString()

            if (selectedSubject != null && selectedSubject != "Subjects") {
                val subject = Subject(selectedDate, selectedSubject)

                if (isDuplicateSubject(subject)) {
                    // Show a notification that the subject already exists for the selected date
                    Toast.makeText(requireContext(), "Subject already set for this date", Toast.LENGTH_SHORT).show()
                } else {
                    selectedSubjects.add(subject)
                    selectedSubjectsAdapter.notifyItemInserted(selectedSubjects.size - 1)
                    selectedSubjectsAdapter.updateGroupedSubjects() // Update grouped subjects
                    saveSelectedSubjects() // Save selected subjects to SharedPreferences
                }
            }
        }

        return rootView
    }

    private fun deleteSubject(subject: Subject) {
        selectedSubjects.remove(subject)
        selectedSubjectsAdapter.notifyDataSetChanged()
        selectedSubjectsAdapter.updateGroupedSubjects()
        saveSelectedSubjects()
        Toast.makeText(requireContext(), "Subject deleted", Toast.LENGTH_SHORT).show()
    }

    private fun deleteDate(date: String) {
        val subjectsToRemove = selectedSubjects.filter { it.date == date }
        selectedSubjects.removeAll(subjectsToRemove)
        selectedSubjectsAdapter.notifyDataSetChanged()
        selectedSubjectsAdapter.updateGroupedSubjects()
        saveSelectedSubjects()
        Toast.makeText(requireContext(), "Date and subjects deleted", Toast.LENGTH_SHORT).show()
    }


    private fun isDuplicateSubject(subject: Subject): Boolean {
        // Check if the subject already exists in the selectedSubjects list
        for (existingSubject in selectedSubjects) {
            if (existingSubject.date == subject.date && existingSubject.name == subject.name) {
                return true
            }
        }
        return false
    }

    private fun saveSelectedSubjects() {
        val editor = sharedPreferences.edit()
        val subjectsJson = Gson().toJson(selectedSubjects)
        editor.putString("selectedSubjects", subjectsJson)
        editor.apply()
    }

    private fun restoreSelectedSubjects() {
        val subjectsJson = sharedPreferences.getString("selectedSubjects", null)
        if (!subjectsJson.isNullOrEmpty()) {
            val subjectsList = Gson().fromJson(subjectsJson, Array<Subject>::class.java).toList()
            selectedSubjects.addAll(subjectsList)
            selectedSubjectsAdapter.notifyDataSetChanged()
            selectedSubjectsAdapter.updateGroupedSubjects()
        }
    }
}

data class Subject(val date: String, val name: String)

class SelectedSubjectsAdapter(
    private val subjects: List<Subject>,
    private val onDeleteSubject: (Subject) -> Unit,
    private val onDeleteDate: (String) -> Unit
) : RecyclerView.Adapter<SelectedSubjectsAdapter.SubjectViewHolder>() {

    private var groupedSubjects: Map<String, List<Subject>> = emptyMap() // Initialize as an empty map

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView1: TextView = itemView.findViewById(R.id.dateTextView1)
        val assignedSubjectText1: TextView = itemView.findViewById(R.id.assignedSubjectText1)
        val dateTextView2: TextView = itemView.findViewById(R.id.dateTextView2)
        val assignedSubjectText2: TextView = itemView.findViewById(R.id.assignedSubjectText2)
        val dateTextView3: TextView = itemView.findViewById(R.id.dateTextView3)
        val assignedSubjectText3: TextView = itemView.findViewById(R.id.assignedSubjectText3)

        init {
            // Set long click listener for the item view
            itemView.setOnLongClickListener {
                val position = adapterPosition
                val subject = subjects[position]
                onDeleteSubject(subject)
                true
            }

            // Set long click listener for the date text view
            dateTextView1.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull(position * 3)
                date?.let { onDeleteDate(it) }
                true
            }

            // Set long click listener for the assigned subject text view
            assignedSubjectText1.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull(position * 3)
                val subjectIndex = 0
                val subject = groupedSubjects[date]?.getOrNull(subjectIndex)
                subject?.let { onDeleteSubject(it) }
                true
            }

            // Set long click listener for dateTextView2
            dateTextView2.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull((position * 3) + 1)
                date?.let { onDeleteDate(it) }
                true
            }

            // Set long click listener for assignedSubjectText2
            assignedSubjectText2.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull((position * 3) + 1)
                val subjectIndex = 0
                val subject = groupedSubjects[date]?.getOrNull(subjectIndex)
                subject?.let { onDeleteSubject(it) }
                true
            }

            // Set long click listener for dateTextView3
            dateTextView3.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull((position * 3) + 2)
                date?.let { onDeleteDate(it) }
                true
            }

            // Set long click listener for assignedSubjectText3
            assignedSubjectText3.setOnLongClickListener {
                val position = adapterPosition
                val date = groupedSubjects.keys.toList().getOrNull((position * 3) + 2)
                val subjectIndex = 0
                val subject = groupedSubjects[date]?.getOrNull(subjectIndex)
                subject?.let { onDeleteSubject(it) }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_subject, parent, false)
        return SubjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val dateList = groupedSubjects.keys.toList()
        val startIndex = position * 3

        if (startIndex < dateList.size) {
            val date1 = dateList[startIndex]
            val subjects1 = groupedSubjects[date1]

            holder.dateTextView1.text = date1
            holder.assignedSubjectText1.text = getSubjectsText(subjects1)

            holder.dateTextView1.visibility = View.VISIBLE
            holder.assignedSubjectText1.visibility = View.VISIBLE

            holder.itemView.visibility = View.VISIBLE
        } else {
            holder.itemView.visibility = View.GONE
        }

        // Set visibility and values for dateTextView2, assignedSubjectText2, dateTextView3, assignedSubjectText3
        val date2 = dateList.getOrNull(startIndex + 1)
        val subjects2 = groupedSubjects[date2]
        if (date2 != null && subjects2 != null) {
            holder.dateTextView2.text = date2
            holder.assignedSubjectText2.text = getSubjectsText(subjects2)

            holder.dateTextView2.visibility = View.VISIBLE
            holder.assignedSubjectText2.visibility = View.VISIBLE
        } else {
            holder.dateTextView2.visibility = View.GONE
            holder.assignedSubjectText2.visibility = View.GONE
        }

        val date3 = dateList.getOrNull(startIndex + 2)
        val subjects3 = groupedSubjects[date3]
        if (date3 != null && subjects3 != null) {
            holder.dateTextView3.text = date3
            holder.assignedSubjectText3.text = getSubjectsText(subjects3)

            holder.dateTextView3.visibility = View.VISIBLE
            holder.assignedSubjectText3.visibility = View.VISIBLE
        } else {
            holder.dateTextView3.visibility = View.GONE
            holder.assignedSubjectText3.visibility = View.GONE
        }

    }

    private fun getSubjectsText(subjects: List<Subject>?): String {
        return subjects?.joinToString("\n") { it.name } ?: ""
    }


    override fun getItemCount(): Int {
        val dateSize = groupedSubjects.size
        val maxSubjects = dateSize * 3
        return if (maxSubjects > subjects.size) dateSize else (subjects.size + 2) / 3
    }

    fun updateGroupedSubjects() {
        groupedSubjects = subjects.sortedBy { formatDate(it.date) }.groupBy { formatDate(it.date) }
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }



}
