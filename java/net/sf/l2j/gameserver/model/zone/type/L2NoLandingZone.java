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
package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * A no landing zone
 * @author durgus
 */
public class L2NoLandingZone extends L2ZoneType
{
	private int dismountDelay = 5;
	
	public L2NoLandingZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dismountDelay"))
			dismountDelay = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.NO_LANDING, true);
			if (((Player) character).getMountType() == 2)
			{
				character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN));
				((Player) character).enteredNoLanding(dismountDelay);
				((Player) character).teleToLocation(83597, 147888, -3405, 0);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.NO_LANDING, false);
			if (((Player) character).getMountType() == 2)
				((Player) character).exitedNoLanding();
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
}