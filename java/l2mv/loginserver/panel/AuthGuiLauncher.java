package l2mv.loginserver.panel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import l2mv.loginserver.AuthServer;
import l2mv.loginserver.Config;
import l2mv.loginserver.panel.gui.AuthDialog;
import l2mv.loginserver.panel.gui.AuthPanel;
import l2mv.loginserver.panel.gui.AuthSplash;
import l2mv.loginserver.panel.gui.AuthUiLog;

public final class AuthGuiLauncher
{
	private AuthGuiLauncher()
	{
	}
	
	public static void main(String[] args)
	{
		try
		{
			installVisualDefaults();
			AuthSplash.showFor(2500L);
			
			final AuthPanel[] panelRef = new AuthPanel[1];
			
			SwingUtilities.invokeAndWait(() -> {
				panelRef[0] = new AuthPanel();
				AuthUiLog.attach(panelRef[0]);
				panelRef[0].setVisible(true);
			});
			
			AuthUiLog.installSystemRedirect();
			
			Thread starter = new Thread(() -> {
				try
				{
					panelRef[0].setStarting();
					AuthUiLog.info("Inicializando núcleo do Auth Server...");
					
					AuthServer.bootstrap();
					
					panelRef[0].setOnline(Config.LOGIN_HOST + ":" + Config.PORT_LOGIN, Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
					AuthUiLog.info("Auth Server iniciado com sucesso.");
				}
				catch (Throwable t)
				{
					AuthUiLog.error("Falha crítica ao iniciar o Auth Server.", t);
					panelRef[0].setError("Falha na inicialização");
					SwingUtilities.invokeLater(() -> AuthDialog.showMessage(panelRef[0], "Falha na inicialização", "Não foi possível iniciar o Auth Server.\n\nVerifique a conexão com o banco, a porta de login e os logs do painel.", AuthDialog.Type.ERROR));
				}
			}, "AuthServer-Starter");
			
			starter.setDaemon(false);
			starter.start();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			
			try
			{
				AuthDialog.showMessage(null, "L2JPremium Auth", "Não foi possível abrir o painel do Auth Server.\n\n" + t.getMessage(), AuthDialog.Type.ERROR);
			}
			catch (Throwable ignored)
			{
			}
			
			System.exit(1);
		}
	}

	private static void installVisualDefaults()
	{
		try
		{
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");

			UIManager.put("Button.font", UIManager.getFont("Button.font"));
			UIManager.put("OptionPane.messageFont", UIManager.getFont("Label.font"));
			UIManager.put("OptionPane.buttonFont", UIManager.getFont("Button.font"));
		}
		catch (Throwable ignored)
		{
		}
	}
}
