/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import com.local.entities.ProjectDesdac;
import com.local.gui.JFrameDesdacTool;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.modcs.model.ProgressListener;
import org.modcs.tools.MercuryFacade;
import org.modcs.tools.spn.model.Definition;
import org.modcs.tools.spn.model.EDSPN;
import org.modcs.tools.spn.model.Place;
import org.modcs.tools.spn.model.RewardMeasure;
import org.modcs.tools.spn.parser.ParsedRewardMeasure;
import org.modcs.tools.spn.simulator.engine.ResultsStationary;
import org.modcs.tools.spn.simulator.engine.StationaryParameters;

/**
 *
 * @author iure
 */
public class ModelAvaliator {

    private static ModelAvaliator modelAvaliator;

    private EDSPN edspn;

    private JFrameDesdacTool desdacTool;

    private HashMap<String, Integer> strandartPlaces;

    private String path;

    Solution candidate;

    private int analisysNumber = 0;

    private ModelAvaliator(String path) {
        this.strandartPlaces = new HashMap<>();
        this.path = path;
        openEDSPN(path);
    }

    private ModelAvaliator() {

    }

    public static synchronized ModelAvaliator getInstance() {

        return modelAvaliator;

    }

    public static synchronized ModelAvaliator getInstance(String path) {
        if (modelAvaliator == null) {
            modelAvaliator = new ModelAvaliator(path);
        }
        return modelAvaliator;

    }

    public void changeMetric(String metricName, String metric) {
        ArrayList<RewardMeasure> measures = edspn.getRewardMeasures();
        for (RewardMeasure measure : measures) {
            if (measure.getName().equalsIgnoreCase(metricName)) {
                measure.setStringExpression(metric);
            }
        }

    }

    public void removeMetric(String metricName) {
        ArrayList<RewardMeasure> mes = edspn.getRewardMeasures();

        ArrayList<RewardMeasure> remove = new ArrayList<>();

        for (RewardMeasure me : mes) {
            if (me.getName().contains(metricName)) {
                remove.add(me);
            }
        }

        for (RewardMeasure remove1 : remove) {
            mes.remove(remove1);
        }

    }

    public void newMetric(String metricName, String metric) {

        Point2D point2D = edspn.getRewardMeasures().get(0).getPosition();
        RewardMeasure rewardMeasure = new RewardMeasure(metricName, metric, point2D);

        rewardMeasure.setValue("1.0");
        edspn.getRewardMeasures().add(rewardMeasure);
        edspn.setNumberRewardMeasures(edspn.getNumberRewardMeasures() + 1);
    }

    public void openEDSPN(String path) {

        desdacTool = new JFrameDesdacTool();

        File file = new File(path);

        try {
            desdacTool.openProject(file);

        } catch (Exception ex) {
            Logger.getLogger(ModelAvaliator.class.getName()).log(Level.SEVERE, null, ex);
        }

        ProjectDesdac projectDesdac = desdacTool.getProjectDesdac();

        edspn = projectDesdac.getAppSPN().getEdspn();

        ArrayList<Place> places = edspn.getPlaces();

        for (Place place : places) {

            strandartPlaces.put(place.getName(), place.getMarking());

        }

        //deb();
    }

    public void changeDefinition(String name, String value) {

        ArrayList<Definition> definitions = edspn.getDefinitions();
        for (Definition definition : definitions) {
            if (definition.getName().equals(name)) {
                definition.setValue(value);

            }

        }

    }

    public void UpdatePLaces(Solution newCandidate) {

        ArrayList<Place> places = edspn.getPlaces();

        for (Place place : places) {
            if (newCandidate.getDefinitions().containsKey(place.getInitialStringMarking())) {
                place.setMarking(newCandidate.getDefinitions().get(place.getInitialStringMarking()).getValue().intValue());

            }
        }

        ArrayList<Definition> definitions = edspn.getDefinitions();

        for (Definition definition : definitions) {
            if (newCandidate.getDefinitions().containsKey(definition.getName())) {
                definition.setValue(newCandidate.getDefinitions().get(definition.getName()).getValue().toString());
            }
        }

    }

    public void UpdatePLaces() {

        ArrayList<Place> places = edspn.getPlaces();

        for (Place place : places) {
            if (candidate.getDefinitions().containsKey(place.getInitialStringMarking())) {
                place.setMarking(candidate.getDefinitions().get(place.getInitialStringMarking()).getValue().intValue());
            } else if (strandartPlaces.containsKey(place.getName())) {
                place.setMarking(strandartPlaces.get(place.getName()));
            }

        }
    }

    public void setCandidate(Solution candidate) {
        this.candidate = candidate;
    }

    public void setAllDefinition(Solution candidate) {
        this.candidate = candidate;

        HashMap<String, Value> definitions = candidate.getDefinitions();

        for (Map.Entry<String, Value> entrySet : definitions.entrySet()) {
            String name = entrySet.getKey();
            Value value = entrySet.getValue();

            String valString;

            if (value.getPatern().getType() == TypeNames.INT || name.equals("nodeVM")) {
                valString = value.getValue().intValue() + "";
            } else {
                valString = value.getValue().doubleValue() + "";
            }

            changeDefinition(name, valString);

        }

        UpdatePLaces();

    }

    public HashMap<String, Double> stationarySolve() {

        return stationarySolve(true, 0.0000001, -1);
    }

    public String getDefESPNValue(String name) {

        ArrayList<Definition> definitions = edspn.getDefinitions();

        for (Definition definition : definitions) {
            if (definition.getName().equals(name)) {
                return definition.getValue();
            }
        }

        return null;

    }

    private void deb() {

        ArrayList<Place> places = edspn.getPlaces();

        for (Place place : places) {

            if (candidate.getDefinitions().containsKey(place.getInitialStringMarking())) {

                System.out.println(place.getInitialStringMarking() + " NEW " + candidate.getDefinitions().
                        get(place.getInitialStringMarking()).getValue() + " OLD " + place.getMarking() + " DEF "
                        + getDefESPNValue(place.getInitialStringMarking()));

                if (!getDefESPNValue(place.getInitialStringMarking()).contains("" + place.getMarking())) {
                    System.out.println("ER");
                }

            } else {
                System.out.println(place.getName() + "   " + place.getMarking());
            }

        }

        ArrayList<Definition> transitions = edspn.getDefinitions();

        for (Definition transition : transitions) {
            if (candidate.getDefinitions().containsKey(transition.getName())) {
                System.out.println(transition.getName() + " NEW " + candidate.getDefinitions().get(transition.getName()).getValue().toString()
                        + " OLD " + transition.getValue());

            }
        }
    }

    public HashMap<String, Double> stationarySimulationSolve(long time) {
        StationaryParameters parameters;

        double confidenceLevel = 0.95;
        double maxRelativeError = 0.1;
        int minFiringTransitions = 50;
        int warmup = 50;
        int run = 1000;
        long maxTimeMilliseconds = time*1000;
        int batchSize = 50000;

        parameters = new StationaryParameters(
                confidenceLevel, maxRelativeError, minFiringTransitions,
                warmup, run, maxTimeMilliseconds, false, batchSize, false);

        ArrayList<ResultsStationary> arr = desdacTool.stationaryAnalisys(edspn, parameters);
        HashMap<String, Double> result = new HashMap<>();

        for (int i = 0; i < arr.size(); i++) {
            result.put(edspn.getRewardMeasures().get(i).getName(), arr.get(i).getMeanValue());
            
           // System.out.println(edspn.getRewardMeasures().get(i).getName()+"  "+ arr.get(i).getMeanValue());

        }
       

        return result;
    }

    public HashMap<String, Double> stationarySolve(boolean isGausSeidelNumericalTechnique, double maxError, int maxIterations) {

        String id = null;

        analisysNumber++;

        // deb();
        MercuryFacade _facade = MercuryFacade.getInstance();

        try {
            ProgressListener listener = new ProgressListener() {
                @Override
                public void update(Object... object) {
                }

                @Override
                public void finish(Object... object) {
                }
            };

            EDSPN.setInstance(edspn);
            List<ParsedRewardMeasure> listR = null;

            listR = _facade.executeSPNStationaryAnalysis(edspn, isGausSeidelNumericalTechnique, maxError,
                    maxIterations, listener, listener, listener, false, "");

            HashMap<String, Double> result = new HashMap<>();

            for (int i = 0; i < listR.size(); i++) {

                result.put(edspn.getRewardMeasures().get(i).getName(), Double.parseDouble(listR.get(i).execute().print()));
                //System.out.println(edspn.getRewardMeasures().get(i).getName());
                // System.out.println(listR.get(i).getStringExpression());
                // System.out.println(Double.parseDouble(listR.get(i).execute().print()));

            }

            /*
             if (result.get("TotalInstances") < 1.1 && result.get("TP") > 0.08) {

             System.out.println("ERRO*********************************");

           
             throw new Exception();
             }
             */
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();

            ArrayList<Definition> defs = edspn.getDefinitions();

            for (Definition def : defs) {
                System.out.println(def.getName() + "  " + def.getValue());
            }

            throw new RuntimeException(exception.getMessage());
        }
    }

//    public void defaulSample() {
//
//        /*
//         HashMap<String, Value> def = new HashMap<>();
//
//         Value value = new Value();
//         value.setValue(9);
//         value.setPatern(new Type(null, 1, 2, TypeNames.INT, null));
//         def.put("ThresholdInstantiate", value);
//
//         value = new Value();
//         value.setPatern(new Type(null, 1, 2, TypeNames.INT, null));
//         value.setValue(1);
//         def.put("stepsize", value);
//         value = new Value();
//         value.setPatern(new Type(null, 1, 2, TypeNames.INT, null));
//         value.setValue(1);
//         def.put("ThresholdDestroy", value);
//         value = new Value();
//         value.setPatern(new Type(null, 1, 2, TypeNames.INT, null));
//         value.setValue(1.56337441774827);
//         def.put("IT", value);
//         value = new Value();
//         value.setPatern(new Type(null, 1, 2, TypeNames.DOUBLE, null));
//         value.setValue(1.869426995464767);
//         def.put("CT", value);
//         value = new Value();
//         value.setPatern(new Type(null, 1, 2, TypeNames.DOUBLE, null));
//         value.setValue(20);
//         def.put("MinServer", value);
//         Solution candidate = new Solution();
//                
//          
//        
//         candidate.setDefinitions(def);
//
//         setAllDefinition(candidate);
//
//         */
//        Type pattern = new Type("ThresholdInstantiate", 6, 10, TypeNames.INT, null);
//
//        Type pattern1 = new Type("stepsize", 1, 2, TypeNames.INT, null);
//
//        Type pattern2 = new Type("ThresholdDestroy", 1, 5, TypeNames.INT, null);
//
//        Type pattern3 = new Type("IT", 21, 30, TypeNames.DOUBLE, null);
//
//        Type pattern4 = new Type("CT", 16, 30, TypeNames.DOUBLE, null);
//
//        Type pattern5 = new Type("MinServer", 1, 10, TypeNames.INT, null);
//
//        ArrayList<Type> definitions = new ArrayList<>();
//
//        definitions.add(pattern);
//        definitions.add(pattern1);
//        definitions.add(pattern2);
//        definitions.add(pattern3);
//        definitions.add(pattern4);
//        definitions.add(pattern5);
//
//        GenerateGreedy generateGreedy = new GenerateGreedy();
//        Solution maxCandidate = generateGreedy.generateValue(definitions, 4);
//        setAllDefinition(maxCandidate);
//
//        HashMap<String, Double> metrics = stationarySolve();
//
//    }
    public int getAnalisysNumber() {
        return analisysNumber;
    }

    public void setAnalisysNumber(int analisysNumber) {
        this.analisysNumber = analisysNumber;
    }

    public JFrameDesdacTool getDesdacTool() {
        return desdacTool;
    }

    public void setDesdacTool(JFrameDesdacTool desdacTool) {
        this.desdacTool = desdacTool;
    }

    public void printPlaces() {
        ArrayList<Place> p = edspn.getPlaces();

        for (Place p1 : p) {
            System.out.println(p1.getName() + " mark " + p1.getMarking());
        }
    }

    public void printDefinition() {
        ArrayList<Definition> p = edspn.getDefinitions();

        for (Definition p1 : p) {
            System.out.println(p1.getName() + " " + p1.getValue());

        }

    }

    public void printResults(HashMap<String, Double> result) {

        for (Map.Entry<String, Double> entrySet : result.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();

            System.out.println(key + " " + value);

        }

    }

}
