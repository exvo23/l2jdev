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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.instancemanager.CharacterKillingManager;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.L2PcPolymorph;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;

/**
 * @author paytaly
 */
public class L2TopPKMonumentInstance extends L2PcPolymorph
{
	public L2TopPKMonumentInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
		if (Config.CKM_ENABLED)
		{
			CharacterKillingManager.getInstance().addPKMorphListener(this);
		}
	}

	@Override
	public void deleteMe()
	{
		super.deleteMe();
		if (Config.CKM_ENABLED)
		{
			CharacterKillingManager.getInstance().removePKMorphListener(this);
		}
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the Player player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			// Calculate the distance between the Player and the L2Npc
			if (!canInteract(player))
			{
				// Set the Player Intention to INTERACT
				player.getAI().setIntention(CtrlIntention.INTERACT, this);
			}
			else
			{
				// Some guards have no HTMs on retail. Bypass the chat window if such guard is met.
				switch (getNpcId())
				{

				    case 50018:
						// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
						player.sendPacket(ActionFailed.STATIC_PACKET);
						return;
				}
				
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, L2Npc.INTERACTION_DISTANCE));
			}
		}
	}
}