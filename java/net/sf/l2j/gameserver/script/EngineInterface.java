/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.script;

import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2World;

/**
 * @author Luis Arias
 */
public interface EngineInterface
{
	public CharNameTable charNametable = CharNameTable.getInstance();

	public IdFactory idFactory = IdFactory.getInstance();
	public ItemTable itemTable = ItemTable.getInstance();
	public SkillTable skillTable = SkillTable.getInstance();
	public RecipeTable recipeTable = RecipeTable.getInstance();
	public SkillTreeTable skillTreeTable = SkillTreeTable.getInstance();
	public CharTemplateTable charTemplates = CharTemplateTable.getInstance();
	public ClanTable clanTable = ClanTable.getInstance();
	public NpcTable npcTable = NpcTable.getInstance();
	public TeleportLocationTable teleTable = TeleportLocationTable.getInstance();
	public L2World world = L2World.getInstance();
	public SpawnTable spawnTable = SpawnTable.getInstance();
	public GameTimeController gameTimeController = GameTimeController.getInstance();
	public AnnouncementTable announcements = AnnouncementTable.getInstance();
	public MapRegionTable mapRegions = MapRegionTable.getInstance();

	public void addQuestDrop(int npcID, int itemID, int min, int max, int chance, String questID, String[] states);

	public void addEventDrop(int[] items, int[] count, double chance, DateRange range);

	public void onPlayerLogin(String[] message, DateRange range);

}
