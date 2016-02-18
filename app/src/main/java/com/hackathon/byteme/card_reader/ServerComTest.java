package com.hackathon.byteme.card_reader;


/**
 * Created by bighoon on 2016-02-18.
 */
public class ServerComTest {
    public static void main(String args[])
    {
        Server_com com = new Server_com("10.6.6.10", 50);

        com.setData("12345678 butts");
        Thread thr = new Thread(com);
        thr.start();
    }
}