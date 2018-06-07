package edu.fgu.dclab;

import com.sun.beans.editors.StringEditor;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;


public class Servant implements Runnable {

    private ObjectOutputStream out = null;
    public String source = null;
    public static String Location = "家";
    public static String[] BAG = new String[8];
    public String station = "A";

    private Socket socket = null;

    private ChatRoom room = null;

    public Servant(Socket socket, ChatRoom room) {
        this.room = room;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(
                    this.socket.getOutputStream()
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        greet();
    }

    public void process(Message message) {
        switch (message.getType()) {
            case Message.ROOM_STATE:
                this.write(message);
                break;

            case Message.CHAT:

                if (station.equals("A") == false)
                    BagCrtl(message);
                else
                    MessgageProcess(message);
                break;

            case Message.LOGIN:
                if (this.source == null) {
                    this.source = ((LoginMessage) message).ID;
                    this.room.multicast(new ChatMessage(
                            "站長",
                            MessageFormat.format("{0} 進入了聊天室。", this.source)
                    ));

                    this.room.multicast(new RoomMessage(
                            room.getRoomNumber(),
                            room.getNumberOfGuests()
                    ));
                }

                break;

            default:
        }
    }





    public void write(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greet() {
        String[] greetings = {
                "歡迎來到 無聊之站 聊天室",
                "請問你的【暱稱】?"
        };

        for (String msg : greetings) {
            write(new ChatMessage("站長", msg));
        }
    }

    @Override
    public void run() {
        Message message;

        try (
                ObjectInputStream in = new ObjectInputStream(
                        this.socket.getInputStream()
                )
        ) {
            this.process((Message)in.readObject());

            while ((message = (Message) in.readObject()) != null) {
                this.room.multicast(message);
            }

            this.out.close();
        }
        catch (IOException e) {
            System.out.println("Servant: I/O Exc eption");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    void MessgageProcess(Message message){
        String MESG = ((ChatMessage) message).MESSAGE;
        for (int i = 0; i < 8; i++)
            if (BAG[i] == null)
                BAG[i] = "空";
        if (MESG.equals("Sell"))//出貨
        {
            write(new ChatMessage("站長", message.getSource()+"販售了東西"));
        }
        else if (MESG.equals("家") || MESG.equals("牛羊小屋") || MESG.equals("雞舍") || MESG.equals("農田"))
            FarmInstruction.FarmMove(message,this,MESG);//人物移動
        else if (MESG.equals("查看背包") || MESG.equals("使用物品") || MESG.equals("丟棄物品") || MESG.equals("個人資料"))//人物指令
        {
            String A = BAG[0]+"，"+BAG[1]+"，"+BAG[2]+"，"+BAG[3]+"，"+BAG[4]+"，"+BAG[5]+"，"+BAG[6]+"，"+BAG[7];
            if (MESG.equals("查看背包"))
                write(new ChatMessage("站長", "背包內的東西有：\n" +"\t"+ A));
            else if (MESG.equals("使用物品"))
            {
                write(new ChatMessage("站長","請輸入要使用的物品\n"+"背包內的物品有：\n" +"\t"+ A));
                station = "B";
            }

            else if (MESG.equals("丟棄物品"))
            {
                write(new ChatMessage("站長","請輸入要使用的物品\n"+"背包內的物品有：\n" +"\t"+ A));
                station = "C";
            }
            else if (MESG.equals("個人資料"))
                write(new ChatMessage("站長", "顯示個人資料"));
        }
        else  if (MESG.equals("現在位置"))//地點顯示
            write(new ChatMessage("站長", message.getSource()+"現在位於"+Location));
        else {//對應地區的指令操作
            if (Location.equals("家"))
                FarmInstruction.Home(message,this,MESG, BAG);
            else if (Location.equals("牛羊小屋"))
                FarmInstruction.CowSheep(message,this,MESG, BAG);
            else if (Location.equals("雞舍"))
                FarmInstruction.Chicken(message,this,MESG, BAG);
            else if (Location.equals("農田"))
                FarmInstruction.Field(message,this,MESG, BAG);
            else
                this.write(message);
        }

    }

    void BagCrtl(Message message){
        String MESG = ((ChatMessage) message).MESSAGE;
        String A = BAG[0]+","+BAG[1]+","+BAG[2]+","+BAG[3]+","+BAG[4]+","+BAG[5]+","+BAG[6]+","+BAG[7];
        if(MESG.equals("空"))
            write(new ChatMessage("站長","請輸入要使用的物品\n"+"背包內的物品有：\n" +"\t"+ A));
        else if (MESG.equals("返回"))
        {
            station = "A";
            write(new ChatMessage("站長","回到初始指令"));
        }
        else
        {
            if (station.equals("B"))
            {
                for (int i = 0; i < 8; i++){
                    if (BAG[i].equals(MESG))
                    {
                        write(new ChatMessage("站長","使用" + BAG[i]));
                        BAG[i] = null;
                        station = "A";
                        i += 10;
                    }
                }
            }
            if (station.equals("C"))
            {
                for (int i = 0; i < 8; i++){
                    if (BAG[i].equals(MESG))
                    {
                        write(new ChatMessage("站長","丟棄" + BAG[i]));
                        BAG[i] = "空";
                        station = "A";
                        i += 10;
                    }
                }
            }
        }
    }
}

// Servant.java
