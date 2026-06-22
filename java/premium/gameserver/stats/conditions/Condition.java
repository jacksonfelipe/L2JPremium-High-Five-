package premium.gameserver.stats.conditions;

import premium.commons.annotations.Nullable;
import premium.gameserver.network.serverpackets.SysMsgContainer;
import premium.gameserver.network.serverpackets.SystemMessage2;
import premium.gameserver.network.serverpackets.components.IStaticPacket;
import premium.gameserver.network.serverpackets.components.SystemMsg;
import premium.gameserver.stats.Env;

public abstract class Condition
{
	public static final Condition[] EMPTY_ARRAY = new Condition[0];
	
	private SystemMsg _message;
	
	public final void setSystemMsg(int msgId)
	{
		_message = SystemMsg.valueOf(msgId);
	}
	
	public final SystemMsg getSystemMsg()
	{
		return _message;
	}
	
	@Nullable
	public IStaticPacket getSystemMsg(SysMsgContainer.IArgument... arguments)
	{
		if (_message == null)
		{
			return null;
		}
		if (arguments.length == 0 || _message.size() == 0)
		{
			return _message;
		}
		final SystemMessage2 msg = new SystemMessage2(_message);
		for (SysMsgContainer.IArgument argument : arguments)
		{
			msg.add(argument);
		}
		return msg;
	}
	
	public final boolean test(Env env)
	{
		return testImpl(env);
	}
	
	protected abstract boolean testImpl(Env env);
}
