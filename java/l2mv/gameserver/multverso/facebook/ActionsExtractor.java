package l2mv.gameserver.multverso.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public interface ActionsExtractor
{
	public static final SimpleDateFormat FACEBOOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	void extractData(String p0) throws IOException;
	
	default JSONObject call(URL apiCallURL) throws IOException
	{
		HttpURLConnection httpConn = (HttpURLConnection) apiCallURL.openConnection();
		try
		{
			int responseCode = httpConn.getResponseCode();
			if (responseCode >= 400)
			{
				InputStream errStream = httpConn.getErrorStream();
				String errResponse = "";
				if (errStream != null)
				{
					errResponse = IOUtils.toString(errStream, "UTF-8");
					errStream.close();
				}
				String errorMsg = "[Facebook API Error] Response code: " + responseCode + " for URL: " + apiCallURL + " | Error Response: " + errResponse;
				System.err.println(errorMsg);
				throw new IOException(errorMsg);
			}
			InputStream inputStream = httpConn.getInputStream();
			String result = IOUtils.toString(inputStream, "UTF-8");
			inputStream.close();
			return new JSONObject(result);
		}
		finally
		{
			httpConn.disconnect();
		}
	}
	
	default long parseFacebookDate(String date) throws ParseException
	{
		return ActionsExtractor.FACEBOOK_DATE_FORMAT.parse(date).getTime();
	}
}
