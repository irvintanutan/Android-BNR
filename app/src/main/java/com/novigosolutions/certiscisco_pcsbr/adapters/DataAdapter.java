package com.novigosolutions.certiscisco_pcsbr.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.MenuForm;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author irvin
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<MenuForm> form;
    private Context context;

    public DataAdapter(List<MenuForm> form , Context context) {
        this.form = form;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.menu_icon.setImageResource(form.get(position).getPhotoid());
        holder.menu_name.setText(form.get(position).getMenuName());
    }


    @Override
    public int getItemCount() {
        return form.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView menu_name , badge;
        private ImageView menu_icon;

        public ViewHolder(View view) {
            super(view);

            badge = view.findViewById(R.id.badgeNotification);
            menu_name = view.findViewById(R.id.menuText);
            menu_icon = view.findViewById(R.id.image);
        }
    }

}