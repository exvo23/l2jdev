package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExColosseumFenceInfoPacket;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;

public final class L2FenceInstance extends L2Object
{
   private int _type;
   private int _width;
   private int _length;
   private int xMin;
   private int xMax;
   private int yMin;
   private int yMax;

   public L2FenceInstance(int objectId, int type, int width, int length, int x, int y) 
   {
       super(objectId);
       _type = type;
       _width = width;
       _length = length;

       xMin = x - width/2;
       xMax = x + width/2;
       yMin = y - length/2;
       yMax = y + length/2;
       System.out.println(xMin+ " "+ yMin+ " "+ xMax + " "+ yMax);
   }

   public boolean isBetween(int x, int y, int tx, int ty) {
       if (x < xMin && tx < xMin)
           return false;

       if (x > xMax && tx > xMax)
           return false;

       if (y < yMin && ty < yMin)
           return false;

       if (y > yMax && ty > yMax)
           return false;

       if(x > xMin && tx > xMin && x < xMax && tx < xMax)
       {
           if(y > yMin && ty > yMin && y < yMax && ty < yMax)
               return false;
       }

       boolean atLeastACross = crossLinePart(xMin,yMin,xMax,yMin,x,y,tx,ty)
               ||crossLinePart(xMax,yMin,xMax,yMax,x,y,tx,ty)
               ||crossLinePart(xMax,yMax,xMin,yMax,x,y,tx,ty)
               ||crossLinePart(xMin,yMax,xMin,yMin,x,y,tx,ty);
       
       if(atLeastACross)
           return true;
       
       return false;
   }

   private boolean crossLinePart(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4)
   {
       float[] result = intersection(x1,y1, x2,y2, x3,y3,x4,y4);
       
       if(result == null)
           return false;
       
       float xCross = result[0];
       float yCross = result[1];

       if (xCross <= xMax && xCross >= xMin)
           return true;

       if (yCross <= yMax && yCross >= yMin)
           return true;
       
       return false;
   }

   @SuppressWarnings("static-method")
private float[] intersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) 
   {
       float d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
       if (d == 0)
           return null;

       float xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2)
               * (x3 * y4 - y3 * x4))
               / d;
       float yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2)
               * (x3 * y4 - y3 * x4))
               / d;

       float[] result = new float[2];
       result[0] = xi;
       result[1] = yi;
       return result;
   }
   
   
   @Override
   public void sendInfo(Player activeChar)
   {
       activeChar.sendPacket(new ExColosseumFenceInfoPacket(this));
   }
   
   public int getType()
   {
       return _type;
   }
   
   public int getWidth()
   {
       return _width;
   }
  
   public int getLength()
   {
       return _length;
   }
   
   @Override
   public boolean isAutoAttackable(L2Character attacker)
   {
       return false;
   }
   
   public void onActionShift(L2GameClient client)
   {
       Player player = client.getActiveChar();
       if (player == null)
           return;
      
       if (player.getAccessLevel().isGm())
       {
           // Set the target of the Player player
           player.setTarget(this);
           
           // Send a Server->Client packet MyTargetSelected to the Player player
           MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
           player.sendPacket(my);
           
           //player.sendMessage("ObjectId of targeted fence: " + getObjectId());
       }
       else
           player.sendPacket(ActionFailed.STATIC_PACKET);
   }
   
}