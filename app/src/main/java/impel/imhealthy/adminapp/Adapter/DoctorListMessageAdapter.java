package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.Model.DoctorListDialogModel;
import impel.imhealthy.adminapp.R;


public class DoctorListMessageAdapter extends RecyclerView.Adapter<DoctorListMessageAdapter.ViewHolder> {

    int lastPosition = -1;
    private Context context;
    protected List<DoctorListDialogModel> data = new ArrayList<>();


    public DoctorListMessageAdapter(Context context, List<DoctorListDialogModel> data) {
            this.context = context;
        this.data = data;

    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public DoctorListMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctordetails, parent, false);
        DoctorListMessageAdapter.ViewHolder vh = new DoctorListMessageAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListMessageAdapter.ViewHolder viewHolder, int i) {


        viewHolder.itemView.setTag(data.get(i));

        final DoctorListDialogModel d = data.get(i);

        viewHolder.doctor_name.setText(d.getDoctor_name());
        viewHolder.mobile.setText(d.getMobile());

        Log.d("mobile number",d.getMobile());

        viewHolder.llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("smsto:"));
                i.setType("vnd.android-dir/mms-sms");
                i.putExtra("address", new String(d.getMobile()));
                i.putExtra("sms_body","");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(i);
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView doctor_name,mobile;
        LinearLayout llayout;

        public ViewHolder(View itemView) {
            super(itemView);

            doctor_name = itemView.findViewById(R.id.doctor_name);
            mobile = itemView.findViewById(R.id.mobile);
            llayout = itemView.findViewById(R.id.llayout);


        }
    }
}
