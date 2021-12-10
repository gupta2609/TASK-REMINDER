package com.example.taskreminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Activity context;
    private List<UserEntity> datalist;
    private RoomDb db;
    Date date = null;
    String outputDateString = null;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.UK);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.UK);


    public MainAdapter(Activity context,List<UserEntity>datalist){

        this.context=context;
        this.datalist=datalist;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEntity ue = datalist.get(position);
        db = RoomDb.getDbInstance(context);
        holder.title.setText(ue.getTitle());
        holder.discription.setText(ue.getDescription());
        holder.status.setText(ue.getStatus());
        holder.time.setText(ue.getTime());
        try {
            date = inputDateFormat.parse(ue.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserEntity task = datalist.get(position);
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menuDelete:
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                                    setPositiveButton(R.string.yes, (dialog, which) -> {
                                        deleteTaskFromId(task.getId(), position);
                                    })
                                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                            break;
                        case R.id.menuUpdate:
                            Intent intent = new Intent(context,AddTask.class);
                            intent.putExtra("flag",task.getId());
                            context.startActivity(intent);
                            break;
                        case R.id.menuComplete:
                            AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context);
                            completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                                    setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task.getId(), position))
                                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,discription,date,time,month,day,status;
        ImageView option;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            discription = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time);
            month = itemView.findViewById(R.id.month);
            status = itemView.findViewById(R.id.status);
            option = itemView.findViewById(R.id.options);
        }
    }
    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<UserEntity>> {
            @Override
            protected List<UserEntity> doInBackground(Void... voids) {
                RoomDb.getDbInstance(context).userDao().deleteTaskFromId(taskId);

                return datalist;
            }

            @Override
            protected void onPostExecute(List<UserEntity> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }
    private void removeAtPosition(int position){
        datalist.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datalist.size());
    }
    public void showCompleteDialog(int taskId, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            deleteTaskFromId(taskId, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}
