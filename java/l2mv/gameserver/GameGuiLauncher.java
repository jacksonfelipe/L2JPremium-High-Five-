package l2mv.gameserver;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import l2mv.gameserver.gui.GamePanel;
import l2mv.gameserver.gui.GameSplash;
import l2mv.gameserver.gui.GameUiLog;

public final class GameGuiLauncher
{
	private GameGuiLauncher()
	{
	}

	public static void main(String[] args)
	{
		try
		{
			GameSplash.showFor(2500L);

			final GamePanel[] panelRef = new GamePanel[1];

			SwingUtilities.invokeAndWait(() ->
			{
				panelRef[0] = new GamePanel();
				GameUiLog.attach(panelRef[0]);
				panelRef[0].setVisible(true);
			});

			GameUiLog.installSystemRedirect();

			Thread starter = new Thread(() ->
			{
				try
				{
					panelRef[0].setStarting();

					GameUiLog.info("Inicializando núcleo do Game Server...");
					GameServer.bootstrap();

					panelRef[0].setOnline();
					GameUiLog.info("Game Server iniciado com sucesso.");
				}
				catch (Throwable t)
				{
					GameUiLog.error("Falha crítica ao iniciar o Game Server.", t);
					panelRef[0].setError("Falha na inicialização");
				}
			}, "GameServer-Starter");

			starter.setDaemon(false);
			starter.start();
		}
		catch (Throwable t)
		{
			t.printStackTrace();

			try
			{
				JOptionPane.showMessageDialog(
					null,
					"Não foi possível abrir o painel do Game Server.\n\n" + t.getMessage(),
					"L2JPremium Game Server",
					JOptionPane.ERROR_MESSAGE
				);
			}
			catch (Throwable ignored)
			{
			}

			System.exit(1);
		}
	}
}
