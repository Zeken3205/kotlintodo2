package com.example.kotlintodo2.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.example.kotlintodo2.databinding.FragmentAddTodoPopupBinding
import com.example.kotlintodo2.ui.Scheduler.SchedularFragment
import com.example.kotlintodo2.ui.home.HomeFragment
import com.example.kotlintodo2.utils.ToDoData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText


class AddTodoPopupFragment : BottomSheetDialogFragment(),TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener: DialogNextButtonClickListener
    private var toDoData: ToDoData? = null




    fun setListener(listener: HomeFragment) {
        this.listener = listener;
    }
    fun setListener(listener: SchedularFragment) {
        this.listener = listener;
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String,date:String,time:String) = AddTodoPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
                putString("date",date)
                putString("time",time)
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
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString(),
                arguments?.getString("date").toString(),
                arguments?.getString("time").toString(),

                )
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
            if (todotask.isNotEmpty()) {
                if(toDoData==null){
                    listener.onSaveTask(todotask, binding.popuptodotaskname,binding.popupdate,binding.popuptime)
                }else{
                    toDoData?.task=todotask
                    toDoData?.date=tododate
                    toDoData?.time=todotime
                    listener.onUpdateTask(toDoData!!,binding.popuptodotaskname,binding.popupdate,binding.popuptime)
                }
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btntime.setOnClickListener {
            var hour:Int=0
            var minute:Int=0
            TimePickerDialog(requireContext(),this,hour,minute,false).show()
        }
    }

    interface DialogNextButtonClickListener {
        fun onSaveTask(
            todo: String,
            popuptodotaskname: TextInputEditText,
            popupdate: EditText,
            popuptime: EditText
        )
        fun onUpdateTask(
            toDoData: ToDoData,
            popuptodotaskname: TextInputEditText,
            popupdate: EditText,
            popuptime: EditText
        )
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val savedHour:Int =p1
        val savedMinute:Int=p2
        val timeString = "$savedHour:$savedMinute"
        val editable = Editable.Factory.getInstance().newEditable(timeString)
        binding.popuptime.text = editable
    }


}