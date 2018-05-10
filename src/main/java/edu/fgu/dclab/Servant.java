package edu.fgu.dclab;

import com.sun.beans.editors.StringEditor;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Servant implements Runnable {

    private ObjectOutputStream out = null;
    public String source = null;

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
                String MESG = ((ChatMessage) message).MESSAGE;
                if (MESG.equals("time"))
                {
                    if (this.source != null && this.source.equals(message.getSource()))
                        Time(message);
                }
                else
                    this.write(message);
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

    public void Time(Message message){
        Date  date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Time = sdf.format(date);
        write(new ChatMessage("站長", "現在時間：「" + Time + "」"));
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
}

// Servant.java
