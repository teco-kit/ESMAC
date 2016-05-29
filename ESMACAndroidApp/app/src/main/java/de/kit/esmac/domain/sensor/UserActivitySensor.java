package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 13.02.2015.
 */
public class UserActivitySensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new UserActivitySensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new UserActivitySensor[0];
        }
    };
    private String operator;
    private String value;

    private UserActivitySensor(Parcel in) {
        operator = in.readString();
        value = in.readString();
    }

    public UserActivitySensor() {

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean proveExpression() {
        List<String> activityList = Arrays.asList(value.split(","));
        String activity = sensor.getActivityAsString();
        if (!activity.equals("")) {
            if (operator.equals("=")) {
                return activityList.contains(activity) && sensor.getDetectedActivity().getConfidence() > 50;
            } else {
                return !(activityList.contains(activity) && sensor.getDetectedActivity().getConfidence() > 50);
            }
        } else return false;
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor && this.sensor == null) {
            this.sensor = (de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor) abstractSensor;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeString(value);

    }

    @Override
    public String toString() {
        return getSensorString() + ": " + operator + " [" + value + "]";
    }
}
