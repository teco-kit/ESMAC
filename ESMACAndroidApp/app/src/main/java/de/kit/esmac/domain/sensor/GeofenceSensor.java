package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.GeofencingRequest;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 08.02.2015.
 */
public class GeofenceSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new GeofenceSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new GeofenceSensor[0];
        }
    };
    private double latitudeValue;
    private double longitudeValue;
    private float radius;
    private String operator;
    private String name;

    public GeofenceSensor(Parcel in) {
        latitudeValue = in.readDouble();
        longitudeValue = in.readDouble();
        radius = in.readFloat();
        operator = in.readString();
        name = in.readString();
    }

    public GeofenceSensor() {
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean proveExpression() {
        if (sensor.getGeofenceEventParcelable() != null) {
            if (operator.equals("=")) {
                return sensor.getGeofenceEventParcelable().getGeofenceTransition() == GeofencingRequest.INITIAL_TRIGGER_ENTER;
            } else if (operator.equals("!=")) {
                return !(sensor.getGeofenceEventParcelable().getGeofenceTransition() == GeofencingRequest.INITIAL_TRIGGER_ENTER);
            }
        }
        //ist null wenn man nicht in der Nähe einer Location war, daher false
        return false;
    }

    @Override
    public String getSensorString() {
        return "geofence";
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceSensor && sensor == null) {
            this.sensor = (de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceSensor) abstractSensor;
            sensor.addGeofence(name, latitudeValue, longitudeValue, radius);
        }
    }

    @Override
    public String toString() {
        return "geofence: " + " " + name + " " + operator + " " + latitudeValue + " " + longitudeValue + " " + radius;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitudeValue);
        dest.writeDouble(longitudeValue);
        dest.writeFloat(radius);
        dest.writeString(operator);
        dest.writeString(name);
    }
}
