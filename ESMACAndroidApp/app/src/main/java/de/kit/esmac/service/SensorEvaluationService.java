package de.kit.esmac.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.kit.esmac.domain.ParsedESMDummyObject;
import de.kit.esmac.domain.notification.Notification;
import de.kit.esmac.domain.rule.Rule;
import de.kit.esmac.domain.screen.ScreenFragment;
import de.kit.esmac.xml.XMLParser;
import de.kit.esmac.log.LogFileWriter;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;
import de.kit.sensorlibrary.sensor.ValueChangedEvent;
import de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerChangedEvent;
import de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor;
import de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensorChangedListener;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightChangedEvent;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensorChangedListener;
import de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothDeviceChangedEvent;
import de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothDeviceChangedListener;
import de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor;
import de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceChangedEvent;
import de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceChangedListener;
import de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceSensor;
import de.kit.sensorlibrary.sensor.locationsensor.LocationChangedEvent;
import de.kit.sensorlibrary.sensor.locationsensor.LocationChangedListener;
import de.kit.sensorlibrary.sensor.locationsensor.LocationSensor;
import de.kit.sensorlibrary.sensor.notificationsensor.NotificationChangedEvent;
import de.kit.sensorlibrary.sensor.notificationsensor.NotificationChangedListener;
import de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor;
import de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivityChangedEvent;
import de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivityListener;
import de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor;
import de.kit.sensorlibrary.sensor.telephonesensor.TelephoneCallStateChangedEvent;
import de.kit.sensorlibrary.sensor.telephonesensor.TelephoneCallStateChangedListener;
import de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor;
import de.kit.sensorlibrary.sensor.timesensor.TimeChangedListener;
import de.kit.sensorlibrary.sensor.timesensor.TimeSensor;
import de.kit.sensorlibrary.sensor.useractivitysensor.UserActivityChangedEvent;
import de.kit.sensorlibrary.sensor.useractivitysensor.UserActivityChangedListener;
import de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor;
import de.kit.sensorlibrary.sensor.weathersensor.WeatherSensor;
import mf.javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Robert on 11.02.2015.
 */
public class SensorEvaluationService extends Service {
    LogFileWriter logFileWriter;
    private HashMap<String, AbstractSensorImpl> sensorInterfaceList = new HashMap<>();
    private List<Rule> evaluationRules;
    private Notification notification;
    private List<String> sensorList;
    private List<ScreenFragment> screenFragments;
    private long executeTime = 0;
    private int notificationCounter;
    private SharedPreferences sharedPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        parseXml();
        registerLogReceiver();
        initializeVariablesFromSharedPreferences();
        Log.d("SENSORLIST", sensorList.toString());
        initializeSensors();
        for (Rule rule : evaluationRules) {
            rule.initializeRule(sensorInterfaceList);
        }
        initializeLogging();
        //Start_Sticky forces android to keep the service in Memory
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Nothing to do here
        return null;
    }

    private void initializeLogging() {
        System.out.println(sensorInterfaceList.values());
        logFileWriter = new LogFileWriter(sensorInterfaceList.values(), screenFragments);
        logFileWriter.createAndInitLogFileIfNotExist();
    }

    private void initializeVariablesFromSharedPreferences() {
        sharedPreferences = getSharedPreferences("de.kit.esmdummy", MODE_PRIVATE);
        notificationCounter = sharedPreferences.getInt("notificationCounter", 0);
        executeTime = sharedPreferences.getLong("lastNotificationTime", 0);
        createResetCounterAlarm();
    }

    private void registerLogReceiver() {
        IntentFilter filter = new IntentFilter("de.kit.esmdummy.log");
        LogBroadcastReceiver logBroadcastReceiver = new LogBroadcastReceiver();
        registerReceiver(logBroadcastReceiver, filter);
    }

    private void parseXml() {
        XMLParser parser = new XMLParser(this);
        try {
            final ParsedESMDummyObject parsedESMDummyObject = parser.parseXmlDocument(new File(
                    "sdcard/de.kit.esmdummy/masterarbeit.xml"));
            sensorList = parsedESMDummyObject.getSensorList();
            evaluationRules = parsedESMDummyObject.getRules();
            notification = parsedESMDummyObject.getNotification();
            screenFragments = parsedESMDummyObject.getFragments();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSensors() {
        LocationSensor locationSensor;
        for (String sensorString : sensorList) {
            switch (sensorString) {
                case WeatherSensor.IDENTIFIER:
                    locationSensor = new LocationSensor(getApplicationContext(), notification.getMinimumTimeBorder());
                    locationSensor.openSensor();
                    final WeatherSensor weatherSensor;
                    weatherSensor = new WeatherSensor(locationSensor, false);
                    //auch wenn sich der Standort nicht ändert, muss das Wetter regelmäßig überprüft werden.
                    // Es handelt sich hier um einen Sonderfall, da das Wetter direkt vom Standort abhängig ist.
                    locationSensor.addValueChangedListener(new LocationChangedListener() {
                        @Override
                        public void onValueChanged(LocationChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    locationSensor.openSensor();
                    weatherSensor.openSensor();
                    sensorInterfaceList.put(LocationSensor.IDENTIFIER, locationSensor);
                    sensorInterfaceList.put(sensorString, weatherSensor);
                    break;
                case LocationSensor.IDENTIFIER:
                    //Falls Wetter den LocationSensor schon initialisiert hat, sollte er ihn kein zweites Mal initialisieren
                    //ist ein Spezialfall, der nirgendwo sonst vorkommen kann
                    if (!sensorInterfaceList.containsKey(LocationSensor.IDENTIFIER)) {
                        locationSensor = new LocationSensor(getApplicationContext(), notification.getMinimumTimeBorder());
                        locationSensor.addValueChangedListener(new LocationChangedListener() {
                            @Override
                            public void onValueChanged(LocationChangedEvent event) {
                                proveAllRules();
                            }
                        });
                        locationSensor.openSensor();
                        sensorInterfaceList.put(LocationSensor.IDENTIFIER, locationSensor);
                    }
                    break;
                case GeofenceSensor.IDENTIFIER:
                    GeofenceSensor geofenceSensor = new GeofenceSensor(getApplicationContext());
                    geofenceSensor.addValueChangedListener(new GeofenceChangedListener() {
                        @Override
                        public void onValueChanged(GeofenceChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    geofenceSensor.openSensor();
                    sensorInterfaceList.put(sensorString, geofenceSensor);
                    break;
                case TimeSensor.IDENTIFIER:
                    final TimeSensor timeSensor = new TimeSensor(getApplicationContext());
                    timeSensor.addValueChangedListener(new TimeChangedListener() {
                        @Override
                        public void onValueChanged(ValueChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    timeSensor.openSensor();
                    sensorInterfaceList.put(sensorString, timeSensor);
                    break;
                case UserActivitySensor.IDENTIFIER:
                    UserActivitySensor userActivitySensor = new UserActivitySensor(getApplicationContext(), 1000);
                    userActivitySensor.addValueChangedListener(new UserActivityChangedListener() {
                        @Override
                        public void onValueChanged(UserActivityChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    userActivitySensor.openSensor();
                    sensorInterfaceList.put(sensorString, userActivitySensor);
                    break;
                case BluetoothSensor.IDENTIFIER:
                    BluetoothSensor bluetoothSensor = new BluetoothSensor(getApplicationContext());
                    bluetoothSensor.addValueChangedListener(new BluetoothDeviceChangedListener() {
                        @Override
                        public void onValueChanged(BluetoothDeviceChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    bluetoothSensor.openSensor();
                    sensorInterfaceList.put(sensorString, bluetoothSensor);
                    break;
                case NotificationSensor.IDENTIFIER:
                    NotificationSensor notificationSensor = new NotificationSensor(getApplicationContext());
                    notificationSensor.
                            addValueChangedListener(new NotificationChangedListener() {
                                @Override
                                public void onValueChanged(NotificationChangedEvent event) {
                                    proveAllRules();
                                }
                            });
                    notificationSensor.openSensor();
                    sensorInterfaceList.put(sensorString, notificationSensor);
                    break;
                case LightSensor.IDENTIFIER:
                    LightSensor lightSensor = new LightSensor(getApplicationContext(), 1000);
                    lightSensor.addValueChangedListener(new LightSensorChangedListener() {
                        @Override
                        public void onValueChanged(LightChangedEvent e) {
                            proveAllRules();
                        }
                    });
                    lightSensor.openSensor();
                    sensorInterfaceList.put(sensorString, lightSensor);
                    break;
                case ScreenActivitySensor.IDENTIFIER:
                    ScreenActivitySensor screenActivitySensor = new ScreenActivitySensor(getApplicationContext());
                    screenActivitySensor.addValueChangedListener(new ScreenActivityListener() {
                        @Override
                        public void onValueChanged(ScreenActivityChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    screenActivitySensor.openSensor();
                    sensorInterfaceList.put(sensorString, screenActivitySensor);
                    break;
                case TelephoneSensor.IDENTIFIER:
                    TelephoneSensor telephoneSensor = new TelephoneSensor(getApplicationContext());
                    telephoneSensor.addValueChangedListener(new TelephoneCallStateChangedListener() {
                        @Override
                        public void onValueChanged(TelephoneCallStateChangedEvent event) {
                            proveAllRules();
                        }
                    });
                    telephoneSensor.openSensor();
                    sensorInterfaceList.put(sensorString, telephoneSensor);
                    break;
                case AccelerometerSensor.IDENTIFIER:
                    AccelerometerSensor accelerometerSensor = new AccelerometerSensor(getApplicationContext(), 1000);
                    accelerometerSensor.addValueChangedListener(new AccelerometerSensorChangedListener() {
                        @Override
                        public void onValueChanged(AccelerometerChangedEvent e) {
                            proveAllRules();
                        }
                    });
                    accelerometerSensor.openSensor();
                    sensorInterfaceList.put(sensorString, accelerometerSensor);
                    break;
            }
        }
    }

    private void proveAllRules() {
        //schreibe bei jeder Änderung eines Sensors einen neuen Logeintrag
        notificationCounter = sharedPreferences.getInt("notificationCounter", 0);
        executeTime = sharedPreferences.getLong("lastNotificationTime", 0);
        long actualTime = System.currentTimeMillis();
        boolean activate = false;
        String reason = "";
        refreshSensors();
        for (Rule rule : evaluationRules) {
            boolean b = rule.proveRule();
            activate = activate || b;
            if (b == true) {
                reason += rule.toString();
            }
        }
        if (notification.getMaxNotifications() > notificationCounter &&
                notification.getMinimumTimeBorder() < (actualTime - executeTime)) {
            if (activate) {
                executeTime = actualTime;
                notification.createNotification(getApplicationContext());
                notificationCounter++;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("notificationCounter", notificationCounter);
                editor.putLong("lastNotificationTime", executeTime);
                editor.commit();
            }
            logFileWriter.writeLog("", activate, reason, false, 0, false, sharedPreferences);
        } else {
            logFileWriter.writeLog("", false, "", false, 0, false, sharedPreferences);

        }

    }

    private void refreshSensors() {
        if (sensorList.contains(BluetoothSensor.IDENTIFIER)) {
            ((BluetoothSensor) sensorInterfaceList.get(BluetoothSensor.IDENTIFIER)).startDiscovery();
        }
        if (sensorList.contains(WeatherSensor.IDENTIFIER)) {
            ((WeatherSensor) sensorInterfaceList.get(WeatherSensor.IDENTIFIER)).getWeather();
        }
    }

    private void createResetCounterAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Intent intent = new Intent(this, CounterAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    class LogBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String allAnswerString = intent.getStringExtra("allAnswers");
            long delay = intent.getLongExtra("delay", 0);
            boolean isVoluntary = !notification.isNotificationVisible(context);
            logFileWriter.writeLog(allAnswerString, false, "", true, delay, isVoluntary, SensorEvaluationService.this.sharedPreferences);
            notification.cancelNotification(context);
        }
    }
}