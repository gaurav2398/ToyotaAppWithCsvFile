package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.Model.MemberModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.TestResultActivity;


public class MemberAdapterNew<FragmentManagerImpl> extends RecyclerView.Adapter<MemberAdapterNew.ViewHolder> {

    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    int lastPosition = 0;
    private Context context;
    String getpostid, user_id;
    private boolean isVertical;
    protected List<MemberModel> data = new ArrayList<>();
    private Context ctx;
    Switch wifi, location;

    WifiManager wifiManager, wifiManager1;
    String likecountapi, getCommenttxt;


    public MemberAdapterNew(Context context, List<MemberModel> data) {
        this.isVertical = isVertical;
        this.context = context;
        this.data = data;
        wifiManager1 = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isVertical() {
        return isVertical;
    }


    @Override
    public MemberAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memberdetails, parent, false);
        MemberAdapterNew.ViewHolder vh = new MemberAdapterNew.ViewHolder(v);

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
                WifiInfo wifiInfo = wifiManager1.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = Formatter.formatIpAddress(ip);

                //        Toast.makeText(TestScreenActivity.this, "Please Connect to Wifi !! \n", Toast.LENGTH_LONG).show();
                //context.startActivity(new Intent(context, TestResultActivity.class));

                Intent i = new Intent(context, TestResultActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", d.getId());
                i.putExtra("ipaddress", ipAddress);
                Log.d("member id   ",d.getId());
                context.startActivity(i);

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
