package l2mv.gameserver.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GameUiLog
{
	private static volatile GamePanel panel;

	private static final PrintStream ORIGINAL_OUT = System.out;
	private static final PrintStream ORIGINAL_ERR = System.err;

	private static volatile boolean redirectInstalled;

	private static volatile String lastManualMessage;
	private static volatile long lastManualTime;
	private static volatile long suppressSystemUntil;

	private static final Map<String, String> TRANSLATIONS = new LinkedHashMap<String, String>();

	static
	{
		TRANSLATIONS.put("GameServer Started", "GameServer iniciado");
		TRANSLATIONS.put("Maximum Numbers of Connected Players", "Número máximo de jogadores conectados");
		TRANSLATIONS.put("Cannot bind address", "Não foi possível abrir o endereço");
		TRANSLATIONS.put("Port ", "Porta ");
		TRANSLATIONS.put("is allready binded", "já está em uso");
		TRANSLATIONS.put("Please free it and restart server", "libere a porta e reinicie o servidor");
		TRANSLATIONS.put("Telnet server is currently disabled", "Servidor Telnet está desativado");
		TRANSLATIONS.put("Loading Scripts", "Carregando scripts");
		TRANSLATIONS.put("Spawn Manager", "Gerenciador de spawns");
		TRANSLATIONS.put("Loading Images", "Carregando imagens");
		TRANSLATIONS.put("Loading Olympiad System", "Carregando sistema de Olimpíada");
		TRANSLATIONS.put("Olympiad System Loaded", "Sistema de Olimpíada carregado");
		TRANSLATIONS.put("Loaded Proxy System", "Sistema de proxy carregado");
		TRANSLATIONS.put("Loaded AUTO - Donation System", "Sistema automático de doação carregado");
		TRANSLATIONS.put("Delayed Items Manager", "Gerenciador de itens atrasados");
		TRANSLATIONS.put("Preparing Drop Calculator", "Preparando calculadora de drops");
		TRANSLATIONS.put("Scripts", "Scripts");
		TRANSLATIONS.put("QuestManager", "Gerenciador de quests");
		TRANSLATIONS.put("Lineage World", "Mundo Lineage");
		TRANSLATIONS.put("Banned HWIDS", "HWIDs banidos");
		TRANSLATIONS.put("Clan Crests", "Crests de clãs");
		TRANSLATIONS.put("Fish Table", "Tabela de pescaria");
		TRANSLATIONS.put("Skills", "Skills");
		TRANSLATIONS.put("Augmentation Data", "Dados de augment");
		TRANSLATIONS.put("Level Up Table", "Tabela de level up");
		TRANSLATIONS.put("Item Logs", "Logs de itens");
		TRANSLATIONS.put("Auctioneer", "Leiloeiro");
		TRANSLATIONS.put("Admin Commands", "Comandos admin");
		TRANSLATIONS.put("Players Commands", "Comandos de jogadores");
		TRANSLATIONS.put("Boats", "Barcos");
		TRANSLATIONS.put("Dimensional Rift", "Dimensional Rift");
		TRANSLATIONS.put("Seven Signs", "Seven Signs");
		TRANSLATIONS.put("Auto Cleaner", "Limpeza automática");
		TRANSLATIONS.put("Hellbound", "Hellbound");
		TRANSLATIONS.put("Offline Buffers", "Buffers offline");
		TRANSLATIONS.put("DataBase Cleaner Loaded", "Limpeza do banco de dados carregada");
	}

	private GameUiLog()
	{
	}

	public static void attach(GamePanel gamePanel)
	{
		panel = gamePanel;
	}

	public static void installSystemRedirect()
	{
		if (redirectInstalled)
		{
			return;
		}

		redirectInstalled = true;

		System.setOut(new PrintStream(new UiOutputStream("CONSOLE"), true));
		System.setErr(new PrintStream(new UiOutputStream("ERRO"), true));
	}

	public static void info(String message)
	{
		write("INFO", message, null, true);
	}

	public static void warn(String message)
	{
		write("AVISO", message, null, true);
	}

	public static void error(String message, Throwable throwable)
	{
		write("ERRO", message, throwable, true);
	}

	private static void writeSystem(String level, String message)
	{
		long now = System.currentTimeMillis();

		if (now < suppressSystemUntil)
		{
			return;
		}

		String last = lastManualMessage;

		if (last != null && !last.isEmpty() && message.contains(last) && (now - lastManualTime) < 1500L)
		{
			return;
		}

		write(level, translate(message), null, false);
	}

	private static String translate(String message)
	{
		if (message == null)
		{
			return "";
		}

		String translated = message;

		for (Map.Entry<String, String> entry : TRANSLATIONS.entrySet())
		{
			translated = translated.replace(entry.getKey(), entry.getValue());
		}

		return translated;
	}

	private static void write(String level, String message, Throwable throwable, boolean manual)
	{
		if (message == null)
		{
			message = "";
		}

		if (manual)
		{
			lastManualMessage = message;
			lastManualTime = System.currentTimeMillis();

			if (throwable != null)
			{
				suppressSystemUntil = System.currentTimeMillis() + 2500L;
			}
		}

		GamePanel gamePanel = panel;

		if (gamePanel != null)
		{
			gamePanel.log(level, translate(message), throwable);
			return;
		}

		PrintStream stream = "ERRO".equals(level) ? ORIGINAL_ERR : ORIGINAL_OUT;
		stream.println("[" + level + "] " + translate(message));

		if (throwable != null)
		{
			throwable.printStackTrace(stream);
		}
	}

	private static final class UiOutputStream extends OutputStream
	{
		private final String level;
		private final StringBuilder buffer = new StringBuilder(256);

		private UiOutputStream(String level)
		{
			this.level = level;
		}

		@Override
		public synchronized void write(int b) throws IOException
		{
			if (b == '\r')
			{
				return;
			}

			if (b == '\n')
			{
				flushBuffer();
				return;
			}

			buffer.append((char) b);
		}

		@Override
		public synchronized void flush() throws IOException
		{
			flushBuffer();
		}

		private void flushBuffer()
		{
			if (buffer.length() == 0)
			{
				return;
			}

			String line = buffer.toString().trim();
			buffer.setLength(0);

			if (!line.isEmpty())
			{
				GameUiLog.writeSystem(level, line);
			}
		}
	}
}
