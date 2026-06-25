package premium.gameserver.multverso.streaming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import premium.commons.threading.RunnableImpl;
import premium.gameserver.ConfigHolder;
import premium.gameserver.ThreadPoolManager;
import premium.gameserver.utils.Log;

public class TwitchParser
{
	private static final Logger LOG = LoggerFactory.getLogger(TwitchParser.class);
	
	public TwitchParser()
	{
		final long delay = ConfigHolder.getLong("StreamCheckTwitchDelay");
		if (delay <= 0)
		{
			return;
		}
		
		ThreadPoolManager.getInstance().scheduleAtFixedDelay(new CheckTwitch(delay), delay, delay);
	}
	
	public static TwitchParser getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		private static final TwitchParser instance = new TwitchParser();
	}
	
	private static class CheckTwitch extends RunnableImpl
	{
		private CheckTwitch(long delayBetweenChecks)
		{
		}
		
		@Override
		public void runImpl()
		{
			if (!ConfigHolder.getBool("AllowStreamingSystem"))
			{
				return;
			}
			
			parseTwitch();
		}
		
		private static void parseTwitch()
		{
			try
			{
				final JSONObject data = getAllActiveChannels();
				final JSONArray streams = data.getJSONArray("streams");
				Log.logStream("Starting to parse " + data.getInt("_total") + " active streams!");
				System.currentTimeMillis();
				for (int streamIndex = 0; streamIndex < streams.length(); ++streamIndex)
				{
					final JSONObject stream = streams.getJSONObject(streamIndex);
					if (stream.get("game") != null && !stream.isNull("game"))
					{
						try
						{
							stream.getString("game");
							stream.getInt("viewers");
							final JSONObject channel = stream.getJSONObject("channel");
							if (channel.get("status") != null && !channel.isNull("status"))
							{
								channel.getString("display_name");
								channel.getString("status");
							}
						}
						catch (JSONException e)
						{
							LOG.error("JSON Exception! Stream: " + stream, e);
						}
					}
				}
			}
			catch (MalformedURLException e2)
			{
				LOG.error("Config \"TwitchActiveStreamsURL\" has wrong Value!", e2);
			}
			catch (IOException e3)
			{
				LOG.error("Error while connecting to Twitch!", e3);
			}
		}
		
		private static JSONObject getAllActiveChannels() throws MalformedURLException, IOException
		{
			final URL url = new URL(ConfigHolder.getString("TwitchActiveStreamsURL"));
			final URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("client_id", ConfigHolder.getString("TwitchClientId"));
			try (InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream()))
			{
				final BufferedReader br = new BufferedReader(isr);
				final StringBuilder finalText = new StringBuilder();
				String strLine;
				while ((strLine = br.readLine()) != null)
				{
					finalText.append(strLine);
				}
				return new JSONObject(finalText.toString());
			}
		}
	}
}
