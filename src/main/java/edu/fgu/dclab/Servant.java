package edu.fgu.dclab;

import com.sun.beans.editors.StringEditor;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;


public class Servant implements Runnable {

    private ObjectOutputStream out = null;
    public String source = null;
    public static String Location = "Home";
    public static String[] BAG = new String[8];

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

        if (MESG.equals("Sell"))//出貨
        {
            write(new ChatMessage("站長", message.getSource()+"販售了東西"));
        }
        else if (MESG.equals("羊毛"))
            for (int i = 0 ; i < 8; i++)
            {
                if (BAG[i].equals(MESG))
                {
                    write(new ChatMessage("站長", "使用"+MESG));
                    BAG[i] = null;
                    i += 10;
                }
            }
        else if (MESG.equals("Home") || MESG.equals("CowSheep") || MESG.equals("Chicken") || MESG.equals("Field"))
            FarmInstruction.FarmMove(message,this,MESG);//人物移動
        else if (MESG.equals("A") || MESG.equals("B") || MESG.equals("C") || MESG.equals("D"))//人物指令
        {
            String A = BAG[0]+","+BAG[1]+","+BAG[2]+","+BAG[3]+","+BAG[4]+","+BAG[5]+","+BAG[6]+","+BAG[7];
            if (MESG.equals("A"))
                write(new ChatMessage("站長", A));
                /*for (int i = 0 ; i < 8; i++)
                {
                    if (BAG[i] == null)
                        write(new ChatMessage("站長", "空"));
                    else
                        write(new ChatMessage("站長", BAG[i]));
                }*/

            else if (MESG.equals("B"))
                ;

            else if (MESG.equals("C"))
                write(new ChatMessage("站長", "丟棄物品"));
            else if (MESG.equals("D"))
                write(new ChatMessage("站長", "個人資料"));
        }

        else  if (MESG.equals("Location"))//地點顯示
            write(new ChatMessage("站長", message.getSource()+"現在位於"+Location));
        else {//對應地區的指令操作
            if (Location.equals("Home"))
                FarmInstruction.Home(message,this,MESG, BAG);
            else if (Location.equals("CowSheep"))
                FarmInstruction.CowSheep(message,this,MESG, BAG);
            else if (Location.equals("Chicken"))
                FarmInstruction.Chicken(message,this,MESG, BAG);
            else if (Location.equals("Field"))
                FarmInstruction.Field(message,this,MESG, BAG);
            else
                this.write(message);
        }

    }
}

// Servant.java