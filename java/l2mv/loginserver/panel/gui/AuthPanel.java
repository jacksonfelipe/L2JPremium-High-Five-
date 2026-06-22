package l2mv.loginserver.panel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public final class AuthPanel extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static final Color BG = new Color(13, 14, 20);
	private static final Color SURFACE = new Color(22, 24, 34);
	private static final Color SURFACE_ALT = new Color(31, 34, 48);
	private static final Color BORDER = new Color(61, 66, 88);
	private static final Color LOG_BG = new Color(8, 10, 16);

	private static final Color ORANGE = new Color(255, 137, 54);
	private static final Color ORANGE_HOVER = new Color(255, 156, 86);

	private static final Color RED = new Color(226, 72, 83);
	private static final Color RED_HOVER = new Color(241, 93, 103);

	private static final Color GREEN = new Color(78, 201, 122);
	private static final Color BLUE = new Color(88, 166, 255);
	private static final Color YELLOW = new Color(244, 191, 79);

	private static final Color TEXT = new Color(238, 239, 245);
	private static final Color MUTED = new Color(160, 166, 184);

	private final JLabel statusValue = new JLabel("Preparando");
	private final JLabel clientValue = new JLabel("-");
	private final JLabel gameValue = new JLabel("-");
	private final JLabel databaseValue = new JLabel("Aguardando");

	private final JTextPane logPane = new JTextPane();

	private final JButton restartButton = createButton("Reiniciar Auth", AuthIcons.Type.RESTART, ORANGE, ORANGE_HOVER);
	private final JButton shutdownButton = createButton("Desligar Tudo", AuthIcons.Type.POWER, RED, RED_HOVER);
	private final JButton clearButton = createButton("Limpar Logs", AuthIcons.Type.CLEAR, SURFACE_ALT, new Color(45, 49, 68));

	public AuthPanel()
	{
		super("L2JPremium - Painel do Auth Server");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(980, 640));
		setSize(1040, 690);
		setLocationRelativeTo(null);
		setIconImages(AuthResources.getApplicationIcons());

		setContentPane(createRoot());

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				requestShutdown();
			}
		});

		log("INFO", "Painel visual carregado. Aguardando inicialização do Auth Server...", null);
	}

	public void setStarting()
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText("Inicializando");
			statusValue.setForeground(YELLOW);

			databaseValue.setText("Conectando");
			databaseValue.setForeground(YELLOW);

			restartButton.setEnabled(true);
			shutdownButton.setEnabled(true);
		});
	}

	public void setOnline(String clientAddress, String gameServerAddress)
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText("Online");
			statusValue.setForeground(GREEN);

			clientValue.setText(clientAddress);
			clientValue.setForeground(BLUE);

			gameValue.setText(gameServerAddress);
			gameValue.setForeground(BLUE);

			databaseValue.setText("Conectado");
			databaseValue.setForeground(GREEN);
		});
	}

	public void setError(String message)
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText(message);
			statusValue.setForeground(RED);

			databaseValue.setText("Verifique os logs");
			databaseValue.setForeground(RED);

			restartButton.setEnabled(true);
			shutdownButton.setEnabled(true);
		});
	}

	public void log(String level, String message, Throwable throwable)
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				StyledDocument document = logPane.getStyledDocument();

				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				String line = "[" + time + "] [" + level + "] " + message + "\n";

				document.insertString(document.getLength(), line, getStyleFor(level));

				if (throwable != null)
				{
					StringWriter sw = new StringWriter();
					throwable.printStackTrace(new PrintWriter(sw));

					document.insertString(document.getLength(), sw.toString() + "\n", getStyleFor("ERRO"));
				}

				if (document.getLength() > 90000)
				{
					document.remove(0, 25000);
				}

				logPane.setCaretPosition(document.getLength());
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		});
	}

	private JPanel createRoot()
	{
		JPanel root = new JPanel(new BorderLayout(18, 18));
		root.setBackground(BG);
		root.setBorder(new EmptyBorder(18, 18, 18, 18));

		root.add(createHeader(), BorderLayout.NORTH);
		root.add(createCenter(), BorderLayout.CENTER);

		return root;
	}

	private Component createHeader()
	{
		RoundPanel header = new RoundPanel(SURFACE);
		header.setLayout(new BorderLayout(18, 0));
		header.setBorder(new EmptyBorder(18, 20, 18, 20));

		JLabel logo = createLogo();

		JPanel titleBox = new JPanel(new BorderLayout(0, 5));
		titleBox.setOpaque(false);

		JLabel title = new JLabel("L2JPremium Auth Server");
		title.setForeground(TEXT);
		title.setFont(new Font("Segoe UI", Font.BOLD, 25));

		JLabel subtitle = new JLabel("Painel de controle.");
		subtitle.setForeground(MUTED);
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		titleBox.add(title, BorderLayout.NORTH);
		titleBox.add(subtitle, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
		buttons.setOpaque(false);
		buttons.add(restartButton);
		buttons.add(shutdownButton);
		buttons.add(clearButton);

		restartButton.addActionListener(e -> requestRestart());
		shutdownButton.addActionListener(e -> requestShutdown());
		clearButton.addActionListener(e -> clearLogs());

		header.add(logo, BorderLayout.WEST);
		header.add(titleBox, BorderLayout.CENTER);
		header.add(buttons, BorderLayout.EAST);

		return header;
	}

	private Component createCenter()
	{
		JPanel center = new JPanel(new BorderLayout(16, 16));
		center.setOpaque(false);

		JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
		cards.setOpaque(false);

		cards.add(createInfoCard("Status", statusValue, AuthIcons.Type.STATUS, GREEN));
		cards.add(createInfoCard("Clientes", clientValue, AuthIcons.Type.CLIENTS, BLUE));
		cards.add(createInfoCard("GameServers", gameValue, AuthIcons.Type.GAMESERVER, ORANGE));
		cards.add(createInfoCard("Banco de Dados", databaseValue, AuthIcons.Type.DATABASE, YELLOW));

		center.add(cards, BorderLayout.NORTH);
		center.add(createLogArea(), BorderLayout.CENTER);

		return center;
	}

	private Component createLogArea()
	{
		RoundPanel wrapper = new RoundPanel(SURFACE);
		wrapper.setLayout(new BorderLayout(0, 12));
		wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));

		JPanel titleBox = new JPanel(new BorderLayout(10, 0));
		titleBox.setOpaque(false);

		JLabel icon = new JLabel(AuthIcons.of(AuthIcons.Type.LOG, BLUE, 22));
		icon.setPreferredSize(new Dimension(28, 28));

		JPanel textBox = new JPanel(new BorderLayout());
		textBox.setOpaque(false);

		JLabel title = new JLabel("Console Visual do Auth");
		title.setForeground(TEXT);
		title.setFont(new Font("Segoe UI", Font.BOLD, 17));

		JLabel subtitle = new JLabel("Mensagens principais, avisos, erros e eventos do login server.");
		subtitle.setForeground(MUTED);
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		textBox.add(title, BorderLayout.NORTH);
		textBox.add(subtitle, BorderLayout.CENTER);

		titleBox.add(icon, BorderLayout.WEST);
		titleBox.add(textBox, BorderLayout.CENTER);

		logPane.setEditable(false);
		logPane.setBackground(LOG_BG);
		logPane.setForeground(TEXT);
		logPane.setFont(new Font("Consolas", Font.PLAIN, 13));
		logPane.setBorder(new EmptyBorder(12, 12, 12, 12));

		createStyles();

		JScrollPane scroll = new JScrollPane(logPane);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER));
		scroll.getViewport().setBackground(LOG_BG);

		wrapper.add(titleBox, BorderLayout.NORTH);
		wrapper.add(scroll, BorderLayout.CENTER);

		return wrapper;
	}

	private static JPanel createInfoCard(String title, JLabel value, AuthIcons.Type iconType, Color accent)
	{
		RoundPanel card = new RoundPanel(SURFACE);
		card.setLayout(new BorderLayout(12, 0));
		card.setBorder(new EmptyBorder(15, 16, 15, 16));

		RoundPanel iconBox = new RoundPanel(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
		iconBox.setLayout(new BorderLayout());
		iconBox.setPreferredSize(new Dimension(44, 44));
		iconBox.add(new JLabel(AuthIcons.of(iconType, accent, 23), SwingConstants.CENTER), BorderLayout.CENTER);

		JPanel textBox = new JPanel(new BorderLayout(0, 8));
		textBox.setOpaque(false);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(MUTED);
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		value.setForeground(TEXT);
		value.setFont(new Font("Segoe UI", Font.BOLD, 16));
		value.setHorizontalAlignment(SwingConstants.LEFT);

		textBox.add(titleLabel, BorderLayout.NORTH);
		textBox.add(value, BorderLayout.CENTER);

		card.add(iconBox, BorderLayout.WEST);
		card.add(textBox, BorderLayout.CENTER);

		return card;
	}

	private static JLabel createLogo()
	{
		ImageIcon logoIcon = AuthResources.getLogoIcon(135, 82);

		if (logoIcon == null)
		{
			JLabel fallback = new JLabel("L2JP");
			fallback.setForeground(ORANGE);
			fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
			fallback.setHorizontalAlignment(SwingConstants.CENTER);
			fallback.setPreferredSize(new Dimension(142, 76));
			return fallback;
		}

		Image image = logoIcon.getImage();
		ImageIcon scaled = new ImageIcon(image);

		JLabel label = new JLabel(scaled, SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(145, 76));
		return label;
	}

	private void requestRestart()
	{
		boolean confirmed = AuthDialog.confirm(
			this,
			"Reiniciar Auth Server",
			"O Auth Server será reiniciado agora.\n\nO painel será fechado e aberto novamente pelo start_login.vbs.",
			AuthDialog.Type.WARNING,
			"Reiniciar",
			"Cancelar"
		);

		if (!confirmed)
		{
			return;
		}

		restartButton.setEnabled(false);
		shutdownButton.setEnabled(false);

		log("AVISO", "Reinicialização solicitada pelo painel administrativo.", null);
		log("AVISO", "Encerrando processo atual com código 2 para o VBS relançar o Auth Server.", null);

		Timer timer = new Timer(700, e ->
		{
			((Timer) e.getSource()).stop();
			Runtime.getRuntime().exit(2);
		});

		timer.setRepeats(false);
		timer.start();
	}

	private void requestShutdown()
	{
		boolean confirmed = AuthDialog.confirm(
			this,
			"Desligar Auth Server",
			"Deseja realmente desligar o Auth Server?\n\nO processo será encerrado e não será reiniciado automaticamente.",
			AuthDialog.Type.ERROR,
			"Desligar",
			"Cancelar"
		);

		if (!confirmed)
		{
			return;
		}

		restartButton.setEnabled(false);
		shutdownButton.setEnabled(false);

		log("AVISO", "Desligamento solicitado pelo painel administrativo.", null);
		log("AVISO", "Encerrando Auth Server com segurança.", null);

		Timer timer = new Timer(700, e ->
		{
			((Timer) e.getSource()).stop();
			Runtime.getRuntime().exit(0);
		});

		timer.setRepeats(false);
		timer.start();
	}

	private void clearLogs()
	{
		logPane.setText("");
		log("INFO", "Console visual limpo pelo administrador.", null);
	}

	private static JButton createButton(String text, AuthIcons.Type iconType, Color normal, Color hover)
	{
		JButton button = new JButton(text, AuthIcons.of(iconType, TEXT, 18));
		button.setForeground(TEXT);
		button.setBackground(normal);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setPreferredSize(new Dimension(150, 44));
		button.setIconTextGap(8);

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

	private void createStyles()
	{
		StyledDocument document = logPane.getStyledDocument();

		createStyle(document, "INFO", BLUE);
		createStyle(document, "AVISO", YELLOW);
		createStyle(document, "ERRO", RED);
		createStyle(document, "CONSOLE", MUTED);
	}

	private static void createStyle(StyledDocument document, String name, Color color)
	{
		Style style = document.addStyle(name, null);
		StyleConstants.setForeground(style, color);
		StyleConstants.setFontFamily(style, "Consolas");
		StyleConstants.setFontSize(style, 13);
	}

	private Style getStyleFor(String level)
	{
		Style style = logPane.getStyledDocument().getStyle(level);

		if (style == null)
		{
			style = logPane.getStyledDocument().getStyle("CONSOLE");
		}

		return style;
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
			g2.setColor(color);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

			g2.setColor(BORDER);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

			g2.dispose();

			super.paintComponent(g);
		}
	}
}
