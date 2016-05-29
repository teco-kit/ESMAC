package de.kit.esmac.domain.rule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import de.kit.esmac.domain.sensor.Sensor;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

public class SensorExpression implements Parcelable, RulePart {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new SensorExpression(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new SensorExpression[0];
        }
    };
    private boolean negated;
    private Sensor sensor;

    private SensorExpression(Parcel in) {
        sensor = in.readParcelable(Sensor.class.getClassLoader());
        if (in.readInt() == 1) {
            negated = true;
        } else {
            negated = false;
        }
    }

    public SensorExpression() {

    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public String toString() {
        return sensor.toString();
    }

    public boolean proveExpression() {
        return sensor.proveExpression();
    }

    public void initialize(HashMap<String, AbstractSensorImpl> sensorHashMap) {
        sensor.initializeSensor(sensorHashMap.get(sensor.getSensorString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sensor, 0);
        dest.writeInt(negated ? 1 : 0);
    }
}
