package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 07.02.2015.
 */
public class WeatherSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.weathersensor.WeatherSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new WeatherSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new WeatherSensor[0];
        }
    };
    private String operator;
    private String value;
    private String key;

    private WeatherSensor(Parcel in) {
        this.key = in.readString();
        this.operator = in.readString();
        this.value = in.readString();
    }

    public WeatherSensor() {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean proveExpression() {
        sensor.getWeather();
        String result = "";
        if (key.equals("condition")) {
            result = sensor.getCondition();
            return ESMDummyHelper.getBooleanForRestrictedOperator(value, result, operator);
        } else if (key.equals("degree")) {
            double valueDegree = Double.valueOf(value);
            double valueResult = sensor.getTemperature();
            return ESMDummyHelper.getBooleanForFullOperator(valueDegree, valueResult, operator);
        }
        return false;
    }

    @Override
    public String getSensorString() {
        return "weather";
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.weathersensor.WeatherSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.weathersensor.WeatherSensor) abstractSensor;
        }
    }

    @Override
    public String toString() {
        return key + " " + operator + " " + value;
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
}
