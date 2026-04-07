/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author iure
 */
public class Type implements Serializable {

    private String typeName;

    private double min;

    private double max;

    private TypeNames type;
    private ArrayList<EvaluableType> sequenceList;

    private Type influentType;

    public Type(String parameterName, double min, double max, TypeNames type, ArrayList<EvaluableType> sequenceList) {
        this.typeName = parameterName;
        this.min = min;
        this.max = max;
        this.type = type;
        this.sequenceList = sequenceList;

    }

    public Type getInfluentType() {
        return influentType;
    }

    public void setInfluentType(Type influentType) {
        this.influentType = influentType;
    }

    public TypeNames getType() {
        return type;
    }

    public void setType(TypeNames type) {
        this.type = type;
    }

    public String getTyperName() {
        return typeName;
    }

    public void setTypeName(String parameterName) {
        this.typeName = parameterName;
    }

    public ArrayList<EvaluableType> getSequenceList() {
        return sequenceList;
    }

    public void setSequenceList(ArrayList<EvaluableType> sequenceList) {
        this.sequenceList = sequenceList;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public Value changeParameter(Value value, String nameParameter, HashMap<String, Value> definitions) {

        if (nameParameter.endsWith("CT") && definitions.containsKey("vmWork")) {
            Value newValue = new Value();
            double factor = definitions.get("vmWork").getValue().doubleValue();
            double result;
            double b = 0;
            double a = 0;
            newValue.setPatern(value.getPatern());

            ArrayList<EvaluableType> evs = value.getPatern().getSequenceList();

            EvaluableType ev = evs.get(value.getItemList());

            if (ev.getName().equalsIgnoreCase("t2.micro")) {
                b = 2.63;
                a = 14.583;
            }
            if (ev.getName().equalsIgnoreCase("t2.small")) {
                b = 3.59;
                a = 14.156;
            }
            if (ev.getName().equalsIgnoreCase("t2.medium")) {
                b = 5.37;
                a = 7.846;
            }
            if (ev.getName().equalsIgnoreCase("t2.large")) {
                b = 6.21;
                a = 7.548;
            }

            result = b + factor * a;
            newValue.setItemList(value.getItemList());
            newValue.setValue(result);
            
            //p validacao
            //newValue.setValue(20.39);
            
            return newValue;
        }

        return value;
    }

  

}
