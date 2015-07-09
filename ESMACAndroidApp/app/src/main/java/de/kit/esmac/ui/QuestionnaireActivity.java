package de.kit.esmac.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import de.kit.esmac.domain.ParsedESMDummyObject;
import de.kit.esmac.domain.screen.ScreenFragment;
import de.kit.esmac.R;
import de.kit.esmac.domain.screen.question.AbstractQuestion;
import de.kit.esmac.xml.XMLParser;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;
import mf.javax.xml.parsers.ParserConfigurationException;


public class QuestionnaireActivity extends Activity {

    private static final String TAG = QuestionnaireActivity.class.getName();
    HashMap<String, AbstractSensorImpl> sensorInterfaceList;
    private int selectedScreenId = 0;
    private List<ScreenFragment> fragmentList;
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        final RelativeLayout item = (RelativeLayout) findViewById(R.id.substituteRelative);
        XMLParser test = new XMLParser(this);
        try {
            final ParsedESMDummyObject parsedESMDummyObject = test.parseXmlDocument(new File(
                    "sdcard/de.kit.esmdummy/masterarbeit.xml"));
            drawMainUI(item, parsedESMDummyObject);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void drawMainUI(RelativeLayout item, final ParsedESMDummyObject parsedESMDummyObject) {
        fragmentList = parsedESMDummyObject.getFragments();
        addAllFragmentsAndShowFirst(item, fragmentList);
        final Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setEnabled(false);
        if (fragmentList.size() - 1 == selectedScreenId) {
            nextButton.setText("submit");
        }
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedScreenId + 1 < fragmentList.size()) {
                    selectedScreenId++;
                    changeVisibleFragment(fragmentList, selectedScreenId - 1, selectedScreenId);
                    enableButtons(nextButton);
                } else {
                    long delay = System.currentTimeMillis() - startTime;
                    submitAnswers(fragmentList, delay);
                    Toast toast = Toast.makeText(getApplicationContext(), "Thanks for answering!", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }
        });
        final Button logcatButton = (Button) findViewById(R.id.logcatButton);
        logcatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
                            Environment.getExternalStorageDirectory().toString()
                                    + "/log.log")));
                    Process logcat = Runtime.getRuntime().exec("logcat -v thread -d");

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(logcat.getInputStream()));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                    writer.close();
                    parsedESMDummyObject.getNotification().createNotification(getApplicationContext());
                } catch (IOException e) {
                } finally {

                }
            }
        });
        //logcatButton.setVisibility(View.GONE);
    }


    private void addAllFragmentsAndShowFirst(RelativeLayout item, List<ScreenFragment> fragmentList) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (ScreenFragment screenFragment : fragmentList) {
            ft.add(item.getId(), screenFragment);
            ft.hide(screenFragment);
        }
        ft.show(fragmentList.get(selectedScreenId));
        ft.commit();
    }
    // IntentFilter filter = new IntentFilter("de.kit.contextprovider.screenintent");
    // registerReceiver(tReceiver, filter);

    // ContentResolver cr = getContentResolver();
    // Cursor cursor = cr.query(Uri
    // .parse("content://de.kit.kitprofiler.contextsearch"), new String[] {
    // "Time", "Location", "Weather", "Notification"
    // }, null,
    // null, null);
    // while (cursor.moveToNext()) {
    // for (int i = 0; i < cursor.getColumnCount(); i++) {
    // System.out.println(cursor.getString(i));
    // }
    // }


    // }

    public void enableButtons(final Button nextButton) {
        if ((selectedScreenId == (fragmentList.size() - 1))) {
            nextButton.setText("submit");
            nextButton.setEnabled(getActiveFragment().allQuestionsAnswered());
        } else {
            nextButton.setText("next");
            nextButton.setEnabled(getActiveFragment().allQuestionsAnswered());
        }
    }

    private void submitAnswers(List<ScreenFragment> fragmentList, long delay) {
        String allAnswerStrings = "";
        for (ScreenFragment screenFragment : fragmentList) {
            for (AbstractQuestion question : screenFragment.getQuestions()) {
                allAnswerStrings += question.getAnswer() + ";";
            }
        }
        Intent intent = new Intent("de.kit.esmdummy.log");
        intent.putExtra("allAnswers", allAnswerStrings);
        intent.putExtra("delay", delay);
        sendBroadcast(intent);
    }

    private void changeVisibleFragment(final List<ScreenFragment> fragmentList,
                                       int hideFragmentId,
                                       int showFragmentId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(fragmentList.get(hideFragmentId));
        ft.show(fragmentList.get(showFragmentId));
        ft.commit();
    }

    public boolean isLastFragment() {
        return selectedScreenId == (fragmentList.size() - 1);
    }

    public ScreenFragment getActiveFragment() {
        return fragmentList.get(selectedScreenId);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        /**
         * TODO: Vorerst so implementiert, da bei einem wiederherstellen die Anwendung sonst crashed
         * --> entweder Parceable in Fragen implementieren oder XML festablegen
         */
        //super.onSaveInstanceState(outState);
    }
}
