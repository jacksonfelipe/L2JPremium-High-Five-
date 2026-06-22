package l2mv.gameserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public final class GameDialog
{
	private static final Color BG = new Color(13, 14, 20);
	private static final Color SURFACE = new Color(23, 25, 36);
	private static final Color SURFACE_ALT = new Color(32, 35, 49);
	private static final Color BORDER = new Color(72, 78, 105);
	private static final Color TEXT = new Color(238, 239, 245);
	private static final Color MUTED = new Color(165, 171, 188);
	private static final Color ORANGE = new Color(255, 137, 54);
	private static final Color ORANGE_HOVER = new Color(255, 156, 86);
	private static final Color RED = new Color(226, 72, 83);
	private static final Color RED_HOVER = new Color(241, 93, 103);

	private GameDialog()
	{
	}

	public static boolean confirm(Component parent, String title, String message, DialogType type)
	{
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		JDialog dialog = createDialog(owner, title);
		final boolean[] accepted = new boolean[1];

		Color accent = type == DialogType.DANGER ? RED : ORANGE;
		Color accentHover = type == DialogType.DANGER ? RED_HOVER : ORANGE_HOVER;
		Icon icon = type == DialogType.DANGER ? GameIcons.power(accent) : GameIcons.restart(accent);

		RoundPanel content = new RoundPanel(SURFACE);
		content.setLayout(new BorderLayout(16, 16));
		content.setBorder(new EmptyBorder(20, 20, 18, 20));

		JLabel iconLabel = new JLabel(icon);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setPreferredSize(new Dimension(46, 46));

		JPanel textBox = new JPanel(new BorderLayout(0, 8));
		textBox.setOpaque(false);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(TEXT);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

		JLabel messageLabel = new JLabel(toHtml(message));
		messageLabel.setForeground(MUTED);
		messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		textBox.add(titleLabel, BorderLayout.NORTH);
		textBox.add(messageLabel, BorderLayout.CENTER);

		JPanel center = new JPanel(new BorderLayout(14, 0));
		center.setOpaque(false);
		center.add(iconLabel, BorderLayout.WEST);
		center.add(textBox, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
		buttons.setOpaque(false);
		buttons.setBorder(new EmptyBorder(8, 60, 0, 0));

		JButton yes = createButton("Confirmar", accent, accentHover);
		JButton no = createButton("Cancelar", SURFACE_ALT, new Color(45, 49, 68));

		yes.addActionListener(e ->
		{
			accepted[0] = true;
			dialog.dispose();
		});

		no.addActionListener(e ->
		{
			accepted[0] = false;
			dialog.dispose();
		});

		buttons.add(yes);
		buttons.add(no);

		content.add(center, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);

		return accepted[0];
	}

	public static void showError(Component parent, String title, String message)
	{
		Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
		JDialog dialog = createDialog(owner, title);

		RoundPanel content = new RoundPanel(SURFACE);
		content.setLayout(new BorderLayout(16, 16));
		content.setBorder(new EmptyBorder(20, 20, 18, 20));

		JLabel iconLabel = new JLabel(GameIcons.warning(RED));
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setPreferredSize(new Dimension(46, 46));

		JPanel textBox = new JPanel(new BorderLayout(0, 8));
		textBox.setOpaque(false);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(TEXT);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

		JLabel messageLabel = new JLabel(toHtml(message));
		messageLabel.setForeground(MUTED);
		messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		textBox.add(titleLabel, BorderLayout.NORTH);
		textBox.add(messageLabel, BorderLayout.CENTER);

		JPanel center = new JPanel(new BorderLayout(14, 0));
		center.setOpaque(false);
		center.add(iconLabel, BorderLayout.WEST);
		center.add(textBox, BorderLayout.CENTER);

		JButton ok = createButton("Entendi", RED, RED_HOVER);
		ok.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(new EmptyBorder(8, 180, 0, 0));
		buttonPanel.add(ok, BorderLayout.CENTER);

		content.add(center, BorderLayout.CENTER);
		content.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private static JDialog createDialog(Window owner, String title)
	{
		JDialog dialog;

		if (owner instanceof Frame)
		{
			dialog = new JDialog((Frame) owner, title, true);
		}
		else if (owner instanceof Dialog)
		{
			dialog = new JDialog((Dialog) owner, title, true);
		}
		else
		{
			dialog = new JDialog((Frame) null, title, true);
		}

		dialog.setUndecorated(true);
		dialog.setBackground(new Color(0, 0, 0, 0));
		dialog.setMinimumSize(new Dimension(460, 210));

		return dialog;
	}

	private static JButton createButton(String text, Color normal, Color hover)
	{
		JButton button = new JButton(text);
		button.setForeground(TEXT);
		button.setBackground(normal);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (button.isEnabled())
				{
					button.setBackground(hover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				button.setBackground(normal);
			}
		});

		return button;
	}

	private static String toHtml(String message)
	{
		return "<html><body style='width:330px'>" + escape(message).replace("\n", "<br>") + "</body></html>";
	}

	private static String escape(String text)
	{
		if (text == null)
		{
			return "";
		}

		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public enum DialogType
	{
		WARNING,
		DANGER
	}

	private static final class RoundPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private final Color color;

		private RoundPanel(Color color)
		{
			this.color = color;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(BG);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
			g2.setColor(color);
			g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24);
			g2.setColor(BORDER);
			g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);
			g2.dispose();

			super.paintComponent(g);
		}
	}
}
