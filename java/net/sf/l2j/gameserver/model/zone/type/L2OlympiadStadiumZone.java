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

import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameTask;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import net.sf.l2j.gameserver.network.serverpackets.ExOlympiadUserInfo;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * An olympiad stadium
 * @author durgus, DS
 */
public class L2OlympiadStadiumZone extends L2SpawnZone
{
	OlympiadGameTask _task = null;
	
	public L2OlympiadStadiumZone(int id)
	{
		super(id);
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public final void broadcastStatusUpdate(Player player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (Player plyr : getKnownTypeInside(Player.class))
		{
			if (!plyr.isInsideZone(ZoneId.TOURNAMENT) && plyr.inObserverMode() || plyr.getOlympiadSide() != player.getOlympiadSide())
				plyr.sendPacket(packet);
		}
	}
	
	public final void broadcastPacketToObservers(L2GameServerPacket packet)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (!player.isInsideZone(ZoneId.TOURNAMENT) && player.inObserverMode())
				player.sendPacket(packet);
		}
	}
	
	@Override
	protected final void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		character.setInsideZone(ZoneId.NO_RESTART, true);
		character.setInsideZone(ZoneId.OLYMPIAD, true);
		
		if (character instanceof Player)
		{
			final Player player = character.getActingPlayer();
			{
				if (player.isOlympiadProtection() || player.inObserverMode() && !player.isArenaProtection() && !player.isArena1x1() && !player.isArenaAttack() && !player.isInArenaEvent() && !player.isInArena() && !player.isArenaObserv())
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							if (!player.isInsideZone(ZoneId.PEACE))
							{
								if (_task != null)
								{
									if (_task.isBattleStarted())
									{
										character.setInsideZone(ZoneId.PVP, true);
										if (character instanceof Player)
										{
											character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
											_task.getGame().sendOlympiadInfo(character);
										}
									}
								}
								
								if (character instanceof L2Playable)
								{
									// only participants, observers and GMs allowed
									if (!player.isGM() && !player.isInOlympiadMode() && !player.inObserverMode() && !player.isArenaProtection() && !player.isArena1x1() && !player.isArenaAttack() && !player.isInArenaEvent() && !player.isInArena() && !player.isArenaObserv())
										ThreadPool.execute(new KickPlayer(player));
								}
							}
						}
					}, 2000);
				}
			}
		}
		
	}
	
	@Override
	protected final void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		character.setInsideZone(ZoneId.NO_RESTART, false);
		character.setInsideZone(ZoneId.OLYMPIAD, false);
		
		if (character instanceof Player)
		{
			final Player player = character.getActingPlayer();
			{
				if (player.isOlympiadProtection() || player.inObserverMode())
				{
					if (_task != null)
					{
						if (_task.isBattleStarted())
						{
							character.setInsideZone(ZoneId.PVP, false);
							if (character instanceof Player)
							{
								character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
								character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
							}
						}
					}
				}
			}
		}
		/*
		 * if (_task != null) { if (_task.isBattleStarted()) { character.setInsideZone(ZoneId.PVP, false); if (character instanceof Player) { character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE)); character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET); } } }
		 */
	}
	
	public final void updateZoneStatusForCharactersInside()
	{
		if (_task == null)
			return;
		
		final boolean battleStarted = _task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE);
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE);
		
		for (L2Character character : _characterList)
		{
			if (character == null)
				continue;
			
			if (battleStarted)
			{
				character.setInsideZone(ZoneId.PVP, true);
				if (character instanceof Player)
					character.sendPacket(sm);
			}
			else
			{
				character.setInsideZone(ZoneId.PVP, false);
				if (character instanceof Player)
				{
					character.sendPacket(sm);
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
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
	
	private static final class KickPlayer implements Runnable
	{
		private Player _player;
		
		public KickPlayer(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player != null)
			{
				final L2Summon summon = _player.getPet();
				if (summon != null)
					summon.unSummon(_player);
				
				_player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				_player = null;
			}
		}
	}
}