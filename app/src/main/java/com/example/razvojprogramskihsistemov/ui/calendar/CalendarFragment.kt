import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.example.razvojprogramskihsistemov.R

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize the CalendarView
        calendarView = rootView.findViewById(R.id.calendarView)

        // Set an event listener to handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle the selected date
            // You can perform any logic or actions based on the selected date
            // For example, update a TextView with the selected date
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            // TODO: Perform your desired action with the selected date
        }

        return rootView
    }
}
