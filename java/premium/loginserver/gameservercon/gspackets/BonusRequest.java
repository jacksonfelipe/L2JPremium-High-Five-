package premium.loginserver.gameservercon.gspackets;

import premium.loginserver.accounts.Account;
import premium.loginserver.gameservercon.ReceivablePacket;

public class BonusRequest extends ReceivablePacket
{
	private String account;
	private double bonus;
	private int bonusExpire;
	
	@Override
	protected void readImpl()
	{
		account = readS();
		bonus = readF();
		bonusExpire = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Account acc = new Account(account);
		acc.restore();
		acc.setBonus(bonus);
		acc.setBonusExpire(bonusExpire);
		acc.update();
	}
}
