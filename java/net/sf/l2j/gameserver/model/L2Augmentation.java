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
package net.sf.l2j.gameserver.model;

import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.AugmentationData.AugStat;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.gameserver.skills.basefuncs.FuncAdd;
import net.sf.l2j.gameserver.skills.basefuncs.LambdaConst;

/**
 * Used to store an augmentation and its boni
 * @author durgus
 */
public final class L2Augmentation
{
	private int _effectsId = 0;
	private AugmentationStatBoni _boni = null;
	private L2Skill _skill = null;
	
	public L2Augmentation(int effects, L2Skill skill)
	{
		_effectsId = effects;
		_boni = new AugmentationStatBoni(_effectsId);
		_skill = skill;
	}
	
	public L2Augmentation(int effects, int skill, int skillLevel)
	{
		this(effects, skill != 0 ? SkillTable.getInstance().getInfo(skill, skillLevel) : null);
	}
	
	public static class AugmentationStatBoni
	{
		private final Stats _stats[];
		private final float _values[];
		private boolean _active;
		
		public AugmentationStatBoni(int augmentationId)
		{
			_active = false;
			List<AugStat> as = AugmentationData.getInstance().getAugStatsById(augmentationId);
			
			_stats = new Stats[as.size()];
			_values = new float[as.size()];
			
			int i = 0;
			for (AugStat aStat : as)
			{
				_stats[i] = aStat.getStat();
				_values[i] = aStat.getValue();
				i++;
			}
		}
		
		public void applyBonus(Player player)
		{
			// make sure the bonuses are not applied twice..
			if (_active)
				return;
			
			for (int i = 0; i < _stats.length; i++)
				((L2Character) player).addStatFunc(new FuncAdd(_stats[i], 0x40, this, new LambdaConst(_values[i])));
			
			_active = true;
		}
		
		public void removeBonus(Player player)
		{
			// make sure the bonuses are not removed twice
			if (!_active)
				return;
			
			((L2Character) player).removeStatsByOwner(this);
			
			_active = false;
		}
	}
	
	public int getAttributes()
	{
		return _effectsId;
	}
	
	/**
	 * Get the augmentation "id" used in serverpackets.
	 * @return augmentationId
	 */
	public int getAugmentationId()
	{
		return _effectsId;
	}
	
	public L2Skill getSkill()
	{
		return _skill;
	}
	
	/**
	 * Applies the bonuses to the player.
	 * @param player
	 */
	public void applyBonus(Player player)
	{
		boolean updateTimeStamp = false;
		_boni.applyBonus(player);
		
		// add the skill if any
		if (_skill != null)
		{
			player.addSkill(_skill);
			if (_skill.isActive())
			{
				if (player.getReuseTimeStamp().containsKey(_skill.getReuseHashCode()))
				{
					final long delay = player.getReuseTimeStamp().get(_skill.getReuseHashCode()).getRemaining();
					if (delay > 0)
					{
						player.disableSkill(_skill, delay);
						updateTimeStamp = true;
					}
				}
			}
			player.sendSkillList();
			if (updateTimeStamp)
				player.sendPacket(new SkillCoolTime(player));
		}
	}
	
	/**
	 * Removes the augmentation bonuses from the player.
	 * @param player
	 */
	public void removeBonus(Player player)
	{
		_boni.removeBonus(player);
		
		// remove the skill if any
		if (_skill != null)
		{
			if (_skill.isPassive())
				player.removeSkill(_skill, false, true);
			else
				player.removeSkill(_skill, false, false);
			
			if (_skill.isPassive() && Config.DELETE_AUGM_PASSIVE_ON_CHANGE || _skill.isActive() && Config.DELETE_AUGM_ACTIVE_ON_CHANGE)
			{
				// Iterate through all effects currently on the character.
				final L2Effect[] effects = player.getAllEffects();
				
				for (final L2Effect currenteffect : effects)
				{
					final L2Skill effectSkill = currenteffect.getSkill();
					
					if (effectSkill.getId() == _skill.getId())
					{
						player.sendMessage("You feel the power of " + effectSkill.getName() + " leaving yourself.");
						currenteffect.exit(false);
					}
				}
			}
			player.sendSkillList();
		}
	}
}