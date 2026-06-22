package l2mv.gameserver.gui;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public final class GameResources
{
	private static final String[] ROOTS =
	{
		".",
		"..",
		"../.."
	};

	private GameResources()
	{
	}

	public static File find(String relativePath)
	{
		for (String root : ROOTS)
		{
			File file = new File(root, relativePath);

			if (file.exists())
			{
				return file;
			}
		}

		return null;
	}

	public static File findFirst(String... relativePaths)
	{
		for (String relativePath : relativePaths)
		{
			File file = find(relativePath);

			if (file != null)
			{
				return file;
			}
		}

		return null;
	}

	public static ImageIcon loadIcon(int size, String... relativePaths)
	{
		File file = findFirst(relativePaths);

		if (file == null)
		{
			return null;
		}

		ImageIcon original = new ImageIcon(file.getAbsolutePath());

		if (original.getIconWidth() <= 0 || original.getIconHeight() <= 0)
		{
			return null;
		}

		Image scaled = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

	public static List<Image> loadWindowIcons()
	{
		List<Image> images = new ArrayList<>();

		String[] icons =
		{
			"img/16x16.png",
			"img/32x32.png",
			"img/icon_16.png",
			"img/icon_32.png",
			"img/l2jpremium_16.png",
			"img/l2jpremium_32.png",
			"img/splash_l2jpremium.png"
		};

		for (String icon : icons)
		{
			File file = find(icon);

			if (file == null)
			{
				continue;
			}

			ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());

			if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0)
			{
				images.add(imageIcon.getImage());
			}
		}

		return images;
	}
}
