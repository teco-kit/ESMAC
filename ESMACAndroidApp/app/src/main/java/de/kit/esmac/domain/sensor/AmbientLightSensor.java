package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor;

/**
 * Created by Robert on 16.02.2015.
 */
public class AmbientLightSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<LightSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new AmbientLightSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new AmbientLightSensor[0];
        }
    };
    private float value;
    private String operator;

    private AmbientLightSensor(Parcel in) {
        operator = in.readString();
        value = in.readInt();
    }

    public AmbientLightSensor() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public boolean proveExpression() {
        return ESMDummyHelper.getBooleanForFullOperator(value, sensor.getSensorValue(), operator);
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor) abstractSensor;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeFloat(value);
    }

    @Override
    public String toString() {
        return "ambientlight " + operator + " " + value;
    }
}
