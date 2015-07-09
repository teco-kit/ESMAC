package de.kit.esmac.domain.sensor;

import android.os.Parcel;

import de.kit.sensorlibrary.sensor.SensorInterface;

/**
 * Created by Robert on 30.05.2015.
 */
public abstract class AbstractSensorImpl<T extends SensorInterface> implements Sensor {

    protected T sensor;

    @Override
    public boolean proveExpression() {
        return false;
    }

    @Override
    public String getSensorString() {
        return "";
    }

    @Override
    public void initializeSensor(de.kit.sensorlibrary.sensor.AbstractSensorImpl abstractSensor) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
