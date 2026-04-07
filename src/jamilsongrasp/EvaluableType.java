/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author iure
 */
public class EvaluableType implements Comparable<EvaluableType>, Serializable{
    
    private String name;
    private HashMap<String,Number> values;
    
    private String mainParameter;


    private double cost;

    public EvaluableType(HashMap<String,Number> values, String mainParameter, double cost, String name) {
        this.values = values;
        this.mainParameter = mainParameter;
        this.cost = cost;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

    public String getMainParameter() {
        return mainParameter;
    }

    public void setMainParameter(String mainParameter) {
        this.mainParameter = mainParameter;
    }


    
 

    public HashMap<String,Number>  getValues() {
        return values;
    }

    public void setValues(HashMap<String,Number> values) {
        this.values = values;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    
    @Override
    public int compareTo(EvaluableType t) {
        
        Number otherValue = ((EvaluableType) t).getValues().get(mainParameter);
        
        if(otherValue.doubleValue()>this.getValues().get(mainParameter).doubleValue())
            return -1;
        if(otherValue.doubleValue()<this.getValues().get(mainParameter).doubleValue())
            return 1;
        return 0;
                    
        
       
    }
    
 
    
    
    
}
