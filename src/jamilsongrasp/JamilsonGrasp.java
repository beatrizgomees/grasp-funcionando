/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author iure
 */
public class JamilsonGrasp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        String model = "C:\\Users\\bea\\Downloads\\jam31.xml";

        ModelAvaliator avaliator = ModelAvaliator.getInstance(model);

        GenerateRamdown generateRamdown = new GenerateRamdown();

        Type pattern = new Type("Large", 0, 0, TypeNames.INT, null);
        Type pattern1 = new Type("Medium", 0,0, TypeNames.INT, null);
        Type pattern2 = new Type("Small", 0, 0, TypeNames.INT, null);
        Type pattern3 = new Type("prob1", 0, 0, TypeNames.DOUBLE, null);
        Type pattern4 = new Type("prob2", 0, 0, TypeNames.DOUBLE, null);
        Type pattern5 = new Type("prob3", 0, 0, TypeNames.DOUBLE, null);
        Type pattern6 = new Type("sizeInfra", 1, 10, TypeNames.INT, null);

        ArrayList<Type> definitions = new ArrayList<>();
        definitions.add(pattern);
        definitions.add(pattern1);
        definitions.add(pattern2);
        definitions.add(pattern3);
        definitions.add(pattern4);
        definitions.add(pattern5);
        definitions.add(pattern6);
        
        Grasp grasp = new Grasp();
        
        //iteracoes do grasp
        int graspIteration =30;  //100 10
        //iteracoes construcao
        int graspConstructionIterations =10; // 30 5
        //iteracoes busca loca
        int localsearchIterarions = 10; //40 5
        double rcl = 0.7;
        long time=15;  //60


       // Solution solution= new Solution(definitions);
       
       //avaliator.changeDefinition("InterJobRate", "8.0");
        Solution solution = grasp.executeGrasp(avaliator, definitions, graspIteration, graspConstructionIterations, localsearchIterarions, rcl, time);
        solution.printParameter(true, true);
//        Solution solution =generateRamdown.generateValue(definitions);
//        
//        avaliator.setAllDefinition(solution);
//        
//        avaliator.stationarySimulationSolve(60);
//        
//        solution.printParameter(true, false);
//        
//        System.out.println(solution.coa());
//        
//        Solution sollocal = generateRamdown.getNeighbor(solution, definitions);
//        
//        avaliator.setAllDefinition(sollocal);
//        
//        System.out.println("@@@@@@@");
//        
//        avaliator.stationarySimulationSolve(60);
//        
//        sollocal.printParameter(true, false);
//        System.out.println(sollocal.coa());

    }

}
