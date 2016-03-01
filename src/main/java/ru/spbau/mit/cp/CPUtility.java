package ru.spbau.mit.cp;

import java.io.*;

/**
 * Created by Egor Gorbunov on 01.03.16.
 * email: egor-mailbox@ya.ru
 */
public class CPUtility {

    private static int MAX_BUFFER_SIZE = 8192;

    private static String copyFile(String from, String to) {
        BufferedInputStream br;
        BufferedOutputStream bw;
        try {
            br = new BufferedInputStream(new FileInputStream(from), MAX_BUFFER_SIZE);
        } catch (FileNotFoundException e) {
            return "ERROR: Can't open file to copy from: [ " + from + " ]";
        }

        try {
            bw = new BufferedOutputStream(new FileOutputStream(to), MAX_BUFFER_SIZE);
        } catch (IOException e) {
            return "ERROR: Can't open destination file: [ " + to + " ]";
        }

        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        try {
            while (true) {
                int n = br.read(buffer);
                if (n < 0) {
                    break;
                }
                bw.write(buffer, 0, n);
            }
            br.close();
            bw.close();
        } catch (IOException e) {
            return "ERROR: Fail during copy.";
        }


        return null;
    }


    public static void main(String[] args) {
        String result;
        try {
            result = copyFile(args[0], args[1]);
        } catch (IndexOutOfBoundsException e) {
            result = "ERROR: wrong command line arguments";
        }

        if (result != null) {
            System.out.println(result);
        }
    }
}
