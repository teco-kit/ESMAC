package de.kit.esmac.domain;

import java.util.List;

import de.kit.esmac.domain.notification.Notification;
import de.kit.esmac.domain.screen.ScreenFragment;
import de.kit.esmac.domain.rule.Rule;

/**
 * Created by Robert on 06.02.2015.
 */
public class ParsedESMDummyObject {
    private List<ScreenFragment> fragments;
    private Notification notification;
    private List<Rule> rules;
    private List<String> sensorList;
    private boolean voluntary;


    public ParsedESMDummyObject(List<ScreenFragment> fragments, List<Rule> rules, List<String> sensorList, Notification notification, boolean voluntary) {

        this.fragments = fragments;
        this.rules = rules;
        this.sensorList = sensorList;
        this.notification = notification;
        this.voluntary = voluntary;
    }

    public List<ScreenFragment> getFragments() {
        return fragments;
    }

    public Notification getNotification() {
        return notification;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<String> getSensorList() {
        return sensorList;
    }

    public boolean isVoluntary() {
        return voluntary;
    }
}
