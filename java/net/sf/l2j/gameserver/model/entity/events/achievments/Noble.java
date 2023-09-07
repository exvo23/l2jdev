package net.sf.l2j.gameserver.model.entity.events.achievments;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.events.achievments.base.Condition;

/**
 * @author Matim
 * @version 1.0
 */
public class Noble extends Condition
{
	public Noble(Object value)
	{
		super(value);
		setName("Noble");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
			return false;

		if (player.isNoble())
			return true;

		return false;
	}
}