package de.kit.esmac.log;

import android.content.SharedPreferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;

import de.kit.esmac.domain.screen.ScreenFragment;
import de.kit.esmac.domain.screen.question.AbstractQuestion;
import de.kit.sensorlibrary.sensor.AbstractSensorImpl;
import de.kit.sensorlibrary.sensor.SensorInterface;


/**
 * Created by Robert on 02.04.2015.
 */
public class LogFileWriter {
    private final List<ScreenFragment> screenFragments;
    Collection<AbstractSensorImpl> sensorInterfaces;
    String filePath;
    private BufferedWriter bufferedWriter;

    public LogFileWriter(Collection<AbstractSensorImpl> sensorInterfaces, List<ScreenFragment> screenFragments) {
        this.sensorInterfaces = sensorInterfaces;
        this.screenFragments = screenFragments;
    }

    /**
     * Create Log-File if not exists and write the header.
     * Every start of the Service a new File will created.
     */
    public void createAndInitLogFileIfNotExist() {
        filePath = "sdcard/de.kit.esmdummy/masterarbeit" + System.currentTimeMillis() + ".log";
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
                bufferedWriter.write("timeStamp;");
                for (SensorInterface sensorInterface : sensorInterfaces) {
                    for (String column : sensorInterface.getLogColumns()) {
                        bufferedWriter.write(column + ";");
                    }
                }
                for (ScreenFragment screenFragment : screenFragments) {
                    for (AbstractQuestion abstractQuestion : screenFragment.getQuestions()) {
                        bufferedWriter.write(abstractQuestion.getName() + ";");
                    }
                }
                bufferedWriter.write("isNotification;reason;notificationCounter;lastNotification;isSubmit;completionTime;isVoluntary");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Log one line with Parameters
     *
     * @param allAnswerString   all Answers from Question-UI
     * @param isNotification    was notified
     * @param reason            rule which was triggered
     * @param isSubmit          was submitted
     * @param delay             delay from opening activity to submit
     * @param isVoluntary       was voluntary submit
     * @param sharedPreferences the shared Preferences for accessing last Notification Time and Counter
     */
    public void writeLog(String allAnswerString, boolean isNotification, String reason, boolean isSubmit, long delay, boolean isVoluntary, SharedPreferences sharedPreferences) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(filePath), true));
            bufferedWriter.write(System.currentTimeMillis() + ";");
            for (SensorInterface sensorInterface : sensorInterfaces) {
                for (String value : sensorInterface.getLog()) {
                    bufferedWriter.write(value + ";");
                }
            }
            if (!allAnswerString.equals("")) {
                bufferedWriter.write(allAnswerString);
            } else {
                for (ScreenFragment screenFragment : screenFragments) {
                    for (AbstractQuestion abstractQuestion : screenFragment.getQuestions()) {
                        bufferedWriter.write(abstractQuestion.getAnswer() + ";");
                    }
                }
            }
            int notificationCounter = sharedPreferences.getInt("notificationCounter", 0);
            long executeTime = sharedPreferences.getLong("lastNotificationTime", 0);

            bufferedWriter.write(isNotification + ";" + reason + ";" + notificationCounter + ";"
                    + executeTime + ";" + isSubmit + ";" + delay + ";" + isVoluntary + ";");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
