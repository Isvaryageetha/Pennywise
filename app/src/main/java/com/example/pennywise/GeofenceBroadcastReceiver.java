package com.example.pennywise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "geofence_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event == null || event.hasError()) return;

        int transition = event.getGeofenceTransition();

        for (Geofence geofence : event.getTriggeringGeofences()) {
            String requestId = geofence.getRequestId();
            String[] parts = requestId.split(",");
            if (parts.length != 2) continue; // safety check

            double lat, lng;
            try {
                lat = Double.parseDouble(parts[0]);
                lng = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }

            String locationName = getLocationName(context, lat, lng);

            String message;
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                message = "You entered " + locationName + "! Keep your expenses low.";
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                message = "You left " + locationName;
            } else {
                message = "Location changed: " + locationName;
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Location Alert")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);

            // ✅ Safely send notification with try-catch
            try {
                manager.notify(locationName.hashCode(), builder.build());
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getLocationName(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getSubLocality();
                if (locality == null) locality = address.getThoroughfare();
                if (locality == null) locality = address.getFeatureName();
                return locality != null ? locality : "Unknown Location";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }
}
