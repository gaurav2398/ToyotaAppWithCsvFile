package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.Model.DoctorListDialogModel;
import impel.imhealthy.adminapp.R;

public class DoctorListWhatsappAdapter extends RecyclerView.Adapter<DoctorListWhatsappAdapter.ViewHolder> {

    int lastPosition = -1;
    private Context context;
    protected List<DoctorListDialogModel> data = new ArrayList<>();


    public DoctorListWhatsappAdapter(Context context, List<DoctorListDialogModel> data) {
            this.context = context;
        this.data = data;

    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public DoctorListWhatsappAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctordetails, parent, false);
        DoctorListWhatsappAdapter.ViewHolder vh = new DoctorListWhatsappAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListWhatsappAdapter.ViewHolder viewHolder, int i) {


        viewHolder.itemView.setTag(data.get(i));

        final DoctorListDialogModel d = data.get(i);

        viewHolder.doctor_name.setText(d.getDoctor_name());
        viewHolder.mobile.setText(d.getMobile());

        Log.d("mobile number",d.getMobile());

        viewHolder.llayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
                if (isAppInstalled) {

                    String url = "https://api.whatsapp.com/send?phone=" + d.getMobile();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);

                    try {
                        context.startActivity(i);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context,
                                "Whatsapp have not been installed.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Do whatever we want to do if application not installed
                    // For example, Redirect to play store

                    Toast.makeText(context, "Application is not currently installed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
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
