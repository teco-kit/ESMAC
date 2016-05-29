package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 16.02.2015.
 */
public class ScreenActivitySensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new ScreenActivitySensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new ScreenActivitySensor[0];
        }
    };
    private String operator;
    private String value;

    private ScreenActivitySensor(Parcel in) {
        operator = in.readString();
        value = in.readString();
    }

    public ScreenActivitySensor() {

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
        return ESMDummyHelper.getBooleanForRestrictedOperator(value, String.valueOf(sensor.isActive()), operator);
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor) abstractSensor;
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
        return "lightsensor " + operator + " " + value;
    }
}
