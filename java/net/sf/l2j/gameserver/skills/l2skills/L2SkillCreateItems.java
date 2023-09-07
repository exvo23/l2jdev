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
package net.sf.l2j.gameserver.skills.l2skills;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PetItemList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.StatsSet;

public class L2SkillCreateItems extends L2Skill
{
	private final int[] _createItemId;
	private final int[] _createItemCount;
	
	public L2SkillCreateItems(StatsSet set)
	{
		super(set);
		_createItemId = set.getIntegerArray("create_item_ids");
		_createItemCount = set.getIntegerArray("create_item_counts");
	}
	
	/**
	 * @see net.sf.l2j.gameserver.model.L2Skill#useSkill(net.sf.l2j.gameserver.model.actor.L2Character, net.sf.l2j.gameserver.model.L2Object[])
	 */
	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		Player player = activeChar.getActingPlayer();
		if (activeChar.isAlikeDead())
			return;
		
		if (activeChar instanceof L2Playable)
		{
			if (_createItemId == null || _createItemCount == null || _createItemId.length != _createItemCount.length)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
				sm.addSkillName(this);
				activeChar.sendPacket(sm);
				return;
			}
			
			for (int i = 0; i < _createItemId.length; i++)
			{
				if (activeChar instanceof L2PetInstance)
				{
					activeChar.getInventory().addItem("Skill", _createItemId[i], _createItemCount[i], player, activeChar);
					player.sendPacket(new PetItemList((L2PetInstance) activeChar));
				}
				else if (activeChar instanceof Player)
					player.addItem("Skill", _createItemId[i], _createItemCount[i], activeChar, true);
			}
		}
	}
}