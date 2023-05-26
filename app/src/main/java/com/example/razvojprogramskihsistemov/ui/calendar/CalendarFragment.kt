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
import com.example.razvojprogramskihsistemov.ui.calendar.SubjectProvider
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var subjectDropdown: Spinner
    private lateinit var subjectAdapter: ArrayAdapter<String>
    private lateinit var assignedSubjectText: TextView
    private lateinit var selectedSubjectsRecyclerView: RecyclerView
    private lateinit var selectedSubjectsAdapter: SelectedSubjectsAdapter
    private val selectedSubjects = mutableListOf<Subject>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

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
        selectedSubjectsAdapter = SelectedSubjectsAdapter(selectedSubjects)
        selectedSubjectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        selectedSubjectsRecyclerView.adapter = selectedSubjectsAdapter

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
                }
            }
        }

        return rootView
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
}

data class Subject(val date: String, val name: String)

class SelectedSubjectsAdapter(private val subjects: List<Subject>) :
    RecyclerView.Adapter<SelectedSubjectsAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.dateTextView.text = subject.date
        holder.nameTextView.text = subject.name
    }

    override fun getItemCount(): Int {
        return subjects.size
    }
}
