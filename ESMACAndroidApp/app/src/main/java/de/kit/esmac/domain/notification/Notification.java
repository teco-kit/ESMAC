package de.kit.esmac.domain.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;

import de.kit.esmac.R;
import de.kit.esmac.ui.QuestionnaireActivity;

/**
 * Created by Robert on 06.02.2015.
 */
public class Notification implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Notification[0];
        }
    };
    private boolean vibrate;
    private boolean ring;
    private boolean notificationLed;
    private int maxNotifications;
    private long minimumTimeBorder;
    private NotificationManager notificationManager;
    private android.app.Notification notification;

    public Notification(boolean vibrate, boolean ring, boolean notificationLed, int maxNotifications, long cooldownTime) {
        this.vibrate = vibrate;
        this.ring = ring;
        this.notificationLed = notificationLed;
        this.maxNotifications = maxNotifications;
        this.minimumTimeBorder = cooldownTime;
    }

    public Notification(Parcel in) {
        this.vibrate = intToBool(in.readInt());
        this.ring = intToBool(in.readInt());
        this.notificationLed = intToBool(in.readInt());
        this.minimumTimeBorder = in.readLong();
        this.maxNotifications = in.readInt();

    }

    public int getMaxNotifications() {
        return maxNotifications;
    }

    public long getMinimumTimeBorder() {
        return minimumTimeBorder;
    }

    private int boolToInt(boolean bool) {
        if (bool) {
            return 1;
        } else return 0;
    }

    private boolean intToBool(int i) {
        if (i == 1) {
            return true;
        } else return false;
    }

    private void initializeNotificationManager(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void buildNotification(Context context) {
        Intent resultIntent = new Intent(context, QuestionnaireActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_description))
                .setContentIntent(resultPendingIntent);
        //TODO: Noch unschön
        int defaults = 0;
        if (vibrate) {
            if (defaults == 0) {
                defaults = android.app.Notification.DEFAULT_VIBRATE;
            } else {
                defaults = defaults | android.app.Notification.DEFAULT_VIBRATE;
            }
        }
        if (ring) {
            if (defaults == 0) {
                defaults = android.app.Notification.DEFAULT_SOUND;
            } else {
                defaults = defaults | android.app.Notification.DEFAULT_SOUND;
            }
        }
        if (notificationLed) {
            if (defaults == 0) {
                defaults = android.app.Notification.DEFAULT_LIGHTS;
            } else {
                defaults = defaults | android.app.Notification.DEFAULT_LIGHTS;
            }
        }
        builder.setDefaults(defaults);
        this.notification = builder.build();
    }


    public void createNotification(Context context) {
        if (this.notificationManager == null) {
            initializeNotificationManager(context);
        }
        buildNotification(context);
        this.notificationManager.notify(1, this.notification);
    }

    public void cancelNotification(Context context) {
        if (this.notificationManager == null) {
            initializeNotificationManager(context);
        }
        this.notificationManager.cancel(1);
    }

    public boolean isNotificationVisible(Context context) {
        Intent notificationIntent = new Intent(context, QuestionnaireActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(boolToInt(vibrate));
        dest.writeInt(boolToInt(ring));
        dest.writeInt(boolToInt(notificationLed));
        dest.writeLong(minimumTimeBorder);
        dest.writeInt(maxNotifications);
    }
}
