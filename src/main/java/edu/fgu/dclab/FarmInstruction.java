package edu.fgu.dclab;



import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.io.ObjectOutputStream;

public class FarmInstruction{

    public int CowNumber = 0;
    public int SheepNumber = 0;
    public int ChickenNumber = 0;

    //在家裡的程式碼
    public static void Home(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("Sleep"))
            servant.write(new ChatMessage("站長", message.getSource()+"進入睡眠"));
        else if (MESG.equals("Buy"))
            servant.write(new ChatMessage("站長", message.getSource()+"購買了道具"));
        else if (MESG.equals("BuyList"))
            servant.write(new ChatMessage("站長", message.getSource()+"道具購買清單"));
        else
            servant.write(message);
    }

    //在牛羊小屋的程式碼
    public static void CowSheep(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("Keep"))
            servant.write(new ChatMessage("站長", message.getSource()+"餵食了牛羊"));
        else if (MESG.equals("Miking"))
            for (int i = 0; i < 8; i++)
            {
                if (BAG[i] == null)
                {
                    BAG[i] = "牛奶";
                    servant.write(new ChatMessage("站長", message.getSource()+"擠了牛奶"));
                    i += 10;
                }
                else if (i == 7)
                    servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
            }
        else if (MESG.equals("Cut"))
            for (int i = 0; i < 8; i++)
            {
                if (BAG[i] == null)
                {
                    BAG[i] = "羊毛";
                    servant.write(new ChatMessage("站長", message.getSource()+"剪了羊毛"));
                    i += 10;
                }
                else if (i == 7)
                    servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
            }
        else if (MESG.equals("Brush"))
            servant.write(new ChatMessage("站長", message.getSource()+"幫牛羊刷背"));
        else
            servant.write(message);

    }

    //在雞舍的程式碼
    public static void Chicken(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("撿拾"))
            for (int i = 0; i < 8; i++)
            {
                if (BAG[i] == null)
                {
                    BAG[i] = "雞蛋";
                    servant.write(new ChatMessage("站長", message.getSource()+"撿取了雞蛋"));
                    i += 10;
                }
                else if (i == 7)
                    servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
            }
        else if (MESG.equals("放置飼料"))
            servant.write(new ChatMessage("站長", message.getSource()+"餵食了雞群"));
        else
            servant.write(message);
    }

    //在農田的程式碼
    public static void Field(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("澆水"))
            servant.write(new ChatMessage("站長", message.getSource()+"在農田澆水了"));
        else if (MESG.equals("播種"))
            servant.write(new ChatMessage("站長", message.getSource()+"在農田撒下種子"));
        else if (MESG.equals("收割"))
            servant.write(new ChatMessage("站長", message.getSource()+"收割了作物"));
        else
            servant.write(message);
    }

    //在農場移動的指令
    public static void FarmMove(Message message, Servant servant,String MESG){
        if (MESG.equals(servant.Location))
           servant.write(new ChatMessage("站長", message.getSource()+"已抵達目的地"));
        else
        {
            servant.Location = MESG;
            servant.write(new ChatMessage("站長", message.getSource()+"前往"+servant.Location));
        }
    }

    public void BagCtr(Message message, Servant servant,String MESG){
        String[] BAG = servant.BAG;
        if (MESG.equals("撿拾"))
            servant.write(new ChatMessage("站長", message.getSource()+"撿取了雞蛋"));
    }
}
