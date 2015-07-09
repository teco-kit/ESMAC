package de.kit.esmac.domain.rule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

public class Rule implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Rule(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Rule[0];
        }
    };
    private SensorConjunction conjunction;
    private SensorExpression expression;

    private Rule(Parcel in) {
        Parcelable part = in.readParcelable(RulePart.class.getClassLoader());
        if (part instanceof SensorConjunction) {
            conjunction = (SensorConjunction) part;
        } else {
            expression = (SensorExpression) part;
        }
    }

    public Rule() {

    }

    public SensorExpression getExpression() {
        return expression;
    }

    public void setExpression(SensorExpression expression) {
        this.expression = expression;
    }

    public boolean proveRule() {
        if (conjunction != null) {
            return conjunction.proveConjunction();
        } else {
            return expression.proveExpression();
        }

    }

    public void initializeRule(HashMap<String, AbstractSensorImpl> sensorHashMap) {
        if (conjunction != null) {
            conjunction.initializeConjunction(sensorHashMap);
        } else {
            expression.initialize(sensorHashMap);
        }

    }

    public SensorConjunction getConjunction() {
        return conjunction;
    }

    public void setConjunction(SensorConjunction conjunction) {
        this.conjunction = conjunction;
    }

    @Override
    public String toString() {
        if (conjunction != null) {
            return "Rule:" + conjunction.toString();
        } else {
            return "Rule:" + expression.toString();
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (conjunction != null) {
            dest.writeParcelable(conjunction, 0);
        } else {
            dest.writeParcelable(expression, 0);
        }
    }
}
