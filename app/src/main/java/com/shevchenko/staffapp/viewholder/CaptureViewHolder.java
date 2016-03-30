package com.shevchenko.staffapp.viewholder;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.shevchenko.staffapp.R;

public class CaptureViewHolder {

    public CaptureViewHolder(View view) {
        TextView discovery = (TextView) view.findViewById(R.id.discovery);
        TextView pairing = (TextView) view.findViewById(R.id.pairing);
        TextView capture = (TextView) view.findViewById(R.id.capture);
        ListView discoveryList = (ListView) view.findViewById(R.id.discovery_list);
        ListView pairingList = (ListView) view.findViewById(R.id.pairing_list);
        ListView captureList = (ListView) view.findViewById(R.id.capture_list);


    }
}
