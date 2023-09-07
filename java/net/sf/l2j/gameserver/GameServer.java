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
package net.sf.l2j.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.Server;
import net.sf.l2j.Team;
import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.cache.ImagesCache;
import net.sf.l2j.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.sf.l2j.gameserver.datatables.AccessLevels;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.ArmorSetsTable;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.BookmarkTable;
import net.sf.l2j.gameserver.datatables.BufferTable;
import net.sf.l2j.gameserver.datatables.BuyListTable;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.FishTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.HelperBuffTable;
import net.sf.l2j.gameserver.datatables.HennaTable;
import net.sf.l2j.gameserver.datatables.HerbDropTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.NpcWalkerRoutesTable;
import net.sf.l2j.gameserver.datatables.PetDataTable;
import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.datatables.SpellbookTable;
import net.sf.l2j.gameserver.datatables.StaticObjects;
import net.sf.l2j.gameserver.datatables.SummonItemsData;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.datatables.custom.AntiBotTable;
import net.sf.l2j.gameserver.datatables.custom.EnchantTable;
import net.sf.l2j.gameserver.datatables.custom.EnchantVipTable;
import net.sf.l2j.gameserver.datatables.custom.FakePcsTable;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.datatables.custom.OfflineTradersTable;
import net.sf.l2j.gameserver.datatables.custom.SkipTable;
import net.sf.l2j.gameserver.datatables.custom.SoulCrystalData;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.SkillHandler;
import net.sf.l2j.gameserver.handler.TutorialHandler;
import net.sf.l2j.gameserver.handler.UserCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.handler.password.PasswordChanger;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.AuctionManager;
import net.sf.l2j.gameserver.instancemanager.AutoSpawnManager;
import net.sf.l2j.gameserver.instancemanager.BoatManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CastleManorManager;
import net.sf.l2j.gameserver.instancemanager.CharacterKillingManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.ClassDamageManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.instancemanager.DayNightSpawnManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.FishingChampionshipManager;
import net.sf.l2j.gameserver.instancemanager.FourSepulchersManager;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import net.sf.l2j.gameserver.instancemanager.MercTicketManager;
import net.sf.l2j.gameserver.instancemanager.MovieMakerManager;
import net.sf.l2j.gameserver.instancemanager.OlyClassDamageManager;
import net.sf.l2j.gameserver.instancemanager.PetitionManager;
import net.sf.l2j.gameserver.instancemanager.QuestManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossInfoManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossPointsManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.SevenSignsFestival;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.AchievementsManager;
import net.sf.l2j.gameserver.instancemanager.custom.DropZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.HwidManager;
import net.sf.l2j.gameserver.instancemanager.custom.IPManager;
import net.sf.l2j.gameserver.instancemanager.custom.ZergManager;
import net.sf.l2j.gameserver.instancemanager.games.MonsterRace;
import net.sf.l2j.gameserver.model.L2Manor;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.entity.events.ColorSystem;
import net.sf.l2j.gameserver.model.entity.events.PCBangPoint;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFManager;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMConfig;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMManager;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMConfig;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMManager;
import net.sf.l2j.gameserver.model.entity.events.partyfarm.InitialPartyFarm;
import net.sf.l2j.gameserver.model.entity.events.partyfarm.PartyFarm;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTManager;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaConfig;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaEvent;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaTask;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchRoomList;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchWaitingList;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GamePacketHandler;
import net.sf.l2j.gameserver.pathfinding.PathFinding;
import net.sf.l2j.gameserver.script.EventDroplist;
import net.sf.l2j.gameserver.script.faenor.FaenorScriptEngine;
import net.sf.l2j.gameserver.scripting.L2ScriptEngineManager;
import net.sf.l2j.gameserver.taskmanager.ItemsAutoDestroyTaskManager;
import net.sf.l2j.gameserver.taskmanager.KnownListUpdateTaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;
import net.sf.l2j.util.Util;

import org.mmocore.network.SelectorConfig;
import org.mmocore.network.SelectorThread;

import Dev.Config.ConfigDev;
import Dev.Dungeon.DungeonManager;
import Dev.Dungeon.InstanceManager;
import hwid.Hwid;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;

public class GameServer
{
	private static final Logger _log = Logger.getLogger(GameServer.class.getName());
	
	final long serverLoadStart = System.currentTimeMillis();
	
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	private final LoginServerThread _loginThread;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public GameServer() throws Exception
	{
		gameServer = this;
		
		IdFactory.getInstance();
		new File("./data/crests").mkdirs();
		
		Util.printSection("World");
		GameTimeController.getInstance();
		L2World.getInstance();
		MapRegionTable.getInstance();
		AnnouncementTable.getInstance();
		BookmarkTable.getInstance();
		EventDroplist.getInstance();
		FaenorScriptEngine.getInstance();
		
		Util.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		
		Util.printSection("Items");
		IconTable.getInstance();
		ItemTable.getInstance();
		SummonItemsData.getInstance();
		BuyListTable.getInstance();
		MultisellData.getInstance();
		RecipeTable.getInstance();
		ArmorSetsTable.getInstance();
		FishTable.getInstance();
		SpellbookTable.getInstance();
		EnchantTable.getInstance();
		EnchantVipTable.getInstance();
		SoulCrystalData.getInstance();
		
		Util.printSection("Augments");
		AugmentationData.getInstance();
		
		Util.printSection("Characters");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		CharTemplateTable.getInstance();
		CharNameTable.getInstance();
		GmListTable.getInstance();
		RaidBossPointsManager.getInstance();
		
		Util.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			_log.config("Community server is disabled.");
		
		Util.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.load();
		ImagesCache.getInstance();
		TeleportLocationTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		PetitionManager.getInstance();
		HennaTable.getInstance();
		HelperBuffTable.getInstance();
		CursedWeaponsManager.getInstance();
		SkipTable.getInstance();
		
		Util.printSection("Clans");
		ClanTable.getInstance();
		AuctionManager.getInstance();
		ClanHallManager.getInstance();
		
		Util.printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
			PathFinding.getInstance();
		
		Util.printSection("World Bosses");
		GrandBossManager.getInstance();
		
		Util.printSection("Zones");
		ZoneManager.getInstance();
		GrandBossManager.getInstance().initZones();
		
		Util.printSection("Castles");
		CastleManager.getInstance().load();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		
		Util.printSection("Sieges");
		SiegeManager.getInstance();
		SiegeManager.getSieges();
		MercTicketManager.getInstance();
		
		Util.printSection("Manor Manager");
		CastleManorManager.getInstance();
		L2Manor.getInstance();
		
		Util.printSection("NPCs");
		BufferTable.getInstance();
		HerbDropTable.getInstance();
		PetDataTable.getInstance();
		NpcTable.getInstance();
		NpcWalkerRoutesTable.getInstance();
		DoorTable.getInstance();
		StaticObjects.load();
		SpawnTable.getInstance();
		RaidBossSpawnManager.getInstance();
		DayNightSpawnManager.getInstance().trim().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		FakePcsTable.getInstance();
		RaidBossInfoManager.getInstance();
		
		Util.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		Hero.getInstance();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance().init();
		
		Util.printSection("Quests & Scripts");
		QuestManager.getInstance();
		BoatManager.getInstance();
		
		if (!Config.ALT_DEV_NO_SCRIPTS)
		{
			try
			{
				File scripts = new File("./data/scripts.cfg");
				L2ScriptEngineManager.getInstance().executeScriptList(scripts);
			}
			catch (IOException ioe)
			{
				_log.severe("Failed loading scripts.cfg, no script going to be loaded");
			}
			QuestManager.getInstance().report();
		}
		else
			_log.config("QuestManager: Skipping scripts.");
		
		if (Config.SAVE_DROPPED_ITEM)
			ItemsOnGroundManager.getInstance();
		
		if (Config.ITEM_AUTO_DESTROY_TIME > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
			ItemsAutoDestroyTaskManager.getInstance();
		
		Util.printSection("Monster Derby Track");
		MonsterRace.getInstance();
		
		Util.printSection("Handlers");
		_log.config("AutoSpawnHandler: Loaded " + AutoSpawnManager.getInstance().size() + " handlers.");
		_log.config("AdminCommandHandler: Loaded " + AdminCommandHandler.getInstance().size() + " handlers.");
		_log.config("ChatHandler: Loaded " + ChatHandler.getInstance().size() + " handlers.");
		_log.config("ItemHandler: Loaded " + ItemHandler.getInstance().size() + " handlers.");
		_log.config("SkillHandler: Loaded " + SkillHandler.getInstance().size() + " handlers.");
		_log.config("TutorialHandler: Loaded " + TutorialHandler.getInstance().size() + " handlers.");
		_log.config("UserCommandHandler: Loaded " + UserCommandHandler.getInstance().size() + " handlers.");
		_log.config("VoicedCommandHandler: Loaded " + VoicedCommandHandler.getInstance().size() + " handlers.");
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance();
		
		Util.printSection("Custom");
		IPManager.getInstance();
		HwidManager.getInstance();
		ZergManager.getInstance();
		
		DungeonManager.getInstance();
		InstanceManager.getInstance();
		
		DropZoneManager.getInstance();
		PasswordChanger.getInstance();
		AchievementsManager.getInstance();
		AntiBotTable.getInstance().loadImage();
		ColorSystem.getInstance();
		
		if (Config.ENABLE_CLASS_DAMAGES)
			ClassDamageManager.loadConfig();
		
		if (Config.ENABLE_OLY_CLASS_DAMAGES)
			OlyClassDamageManager.loadConfig();
		
		Util.printSection("Events");
		
		if (Config.PCB_ENABLE)
			ThreadPool.scheduleAtFixedRate(PCBangPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			OfflineTradersTable.restoreOfflineTraders();
		
		if (Config.CKM_ENABLED)
			CharacterKillingManager.getInstance().init();
		
		if (Config.PARTY_FARM_BY_TIME_OF_DAY)
		{
			InitialPartyFarm.getInstance().StartCalculationOfNextEventTime();
			_log.info("[Party Farm Time]: Enabled");
		}
		else if (Config.START_PARTY)
		{
			_log.info("[Start Spawn Party Farm]: Enabled");
			ThreadPool.schedule(new SpawnMonsters(), Config.NPC_SERVER_DELAY * 1000);
		}
		
		ArenaConfig.init();
		if (ArenaConfig.TOURNAMENT_EVENT_TIME)
		{
			_log.info("Tournament Event is enabled.");
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else if (ArenaConfig.TOURNAMENT_EVENT_START)
		{
			_log.info("Tournament Event is enabled.");
			ArenaTask.spawnNpc1();
		}
		else
			_log.info("Tournament Event is disabled");
		
		CTFConfig.init();
		CTFManager.getInstance();
		
		DMConfig.init();
		DMManager.getInstance();
		
		LMConfig.init();
		LMManager.getInstance();
		
		TvTConfig.init();
		TvTManager.getInstance();
		
		Util.printSection("Fake Players");
		FakePlayerConfig.init();
		FakePlayerManager.initialise();
		
		Util.printSection("HwId");
		Hwid.Init();
		
		Util.printSection("System");
		TaskManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		_log.config("IdFactory: Free ObjectIDs remaining: " + IdFactory.getInstance().size());
		
		KnownListUpdateTaskManager.getInstance();
		MovieMakerManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_log.info("Deadlock detector is enabled. Timer: " + Config.DEADLOCK_CHECK_INTERVAL + "s.");
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_log.info("Deadlock detector is disabled.");
			_deadDetectThread = null;
		}
		
		System.gc();
		
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		
		Util.printSection("L2JDev - Status");
		_log.info("Gameserver have started, used memory: " + usedMem + " / " + totalMem + " Mo.");
		_log.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		_log.info("Server Loaded in " + ((System.currentTimeMillis() - serverLoadStart) / 1000) + " seconds.");
		
		Util.printSection("Login");
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				_log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all available IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		Server.serverMode = Server.MODE_GAMESERVER;
		
		final String LOG_FOLDER = "./log"; // Name of folder for log file
		final String LOG_NAME = "config/log.cfg"; // Name of log file
		
		// Create log folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		InputStream is = new FileInputStream(new File(LOG_NAME));
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		
		Team.info();
		
		// Initialize config
		Config.load();
		ConfigDev.loadGameServer();
		
		// Factories
		XMLDocumentFactory.getInstance();
		L2DatabaseFactory.getInstance();
		Util.printSection("ThreadPool");
		ThreadPool.init();
		gameServer = new GameServer();
	}
	
	public class SpawnMonsters implements Runnable
	{
		public SpawnMonsters()
		{
			
		}
		
		@Override
		public void run()
		{
			PartyFarm._aborted = false;
			PartyFarm._started = true;
			PartyFarm.spawnMonsters();
		}
	}
}