package com.example.kotlintodo2.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kotlintodo2.databinding.FragmentAddTodoPopupBinding
import com.example.kotlintodo2.ui.Pending.PendingFragment
import com.example.kotlintodo2.ui.Scheduler.SchedularFragment
import com.example.kotlintodo2.ui.home.HomeFragment
import com.example.kotlintodo2.utils.ToDoData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class AddTodoPopupFragment : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener: DialogNextButtonClickListener
    private var toDoData: ToDoData? = null




    fun setListener(listener: HomeFragment) {
        this.listener = listener;
    }
    fun setListener(listener: SchedularFragment) {
        this.listener = listener;
    }
    fun setListener(listener: PendingFragment) {
        this.listener = listener;
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String,date:String,time:String,completed:Boolean) = AddTodoPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
                putString("date",date)
                putString("time",time)
                putBoolean("Completed",completed)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            toDoData = arguments?.getBoolean("Completed")?.let {
                ToDoData(
                    arguments?.getString("taskId").toString(),
                    arguments?.getString("task").toString(),
                    arguments?.getString("date").toString(),
                    arguments?.getString("time").toString(),
                    it
                )
            }
            binding.popuptodotaskname.setText(toDoData?.task)
            binding.popupdate.setText(toDoData?.date)
            binding.popuptime.setText(toDoData?.time)
        }
        registerEvents();
    }

    private fun registerEvents() {
        binding.popupsave.setOnClickListener {
            val todotask =binding.popuptodotaskname.text.toString()
            val tododate=binding.popupdate.text.toString()
            val todotime=binding.popuptime.text.toString()
            if (todotask.isNotEmpty()&&tododate.isNotEmpty()&&todotime.isNotEmpty()) {
                if(toDoData==null){
                    listener.onSaveTask(todotask, binding.popuptodotaskname,binding.popupdate.text.toString(),binding.popuptime.text.toString())
                }else{
                    toDoData?.task=todotask
                    toDoData?.date=tododate
                    toDoData?.time=todotime
                    listener.onUpdateTask(toDoData!!,binding.popuptodotaskname,binding.popupdate.text.toString(),binding.popuptime.text.toString())
                }
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.popupdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val date = dateFormat.format(calendar.time)
                binding.popupdate.setText(date)
            }, year, month, dayOfMonth)
            datePickerDialog.show()
        }

        binding.popuptime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(requireContext(), { view, hourOfDay, minute ->
                val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val time = timeFormat.format(calendar.time)
                binding.popuptime.setText(time)
            }, hour, minute, false)
            timePickerDialog.show()
        }
    }

    interface DialogNextButtonClickListener {
        fun onSaveTask(
            todo: String,
            popuptodotaskname: TextInputEditText,
            popupdate: String,
            popuptime: String
        )
        fun onUpdateTask(
            toDoData: ToDoData,
            popuptodotaskname: TextInputEditText,
            popupdate: String,
            popuptime: String
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Update the time EditText with the selected time
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val time = timeFormat.format(calendar.time)
        binding.popuptime.setText(time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Update the date EditText with the selected date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date = dateFormat.format(calendar.time)
        binding.popupdate.setText(date)
    }

}