package l2mv.loginserver.panel.gui;

import java.io.OutputStream;
import java.io.PrintStream;

public final class AuthUiLog
{
	private static volatile AuthPanel panel;

	private static final PrintStream ORIGINAL_OUT = System.out;
	private static final PrintStream ORIGINAL_ERR = System.err;

	private static volatile boolean redirectInstalled;

	private static volatile String lastManualMessage;
	private static volatile long lastManualTime;
	private static volatile long suppressSystemUntil;

	private AuthUiLog()
	{
	}

	public static void attach(AuthPanel authPanel)
	{
		panel = authPanel;
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

		write(level, message, null, false);
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

		AuthPanel authPanel = panel;

		if (authPanel != null)
		{
			authPanel.log(level, message, throwable);
			return;
		}

		PrintStream stream = "ERRO".equals(level) ? ORIGINAL_ERR : ORIGINAL_OUT;
		stream.println("[" + level + "] " + message);

		if (throwable != null)
		{
			throwable.printStackTrace(stream);
		}
	}

	private static String translateSystemLine(String line)
	{
		if (line == null)
		{
			return "";
		}

		String text = line.trim();

		if (text.startsWith("Loaded ") && text.endsWith(" server names"))
		{
			return "Nomes de servidores carregados: " + extractBetween(text, "Loaded ", " server names") + ".";
		}

		if (text.equalsIgnoreCase("Loaded whirpool2 as default crypt.") || text.equalsIgnoreCase("Loaded whirlpool2 as default crypt."))
		{
			return "Criptografia padrão carregada: whirlpool2.";
		}

		if (text.startsWith("Cached ") && text.endsWith(" KeyPairs for RSA communication"))
		{
			return "Cache RSA preparado com " + extractBetween(text, "Cached ", " KeyPairs for RSA communication") + " pares de chaves.";
		}

		if (text.startsWith("Stored ") && text.endsWith(" keys for Blowfish communication"))
		{
			return "Chaves Blowfish armazenadas: " + extractBetween(text, "Stored ", " keys for Blowfish communication") + ".";
		}

		if (text.startsWith("Loaded ") && text.endsWith(" registered GameServer(s)."))
		{
			return "GameServer(s) registrados carregados: " + extractBetween(text, "Loaded ", " registered GameServer(s).") + ".";
		}

		if (text.startsWith("Listening for gameservers on "))
		{
			return "Escutando GameServers em " + text.substring("Listening for gameservers on ".length()) + ".";
		}

		if (text.startsWith("Listening for clients on "))
		{
			return "Escutando clientes em " + text.substring("Listening for clients on ".length()) + ".";
		}

		if (text.startsWith("Login Server will automatically restart in ") && text.endsWith(" seconds!"))
		{
			return "Reinicialização automática configurada para " + extractBetween(text, "Login Server will automatically restart in ", " seconds!") + " segundos.";
		}

		if (text.equalsIgnoreCase("Starting Auth Server Restart!"))
		{
			return "Reinicialização do Auth Server iniciada.";
		}

		return text;
	}

	private static String extractBetween(String text, String prefix, String suffix)
	{
		int start = prefix.length();
		int end = text.length() - suffix.length();

		if (start >= 0 && end >= start && end <= text.length())
		{
			return text.substring(start, end).trim();
		}

		return text;
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
		public synchronized void write(int b)
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
		public synchronized void flush()
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
				AuthUiLog.writeSystem(level, translateSystemLine(line));
			}
		}
	}
}