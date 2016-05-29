package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 16.02.2015.
 */
public class TelephoneSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new TelephoneSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new TelephoneSensor[0];
        }
    };
    private String operator;
    private String value;

    private TelephoneSensor(Parcel in) {
        operator = in.readString();
        value = in.readString();
    }

    public TelephoneSensor() {

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
        List<String> telephoneStates = Arrays.asList(value.split(","));
        if (operator.equals("=")) {
            return telephoneStates.contains(sensor.getCallStateAsString());
        } else {
            return !telephoneStates.contains(sensor.getCallStateAsString());
        }
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor) abstractSensor;
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
        return "telephon " + operator + " [" + value + "]";
    }
}
