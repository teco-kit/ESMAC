package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 16.02.2015.
 */
public class AccelerometerSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new AccelerometerSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new AccelerometerSensor[0];
        }
    };
    private float x = Float.NaN;
    private float y = Float.NaN;
    private float z = Float.NaN;
    private float acceleration = Float.NaN;
    private String operator;

    private AccelerometerSensor(Parcel in) {
        operator = in.readString();
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
        acceleration = in.readFloat();
    }

    public AccelerometerSensor() {
        x = Float.NaN;
        y = Float.NaN;
        z = Float.NaN;
        acceleration = Float.NaN;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public boolean proveExpression() {
        boolean match = true;
        float[] values = new float[]{x, y, z, acceleration};
        float[] results = sensor.getValuesAsArray();
        for (int i = 0; i < values.length; i++) {
            if (!Float.isNaN(values[i])) {
                match = match && ESMDummyHelper.getBooleanForFullOperator(values[i], results[i], operator);
            }
        }
        return match;
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor) abstractSensor;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
        dest.writeFloat(acceleration);
    }

    @Override
    public String toString() {
        return getSensorString() + " " + operator + " " + x + " " + y + " " + z + " " + acceleration;
    }
}
