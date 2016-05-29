package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;

import java.util.Arrays;
import java.util.List;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 13.02.2015.
 */
public class NotificationSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new NotificationSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new NotificationSensor[0];
        }
    };
    private String operator;
    private String key;
    private String value;

    private NotificationSensor(Parcel in) {
        key = in.readString();
        operator = in.readString();
        value = in.readString();
    }

    public NotificationSensor() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean proveExpression() {
        List<StatusBarNotification> notificationList = sensor.getNotifications();
        if (notificationList != null) {
            if (key.equals("count")) {
                return ESMDummyHelper.getBooleanForFullOperator(Integer.parseInt(value), sensor.getNotifications().size(), operator);
            } else if (key.equals("package")) {
                List<String> packageList = Arrays.asList(value.split(","));
                for (StatusBarNotification notification : notificationList) {
                    if (packageList.contains(notification.getPackageName())) {
                        if (operator.equals("=")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                if (operator.equals("=")) {
                    return false;
                } else {
                    return true;
                }

            }
        }
        return false;
    }


    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor) abstractSensor;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(operator);
        dest.writeString(value);
    }

    @Override
    public String toString() {
        return getSensorString() + " " + key + ": " + operator + " [" + value + "]";
    }
}
