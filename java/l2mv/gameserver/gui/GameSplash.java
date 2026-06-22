package l2mv.gameserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public final class GameSplash
{
	private GameSplash()
	{
	}

	public static void showFor(long millis)
	{
		File image = GameResources.findFirst(
			"img/splash_l2jpremium.png",
			"img/splash_gameserver.png",
			"splash_l2jpremium.png"
		);

		if (image == null || !image.exists())
		{
			sleep(millis);
			return;
		}

		ImageIcon icon = new ImageIcon(image.getAbsolutePath());

		if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0)
		{
			sleep(millis);
			return;
		}

		final JWindow[] windowRef = new JWindow[1];
		final CountDownLatch shown = new CountDownLatch(1);

		SwingUtilities.invokeLater(() ->
		{
			JWindow window = new JWindow();
			windowRef[0] = window;

			window.setBackground(new Color(0, 0, 0, 0));
			window.setAlwaysOnTop(true);
			window.setFocusableWindowState(false);

			JPanel content = new JPanel(new BorderLayout());
			content.setOpaque(false);

			JLabel logo = new JLabel(icon, SwingConstants.CENTER);
			logo.setOpaque(false);

			content.add(logo, BorderLayout.CENTER);

			window.setContentPane(content);
			window.pack();
			window.setLocationRelativeTo(null);
			window.setVisible(true);

			shown.countDown();
		});

		try
		{
			shown.await(3L, TimeUnit.SECONDS);
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		finally
		{
			SwingUtilities.invokeLater(() ->
			{
				JWindow window = windowRef[0];

				if (window != null)
				{
					window.setVisible(false);
					window.dispose();
				}
			});
		}
	}

	private static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
