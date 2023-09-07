/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.datatables.custom;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.FakePc;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FakePcsTable
{
   private static Logger _log = Logger.getLogger(FakePcsTable.class.getName());
   private Map<Integer, FakePc> _fakePcs;
  
   public static FakePcsTable getInstance()
   {
       return SingletonHolder._instance;
   }
  
   protected FakePcsTable()
   {
       _fakePcs = new HashMap<>();
       load();
   }
  
   public void reload()
   {
       _fakePcs.clear();
       load();
   }
  
   private void load()
   {
       try
       {
           File f = new File("./data/xml/fake_pcs.xml");
           Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
          
           Node n = doc.getFirstChild();
           for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
           {
               if (d.getNodeName().equalsIgnoreCase("npc"))
               {
                   FakePc fpc = new FakePc();
                  
                   int npcId = Integer.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
                  
                   for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
                   {
                       if ("appearance".equalsIgnoreCase(cd.getNodeName()))
                       {
                           fpc.name = cd.getAttributes().getNamedItem("name").getNodeValue();
                           fpc.nameColor = Integer.decode("0x" + cd.getAttributes().getNamedItem("name_color").getNodeValue());
                           fpc.title = cd.getAttributes().getNamedItem("title").getNodeValue();
                           fpc.titleColor = Integer.decode("0x" + cd.getAttributes().getNamedItem("title_color").getNodeValue());
                           fpc.radius = Integer.valueOf(cd.getAttributes().getNamedItem("radius").getNodeValue());
                           fpc.height = Integer.valueOf(cd.getAttributes().getNamedItem("height").getNodeValue());
                           fpc.race = Integer.valueOf(cd.getAttributes().getNamedItem("race").getNodeValue());
                           fpc.sex = Integer.valueOf(cd.getAttributes().getNamedItem("sex").getNodeValue());
                           fpc.classId = Integer.valueOf(cd.getAttributes().getNamedItem("class").getNodeValue());
                           fpc.hairStyle = Integer.valueOf(cd.getAttributes().getNamedItem("hair_style").getNodeValue());
                           fpc.hairColor = Integer.valueOf(cd.getAttributes().getNamedItem("hair_color").getNodeValue());
                           fpc.face = Integer.valueOf(cd.getAttributes().getNamedItem("face").getNodeValue());
                           fpc.hero = Byte.parseByte(cd.getAttributes().getNamedItem("hero").getNodeValue());
                           fpc.enchant = Integer.valueOf(cd.getAttributes().getNamedItem("enchant").getNodeValue());
                       }
                       else if ("items".equalsIgnoreCase(cd.getNodeName()))
                       {
                           fpc.rightHand = Integer.valueOf(cd.getAttributes().getNamedItem("right_hand").getNodeValue());
                           fpc.leftHand = Integer.valueOf(cd.getAttributes().getNamedItem("left_hand").getNodeValue());
                           fpc.chest = Integer.valueOf(cd.getAttributes().getNamedItem("chest").getNodeValue());
                           fpc.legs = Integer.valueOf(cd.getAttributes().getNamedItem("legs").getNodeValue());
                           fpc.gloves = Integer.valueOf(cd.getAttributes().getNamedItem("gloves").getNodeValue());
                           fpc.feet = Integer.valueOf(cd.getAttributes().getNamedItem("feet").getNodeValue());
                           fpc.hair = Integer.valueOf(cd.getAttributes().getNamedItem("hair").getNodeValue());
                           fpc.hair2 = Integer.valueOf(cd.getAttributes().getNamedItem("hair2").getNodeValue());
                       }
                       else if ("clan".equalsIgnoreCase(cd.getNodeName()))
                       {
                           fpc.clanId = Integer.valueOf(cd.getAttributes().getNamedItem("clan_id").getNodeValue());
                           fpc.clanCrest = Integer.valueOf(cd.getAttributes().getNamedItem("clan_crest").getNodeValue());
                           fpc.allyId = Integer.valueOf(cd.getAttributes().getNamedItem("ally_id").getNodeValue());
                           fpc.allyCrest = Integer.valueOf(cd.getAttributes().getNamedItem("ally_crest").getNodeValue());
                           fpc.pledge = Integer.valueOf(cd.getAttributes().getNamedItem("pledge").getNodeValue());
                       }
                      
                   }
                  
                   _fakePcs.put(npcId, fpc);
               }
           }
       }
       catch (Exception e)
       {
           _log.log(Level.WARNING, "FakePcsTable: Error loading from database:" + e.getMessage(), e);
       }
      
       _log.info("FakePcsTable: Loaded " + _fakePcs.size() + " NPC to PC templates.");
   }
  
   public FakePc getFakePc(int npcId)
   {
       return _fakePcs.get(npcId);
   }
  
   private static class SingletonHolder
   {
       protected static final FakePcsTable _instance = new FakePcsTable();
   }
}