package npc.model.birthday;

import java.util.concurrent.Future;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.model.Player;
import premium.gameserver.model.Skill;
import premium.gameserver.model.World;
import premium.gameserver.model.instances.NpcInstance;
import premium.gameserver.tables.SkillTable;
import premium.gameserver.templates.npc.NpcTemplate;

/**
 * @author claww
 * @date 21.01.2013
 */
@SuppressWarnings("serial")
public class BirthDayCakeInstance extends NpcInstance
{
	private static final Skill SKILL = SkillTable.getInstance().getInfo(22035, 1);
	
	private class CastTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (Player player : World.getAroundPlayers(BirthDayCakeInstance.this, 500, 100))
			{
				if (player.getEffectList().getEffectsBySkill(SKILL) != null)
				{
					continue;
				}
				
				SKILL.getEffects(BirthDayCakeInstance.this, player, false, false);
			}
		}
	}
	
	private Future<?> _castTask;
	
	public BirthDayCakeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setTargetable(false);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		_castTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CastTask(), 1000L, 1000L);
	}
	
	@Override
	public void onDespawn()
	{
		super.onDespawn();
		
		_castTask.cancel(false);
		_castTask = null;
	}
}
