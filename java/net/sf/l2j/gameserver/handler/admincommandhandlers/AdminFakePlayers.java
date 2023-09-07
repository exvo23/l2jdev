package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import phantom.FakePlayer;
import phantom.FakePlayerManager;
import phantom.FakePlayerTaskManager;
import phantom.ai.EnchanterAI;
import phantom.ai.walker.CitizenAI;
import phantom.ai.walker.PrivateStoreBuyAI;
import phantom.ai.walker.PrivateStoreSellAI;

public class AdminFakePlayers implements IAdminCommandHandler
{
	private final String fakesFolder = "data/html/admin/";
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_robot",
		"admin_deleterobot",
		"admin_deleteAllrobots",
		"admin_spawncitizen",
		"admin_spawnenchanter",
		"admin_spawnclass",
		"admin_takecontrol",
		"admin_releasecontrol",
		"admin_spawnPrivateStore"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showFakeDashboard(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(fakesFolder + "fakeplayer.htm");
		html.replace("%fakecount%", FakePlayerManager.getFakePlayersCount());
		html.replace("%taskcount%", FakePlayerTaskManager.INSTANCE.getTaskCount());
		activeChar.sendPacket(html);
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_robot"))
		{
			showFakeDashboard(activeChar);
		}
		
		if (command.startsWith("admin_deleterobot"))
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer)
			{
				FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();
				fakePlayer.despawnPlayer();
				showFakeDashboard(activeChar);
			}
			showFakeDashboard(activeChar);
		}
		
		if (command.startsWith("admin_deleteAllrobots"))
		{
			int counter = 0;
			for (FakePlayer fakePlayer : FakePlayerManager.getFakePlayers())
			{
				counter++;
				fakePlayer.despawnPlayer();
			}
			activeChar.sendMessage("A total of " + counter + " fake players have been kicked.");
			showFakeDashboard(activeChar);
		}
		
		if (command.startsWith("admin_spawnenchanter"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
					case "solo":
						FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeSoloPlayer.setFakeAi(new EnchanterAI(fakeSoloPlayer));
						break;
					
					case "clan":
						FakePlayer fakeClanPlayer = FakePlayerManager.spawnClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeClanPlayer.setFakeAi(new EnchanterAI(fakeClanPlayer));
						break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_spawnPrivateStore"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
					case "buy":
						FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPrivatePlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeSoloPlayer.setFakeAi(new PrivateStoreBuyAI(fakeSoloPlayer));
						showFakeDashboard(activeChar);
						
						break;
					
					case "sell":
						FakePlayer fakeClanPlayer = FakePlayerManager.spawnPrivatePlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeClanPlayer.setFakeAi(new PrivateStoreSellAI(fakeClanPlayer));
						showFakeDashboard(activeChar);
						break;
					case "Clanbuy":
						FakePlayer fakeClan1Player = FakePlayerManager.spawnPrivateClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeClan1Player.setFakeAi(new PrivateStoreBuyAI(fakeClan1Player));
						showFakeDashboard(activeChar);
						
						break;
					
					case "Clansell":
						FakePlayer fakeClan2Player = FakePlayerManager.spawnPrivateClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeClan2Player.setFakeAi(new PrivateStoreSellAI(fakeClan2Player));
						showFakeDashboard(activeChar);
						break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_spawncitizen"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
					case "solo":
						FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeSoloPlayer.setFakeAi(new CitizenAI(fakeSoloPlayer));
						showFakeDashboard(activeChar);
						
						break;
					
					case "clan":
						FakePlayer fakeClanPlayer = FakePlayerManager.spawnClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						fakeClanPlayer.setFakeAi(new CitizenAI(fakeClanPlayer));
						showFakeDashboard(activeChar);
						break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_spawnclass"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
					// Without Clan
					case "archer":
						FakePlayer archerFake = FakePlayerManager.spawnArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerFake.assignDefaultAI();
						break;
					case "nuker":
						FakePlayer nukerFake = FakePlayerManager.spawnNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerFake.assignDefaultAI();
						break;
					case "warrior":
						FakePlayer warriorFake = FakePlayerManager.spawnWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorFake.assignDefaultAI();
						break;
					case "dagger":
						FakePlayer daggerFake = FakePlayerManager.spawnDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerFake.assignDefaultAI();
						break;
					// With Clan
					case "archer_clan":
						FakePlayer archerClanFake = FakePlayerManager.spawnClanArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerClanFake.assignDefaultAI();
						break;
					case "nuker_clan":
						FakePlayer nukerClanFake = FakePlayerManager.spawnClanNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerClanFake.assignDefaultAI();
						break;
					case "warrior_clan":
						FakePlayer warriorClanFake = FakePlayerManager.spawnClanWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorClanFake.assignDefaultAI();
						break;
					case "dagger_clan":
						FakePlayer daggerClanFake = FakePlayerManager.spawnClanDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerClanFake.assignDefaultAI();
						break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_takecontrol"))
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer)
			{
				FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();
				fakePlayer.setUnderControl(true);
				activeChar.setPlayerUnderControl(fakePlayer);
				activeChar.sendMessage("You are now controlling: " + fakePlayer.getName());
			}
			else
			{
				activeChar.sendMessage("You can only take control of a Fake Player");
			}
		}
		
		if (command.startsWith("admin_releasecontrol"))
		{
			if (activeChar.isControllingFakePlayer())
			{
				FakePlayer fakePlayer = activeChar.getPlayerUnderControl();
				activeChar.sendMessage("You are no longer controlling: " + fakePlayer.getName());
				fakePlayer.setUnderControl(false);
				activeChar.setPlayerUnderControl(null);
				
			}
			else
			{
				activeChar.sendMessage("You are not controlling a Fake Player");
			}
		}
		
		return true;
	}
}