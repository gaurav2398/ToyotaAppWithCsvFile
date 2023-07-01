package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import impel.imhealthy.adminapp.Model.ExpenseModel;
import impel.imhealthy.adminapp.Model.ToyotaModel;
import impel.imhealthy.adminapp.Model.ToyotaNameAmountModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.StudentListActivity;
import impel.imhealthy.adminapp.TestScreenActivity;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> implements Filterable {

    private Context context;

    SessionManager session;
    String slang;
//    OnItemClickListener mListener;
    private List<ToyotaNameAmountModel> list;

    private List<ToyotaNameAmountModel> list1= new ArrayList<>();

    public StudentListAdapter(Context context, List<ToyotaNameAmountModel> list) {

        this.context = context;
        this.list = list;
        this.list1 = list;

        session = new SessionManager(context);

        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder// implements View.OnClickListener,            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {


        RelativeLayout llayout;
        TextView name, amount;
        CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            checkBox = view.findViewById(R.id.checkBox);
            amount = view.findViewById(R.id.amount);
            llayout = view.findViewById(R.id.llayout);


        }

    }

    @Override
    public StudentListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_details, parent, false);
        return new StudentListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ToyotaNameAmountModel model = list.get(position);
        holder.name.setText(model.getName());
        holder.amount.setText(model.getAmount());

        int total =Integer.parseInt(StudentListActivity.individualtotal.getText().toString())+Integer.parseInt(holder.amount.getText().toString());
        int ctotal =Integer.parseInt(StudentListActivity.companytotal.getText().toString())+Integer.parseInt(holder.amount.getText().toString());

        if (holder.name.getText().toString().equals("Registration Individual")) {
            StudentListActivity.individualtotal.setText(total+"");
        }else if(holder.name.getText().toString().equals("Registration Taxi/Company")) {
            StudentListActivity.companytotal.setText(ctotal+"");
        }else{
            StudentListActivity.individualtotal.setText(total+"");
            StudentListActivity.companytotal.setText(ctotal+"");
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    int total =Integer.parseInt(StudentListActivity.individualtotal.getText().toString())+Integer.parseInt(holder.amount.getText().toString());
                    int ctotal =Integer.parseInt(StudentListActivity.companytotal.getText().toString())+Integer.parseInt(holder.amount.getText().toString());

                    if (holder.name.getText().toString().equals("Registration Individual")) {
                        StudentListActivity.individualtotal.setText(total+"");
                    }else if(holder.name.getText().toString().equals("Registration Taxi/Company")) {
                        StudentListActivity.companytotal.setText(ctotal+"");
                    }else{
                        StudentListActivity.individualtotal.setText(total+"");
                        StudentListActivity.companytotal.setText(ctotal+"");
                    }
                }else{
                    int total =Integer.parseInt(StudentListActivity.individualtotal.getText().toString())-Integer.parseInt(holder.amount.getText().toString());
                    int ctotal =Integer.parseInt(StudentListActivity.companytotal.getText().toString())-Integer.parseInt(holder.amount.getText().toString());
                    if (holder.name.getText().toString().equals("Registration Individual")) {
                        StudentListActivity.individualtotal.setText(total+"");
                    }else if(holder.name.getText().toString().equals("Registration Taxi/Company")) {
                        StudentListActivity.companytotal.setText(ctotal+"");
                    }else{
                        StudentListActivity.individualtotal.setText(total+"");
                        StudentListActivity.companytotal.setText(ctotal+"");
                    }

                }
            }
        });
//        Log.d("CustomerList Adapter", model.getId() + " " + model.getGroupname() + "  " + model.getCustomername() + "   " + model.getCustomernumber());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public android.widget.Filter getFilter() {

        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    list = list1;
                } else {

                    ArrayList<ToyotaNameAmountModel> filteredList = new ArrayList<>();

                    for (ToyotaNameAmountModel androidVersion : list1) {

//                        if (androidVersion.getCustomernumber().toLowerCase().contains(charString) ||
//                                androidVersion.getCustomername().contains(charString)) {
//                            filteredList.add(androidVersion);
//                        }
                    }
                    list = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (ArrayList<ToyotaNameAmountModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}