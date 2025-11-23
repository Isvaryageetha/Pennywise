package com.example.pennywise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Toast.makeText(context, "Geofence error: " + geofencingEvent.getErrorCode(), Toast.LENGTH_SHORT).show();
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Trigger only when entering a geofence
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            for (Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
                String geofenceId = geofence.getRequestId();
                Toast.makeText(context,
                        "You entered " + geofenceId + "! Keep your expenses low 😅",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
