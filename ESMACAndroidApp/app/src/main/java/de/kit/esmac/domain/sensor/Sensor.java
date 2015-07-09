package de.kit.esmac.domain.sensor;

import android.os.Parcelable;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 08.02.2015.
 */
public interface Sensor extends Parcelable {

    boolean proveExpression();

    String getSensorString();

    void initializeSensor(AbstractSensorImpl abstractSensor);

}
