package de.kit.esmac.domain.rule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 07.02.2015.
 */
public class SensorConjunction implements Parcelable, RulePart {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new SensorConjunction(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new SensorConjunction[0];
        }
    };
    private SensorExpression firstSensorExpression;
    private SensorExpression secondSensorExpression;
    private SensorConjunction sensorConjunction;
    private String conjunction;

    private SensorConjunction(Parcel in) {
        int isLeaf = in.readInt();
        this.firstSensorExpression = in.readParcelable(SensorExpression.class.getClassLoader());
        this.conjunction = in.readString();
        if (isLeaf == 0) {
            this.sensorConjunction = in.readParcelable(SensorConjunction.class.getClassLoader());
        } else {
            this.secondSensorExpression = in.readParcelable(SensorExpression.class.getClassLoader());
        }
    }

    public SensorConjunction() {

    }


    public SensorConjunction getSensorConjunction() {
        return sensorConjunction;
    }

    public void setSensorConjunction(SensorConjunction sensorConjunction) {
        this.sensorConjunction = sensorConjunction;
    }

    public SensorExpression getFirstSensorExpression() {
        return firstSensorExpression;
    }

    public void setFirstSensorExpression(SensorExpression firstSensorExpression) {
        this.firstSensorExpression = firstSensorExpression;
    }

    public SensorExpression getSecondSensorExpression() {
        return secondSensorExpression;
    }

    public void setSecondSensorExpression(SensorExpression secondSensorExpression) {
        this.secondSensorExpression = secondSensorExpression;
    }

    public String getConjunction() {
        return conjunction;
    }

    public void setConjunction(String conjunction) {
        this.conjunction = conjunction;
    }

    @Override
    public String toString() {
        if (sensorConjunction != null)
            return firstSensorExpression.toString() + " " + conjunction + " " + sensorConjunction.toString();
        else {
            return firstSensorExpression.toString() + " " + conjunction + " " + secondSensorExpression.toString();
        }
    }

    public void initializeConjunction(HashMap<String, AbstractSensorImpl> sensorHashMap) {
        if (sensorConjunction != null) {
            firstSensorExpression.initialize(sensorHashMap);
            sensorConjunction.initializeConjunction(sensorHashMap);
        } else {
            firstSensorExpression.initialize(sensorHashMap);
            secondSensorExpression.initialize(sensorHashMap);
        }
    }

    public boolean proveConjunction() {
        if (sensorConjunction != null) {
            return resolveConjunction(firstSensorExpression.proveExpression(), sensorConjunction.proveConjunction());
        } else {
            return resolveConjunction(firstSensorExpression.proveExpression(), secondSensorExpression.proveExpression());
        }
    }

    private boolean resolveConjunction(boolean b, boolean a) {
        if (getConjunction().equals("and")) {
            return b && a;
        } else if (getConjunction().equals("or")) {
            return b || a;
        }
        return false;
    }

    private int isLeaf() {
        if (sensorConjunction != null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isLeaf());
        dest.writeParcelable(firstSensorExpression, 0);
        dest.writeString(conjunction);
        if (sensorConjunction != null) {
            dest.writeParcelable(sensorConjunction, 0);
        } else {
            dest.writeParcelable(secondSensorExpression, 0);
        }

    }
}
