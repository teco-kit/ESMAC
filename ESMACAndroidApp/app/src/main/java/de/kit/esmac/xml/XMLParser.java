package de.kit.esmac.xml;

import android.content.Context;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.kit.esmac.domain.ParsedESMDummyObject;
import de.kit.esmac.domain.notification.Notification;
import de.kit.esmac.domain.rule.Rule;
import de.kit.esmac.domain.rule.SensorConjunction;
import de.kit.esmac.domain.rule.SensorExpression;
import de.kit.esmac.domain.screen.ScreenFragment;
import de.kit.esmac.domain.screen.question.AbstractQuestion;
import de.kit.esmac.domain.screen.question.ClosedCheckboxQuestion;
import de.kit.esmac.domain.screen.question.ClosedQuestion;
import de.kit.esmac.domain.screen.question.ClosedRadiobuttonQuestionConditional;
import de.kit.esmac.domain.screen.question.LikertQuestionConditional;
import de.kit.esmac.domain.screen.question.OpenQuestion;
import de.kit.esmac.domain.screen.question.VisualAnalogQuestion;
import de.kit.esmac.domain.screen.question.VisualAnalogQuestionConditional;
import de.kit.esmac.domain.sensor.AccelerometerSensor;
import de.kit.esmac.domain.sensor.AmbientLightSensor;
import de.kit.esmac.domain.sensor.GeofenceSensor;
import de.kit.esmac.domain.sensor.NotificationSensor;
import de.kit.esmac.domain.sensor.ScreenActivitySensor;
import de.kit.esmac.domain.sensor.TelephoneSensor;
import de.kit.esmac.domain.sensor.TimeSensor;
import de.kit.esmac.domain.sensor.WeatherSensor;
import de.kit.esmac.domain.screen.question.ClosedCheckboxQuestionConditional;
import de.kit.esmac.domain.screen.question.ClosedRadiobuttonQuestion;
import de.kit.esmac.domain.screen.question.LikertQuestion;
import de.kit.esmac.domain.sensor.BluetoothSensor;
import de.kit.sensorlibrary.sensor.ambientlightsensor.LightSensor;
import de.kit.sensorlibrary.sensor.locationsensor.LocationSensor;
import de.kit.sensorlibrary.sensor.useractivitysensor.UserActivitySensor;
import mf.javax.xml.parsers.DocumentBuilder;
import mf.javax.xml.parsers.DocumentBuilderFactory;
import mf.javax.xml.parsers.ParserConfigurationException;
import mf.org.apache.xerces.dom.DeferredTextImpl;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;

public class XMLParser {
    private Context context;
    private List<String> sensorString;

    public XMLParser(Context context) {
        this.context = context;
        this.sensorString = new ArrayList<String>();
    }

    //TODO: Strings sollten noch den jeweiligen Klassen zugeordnet werden
    //TODO: evtl schauen ob JAXB noch einsetzbar ist
    //TODO: Refactor um SpaghettiCode zu minimieren
    public ParsedESMDummyObject parseXmlDocument(File xmlDocument)
            throws ParserConfigurationException,
            SAXException, IOException {
        List<ScreenFragment> screens = new ArrayList<ScreenFragment>();
        List<Rule> rules = new ArrayList<Rule>();
        Notification notification = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Element document = builder.parse(xmlDocument).getDocumentElement();
        boolean voluntary = new Boolean(document.getAttribute("voluntary"));
        NodeList root = document.getChildNodes();
        for (int i = 0; i < root.getLength(); i++) {
            Node item = root.item(i);
            if (!(item instanceof DeferredTextImpl)) {
                switch (item.getNodeName()) {
                    case "questions":
                        screens = parseScreens(item);
                        break;
                    case "rules":
                        rules = parseRules(item);
                        break;
                    case "sensors":
                        parseSensors(item);
                        break;
                    case "notification":
                        notification = parseNotification(item);
                        break;
                }
            }

        }
        return new ParsedESMDummyObject(screens, rules, sensorString, notification, voluntary);
    }

    private List<ScreenFragment> parseScreens(Node item) {
        List<ScreenFragment> screens = new ArrayList<ScreenFragment>();
        NodeList screensNodeList = item.getChildNodes();
        for (int j = 0; j < screensNodeList.getLength(); j++) {
            Node screen = screensNodeList.item(j);
            if (!(screen instanceof DeferredTextImpl)) {
                ScreenFragment screenFragment = new ScreenFragment();
                screenFragment.setQuestions(parseQuestions(screen));
                screens.add(screenFragment);
            }
        }
        return screens;
    }

    private String getAttributeForName(Node item, String name) {
        return item.getAttributes().getNamedItem(name).getNodeValue();
    }

    private Notification parseNotification(Node item) {
        boolean ring = new Boolean(getAttributeForName(item, "ring"));
        boolean vibrate = new Boolean(getAttributeForName(item, "vibrate"));
        boolean notificationLed = new Boolean(getAttributeForName(item, "notificationLed"));
        int maxNotifications = new Integer(getAttributeForName(item, "maxNotifications"));
        long cooldownTime = new Long(getAttributeForName(item, "cooldownTime"));
        return new Notification(vibrate, ring, notificationLed, maxNotifications, cooldownTime);
    }

    private void parseSensors(Node item) {
        NodeList sensorNodeList = item.getChildNodes();
        for (int i = 0; i < sensorNodeList.getLength(); i++) {
            Node sensorNode = sensorNodeList.item(i);
            if (!(sensorNode instanceof DeferredTextImpl)) {
                addSensorString(sensorNode.getTextContent());
            }
        }
    }

    private List<Rule> parseRules(Node ruleRoot) {
        List<Rule> rules = new ArrayList<Rule>();
        NodeList ruleNodes = ruleRoot.getChildNodes();
        for (int i = 0; i < ruleNodes.getLength(); i++) {
            Node item = ruleNodes.item(i);
            if (!(item instanceof DeferredTextImpl)) {
                rules.add(parseRule(item));
            }
        }
        return rules;
    }

    private Rule parseRule(Node item) {
        Rule rule = new Rule();
        Node expression = getChildNodeWithName(item.getChildNodes(), "sensorexpression");
        if (expression != null) {
            rule.setExpression(parseSensorExpression(expression));
        } else {
            rule.setConjunction(parseSensorConjunction(getChildNodeWithName(item.getChildNodes(), "sensorconjunction")));
        }
        return rule;
    }

    private Node getChildNodeWithName(NodeList list, String name) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals(name)) {
                return list.item(i);
            }
        }
        return null;
    }

    private Node getNextNamedChildNode(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            Node nextChild = list.item(i);
            if (!(nextChild instanceof DeferredTextImpl)) {
                return nextChild;
            }
        }
        return null;
    }

    private List<Node> getClearedNodeList(NodeList list) {
        List<Node> clearedList = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            Node nextChild = list.item(i);
            if (!(nextChild instanceof DeferredTextImpl)) {
                clearedList.add(nextChild);
            }
        }
        return clearedList;
    }


    private SensorConjunction parseSensorConjunction(Node item) {
        SensorConjunction sensorConjunction = new SensorConjunction();
        List<Node> conjunctionNodes = getClearedNodeList(item.getChildNodes());
        sensorConjunction.setFirstSensorExpression(parseSensorExpression(conjunctionNodes.get(0)));
        sensorConjunction.setConjunction(conjunctionNodes.get(1).getTextContent());
        Node conjunctionOrExpression = conjunctionNodes.get(2);
        if (conjunctionOrExpression.getNodeName() == "sensorconjunction") {
            sensorConjunction.setSensorConjunction(parseSensorConjunction(conjunctionOrExpression));
        } else {
            sensorConjunction.setSecondSensorExpression(parseSensorExpression(conjunctionOrExpression));
        }
        return sensorConjunction;
    }

    private SensorExpression parseSensorExpression(Node firstChild) {
        Node sensor = getNextNamedChildNode(firstChild.getChildNodes());
        SensorExpression expression = new SensorExpression();
        Node sensorKey;
        switch (sensor.getNodeName()) {
            case de.kit.sensorlibrary.sensor.weathersensor.WeatherSensor.IDENTIFIER:
                WeatherSensor weatherSensor = new WeatherSensor();
                sensorKey = getNextNamedChildNode(sensor.getChildNodes());
                weatherSensor.setKey(sensorKey.getNodeName());
                weatherSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                weatherSensor.setValue(getAttributeForName(sensorKey, "value"));
                expression.setSensor(weatherSensor);
                addSensorString(sensor.getNodeName());
                addSensorString(LocationSensor.IDENTIFIER);
                break;
            case de.kit.sensorlibrary.sensor.geofencingsensor.GeofenceSensor.IDENTIFIER:
                GeofenceSensor geofenceSensor = new GeofenceSensor();
                geofenceSensor.setOperator(getAttributeForName(sensor, "operator"));
                geofenceSensor.setLatitudeValue(new Double(getAttributeForName(sensor, "latitude")));
                geofenceSensor.setLongitudeValue(new Double(getAttributeForName(sensor, "longitude")));
                geofenceSensor.setRadius(new Float(getAttributeForName(sensor, "radius")));
                geofenceSensor.setName(getAttributeForName(sensor, "name"));
                expression.setSensor(geofenceSensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.timesensor.TimeSensor.IDENTIFIER:
                TimeSensor timeSensor = new TimeSensor();
                sensorKey = getNextNamedChildNode(sensor.getChildNodes());
                timeSensor.setKey(sensorKey.getNodeName());
                if (sensorKey.getNodeName().equals("timeRange")) {
                    timeSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    timeSensor.setRangeTime(new String[]{getAttributeForName(sensorKey, "beginValue")
                            , getAttributeForName(sensorKey, "endValue")});
                } else if (sensorKey.getNodeName().equals("weekDay")) {
                    timeSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    timeSensor.setTime(parseChildsToSeperatedString(sensorKey.getChildNodes()));
                } else if (sensorKey.getNodeName().equals("random")) {
                    timeSensor.setRangeTime(new String[]{getAttributeForName(sensorKey, "beginValue")
                            , getAttributeForName(sensorKey, "endValue")});
                    timeSensor.setCount(new Integer(getAttributeForName(sensorKey, "count")));
                } else if (sensorKey.getNodeName().equals("timeInterval")) {
                    timeSensor.setRangeTime(new String[]{getAttributeForName(sensorKey, "beginValue")
                            , getAttributeForName(sensorKey, "endValue")});
                    timeSensor.setInterval(new Integer(getAttributeForName(sensorKey, "interval")));
                } else {
                    timeSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    timeSensor.setTime(getAttributeForName(sensorKey, "value"));
                }
                expression.setSensor(timeSensor);
                addSensorString(sensor.getNodeName());
                break;
            case UserActivitySensor.IDENTIFIER:
                de.kit.esmac.domain.sensor.UserActivitySensor userUserActivitySensor = new de.kit.esmac.domain.sensor.UserActivitySensor();
                userUserActivitySensor.setOperator(getAttributeForName(sensor, "operator"));
                userUserActivitySensor.setValue(parseChildsToSeperatedString(sensor.getChildNodes()));
                expression.setSensor(userUserActivitySensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.bluetoothsensor.BluetoothSensor.IDENTIFIER:
                BluetoothSensor bluetoothSensor = new BluetoothSensor();
                sensorKey = getNextNamedChildNode(sensor.getChildNodes());
                bluetoothSensor.setKey(sensorKey.getNodeName());
                if (sensorKey.getNodeName().equals("count")) {
                    bluetoothSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    bluetoothSensor.setValue(getAttributeForName(sensorKey, "value"));
                } else {
                    bluetoothSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    bluetoothSensor.setValue(parseChildsToSeperatedString(sensorKey.getChildNodes()));
                }
                expression.setSensor(bluetoothSensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.notificationsensor.NotificationSensor.IDENTIFIER:
                NotificationSensor notificationSensor = new NotificationSensor();
                sensorKey = getNextNamedChildNode(sensor.getChildNodes());
                notificationSensor.setKey(sensorKey.getNodeName());
                notificationSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                if (sensorKey.getNodeName().equals("count")) {
                    notificationSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    notificationSensor.setValue(getAttributeForName(sensorKey, "value"));
                } else {
                    notificationSensor.setOperator(getAttributeForName(sensorKey, "operator"));
                    notificationSensor.setValue(parseChildsToSeperatedString(sensorKey.getChildNodes()));
                }
                expression.setSensor(notificationSensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.screenactivitysensor.ScreenActivitySensor.IDENTIFIER:
                ScreenActivitySensor screenActivitySensor = new ScreenActivitySensor();
                screenActivitySensor.setOperator(getAttributeForName(sensor, "operator"));
                screenActivitySensor.setValue(getAttributeForName(sensor, "value"));
                expression.setSensor(screenActivitySensor);
                addSensorString(sensor.getNodeName());
                break;
            case LightSensor.IDENTIFIER:
                AmbientLightSensor ambientLightSensor = new AmbientLightSensor();
                ambientLightSensor.setOperator(getAttributeForName(sensor, "operator"));
                ambientLightSensor.setValue(new Float(getAttributeForName(sensor, "value")));
                expression.setSensor(ambientLightSensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.telephonesensor.TelephoneSensor.IDENTIFIER:
                TelephoneSensor telephoneSensor = new TelephoneSensor();
                telephoneSensor.setOperator(getAttributeForName(sensor, "operator"));
                telephoneSensor.setValue(parseChildsToSeperatedString(sensor.getChildNodes()));
                expression.setSensor(telephoneSensor);
                addSensorString(sensor.getNodeName());
                break;
            case de.kit.sensorlibrary.sensor.accelerometersensor.AccelerometerSensor.IDENTIFIER:
                AccelerometerSensor accelerometerSensor = new AccelerometerSensor();
                accelerometerSensor.setOperator(getAttributeForName(sensor, "operator"));
                accelerometerSensor.setX(getFloatOrNan(sensor, "x"));
                accelerometerSensor.setY(getFloatOrNan(sensor, "y"));
                accelerometerSensor.setZ(getFloatOrNan(sensor, "z"));
                accelerometerSensor.setAcceleration(getFloatOrNan(sensor, "acceleration"));
                expression.setSensor(accelerometerSensor);
                addSensorString(sensor.getNodeName());
                break;
        }
        return expression;
    }

    private float getFloatOrNan(Node sensor, String name) {
        Node namedItem = sensor.getAttributes().getNamedItem(name);
        if (namedItem != null) {
            return new Float(namedItem.getNodeValue());
        } else {
            return Float.NaN;
        }

    }

    private String parseChildsToSeperatedString(NodeList childNodes) {
        String child = "";
        List<Node> clearedNodes = getClearedNodeList(childNodes);
        for (int i = 0; i < clearedNodes.size(); i++) {
            if (i != clearedNodes.size() - 1) {
                child += childNodes.item(i).getTextContent() + ",";
            } else {
                child += childNodes.item(i).getTextContent();
            }
        }
        return child;
    }

    private void addSensorString(String nodeName) {
        if (!sensorString.contains(nodeName)) {
            sensorString.add(nodeName);
        }
    }

    private List<AbstractQuestion> parseQuestions(Node questionRoot) {
        List<AbstractQuestion> listQuestions = new ArrayList<AbstractQuestion>();
        NodeList questions = questionRoot.getChildNodes();
        for (int i = 0; i < questions.getLength(); i++) {
            Node item = questions.item(i);
            if (!(item instanceof DeferredTextImpl)) {
                listQuestions.add(parseQuestion(item));
            }
        }
        return listQuestions;

    }

    private AbstractQuestion parseQuestion(Node item) {
        switch (item.getNodeName()) {
            case "openQuestion":
                OpenQuestion open = new OpenQuestion();
                open.setName(getAttributeForName(item, "name"));
                open.setInputType(getAttributeForName(item, "inputType"));
                return (open);
            case "closedRadiobuttonQuestion":
                ClosedRadiobuttonQuestion closedRadiobuttonQuestion = new ClosedRadiobuttonQuestion();
                closedRadiobuttonQuestion.setName(getAttributeForName(item, "name"));
                parseClosedQuestionAnswers(item, closedRadiobuttonQuestion);
                return (closedRadiobuttonQuestion);
            case "closedCheckboxQuestion":
                ClosedCheckboxQuestion closedCheckboxQuestion = new ClosedCheckboxQuestion();
                closedCheckboxQuestion.setName(getAttributeForName(item, "name"));
                parseClosedQuestionAnswers(item, closedCheckboxQuestion);
                return (closedCheckboxQuestion);
            case "visualAnalogQuestion":
                VisualAnalogQuestion visualAnalogQuestion = new VisualAnalogQuestion();
                setVisualAnalogValues(item, visualAnalogQuestion);
                return (visualAnalogQuestion);
            case "likertQuestion":
                LikertQuestion likertQuestion = new LikertQuestion();
                setLikertValues(item, likertQuestion);
                return (likertQuestion);
            case "conditionalQuestion":
                return parseConditionalQuestion(item);


        }
        return null;
    }

    private AbstractQuestion parseConditionalQuestion(Node item) {
        List<Node> clearedList = getClearedNodeList(item.getChildNodes());
        Node conditional = clearedList.get(0);
        Node conditioned = clearedList.get(1);
        switch (conditional.getNodeName()) {
            case "visualAnalogQuestionConditional":
                VisualAnalogQuestionConditional visualAnalogQuestionConditional = new
                        VisualAnalogQuestionConditional();
                setVisualAnalogValues(conditional, visualAnalogQuestionConditional);
                visualAnalogQuestionConditional.setRequiredValue(new Integer(getAttributeForName(conditional, "requiredValue")));
                visualAnalogQuestionConditional.setConditionedQuestion(parseQuestion(conditioned));
                return visualAnalogQuestionConditional;
            case "likertQuestionConditional":
                LikertQuestionConditional likertQuestionConditional = new LikertQuestionConditional();
                setLikertValues(conditional, likertQuestionConditional);
                likertQuestionConditional.setRequiredBound(new Integer(getAttributeForName(conditional, "requiredBound")));
                likertQuestionConditional.setConditionedQuestion(parseQuestion(conditioned));
                return likertQuestionConditional;
            case "closedRadiobuttonQuestionConditional":
                ClosedRadiobuttonQuestionConditional closedRadiobuttonQuestionConditional = new ClosedRadiobuttonQuestionConditional();
                closedRadiobuttonQuestionConditional.setName(getAttributeForName(conditional, "name"));
                parseClosedQuestionAnswers(conditional, closedRadiobuttonQuestionConditional);
                closedRadiobuttonQuestionConditional.setRequiredAnswer(getAttributeForName(conditional, "requiredAnswer"));
                closedRadiobuttonQuestionConditional.setConditionedQuestion(parseQuestion(conditioned));
                return closedRadiobuttonQuestionConditional;
            case "closedCheckboxQuestionConditional":
                ClosedCheckboxQuestionConditional closedCheckboxQuestionConditional =
                        new ClosedCheckboxQuestionConditional();
                closedCheckboxQuestionConditional.setName(getAttributeForName(conditional, "name"));
                parseClosedConditionedQuestionAnswers(conditional, closedCheckboxQuestionConditional);
                closedCheckboxQuestionConditional.setConditionedQuestion(parseQuestion(conditioned));
                return closedCheckboxQuestionConditional;
            default:
                return null;
        }
    }

    private void setLikertValues(Node item, LikertQuestion likertQuestion) {
        likertQuestion.setName(getAttributeForName(item, "name"));
        likertQuestion.setMinDescription(getAttributeForName(item, "minDescription"));
        likertQuestion.setMaxDescription(getAttributeForName(item, "maxDescription"));
        likertQuestion.setMinBound(new Integer(getAttributeForName(item, "minBound")));
        likertQuestion.setMaxBound(new Integer(getAttributeForName(item, "maxBound")));
    }

    private void setVisualAnalogValues(Node item, VisualAnalogQuestion visualAnalogQuestion) {

        visualAnalogQuestion.setName(getAttributeForName(item, "name"));
        visualAnalogQuestion.setPointer(new Integer(getAttributeForName(item, "pointer")));
        visualAnalogQuestion.setMinDescription(getAttributeForName(item, "minDescription"));
        visualAnalogQuestion.setMaxDescription(getAttributeForName(item, "maxDescription"));
        visualAnalogQuestion.setMinBound(new Integer(getAttributeForName(item, "minBound")));
        visualAnalogQuestion.setMaxBound(new Integer(getAttributeForName(item, "maxBound")));
    }

    private void parseClosedQuestionAnswers(Node item,
                                            ClosedQuestion closedQuestion) {
        NodeList answers = item.getChildNodes();
        for (int j = 0; j < answers.getLength(); j++) {
            Node answer = answers.item(j);
            if (!(answer instanceof DeferredTextImpl)) {
                closedQuestion.addAnswerToAnswerList(answer.getTextContent());
            }
        }
    }

    private void parseClosedConditionedQuestionAnswers(Node item,
                                                       ClosedCheckboxQuestionConditional closedQuestion) {
        NodeList answers = item.getChildNodes();
        for (int j = 0; j < answers.getLength(); j++) {
            Node answer = answers.item(j);
            if (!(answer instanceof DeferredTextImpl)) {
                closedQuestion.addAnswerToAnswerList(answer.getTextContent());
                closedQuestion.addRequiredAnswer(new Boolean(getAttributeForName(answer, "required")));
            }
        }
    }
}
