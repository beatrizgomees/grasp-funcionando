/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import jamilsongrasp.TypeNames;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.MersenneTwister;

/**
 *
 * @author iure
 */
public class GenerateRamdown {

    private double range = 0.1;

    MersenneTwister mersenneTwister = new MersenneTwister();

    int largeServer = Utils.largeServer;
    int mediumServer = Utils.mediumServer;
    int smallServer = Utils.smallServer;
    int numLargeResource = Utils.numLargeResource;
    int numMediumResource = Utils.numMediumResource;
    int numSmallResouce = Utils.numSmallResouce;

    Random random = new Random();

    public ArrayList<Integer> createList(Type parameter, double proportion, HashMap<String, Value> definitions, int sizeInfra) {

        int quantity = 0;

        switch (parameter.getType()) {
            case LIST:
                quantity = (int) (parameter.getSequenceList().size() * proportion);
                break;
            case DOUBLE:
                quantity = (int) ((parameter.getMax() - parameter.getMin()) * proportion);
                break;
            case INT:
                quantity = (int) ((parameter.getMax() - parameter.getMin()) * proportion);
                break;

        }

        if (quantity < 1) {
            quantity = 1;
        }

        ArrayList<Integer> parameterValues = new ArrayList<>();

        do {
            Integer value = null;
            if (parameter.getType() == TypeNames.DOUBLE || parameter.getType() == TypeNames.INT) {
                value = generateInt((int) parameter.getMax(), (int) parameter.getMin());
            } else {
                value = mersenneTwister.nextInt(parameter.getSequenceList().size());
            }

            Value valu = new Value();
            valu.setPatern(definitions.get(parameter.getTyperName()).getPatern());

            if (parameter.getType() == TypeNames.DOUBLE || parameter.getType() == TypeNames.INT) {
                valu.setValue(value);
            } else {
                valu.setItemList(value);
            }

            definitions.put(parameter.getTyperName(), valu);

            parameterValues.add(value);

        } while (parameterValues.size() < quantity);

        return parameterValues;
    }

    public Value getLargeValue(Type large, int sizeInfra) {

        int capacityAtual = sizeInfra * largeServer;
        int largeNumber = generateInt(capacityAtual, 0);
        large.setMax(capacityAtual);
        Value largeValue = new Value();
        largeValue.setPatern(large);
        largeValue.setValue(largeNumber * numLargeResource);

        return largeValue;
    }

    public Value getMediumValue(Type medium, int sizeInfra, int largeNumber) {

        int capacityAtual = (int) ((((double) sizeInfra) - ((double) ((double) largeNumber / (double) largeServer))) * mediumServer);
        int mediumnumber = generateInt(capacityAtual, 0);
        medium.setMax(capacityAtual);
        Value mediumValue = new Value();
        mediumValue.setPatern(medium);
        mediumValue.setValue(mediumnumber * numMediumResource);
        return mediumValue;
    }
    public Value getSmallValue(Type small, int sizeInfra, int largeNumber, int mediumnumber) {
        int capacityAtual = (int) ((((double) sizeInfra) - ((double) ((double) largeNumber / (double) largeServer)) - ((double) ((double) mediumnumber / (double) mediumServer))) * smallServer);
        //int smallNumber = capacityAtual;
        int smallNumber = generateInt(capacityAtual, 0);
        Value smallValue = new Value();
        smallValue.setPatern(small);
        smallValue.setValue(smallNumber * numSmallResouce);
        return smallValue;
    }

    public Solution generateValue(ArrayList<Type> parameters) {

        Solution candidate = new Solution(parameters);

        HashMap<String, Value> definitions = new HashMap<>();

        Type sizeType = getType("sizeInfra", parameters);
        int sizeInfra = generateInt((int) sizeType.getMax(), (int) sizeType.getMin());
        Value sizeInfraValue = new Value();
        sizeInfraValue.setPatern(sizeType);
        sizeInfraValue.setValue(sizeInfra);
        definitions.put(sizeType.getTyperName(), sizeInfraValue);

        //Dividir os servidores servidor large
        Type large = getType("Large", parameters);
        definitions.put(large.getTyperName(), getLargeValue(large, sizeInfra));
        int largeNumber = definitions.get("Large").getValue().intValue() / numLargeResource;

        //pegar medium
        Type medium = getType("Medium", parameters);
        definitions.put(medium.getTyperName(), getMediumValue(medium, sizeInfra, largeNumber));
        int mediumnumber = definitions.get("Medium").getValue().intValue() / numMediumResource;

        Type small = getType("Small", parameters);

        definitions.put(small.getTyperName(), getSmallValue(small, sizeInfra, largeNumber, mediumnumber));
        int smallNumber = definitions.get("Small").getValue().intValue() / numSmallResouce;

        //gerar um valor para as probabilidades de distribuir
        Type Plarge = getType("prob1", parameters);
        double probLarge = 0;
        if (largeNumber != 0) {
            probLarge = generateDouble(1, 0);
            if (mediumnumber == 0 && smallNumber == 0) {
                probLarge = 1.0;
            }
        }
        Value pLarge = new Value();
        pLarge.setPatern(Plarge);
        pLarge.setValue(probLarge);
        definitions.put(pLarge.getPatern().getTyperName(), pLarge);

        Type Pmedium = getType("prob2", parameters);
        double probMedium = 0;
        if ((mediumnumber != 0) && (smallNumber == 0.0)) {
            probMedium = 1.0 - probLarge;
        } else if ((mediumnumber != 0) && (smallNumber != 0.0)) {
            probMedium = generateDouble((1.0 - probLarge), 0);
        }
        Value pMediumValue = new Value();
        pMediumValue.setPatern(Pmedium);
        pMediumValue.setValue(probMedium);
        definitions.put(pMediumValue.getPatern().getTyperName(), pMediumValue);

        Type Psmall = getType("prob3", parameters);
        double probSmall = 0;
        if (smallNumber != 0) {
            probSmall = 1.0 - probLarge - probMedium;
        }
        Value pSmallValue = new Value();
        pSmallValue.setPatern(Psmall);
        pSmallValue.setValue(probSmall);
        definitions.put(pSmallValue.getPatern().getTyperName(), pSmallValue);

        candidate.setDefinitions(definitions);

        //adicionar as outras normais
        return candidate;
    }

    public Type getType(String name, ArrayList<Type> parameters) {

        for (Type parameter : parameters) {
            if (parameter.getTyperName().equals(name)) {
                return parameter;
            }
        }

        return null;
    }

    public ArrayList<Solution> generateValues(ArrayList<Type> parameters, int quantity) {

        ArrayList<Solution> candidates = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            //  candidates.add(generateValue(parameters));
        }

        throw new NotImplementedException();

        //return candidates;
    }

    public Solution getNeighbor(Solution candidate, ArrayList<Type> parameters2) {

        Solution newCanditate = candidate.cloneCandidate();

        HashMap<String, Value> parameters = newCanditate.getRealDefinitions();

        //parameters.get("node_on_start")
        HashMap<String, Value> definitions = new HashMap<>();
        int oldsize = parameters.get("sizeInfra").getValue().intValue();
        HashMap<String, Value> vals = getRandowNeigborn(parameters.get("sizeInfra"), 0);
        int sizeInfra = vals.get("sizeInfra").getValue().intValue();
        definitions.putAll(vals);

        if (oldsize != vals.get("sizeInfra").getValue().intValue()) {

            definitions.put("Large", getLargeValue(getType("Large", parameters2), sizeInfra));
            int largeValue = definitions.get("Large").getValue().intValue() / numLargeResource;

            definitions.put("Medium", getMediumValue(getType("Medium", parameters2), sizeInfra, largeValue));
            int mediumValue = definitions.get("Medium").getValue().intValue() / numMediumResource;

            definitions.put("Small", getSmallValue(getType("Small", parameters2), sizeInfra, largeValue, mediumValue));
            int smallValue = definitions.get("Small").getValue().intValue() / numSmallResouce;

            definitions.putAll(getProbabilitiesNeigborn(largeValue, mediumValue, smallValue, 0.1, parameters));

        } else {
            int oldLarge = parameters.get("Large").getValue().intValue();
            //pegar vizinhanca
            vals = getRandowNeigbornVM(parameters.get("Large"), sizeInfra * largeServer, 0, sizeInfra, numLargeResource);
            vals.get("Large").setValue(vals.get("Large").getValue().intValue());
            definitions.putAll(vals);
            if (oldLarge != vals.get("Large").getValue().intValue()) {
                int largeValue = definitions.get("Large").getValue().intValue() / numLargeResource;
                //gerar tudo abaixo de large
                definitions.put("Medium", getMediumValue(getType("Medium", parameters2), sizeInfra, largeValue));
                int mediumValue = definitions.get("Medium").getValue().intValue() / numMediumResource;

                definitions.put("Small", getSmallValue(getType("Small", parameters2), sizeInfra, largeValue, mediumValue));
                int smallValue = definitions.get("Small").getValue().intValue() / numSmallResouce;

                definitions.putAll(getProbabilitiesNeigborn(largeValue, mediumValue, smallValue, 0.1, parameters));

            } else {
                int oldMedium = parameters.get("Medium").getValue().intValue();
                //pegar vizinhanca
                int largeValue = definitions.get("Large").getValue().intValue() / numLargeResource;
                vals = getRandowNeigbornVM(parameters.get("Medium"), (int) ((((double) sizeInfra) - ((double) ((double) largeValue / (double) largeServer))) * mediumServer), 0, sizeInfra, numMediumResource);
                vals.get("Medium").setValue(vals.get("Medium").getValue().intValue());
                definitions.putAll(vals);
                int mediumValue = definitions.get("Medium").getValue().intValue() / numMediumResource;
                definitions.put("Small", getSmallValue(getType("Small", parameters2), sizeInfra, largeValue, mediumValue));
                if (oldMedium != vals.get("Medium").getValue().intValue()) {

                    //int mediumValue = definitions.get("Medium").getValue().intValue() / numMediumResource;
                    //definitions.put("Small", getSmallValue(getType("Small", parameters2), sizeInfra, largeValue, mediumValue));
                    int smallValue = definitions.get("Small").getValue().intValue() / numSmallResouce;

                    definitions.putAll(getProbabilitiesNeigborn(largeValue, mediumValue, smallValue, 0.1, parameters));
                } else {
                    //int largeValue = definitions.get("Large").getValue().intValue() / numLargeResource;
                    //int mediumValue = definitions.get("Medium").getValue().intValue() / numMediumResource;
                    int smallValue = definitions.get("Small").getValue().intValue() / numSmallResouce;

                    definitions.putAll(getProbabilitiesNeigborn(largeValue, mediumValue, smallValue, 0.1, parameters));

                }
            }

        }

        newCanditate.setDefinitions(definitions);

        return newCanditate;

    }

    public HashMap<String, Value> getProbabilitiesNeigborn(int large, int medium, int small, double var, HashMap<String, Value> parameters) {

        double realValue = 1.0;

        HashMap<String, Value> definitions = new HashMap<>();

        double oldplarge = parameters.get("prob1").getValue().doubleValue();
        double newlarge = 0.0;
        if (large != 0) {
            if (medium == 0 && small == 0) {
                newlarge = 1.0;
            } else {
                newlarge = generateNextProb(0, realValue, oldplarge, var);
            }
        }

        //parameters.get("prob1").setValue(newlarge);
        Value prob1 = new Value();
        prob1.setPatern(parameters.get("prob1").getPatern());
        prob1.setValue(newlarge);
        definitions.put("prob1", prob1);
        realValue = realValue - newlarge;

        double oldMedium = 0.0;
        double newMedium = 0.0;
        if (medium != 0.0) {
            if (small != 0) {
                oldMedium = parameters.get("prob2").getValue().doubleValue();
                newMedium = generateNextProb(0, realValue, oldMedium, var);
            } else {
                newMedium = 1.0 - newlarge;
            }
        }
        //parameters.get("prob2").setValue(newMedium);

        realValue = realValue - newMedium;
        Value prob2 = new Value();
        prob2.setPatern(parameters.get("prob2").getPatern());
        prob2.setValue(newMedium);
        definitions.put("prob2", prob2);

        double newSmall;
        if (small != 0) {
            newSmall = realValue;
        } else {
            newSmall = 0.0;
        }
        parameters.get("prob3").setValue(newSmall);
        Value prob3 = new Value();
        prob3.setPatern(parameters.get("prob3").getPatern());
        prob3.setValue(newSmall);
        definitions.put("prob3", prob3);

        return definitions;
    }

    public double generateNextProb(double min, double max, double old, double var) {

        double realmin = old - var, realMax = old + var;
        if ((old - var) < min) {
            realmin = min;
        }
        if ((old + var) > max) {
            realMax = max;
        }

        double nextprob = generateDouble(realMax, realmin);

        return nextprob;
    }

    public HashMap<String, Value> getRandowNeigbornVM(Value oldValue, int maxCap, int minCap, int sizeInfra, int numResources) {

        int oldNumberVM = oldValue.getValue().intValue();

        int rangeNVM = (int) (sizeInfra * range);
        if (rangeNVM < 1) {
            rangeNVM = 1;
        }

        int max = oldNumberVM / numResources + rangeNVM, min = oldNumberVM / numResources - rangeNVM;

        if (max > maxCap) {
            max = maxCap;
        }

        if (min < minCap) {
            min = minCap;
        }

        int numberVMs = generateInt(max, min);

        Value value = new Value();
        value.setPatern(oldValue.getPatern());
        value.setValue(numberVMs * numResources);

        HashMap<String, Value> values = new HashMap<>();
        values.put(oldValue.getPatern().getTyperName(), value);

        return values;
    }

    //deve considerar a ,udança de valores relacionados
    public HashMap<String, Value> getRandowNeigborn(Value oldValue, Integer direction) {

        //quando o tipo foi adicionado por conjunto
        if (direction == null) {
            return null;
        }

        Type parameter = oldValue.getPatern();

        HashMap<String, Value> values = new HashMap<>();

        Value value = new Value();

        value.setPatern(parameter);

        switch (parameter.getType()) {
            case LIST:
                int oldI = oldValue.getItemList();

                //  if (defName != value.getPatern().getSequenceList().get(0).getMainParameter()) {
                //    break;
                //}
                ArrayList<EvaluableType> evaluables = value.getPatern().getSequenceList();

                int newRange = (int) (parameter.getSequenceList().size() * range);

                if (newRange < 2) {
                    newRange = 2;
                }

                int i = mersenneTwister.nextInt(newRange);

                if ((mersenneTwister.nextInt() % 2 == 0) && (direction == 0) || direction > 0) {
                    if ((i + oldI) < parameter.getSequenceList().size()) {
                        i += oldI;
                    } else {
                        i = parameter.getSequenceList().size() - 1;
                    }
                } else {
                    if ((oldI - i) > 0) {
                        i = oldI - i;
                    } else {
                        i = 0;
                    }
                }
                if (i < 0 || i >= value.getPatern().getSequenceList().size()) {
                    int v = value.getPatern().getSequenceList().size();
                    System.out.println("Erro" + v);
                }

                HashMap<String, Number> parans = value.getPatern().getSequenceList().get(i).getValues();

                for (Map.Entry<String, Number> entrySet1 : parans.entrySet()) {
                    String key = entrySet1.getKey();
                    Number value1 = entrySet1.getValue();

                    value = new Value();

                    value.setPatern(parameter);

                    value.setItemList(i);

                    value.setValue(value1);

                    values.put(key, value);

                }
                return values;

            case DOUBLE:

                double newDoubleRange = (parameter.getMax() - parameter.getMin()) * range;

                double newMaxDouble,
                 newMinDouble;

                if (oldValue.getValue().doubleValue() > parameter.getMax() || oldValue.getValue().doubleValue() < parameter.getMin()) {
                    oldValue.setValue(generateDouble(parameter.getMax(), parameter.getMax()));
                }

                if (oldValue.getValue().doubleValue() + newDoubleRange > parameter.getMax()) {
                    newMaxDouble = parameter.getMax();
                } else {
                    newMaxDouble = oldValue.getValue().doubleValue() + newDoubleRange;
                }

                if (oldValue.getValue().doubleValue() - newDoubleRange < parameter.getMin()) {
                    newMinDouble = parameter.getMin();
                } else {
                    newMinDouble = oldValue.getValue().doubleValue() - newDoubleRange;
                }

                if ((mersenneTwister.nextInt() % 2 == 0) && (direction == 0) || direction > 0) {
                    newMinDouble = oldValue.getValue().doubleValue();

                } else {
                    newMaxDouble = oldValue.getValue().doubleValue();
                }

                value.setValue(generateDouble(newMaxDouble, newMinDouble));

                values.put(parameter.getTyperName(), value);
                return values;
            case INT:

                int newIntegerRange = (int) ((parameter.getMax() - parameter.getMin()) * range);

                if (oldValue.getValue().intValue() > parameter.getMax() || oldValue.getValue().intValue() < parameter.getMin()) {
                    oldValue.setValue(generateInt((int) parameter.getMax(), (int) parameter.getMax()));
                }

                if (newIntegerRange < 1) {
                    newIntegerRange = 1;
                }

                if (parameter.getMax() == parameter.getMin()) {
                    newIntegerRange = 0;
                }

                int newMaxInteger = 0,
                 newMinInteger = 0;

                if (oldValue.getValue().intValue() + newIntegerRange > parameter.getMax()) {
                    newMaxInteger = (int) parameter.getMax();
                } else {
                    newMaxInteger = oldValue.getValue().intValue() + newIntegerRange;
                }

                if (oldValue.getValue().intValue() - newIntegerRange < parameter.getMin()) {
                    newMinInteger = (int) parameter.getMin();
                } else {
                    newMinInteger = oldValue.getValue().intValue() - newIntegerRange;
                }

                if (direction > 0) {
                    newMinInteger = oldValue.getValue().intValue();

                } else if (direction < 0) {
                    newMaxInteger = oldValue.getValue().intValue();
                }

                if (newMinInteger > newMaxInteger) {
                    System.out.println("ERRO");
                }

                value.setValue(generateInt((int) newMaxInteger, (int) newMinInteger));

                values.put(parameter.getTyperName(), value);
                //value.setValue(generateInt((int) parameter.getMax(), (int) parameter.getMin()));
                return values;
        }

        return null;

    }

    public Solution CandidateDOE(ArrayList<Type> parameters, int[] matrix) {

        Solution candidate = new Solution(parameters);

        HashMap<String, Value> definitions = new HashMap<>();

        for (int i = 0; i < parameters.size(); i++) {

            Value value = null;
            Number number;

            switch (parameters.get(i).getType()) {
                case LIST:

                    int index = 0;
                    if (matrix[i] > 0) {
                        index = parameters.get(i).getSequenceList().size() - 1;
                    }

                    HashMap<String, Number> choseType = parameters.get(i).getSequenceList().get(index).getValues();

                    for (Map.Entry<String, Number> entrySet : choseType.entrySet()) {
                        String key = entrySet.getKey();
                        Number value1 = entrySet.getValue();

                        value = new Value();

                        value.setItemList(index);

                        value.setPatern(parameters.get(i));

                        number = value1;

                        value.setValue(number);

                        definitions.put(key, value);

                    }

                    break;
                case DOUBLE:

                    value = new Value();
                    value.setPatern(parameters.get(i));

                    number = parameters.get(i).getMin();
                    if (matrix[i] > 0) {
                        number = parameters.get(i).getMax();
                    }

                    value.setValue(number);
                    definitions.put(parameters.get(i).getTyperName(), value);

                    break;
                case INT:

                    value = new Value();
                    value.setPatern(parameters.get(i));

                    number = parameters.get(i).getMin();
                    if (matrix[i] > 0) {
                        number = parameters.get(i).getMax();
                    }

                    value.setValue(number);
                    definitions.put(parameters.get(i).getTyperName(), value);

                    break;
            }

        }

        candidate.setDefinitions(definitions);
        return candidate;
    }

    public HashMap<String, Value> randowParameter(Type parameter) {

        HashMap<String, Value> returnValue = new HashMap();

        Value value = null;

        switch (parameter.getType()) {
            case LIST:

                int i = mersenneTwister.nextInt(parameter.getSequenceList().size());

                HashMap<String, Number> parameters = parameter.getSequenceList().get(i).getValues();

                for (Map.Entry<String, Number> entrySet : parameters.entrySet()) {
                    String key = entrySet.getKey();
                    Number value1 = entrySet.getValue();

                    value = new Value();

                    value.setItemList(i);

                    value.setPatern(parameter);

                    value.setValue(value1);

                    returnValue.put(key, value);

                }

                //value.setValue(value.getPatern().getSequenceList().get(i).getValue());
                break;
            case DOUBLE:

                value = new Value();

                value.setPatern(parameter);

                value.setValue(generateDouble(parameter.getMax(), parameter.getMin()));

                returnValue.put(parameter.getTyperName(), value);

                break;
            case INT:

                value = new Value();

                value.setPatern(parameter);

                value.setValue(generateDouble(parameter.getMax(), parameter.getMin()));

                value.setValue(generateInt((int) parameter.getMax(), (int) parameter.getMin()));

                returnValue.put(parameter.getTyperName(), value);

                break;
        }

        return returnValue;
    }

    private Value generateIntValue(Type parameter, int max, int min) {

        Value value = new Value();

        value.setPatern(parameter);

        value.setValue(generateDouble(parameter.getMax(), parameter.getMin()));

        value.setValue(generateInt((int) parameter.getMax(), (int) parameter.getMin()));

        return value;
    }

    private int generateInt(int max, int min) {

        int result = min + mersenneTwister.nextInt(max - min + 1);

        //int result = min + (random).nextInt(max - min+1);
        //System.out.println("random"+result); 
        return result;
    }

    private double generateDouble(double max, double min) {

        double result = min + (max - min) * mersenneTwister.nextDouble();

        //double result = min + (max - min) * (random).nextDouble();
        return result;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

}
