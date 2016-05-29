package de.kit.esmac.domain.sensor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.kit.esmac.helper.ESMDummyHelper;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;

/**
 * Created by Robert on 12.02.2015.
 */
public class TimeSensor extends de.kit.esmac.domain.sensor.AbstractSensorImpl<de.kit.sensorlibrary.sensor.timesensor.TimeSensor> {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new TimeSensor(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new TimeSensor[0];
        }
    };
    private String key;
    private String time;
    private String[] rangeTime = new String[2];
    private String operator;
    private int count;
    private int interval;

    private TimeSensor(Parcel in) {
        key = in.readString();
        operator = in.readString();
        if (key.equals("timeRange")) {
            in.readStringArray(rangeTime);
        } else if (key.equals("random")) {
            in.readStringArray(rangeTime);
            count = in.readInt();
        } else if (key.equals("timeInterval")) {
            in.readStringArray(rangeTime);
            interval = in.readInt();
        } else {
            time = in.readString();
        }
    }

    public TimeSensor() {

    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String[] getRangeTime() {
        return rangeTime;
    }

    public void setRangeTime(String[] rangeTime) {
        this.rangeTime = rangeTime;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean proveExpression() {
        if (key.equals("weekDay")) {
            List<String> weekDays = Arrays.asList(time.split(","));
            if (operator.equals("=")) {
                return weekDays.contains(sensor.getActualDay());
            } else {
                return !weekDays.contains(sensor.getActualDay());
            }
        } else if (key.equals("onWeekend")) {
            return ESMDummyHelper.getBooleanForRestrictedOperator(time, String.valueOf(sensor.isDayWeekend()), operator);
        } else if (key.equals("daytime")) {
            return ESMDummyHelper.getBooleanForRestrictedOperator(time, sensor.getActualDayTime(), operator);
        } else if (key.equals("timeRange")) {
            Calendar beginCalender = getCalenderForTime(rangeTime[0]);
            Calendar endCalendar = getCalenderForTime(rangeTime[1]);
            Calendar resultCalendar = getCalenderForTime(sensor.getActualTime());
            if (endCalendar.before(beginCalender)) {
                if (resultCalendar.before(beginCalender))
                    beginCalender.setTimeInMillis(beginCalender.getTimeInMillis() - 86400000);
                else
                    endCalendar.setTimeInMillis(endCalendar.getTimeInMillis() + 86400000);
            }
            if (operator.equals("=")) {
                return (beginCalender.before(resultCalendar) && resultCalendar.before(endCalendar));
            } else {
                return !(beginCalender.before(resultCalendar) && resultCalendar.before(endCalendar));
            }
        } else if (key.equals("specificTime")) {
            int[] timeArray = parseTimeToIntValues(time);
            int[] resultArray = parseTimeToIntValues(sensor.getActualTime());
            if (timeArray[0] == resultArray[0] && timeArray[1] == timeArray[1]) {
                return true;
            }
            //TODO: Noch sehr sehr unschön, aber funktioniert vorerst mal
        } else if (key.equals("random")) {
            return sensor.wasRandom();
        } else if (key.equals("timeInterval")) {
            return sensor.wasInterval();
        }

        return false;
    }

    private Calendar getCalenderForTime(String time) {
        int[] timeArray = parseTimeToIntValues(time);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY, timeArray[0]);
        calender.set(Calendar.MINUTE, timeArray[1]);
        calender.set(Calendar.SECOND, timeArray[2]);
        return calender;
    }

    @Override
    public String getSensorString() {
        return de.kit.sensorlibrary.sensor.timesensor.TimeSensor.IDENTIFIER;
    }

    @Override
    public void initializeSensor(AbstractSensorImpl abstractSensor) {
        if (abstractSensor instanceof de.kit.sensorlibrary.sensor.timesensor.TimeSensor && sensor == null) {
            this.sensor = (de.kit.sensorlibrary.sensor.timesensor.TimeSensor) abstractSensor;
            if (key.equals("specificTime")) {
                int[] parsedValues = parseTimeToIntValues(time);
                sensor.addRepeatingToAlarmManager(parsedValues[0], parsedValues[1], parsedValues[2]);
            } else if (key.equals("random")) {
                sensor.setRandomTimesToAlarmClock(count, getCalenderForTime(rangeTime[0]), getCalenderForTime(rangeTime[1]));
            } else if (key.equals("timeInterval")) {
                sensor.setIntervalTimesToAlarmClock(interval, getCalenderForTime(rangeTime[0]), getCalenderForTime(rangeTime[1]));
            }
        }
        sensor.openSensor();
    }

    private int[] parseTimeToIntValues(String time) {
        String[] splitString = time.split(":");
        return new int[]{Integer.valueOf(splitString[0]), Integer.valueOf(splitString[1]), Integer.valueOf(splitString[2])};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(operator);
        if (time != null) {
            dest.writeString(time);
        } else {
            dest.writeStringArray(rangeTime);
            if (key.equals("random")) {
                dest.writeInt(count);
            } else if (key.equals("timeInterval")) {
                dest.writeInt(interval);
            }
        }
    }

    @Override
    public String toString() {
        String toString = "Time: " + key + " " + operator;
        if (time != null) {
            toString += " [" + time + "]";
        } else {
            toString += " " + rangeTime[0] + " " + rangeTime[1];
            if (key.equals("random")) {
                toString += " " + count;
            } else if (key.equals("timeInterval")) {
                toString += " " + interval;
            }
        }
        return toString;
    }
}
