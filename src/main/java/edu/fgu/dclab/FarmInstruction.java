package edu.fgu.dclab;



import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.io.ObjectOutputStream;

public class FarmInstruction{

    public static int CowNumber = 1;
    public static int ChickenNumber = 1;
    public static int SheepNumber = 1;
    public static int EggNumber = 1;
    public static String[] Cow = new String[8];
    public static String[] Chicken = new String[8];
    public static String[] Sheep = new String[8];
    public static String[][][] Farmfield = new String[3][1][1];

    //在家裡的程式碼
    public static void Home(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("睡覺"))
            servant.write(new ChatMessage("站長", message.getSource()+"進入睡眠"));
        else if (MESG.equals("郵購"))
            servant.write(new ChatMessage("站長", message.getSource()+"購買了道具"));
        else if (MESG.equals("翻閱郵購目錄"))
            servant.write(new ChatMessage("站長", message.getSource()+"道具購買清單"));
        else
            servant.write(message);
    }

    //在牛羊小屋的程式碼
    public static void CowSheep(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("放置牧草"))
        {
            servant.write(new ChatMessage("站長", message.getSource()+"牧草已放置"));
            if (CowNumber == 0 && SheepNumber == 0)
                servant.write(new ChatMessage("站長", "由於您沒有牛隻與羊隻，放置牧草並無作用"));
        }
        else if (MESG.equals("擠牛奶"))
            if (CowNumber != 0)
            {
                for (int i = 0; i < 8; i++)
                {
                    if (BAG[i].equals("空"))
                    {
                        BAG[i] = "牛奶";
                        servant.write(new ChatMessage("站長", message.getSource()+"擠了牛奶"));
                        i += 10;
                    }
                    else if (i == 7)
                        servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
                }
            }
            else
                servant.write(new ChatMessage("站長", "您沒有牛隻"));
        else if (MESG.equals("剪羊毛"))
            if (SheepNumber != 0)
            {
                for (int i = 0; i < 8; i++)
                {
                    if (BAG[i].equals("空"))
                    {
                        BAG[i] = "羊毛";
                        servant.write(new ChatMessage("站長", message.getSource()+"剪了羊毛"));
                        i += 10;
                    }
                    else if (i == 7)
                        servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
                }
            }
            else
                servant.write(new ChatMessage("站長", "您沒有羊隻"));
        else if (MESG.equals("刷背"))
        {
            if (CowNumber == 0)
                servant.write(new ChatMessage("站長", "您沒有牛隻"));
            if (SheepNumber ==0)
                servant.write(new ChatMessage("站長", "您沒有羊隻"));
            if (CowNumber != 0 || SheepNumber != 0)
                servant.write(new ChatMessage("站長", message.getSource()+"已幫動物們刷背"));
        }
        else
            servant.write(message);

    }

    //在雞舍的程式碼
    public static void Chicken(Message message, Servant servant,String MESG, String[] BAG){
        if (MESG.equals("撿拾"))
            if (ChickenNumber ==0)
                servant.write(new ChatMessage("站長", "您沒有雞隻，無法生產雞蛋"));
            else  if (EggNumber == 0)
                servant.write(new ChatMessage("站長", "地面上並無雞蛋"));
            else
            {
                for (int i = 0; i < 8; i++)
                {
                    if (BAG[i].equals("空"))
                    {
                        BAG[i] = "雞蛋";
                        EggNumber -= 1;
                        servant.write(new ChatMessage("站長", message.getSource()+"撿取了雞蛋"));
                        i += 10;
                    }
                    else if (i == 7)
                        servant.write(new ChatMessage("站長", message.getSource()+"的背包已滿"));
                }
            }
            if (EggNumber < 0)
                EggNumber = 0;
        else if (MESG.equals("放置飼料"))
        {
            servant.write(new ChatMessage("站長", message.getSource()+"成功放置飼料"));
            if (ChickenNumber == 0)
                servant.write(new ChatMessage("站長", "由於您沒有雞隻，因此放置飼料並無作用"));
        }
        else
            servant.write(message);
    }

    //在農田的程式碼
    public static void Field(Message message, Servant servant,String MESG, String[] BAG, String station){
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
