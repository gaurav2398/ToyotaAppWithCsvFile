package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.AddedDoctorProfileScreen;
import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.Model.FirstModel;
import impel.imhealthy.adminapp.Model.ToyotaNameAmountModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.StudentListActivity;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class FirstListAdapter extends RecyclerView.Adapter<FirstListAdapter.MyViewHolder> {

    private Context context;

    SessionManager session;
    private List<FirstModel> list;

    private List<FirstModel> list1 = new ArrayList<>();

    public FirstListAdapter(Context context, List<FirstModel> list) {

        this.context = context;
        this.list = list;
        this.list1 = list;

        session = new SessionManager(context);

        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder// implements View.OnClickListener,            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {
        LinearLayout llayout;
        TextView name;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.image);
            llayout = view.findViewById(R.id.llayout);
        }
    }

    @Override
    public FirstListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabrecycler, parent, false);
        return new FirstListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final FirstModel model = list.get(position);
        holder.name.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getToUserProfileIntent = new Intent(context, StudentListActivity.class);
                getToUserProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getToUserProfileIntent.putExtra("model", model.getName());
                context.startActivity(getToUserProfileIntent);

            }
        });
        holder.llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getToUserProfileIntent = new Intent(context, StudentListActivity.class);
                getToUserProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getToUserProfileIntent.putExtra("model", model.getName());
                context.startActivity(getToUserProfileIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}