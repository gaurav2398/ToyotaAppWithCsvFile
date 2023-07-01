package impel.imhealthy.adminapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.Model.DaySortingModel;
import impel.imhealthy.adminapp.R;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    int lastPosition = -1;
    private Context context;
    protected List<DaySortingModel> data = new ArrayList<>();


    public DayAdapter(Context context, List<DaySortingModel> data) {
            this.context = context;
        this.data = data;

    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.daydetails, parent, false);
        DayAdapter.ViewHolder vh = new DayAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(data.get(i));

        final DaySortingModel d = data.get(i);

        viewHolder.time.setText(d.getTime());
        viewHolder.address.setText(d.getAddress());
        viewHolder.fever_f.setText(d.getFever_f()+"\u00B0F");
        viewHolder.oxymetry_spo2.setText(d.getOxymetry_spo2());
        viewHolder.oxymetry_bpm.setText(d.getOxymetry_bpm());
        viewHolder.member_name.setText(d.getMember_name());

        if (viewHolder.member_name.getText().toString().equals("[]"))
        {
            viewHolder.member_name.setText("Self");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView time,address,fever_f,oxymetry_spo2,oxymetry_bpm,member_name;

        public ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            address = itemView.findViewById(R.id.address);
            fever_f = itemView.findViewById(R.id.fever_f);
            oxymetry_spo2 = itemView.findViewById(R.id.oxymetry_spo2);
            oxymetry_bpm = itemView.findViewById(R.id.oxymetry_bpm);
            member_name = itemView.findViewById(R.id.member_name);

        }
    }
}
