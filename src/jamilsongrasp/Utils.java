/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author iure
 */
public class Utils {
    
    public static final int largeServer = 8;
    public static final int mediumServer = 4;
    public static final int smallServer = 2;
    public static final int numLargeResource = 18;
    public static final int numMediumResource = 14;
    public static final int numSmallResouce = 8;
    public static final double price = 2487.11;
    public static final double threshould = 150.0;

    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }

    public static long binomial(int n, int k) {
        if (k > n - k) {
            k = n - k;
        }

        long b = 1;
        for (int i = 1, m = n; i <= k; i++, m--) {
            b = b * m / i;
        }
        return b;
    }
    
    public static double fact(double n) {
        double fact = 1; // this  will be the result
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

}
