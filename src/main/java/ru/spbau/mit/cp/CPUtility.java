package ru.spbau.mit.cp;

import java.io.*;

/**
 * Created by Egor Gorbunov on 01.03.16.
 * email: egor-mailbox@ya.ru
 */
public class CPUtility {

    private static int MAX_BUFFER_SIZE = 8192;

    private static void copyFile(String from, String to) throws IOException {
        BufferedInputStream br;
        BufferedOutputStream bw;
        br = new BufferedInputStream(new FileInputStream(from), MAX_BUFFER_SIZE);
        bw = new BufferedOutputStream(new FileOutputStream(to), MAX_BUFFER_SIZE);

        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        while (true) {
            int n = br.read(buffer);
            if (n < 0) {
                break;
            }
            bw.write(buffer, 0, n);
        }
        br.close();
        bw.close();
    }


    public static void main(String[] args) {
        try {
            copyFile(args[0], args[1]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("ERROR: wrong command line arguments");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
