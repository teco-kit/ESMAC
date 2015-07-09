package de.kit.esmac.domain.sensor;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 13.02.2015.
 */
public class BluetoothSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new BluetoothSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new BluetoothSensor[0];
        }
    };
    private String key;
    private String operator;
    private String value;

    private BluetoothSensor(Parcel in) {
        key = in.readString();
        operator = in.readString();
        value = in.readString();
    }

    public BluetoothSensor() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        sensor.startDiscovery();
        if (key.equals("count")) {
            return ESMDummyHelper.getBooleanForFullOperator(Integer.parseInt(value), sensor.getDeviceCount(), operator);
        } else if (key.equals("name")) {
            List<String> stringDeviceList = Arrays.asList(value.split(","));
            List<BluetoothDevice> deviceList = sensor.getDeviceList();
            for (BluetoothDevice device : deviceList) {
                if (stringDeviceList.contains(device.getName())) {
                    if (operator.equals("=")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            if (operator.equals("=")) {
                return false;
            } else {
                return true;
            }
        } else if (key.equals("mac")) {
            List<String> stringDeviceList = Arrays.asList(value.split(";"));
            List<BluetoothDevice> deviceList = sensor.getDeviceList();
            for (BluetoothDevice device : deviceList) {
                if (stringDeviceList.contains(device.getAddress())) {
                    if (operator.equals("=")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            if (operator.equals("=")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor && sensor == null) {
            sensor = (de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor) abstractSensor;
        }
        sensor.startDiscovery();

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

    @Override
    public String toString() {
        return getSensorString() + ": " + key + " " + operator + " [" + value + "]";
    }
}
