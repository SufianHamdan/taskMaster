package com.example.taskmasterapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClicked(int position);
//        void onDeleteTask(int position);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskName.setText(task.getTitle());
        holder.taskStatus.setText(task.getStatus());
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView taskImage;
        private TextView taskName;
        private TextView taskDelete;
        private TextView taskStatus;

        public ViewHolder(@NonNull View itemView, OnTaskClickListener listener) {
            super(itemView);


            taskImage = itemView.findViewById(R.id.task_image);
            taskName = itemView.findViewById(R.id.task_name);
            taskDelete = itemView.findViewById(R.id.task_delete);
            taskStatus = itemView.findViewById(R.id.task_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTaskClicked(getAdapterPosition());
                }
            });

//            taskDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onDeleteTask(getAdapterPosition());
//                }
//            });
        }
    }
}
