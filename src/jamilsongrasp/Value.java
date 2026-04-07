/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

/**
 *
 * @author iure
 */
public class Value {
    
        
    private Number value;
    
    private Type patern;
    
    private Integer itemList;

    public Integer getItemList() {
        return itemList;
    }

    public void setItemList(Integer itemList) {
        this.itemList = itemList;
    }    

    public Type getPatern() {
        return patern;
    }

    public void setPatern(Type patern) {
        this.patern = patern;
    }
    
    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    void getValue(int newMinserver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
}
