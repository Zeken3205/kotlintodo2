package com.example.kotlintodo2.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodo2.databinding.EachTodoItemBinding

class TodoAdapter(private val list:MutableList<ToDoData>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    private var listener:ToDoAdapterClicksInterface?=null
    fun setListener(listener:ToDoAdapterClicksInterface){
        this.listener=listener
    }
    inner class TodoViewHolder(val binding: EachTodoItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding=EachTodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text=this.task
                binding.todoTime.text=this.time
                binding.todoDate.text=this.date
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }
    }
    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }
}