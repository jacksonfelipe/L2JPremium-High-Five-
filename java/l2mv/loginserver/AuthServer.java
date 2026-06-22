package l2mv.loginserver;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import l2mv.commons.net.nio.impl.SelectorConfig;
import l2mv.commons.net.nio.impl.SelectorThread;
import l2mv.commons.threading.RunnableImpl;
import l2mv.loginserver.database.L2DatabaseFactory;
import l2mv.loginserver.gameservercon.GameServerCommunication;
import l2mv.loginserver.panel.gui.AuthUiLog;

public class AuthServer
{
	private static AuthServer authServer;
	
	private final GameServerCommunication _gameServerListener;
	private final SelectorThread<L2LoginClient> _selectorThread;
	
	public static AuthServer getInstance()
	{
		return authServer;
	}
	
	public AuthServer() throws Throwable
	{
		
		info("Inicializando criptografia do Login Server...");
		Config.initCrypt();
		
		info("Carregando gerenciador de GameServers registrados...");
		GameServerManager.getInstance();
		
		L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
		SelectorHelper sh = new SelectorHelper();
		SelectorConfig sc = new SelectorConfig();
		
		_selectorThread = new SelectorThread<>(sc, loginPacketHandler, sh, sh, sh);
		
		info("Abrindo comunicação interna com GameServers em " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT + "...");
		
		_gameServerListener = GameServerCommunication.getInstance();
		_gameServerListener.openServerSocket(Config.GAME_SERVER_LOGIN_HOST.equals("127.0.0.1") ? null : InetAddress.getByName(Config.GAME_SERVER_LOGIN_HOST), Config.GAME_SERVER_LOGIN_PORT);
		
		_gameServerListener.start();
		
		info("Comunicação com GameServers ativa em " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT + ".");
		
		info("Abrindo porta de login para clientes em " + Config.LOGIN_HOST + ":" + Config.PORT_LOGIN + "...");
		
		_selectorThread.openServerSocket(Config.LOGIN_HOST.equals("127.0.0.1") ? null : InetAddress.getByName(Config.LOGIN_HOST), Config.PORT_LOGIN);
		
		_selectorThread.start();
		
		info("Auth Server escutando clientes em " + Config.LOGIN_HOST + ":" + Config.PORT_LOGIN + ".");
		
		if (Config.SCHEDULE_RESTART_SECONDS > 0L)
		{
			info("Reinicialização automática agendada para " + formatSeconds(Config.SCHEDULE_RESTART_SECONDS) + ".");
			ThreadPoolManager.getInstance().schedule(new AuthShutdown(), TimeUnit.SECONDS.toMillis(Config.SCHEDULE_RESTART_SECONDS));
		}
		else
		{
			info("Reinicialização automática desativada nas configurações.");
		}
	}
	
	public GameServerCommunication getGameServerListener()
	{
		return _gameServerListener;
	}
	
	public static void bootstrap() throws Throwable
	{
		new File("./log/").mkdirs();
		info("Pasta de logs preparada em ./log/.");
		
		info("Carregando configurações do Auth Server...");
		Config.load();
		info("Configurações carregadas com sucesso.");
		
		info("Verificando disponibilidade da porta de login...");
		checkFreePorts();
		info("Porta de login disponível para uso.");
		
		info("Carregando driver do banco de dados...");
		Class.forName(Config.DATABASE_DRIVER).getDeclaredConstructor().newInstance();
		
		info("Testando conexão com o banco de dados...");
		L2DatabaseFactory.getInstance().getConnection().close();
		info("Banco de dados conectado com sucesso.");
		
		authServer = new AuthServer();
		
		info("Inicialização completa. Auth Server online.");
	}
	
	public static void checkFreePorts() throws Throwable
	{
		ServerSocket ss = null;
		
		try
		{
			if (Config.LOGIN_HOST.equalsIgnoreCase("127.0.0.1"))
			{
				ss = new ServerSocket(Config.PORT_LOGIN);
			}
			else
			{
				ss = new ServerSocket(Config.PORT_LOGIN, 50, InetAddress.getByName(Config.LOGIN_HOST));
			}
		}
		finally
		{
			if (ss != null)
			{
				try
				{
					ss.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}
	
	public static void main(String[] args) throws Throwable
	{
		bootstrap();
	}
	
	private static void info(String message)
	{
		AuthUiLog.info(message);
		
	}
	
	private static void warn(String message)
	{
		AuthUiLog.warn(message);
		
	}
	
	private static String formatSeconds(long totalSeconds)
	{
		long hours = totalSeconds / 3600L;
		long minutes = (totalSeconds % 3600L) / 60L;
		long seconds = totalSeconds % 60L;
		
		if (hours > 0L)
		{
			return hours + "h " + minutes + "min " + seconds + "s";
		}
		
		if (minutes > 0L)
		{
			return minutes + "min " + seconds + "s";
		}
		
		return seconds + "s";
	}
	
	private static class AuthShutdown extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			warn("Reinicialização automática iniciada pelo agendador do Auth Server.");
			Runtime.getRuntime().exit(2);
		}
	}
}