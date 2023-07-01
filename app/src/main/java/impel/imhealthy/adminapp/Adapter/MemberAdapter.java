package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.AddedMemberProfileActivity;
import impel.imhealthy.adminapp.Model.MemberModel;
import impel.imhealthy.adminapp.R;


public class MemberAdapter<FragmentManagerImpl> extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    int lastPosition = 0;
    private Context context;
    String getpostid, user_id;
    private boolean isVertical;
    protected List<MemberModel> data = new ArrayList<>();
    private Context ctx;

    String likecountapi,getCommenttxt;


    public MemberAdapter(Context context, List<MemberModel> data) {
        this.isVertical = isVertical;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isVertical() {
        return isVertical;
    }


    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memberdetails, parent, false);
        MemberAdapter.ViewHolder vh = new MemberAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.itemView.setTag(data.get(i));

        final MemberModel d = data.get(i);

        viewHolder.memname.setText(d.getMembername());

        Glide.with(context).load(d.getMemberpic()).into(viewHolder.memimage);
        viewHolder.llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent getToUserProfileIntent = new Intent(context, AddedMemberProfileActivity.class);
                getToUserProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getToUserProfileIntent.putExtra("id", d.getId());
                context.startActivity(getToUserProfileIntent);
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView memname;
        LinearLayout llayout;
        ImageView memimage;

        public ViewHolder(View itemView) {
            super(itemView);

            memname = (TextView) itemView.findViewById(R.id.memname);
            llayout = (LinearLayout) itemView.findViewById(R.id.llayout);

            memimage = (ImageView) itemView.findViewById(R.id.memimage);
        }
    }


}
