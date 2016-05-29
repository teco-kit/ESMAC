package de.kit.esmac.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import org.xml.sax.SAXException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URI;
import java.net.HttpURLConnection;

import de.kit.esmac.common.Global;
import de.kit.esmac.domain.ParsedESMDummyObject;
import de.kit.esmac.R;
import de.kit.esmac.service.SensorEvaluationService;
import de.kit.esmac.xml.XMLInterface;
import de.kit.esmac.xml.XMLParser;
import de.kit.esmac.xml.XMLValidator;
import de.kit.esmac.xml.XMLRetriever;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightChangedEvent;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensorChangedListener;
import de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor;
import de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor;
import mf.javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Robert on 31.03.2015.
 */
public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_NOTIFICATIONACCESS = 2;

    @Override
    protected void onPause() {
        super.onPause();
        // um zu verhindern, dass die Fehlermeldungen nicht korrekt angezeigt werden.
        LightSensor lightSensor = new LightSensor(this,1000);
        lightSensor.addValueChangedListener(new LightSensorChangedListener() {
            @Override
            public void onValueChanged(LightChangedEvent e) {
                e.getLumen();
                // ...do something
            }
        });
        lightSensor.openSensor();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT || requestCode == REQUEST_ENABLE_NOTIFICATIONACCESS) {
            recreate();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        Button activateBluetoothButton = (Button) findViewById(R.id.activateBluetoothButton);
        Button activateNotificationButton = (Button) findViewById(R.id.activateNotificationButton);
        activateBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });
        activateNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent, REQUEST_ENABLE_NOTIFICATIONACCESS);
            }
        });

        initializeSharedPreferences();
        writeXsdIfNotExist();
        boolean bluetoothEnabled = true;
        boolean notificationAccessAllowed = true;
        boolean isValid = validateXml(textView);
        System.out.println("The url for the res is: ");
        System.out.println(Global.restapi);
        if (isValid) {
            XMLParser test = new XMLParser(this);
            try {
                final ParsedESMDummyObject parsedESMDummyObject = test.parseXmlDocument(new File(
                        "sdcard/de.kit.esmdummy/masterarbeit.xml"));
                if (parsedESMDummyObject.getSensorList().contains(BluetoothSensor.IDENTIFIER)) {
                    bluetoothEnabled = isBluetoothEnabled();
                }
                if (parsedESMDummyObject.getSensorList().contains(NotificationSensor.IDENTIFIER)) {
                    notificationAccessAllowed = isNotificationAccessAllowed();
                }
                if (bluetoothEnabled && notificationAccessAllowed) {
                    startSensorEvaluationService(parsedESMDummyObject);
                    if (parsedESMDummyObject.isVoluntary()) {
                        startActivity(new Intent(this, QuestionnaireActivity.class));
                        finish();
                    } else {
                        textView.setText("The linked questionnaire is not for voluntary use. You receive a notification if you can interact!");
                    }
                } else {
                    if ((!bluetoothEnabled && !notificationAccessAllowed)) {
                        textView.setText("Bluetooth and Notification access is not enabled! Please activate it!");
                        activateBluetoothButton.setVisibility(View.VISIBLE);
                        activateNotificationButton.setVisibility(View.VISIBLE);
                    } else if (!bluetoothEnabled) {
                        textView.setText("Bluetooth is not enabled! Please activate it!");
                        activateBluetoothButton.setVisibility(View.VISIBLE);
                    } else if (!notificationAccessAllowed) {
                        textView.setText("Notification access is not allowed! Please activate it!");
                        activateNotificationButton.setVisibility(View.VISIBLE);
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private boolean isNotificationAccessAllowed() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver,
                "enabled_notification_listeners");
        String packageName = getApplicationContext().getPackageName();
        if (enabledNotificationListeners == null
                || !enabledNotificationListeners.contains(packageName)) {
            return false;
        } else {
            return true;
        }

    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean validateXml(TextView textView) {
        boolean isValid = false;
        try {
            isValid = XMLValidator.validate("sdcard/de.kit.esmdummy/masterarbeit.xml",
                    "sdcard/de.kit.esmdummy/masterarbeit.xsd");
        } catch (IOException e) {
            textView.setText("XML not found. Please enter the code from the ESM-Server web to add the xml!");
            e.printStackTrace();
        } catch (SAXException e) {
            textView.setText("XML is not valid with XSD in de.kit.esmdummy folder on sdcard. Please use the ESM-Server to create XML-File!");
            e.printStackTrace();
        }
        return isValid;
    }

    /**
     * Initialize Counter on first startup
     */
    private void initializeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("de.kit.esmdummy", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //if notification counter doesn't exist then the DefaultValue Integer.MIN_VALUE comes back --> create it with Value 0
        if (sharedPreferences.getInt("notificationCounter", Integer.MIN_VALUE) == Integer.MIN_VALUE) {
            editor.putInt("notificationCounter", 0);
        }
        editor.commit();
    }

    private void writeXsdIfNotExist() {
        File file = new File("sdcard/de.kit.esmdummy/");
        File xsd = new File("sdcard/de.kit.esmdummy/masterarbeit.xsd");
        if (file.mkdir() && !xsd.exists()) {
            try {
                xsd.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream in = getResources().openRawResource(R.raw.masterarbeit);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(xsd);
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                } catch (IOException e) {
                } finally {
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSensorEvaluationService(ParsedESMDummyObject parsedESMDummyObject) {
        if (!isSensorEvaluationServiceRunning(SensorEvaluationService.class)) {
            Log.d("SERVICE", "NOT RUNNING");
            Intent intent = new Intent(this, SensorEvaluationService.class);
            startService(intent);
        } else {
            Log.d("SERVICE", "RUNNING");
        }
    }

    private boolean isSensorEvaluationServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void retrieveXML(View view) throws IOException {
        EditText codeInput = (EditText) findViewById(R.id.editText);
        String code = codeInput.getText().toString();
        if (!code.isEmpty()) {
            //get request to ESMAC server
            BufferedReader in = null;
            String data = null;
            String[] url = new String[1];
            url[0] = Global.restapi + code + ".xml";
            //url[0] = "http://192.168.10.143:8080/ESMServer/configurations/" + code + ".xml";
            XMLRetriever req = new XMLRetriever(new XMLInterface() {
                @Override
                public void processFinish() {
                    System.out.println("Restarting app");
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(i);
                }
            });
            try {
                req.execute(url);
            }
            catch (Exception e) {
                System.out.println("error " + e.toString());
            }
        }
    }

}
