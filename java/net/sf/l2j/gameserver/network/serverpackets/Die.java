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
package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2SiegeClan;
import net.sf.l2j.gameserver.model.actor.L2Attackable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;

public class Die extends L2GameServerPacket
{
	private final int _charObjId;
	private final boolean _canTeleport;
	private final boolean _fake;
	
	private boolean _sweepable;
	private boolean _allowFixedRes;
	private L2Clan _clan;
	L2Character _activeChar;
	
	public Die(L2Character cha)
	{
		_activeChar = cha;
		_charObjId = cha.getObjectId();
		_canTeleport = !(((cha instanceof Player)) && CTFEvent.isStarted() && CTFEvent.isPlayerParticipant(_charObjId) || TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(_charObjId) || LMEvent.isStarted() && LMEvent.isPlayerParticipant(_charObjId) || DMEvent.isStarted() && DMEvent.isPlayerParticipant(_charObjId) || cha.isInArenaEvent());
		_fake = !cha.isDead();
		
		if (cha instanceof Player)
		{
			Player player = (Player) cha;
			_allowFixedRes = player.getAccessLevel().allowFixedRes();
			_clan = player.getClan();
			
		}
		else if (cha instanceof L2Attackable)
			_sweepable = ((L2Attackable) cha).isSpoiled();
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake)
			return;
		
		writeC(0x06);
		writeD(_charObjId);
		writeD(_canTeleport ? 0x01 : 0); // to nearest village
		
		if (_canTeleport && _clan != null)
		{
			L2SiegeClan siegeClan = null;
			boolean isInDefense = false;
			
			Castle castle = CastleManager.getInstance().getCastle(_activeChar);
			if (castle != null && castle.getSiege().isInProgress())
			{
				// siege in progress
				siegeClan = castle.getSiege().getAttackerClan(_clan);
				if (siegeClan == null && castle.getSiege().checkIsDefender(_clan))
					isInDefense = true;
			}
			
			writeD(_clan.hasHideout() ? 0x01 : 0x00); // to hide away
			writeD(_clan.hasCastle() || isInDefense ? 0x01 : 0x00); // to castle
			writeD(siegeClan != null && !isInDefense && !siegeClan.getFlags().isEmpty() ? 0x01 : 0x00); // to siege HQ
		}
		else
		{
			writeD(0x00); // to hide away
			writeD(0x00); // to castle
			writeD(0x00); // to siege HQ
		}
		
		writeD(_sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		writeD(_allowFixedRes ? 0x01 : 0x00); // FIXED
	}
}