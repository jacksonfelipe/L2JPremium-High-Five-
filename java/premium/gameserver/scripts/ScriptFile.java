package premium.gameserver.scripts;

public interface ScriptFile
{
	public void onLoad();
	
	public void onReload();
	
	public void onShutdown();
}