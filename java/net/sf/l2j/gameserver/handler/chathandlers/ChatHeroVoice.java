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
package net.sf.l2j.gameserver.handler.chathandlers;

import java.util.Collection;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * A chat handler
 * @author durgus
 */
public class ChatHeroVoice implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		17
	};
	
	private static boolean _chatDisabled = false;
	
	/**
	 * Handle chat type 'hero voice'
	 * @see net.sf.l2j.gameserver.handler.IChatHandler#handleChat(int, net.sf.l2j.gameserver.model.actor.Player, java.lang.String, java.lang.String)
	 */
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (isChatDisabled() && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessageId.GM_NOTICE_CHAT_DISABLED);
			return;
		}

		if (activeChar.isHero())
		{
	        if (Config.CHAT_HERO_NEED_PVPS)
	        {
	            if (activeChar.getPvpKills() < Config.PVPS_TO_USE_CHAT_HERO)
	            {
	                 activeChar.sendMessage("You must have at least " + Config.PVPS_TO_USE_CHAT_HERO + " PvP to speak in the hero chat.");
	                 return;
	            }
	        }
	        
	        if (!activeChar.isGM() && !activeChar.getFloodProtectors().getHeroVoice().tryPerformAction("heroVoice"))
	        {
	        	activeChar.sendMessage("You must wait " + (activeChar.getFloodProtectors().getHeroVoice().getNextGameTick() - GameTimeController.getInstance().getGameTicks()) / 10 + " seconds to use hero chat.");
	        	return;
	        }
			
			CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			
			Collection<Player> pls = L2World.getInstance().getAllPlayers().values();
			for (Player player : pls)
				player.sendPacket(cs);
		}
	}
	
 	/**
	 * @return Returns the chatDisabled.
	 */
	public static boolean isChatDisabled()
	{
		return _chatDisabled;
	}

	/**
	 * @param chatDisabled The chatDisabled to set.
	 */
	public static void setIsChatDisabled(boolean chatDisabled)
	{
		_chatDisabled = chatDisabled;
	}
	
	/**
	 * Returns the chat types registered to this handler
	 * @see net.sf.l2j.gameserver.handler.IChatHandler#getChatTypeList()
	 */
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}