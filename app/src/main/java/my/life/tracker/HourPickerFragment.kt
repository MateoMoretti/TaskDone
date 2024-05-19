package my.life.tracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class HourPickerFragment : DialogFragment() {
    private var listener: OnTimeSetListener? = null

    fun setListener(listener: OnTimeSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(requireContext(), listener, hour, minute, true)
    }

    companion object {
        var hour = 0
        var minute = 0
        fun newInstance(listener: OnTimeSetListener?, hour: Int, minute: Int): HourPickerFragment {
            this.hour = hour
            this.minute = minute
            val fragment = HourPickerFragment()
            fragment.setListener(listener)
            return fragment
        }
    }
}
