package l2mv.loginserver.panel.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class AuthResources
{
	private static final String SPLASH = "splash_l2jpremium.png";

	private static final String[] ROOTS =
	{
		"",
		"../",
		"../../",
		"../../../"
	};

	private AuthResources()
	{
	}

	public static File findSplash()
	{
		return findExisting(
			"img/" + SPLASH,
			"resources/img/" + SPLASH,
			"data/img/" + SPLASH,
			SPLASH
		);
	}

	public static ImageIcon getLogoIcon(int preferredWidth, int preferredHeight)
	{
		Image image = readImage(findSplash());

		if (image == null)
		{
			return null;
		}

		int sourceWidth = image.getWidth(null);
		int sourceHeight = image.getHeight(null);

		if (sourceWidth <= 0 || sourceHeight <= 0)
		{
			return null;
		}

		int width = preferredWidth;
		int height = Math.max(1, sourceHeight * width / sourceWidth);

		if (height > preferredHeight)
		{
			height = preferredHeight;
			width = Math.max(1, sourceWidth * height / sourceHeight);
		}

		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public static List<Image> getApplicationIcons()
	{
		List<Image> icons = new ArrayList<>();

		addImage(icons, findExisting(
			"img/16x16.png",
			"img/icon_16.png",
			"img/icon16.png",
			"img/l2jpremium_16.png",
			"img/l2jpremium_16x16.png"
		));

		addImage(icons, findExisting(
			"img/32x32.png",
			"img/icon_32.png",
			"img/icon32.png",
			"img/l2jpremium_32.png",
			"img/l2jpremium_32x32.png"
		));

		addImage(icons, findExisting(
			"img/48x48.png",
			"img/icon_48.png",
			"img/icon48.png",
			"img/l2jpremium_48.png"
		));

		if (icons.isEmpty())
		{
			Image splash = readImage(findSplash());

			if (splash != null)
			{
				icons.add(splash.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
				icons.add(splash.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
				icons.add(splash.getScaledInstance(48, 48, Image.SCALE_SMOOTH));
			}
		}

		return icons;
	}

	public static ImageIcon getApplicationIcon(int size)
	{
		List<Image> icons = getApplicationIcons();

		if (!icons.isEmpty())
		{
			return new ImageIcon(icons.get(0).getScaledInstance(size, size, Image.SCALE_SMOOTH));
		}

		Image splash = readImage(findSplash());

		if (splash != null)
		{
			return new ImageIcon(splash.getScaledInstance(size, size, Image.SCALE_SMOOTH));
		}

		return null;
	}

	private static void addImage(List<Image> images, File file)
	{
		Image image = readImage(file);

		if (image != null)
		{
			images.add(image);
		}
	}

	private static Image readImage(File file)
	{
		if (file == null || !file.exists())
		{
			return null;
		}

		try
		{
			BufferedImage image = ImageIO.read(file);
			return image;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static File findExisting(String... relativePaths)
	{
		for (String root : ROOTS)
		{
			for (String path : relativePaths)
			{
				File file = new File(root + path);

				if (file.exists() && file.isFile())
				{
					return file;
				}
			}
		}

		return null;
	}
}
