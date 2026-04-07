/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author iure
 */
public class Solution {

    private HashMap<String, Double> metrics;

    private HashMap<String, Value> definitions;

    private ArrayList<Type> patters;

    private double cost;

    private double lambdaVM = 1.0 / 2880.0;

    private double lambdaPC = 1.0 / 8760.0;

    private double muPC = 1.0;

    private double muVM = 1.0;

    public Solution(ArrayList<Type> patters) {
        this.patters = patters;
        this.metrics = new HashMap<String, Double>();
        this.definitions = new HashMap<String, Value>();
    }

    public ArrayList<Type> getPatters() {
        return patters;
    }

    public void setPatters(ArrayList<Type> patters) {
        this.patters = patters;
    }

    public HashMap<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(HashMap<String, Double> metrics) {
        this.metrics = metrics;
    }

    public HashMap<String, Value> getDefinitions() {
        return definitions;
    }

    public HashMap<String, Value> getRealDefinitions() {
        return definitions;
    }

    public boolean isValid() {


        if (getResponseTime() < Utils.threshould) {
        System.out.println("getResponseTime(): " + getResponseTime());
        System.out.println("Utils.threshould: " + Utils.threshould);
            //System.out.println("true");
            return true;
        } else {
            return false;
        }
    }

    public void setDefinitions(HashMap<String, Value> definitions) {
        this.definitions = definitions;
    }

    public double getMetricCost() {
        HashMap<String, Double> allmetrics = getMetrics();
        double result = 0.0;
        for (Map.Entry<String, Double> entrySet : allmetrics.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            if (key.contains("CostE")) {
                if (value < 0) {
                    value = 0.0;
                }
                result += value;
            }
        }
        return result;
    }

    public double getCost() {

        if (cost == Double.MAX_VALUE) {
            return cost;
        }

        double infraCost = definitions.get("sizeInfra").getValue().intValue() * Utils.price;
        
        double norminfra= (infraCost-1*Utils.price)/(10*Utils.price-1*Utils.price);
        
        double respTime = getResponseTime();
        
        
        double normrespTime=(respTime-0.0)/(Utils.threshould-0.0);
        
        
        double minincoa=1.999077351;
        double maxincoa=79.90074193;
        
        double coa = coa();
        //System.out.println("coa "+ (coa));
        
        coa=1-(coa-minincoa)/(maxincoa-minincoa);
        
        

        this.cost = Math.sqrt(Math.pow(coa, 2) + Math.pow(normrespTime, 2) + Math.pow(norminfra, 2));
//        System.out.println("coa "+ (coa()));
//        System.out.println("maxincoa "+ (minincoa));
//        System.out.println("incoa "+coa);
//        System.out.println("jnormrespTime "+normrespTime);
//        System.out.println("norminfra "+norminfra);
        
        
       // System.out.println("jamilsongrasp.Solution.getCost()"+this.cost);
        
        return cost;
    }

    public double VMs(int k, int n) {

        double a = (Utils.fact(n) / Utils.fact(n - k)) * Math.pow(muVM, n - k) * Math.pow(lambdaVM, k);

        double b = 0.0;

        for (int i = 0; i <= n; i++) {
            b = b + (Utils.fact(n) / Utils.fact(i)) * Math.pow(lambdaVM, n - i) * Math.pow(muVM, i);
        }

        return a / b;
    }

    public double disp(int j, int p) {

        double result = 0.0;
        for (int i = j; i <= p; i++) {
            result = result + Utils.binomial(p, i) * Math.pow((muPC / (muPC + lambdaPC)), i) * Math.pow((1 - (muPC / (muPC + lambdaPC))), p - i);
        }

        return result;
    }

    public int getServers() {
        int servers = definitions.get("sizeInfra").getValue().intValue();
        return servers;
    }

    public int getVMs() {

        int large = definitions.get("Large").getValue().intValue() / Utils.numLargeResource;
        int medium = definitions.get("Medium").getValue().intValue() / Utils.numMediumResource;
        int small = definitions.get("Small").getValue().intValue() / Utils.numSmallResouce;

        return large + small + medium;
    }

    public double coa() {

        int numVms = getVMs();

        int numServers = getServers();

        //System.out.println("N VM"+numVms+" n SER "+numServers);
        double result = 0.0;
        for (int k = 0; k <= numVms; k++) {
            result = result + Math.pow((muPC / (muPC + lambdaPC)), numServers) * VMs(k, numVms) * (numVms - k);
        }

        for (int j = 1; j <= (numServers - 1); j++) {
            for (int k = 0; k <= (2 * j); k++) {
                result = result + ((disp(j, numServers) - disp(j + 1, numServers)) * (VMs(k, 2 * j)) * (2 * j - k));
            }
        }

        //return result / numVms;
        return result;
    }

    public double getResponseTime() {
        //tp large
//        double tpLarge = metrics.get("TP7") + metrics.get("TP8") + metrics.get("TP9");
//        double tpMedium = metrics.get("TP11") + metrics.get("TP12") + metrics.get("TP13");
//        double tpSmall = metrics.get("TP15") + metrics.get("TP16") + metrics.get("TP17");
//
//        double eLarge = metrics.get("EP7") + metrics.get("EP8") + metrics.get("EP9");
//        double eMedium = metrics.get("EP11") + metrics.get("EP12") + metrics.get("EP13");
//        double eSmall = metrics.get("E15") + metrics.get("E16") + metrics.get("E17");
//
//        double rtLarge = 0.0;
//        
//        double numTypeVM=0.0;
//
//        if (tpLarge != 0.0) {
//            rtLarge = eLarge / tpLarge;
//            numTypeVM+=1.0;
//        }
//
//        double rtMedium = 0;
//        if (tpMedium != 0.0) {
//            rtMedium = eMedium / tpMedium;
//            numTypeVM+=1.0;
//        }
//        double rtSmall = 0.0;
//        if (tpSmall != 0.0) {
//            rtSmall = eSmall / tpSmall;
//            numTypeVM+=1.0;
//        }
//
//        double result = (rtLarge + rtMedium + rtSmall) / numTypeVM;
        //System.out.println("NUm vm =" + numTypeVM);
       // System.out.println("rtLarge =" + rtLarge);
      //  System.out.println("rtMedium=" + rtMedium);
     //   System.out.println("rtSmall =" + rtSmall);
//        if (Double.isNaN(result)) {
//            System.out.println("jamilsongrasp.Solution.getResponseTime()");
//        }

        double rt = metrics.get("RT");

        return rt;
    }

    public void putCost(double value) {
        this.cost = value;
    }

    public Solution cloneCandidate() {

        Solution candidateClone = new Solution(getPatters());

        HashMap<String, Value> definitions = this.definitions;
        HashMap<String, Value> definitionsClone = new HashMap<>();
        for (Map.Entry<String, Value> entrySet : definitions.entrySet()) {
            String key = entrySet.getKey();
            Value value = entrySet.getValue();

            Value value1 = new Value();
            // Number number = value.getValue().;
            if (value.getValue() instanceof Integer) {
                value1.setValue(value.getValue().intValue());
            } else {
                value1.setValue(value.getValue().doubleValue());
            }
            value1.setPatern(value.getPatern());

            if (value.getItemList() != null) {
                value1.setItemList(value.getItemList());
            }

            definitionsClone.put(key, value1);

        }

        HashMap<String, Double> metrics = this.getMetrics();

        HashMap<String, Double> metricsClone = new HashMap<>();

        for (Map.Entry<String, Double> entrySet : metrics.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            metricsClone.put(key, value.doubleValue());
        }
        candidateClone.setDefinitions(definitionsClone);
        candidateClone.setMetrics(metricsClone);
        return candidateClone;

    }

//    public void printParameter(boolean parans, boolean metrics) {
//        HashMap<String, Value> def = getDefinitions();
//
//        if (parans) {
//            for (Map.Entry<String, Value> entrySet : def.entrySet()) {
//                String key = entrySet.getKey();
//                Value value = entrySet.getValue();
//
//                System.out.println(key + "  " + value.getValue().doubleValue());
//
//                if (value.getItemList() != null && value.getPatern().getSequenceList().get(value.getItemList()).getName() != null) {
//                    System.out.println(key + "  " + value.getPatern().getSequenceList().get(value.getItemList()).getName());
//                }
//
//            }
//        }
//
//        HashMap<String, Double> metric = getMetrics();
//
//        if (metrics) {
////            for (Map.Entry<String, Double> entrySet : metric.entrySet()) {
////                String key = entrySet.getKey();
////                Double value = entrySet.getValue();
////                if (!(key.contains("TP")&&key.contains("E"))) {
////                    System.out.println(key + "  " + value);
////                }
////
////            }
//            System.out.println("COST " + this.getCost());
//            System.out.println("RT " + this.getResponseTime());
//            System.out.println("COA " + ( this.coa()));
//            System.out.println("servers " + getServers() * Utils.price);
//        }
//
//    }

public void printParameter(boolean parans, boolean metrics) {
    HashMap<String, Value> def = getDefinitions();

    if (parans) {
        for (Map.Entry<String, Value> entrySet : def.entrySet()) {
            String key = entrySet.getKey();
            Value value = entrySet.getValue();

            // Divide pelo numResource para exibir quantidade real de VMs
            if (key.equals("Large")) {
                System.out.println(key + "  " + value.getValue().intValue() / Utils.numLargeResource);
            } else if (key.equals("Medium")) {
                System.out.println(key + "  " + value.getValue().intValue() / Utils.numMediumResource);
            } else if (key.equals("Small")) {
                System.out.println(key + "  " + value.getValue().intValue() / Utils.numSmallResouce);
            } else {
                System.out.println(key + "  " + value.getValue().doubleValue());
            }

            if (value.getItemList() != null && value.getPatern().getSequenceList().get(value.getItemList()).getName() != null) {
                System.out.println(key + "  " + value.getPatern().getSequenceList().get(value.getItemList()).getName());
            }
        }
    }

    if (metrics) {
        System.out.println("COST " + this.getCost());
        System.out.println("RT " + this.getResponseTime());
        System.out.println("COA " + this.coa());
        System.out.println("servers " + getServers() * Utils.price);
    }
}


}
