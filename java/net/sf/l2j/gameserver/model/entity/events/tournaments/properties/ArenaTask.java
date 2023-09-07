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
package net.sf.l2j.gameserver.model.entity.events.tournaments.properties;

import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.events.tournaments.Arena1x1;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.util.Broadcast;

public abstract class ArenaTask
{
	public static L2Spawn _npcSpawn1;
	public static L2Spawn _npcSpawn2;
	
	public static int _bossHeading = 0;
	
	/** The _in progress. */
	public static boolean _started = false;
	
	public static boolean _aborted = false;
	
	public static void SpawnEvent()
	{
		Arena1x1.getInstance().clear();
		
		spawnNpc1();
		
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Solo Event PvP");
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Duration: " + ArenaConfig.TOURNAMENT_TIME + " minute(s)!");
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Reward: " + ItemTable.getInstance().getTemplate(ArenaConfig.ARENA_REWARD_ID).getName());
		
		_aborted = false;
		_started = true;
		ThreadPool.schedule(Arena1x1.getInstance(), 5000);
		
		waiter(ArenaConfig.TOURNAMENT_TIME * 60 * 1000); // minutes for event time
		
		if (!_aborted)
			finishEvent();
	}
	
	public static void finishEvent()
	{
		Broadcast.gameAnnounceToOnlinePlayers("Tournament: Event Finished!");
		
		unspawnNpc1();
		unspawnNpc2();
		_started = false;
		
		ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		
		for (Player player : L2World.getInstance().getL2Players())
		{
			if (player != null && player.isOnline())
			{
				if (player.isArenaProtection())
				{
					ThreadPool.schedule(new Runnable()
					{
						
						@Override
						public void run()
						{
							if (player.isOnline() && !player.isInArenaEvent() && !player.isArenaAttack())
							{
								if (player.isArena1x1())
									Arena1x1.getInstance().remove(player);
								player.setArenaProtection(false);
							}
						}
					}, 25000);
				}
				
				CreatureSay cs = new CreatureSay(player.getObjectId(), Say2.PARTY, "[Tournament]", ("Next Tournament: " + ArenaEvent.getInstance().getNextTime()) + " (GMT-3)."); // 8D
				player.sendPacket(cs);
			}
		}
	}
	
	public static void spawnNpc1()
	{
		NpcTemplate tmpl = NpcTable.getInstance().getTemplate(ArenaConfig.ARENA_NPC);
		
		try
		{
			_npcSpawn1 = new L2Spawn(tmpl);
			
			_npcSpawn1.setLocx(loc1x()); // loc x
			_npcSpawn1.setLocy(loc1y()); // loc y
			_npcSpawn1.setLocz(loc1z()); // loc z
			_npcSpawn1.getAmount();
			_npcSpawn1.setHeading(ArenaConfig.NPC_Heading);
			_npcSpawn1.setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn1, false);
			
			_npcSpawn1.init();
			_npcSpawn1.getLastSpawn().getStatus().setCurrentHp(999999999);
			_npcSpawn1.getLastSpawn().isAggressive();
			_npcSpawn1.getLastSpawn().decayMe();
			_npcSpawn1.getLastSpawn().spawnMe(_npcSpawn1.getLastSpawn().getX(), _npcSpawn1.getLastSpawn().getY(), _npcSpawn1.getLastSpawn().getZ());
			
			_npcSpawn1.getLastSpawn().broadcastPacket(new MagicSkillUse(_npcSpawn1.getLastSpawn(), _npcSpawn1.getLastSpawn(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	public static void unspawnNpc1()
	{
		if (_npcSpawn1 == null)
			return;
		
		_npcSpawn1.getLastSpawn().deleteMe();
		_npcSpawn1.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(_npcSpawn1, true);
	}
	
	public static void unspawnNpc2()
	{
		if (_npcSpawn2 == null)
			return;
		
		_npcSpawn2.getLastSpawn().deleteMe();
		_npcSpawn2.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(_npcSpawn2, true);
	}
	
	public static int loc1x()
	{
		int loc1x = ArenaConfig.NPC_locx;
		return loc1x;
	}
	
	public static int loc1y()
	{
		int loc1y = ArenaConfig.NPC_locy;
		return loc1y;
	}
	
	public static int loc1z()
	{
		int loc1z = ArenaConfig.NPC_locz;
		return loc1z;
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					
					if (_started)
					{
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: Solo Event PvP");
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds / 60 / 60 + " hour(s) till event finish!");
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: Reward: " + ItemTable.getInstance().getTemplate(ArenaConfig.ARENA_REWARD_ID).getName());
						
					}
					break;
				case 1800: // 30 minutes left
				case 900: // 15 minutes left
				case 600: // 10 minutes left
				case 300: // 5 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					// removeOfflinePlayers();
					
					if (_started)
					{
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds / 60 + " minute(s) till event finish!");
					}
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
				case 10: // 10 seconds left
				case 3: // 3 seconds left
				case 2: // 2 seconds left
				case 1: // 1 seconds left
					if (_started)
						Broadcast.gameAnnounceToOnlinePlayers("Tournament: " + seconds + " second(s) till event finish!");
					
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
}