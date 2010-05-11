package it.polimi.chansonnier.agent;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class YoutubeGrabber
{
	private final Log _log = LogFactory.getLog(YoutubeGrabber.class);
	
	public InputStream getVideo(String pageUrl) {
  		try {
			URL video = new URL(getVideoUrl(pageUrl)); 
            _log.debug("it.polimi.chansonnier.agent.YoutubeGrabber: downloading " + video);
			URLConnection connection = video.openConnection();
			return connection.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
  
  /**
   * 
   * @param pageUrl
   * @return url of the FLV file
   * @throws Exception
   */
  private String getVideoUrl(String pageUrl) throws Exception
  {
	  URL page = new URL(pageUrl);
	  String query = page.getQuery();
	  String[] tokens = query.split("&");
	  HashMap<String, String> params = new HashMap<String, String>();
	  for (int i = 0; i < tokens.length; i++) {
		  String[] parts = tokens[i].split("=");
		  params.put(parts[0], parts[1]);
	  }
	  String vParam = params.get("v");
	  String uParam = pageUrl;
          String video_id = vParam;
          String u_id = uParam;
          if (video_id == null) video_id = inbtwn(URLDecoder.decode(getRedirUrl(u_id), "UTF-8"), "v=", "&");
          String pageSource = URLUtils.retrieve(new URL("http://www.youtube.com/watch?v=" + video_id));

          String title = inbtwn(pageSource, "'VIDEO_TITLE': '", "',");
          if (title == null) title = inbtwn(pageSource, "name=\"title\" content=\"", "\"");
          title = setHTMLEntity(title);

          String token = inbtwn(pageSource, "\"t\": \"", "\"");
          if (token == null) token = inbtwn(pageSource, "&t=", "&");
          if (!token.endsWith("%3D")) token = inbtwnmore(pageSource, "&t=", "&", 2);

          String dl_flvlow = null;
          String dl_flvmed = null;
          String dl_flvmed2 = null;
          String dl_flvhigh = null;

          dl_flvmed = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=34");
          if (dl_flvmed != null) {
        	  System.out.println("flvmed");
        	  return dl_flvmed;
          }
          dl_flvmed2 = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=6");
          if (dl_flvmed2 != null) {
        	  System.out.println("flvmed2");
        	  return dl_flvmed2;
          }
          dl_flvlow = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=5");
          if (dl_flvlow != null) {
        	  System.out.println("flvlow");
        	  return dl_flvlow;
          }
          dl_flvhigh = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=35");
          if (dl_flvhigh != null) {
        	  System.out.println("flvhigh");
        	  return dl_flvhigh;
          }

          // other formats
          String dl_3gplow = null;
          String dl_3gpmed = null;
          String dl_3gphigh = null;
          String dl_mp4high = null;
          String dl_mp4hd = null;
          String dl_mp4hd2 = null;

          if (dl_3gplow == null) dl_3gplow = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=13");
          if (dl_3gplow != null) return dl_3gplow;
          if (dl_3gpmed == null) dl_3gpmed = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=17");
          if (dl_3gpmed != null) return dl_3gpmed;
          if (dl_3gphigh == null) dl_3gphigh = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=36");
          if (dl_3gphigh != null) return dl_3gphigh;
         if (dl_mp4high == null) dl_mp4high = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=18");
          if (dl_mp4high != null) return dl_mp4high;
          if (dl_mp4hd == null) dl_mp4hd = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=22");
          if (dl_mp4hd != null) return dl_mp4hd;
          if (dl_mp4hd2 == null) dl_mp4hd2 = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=37");
          if (dl_mp4hd2 != null) return  dl_mp4hd2; 
      throw new Exception("No suitable file found.");

  }

  private String inbtwn(String input, String startcut, String finishcut)
  {
    String output = null;
    try {
      String[] arr1 = input.split(startcut);
      String[] arr2 = arr1[1].split(finishcut);
      output = arr2[0];
    } catch (Exception ex) {
      return null;
    }
    return output;
  }

  private String inbtwnmore(String input, String startcut, String finishcut, int times)
  {
    String output = null;
    try {
      String[] arr1 = input.split(startcut);
      String[] arr2 = arr1[times].split(finishcut);
      output = arr2[0];
    } catch (Exception ex) {
      return null;
    }
    return output;
  }
  
  private String getRedirUrl(String url)
  {
    String hdr = "";
    try
    {
      HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
      conn.setInstanceFollowRedirects(false);
      conn.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.8) Gecko/20100215 Ubuntu/9.04 (jaunty) Shiretoko/3.5.8");
      hdr = conn.getHeaderField("location");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    return hdr;
  }

  private String setHTMLEntity(String input)
  {
    String output = "";
    try
    {
      output = input.replace("&amp;", "_").toString();
      output = input.replace("&lt;", "_").toString();
      output = input.replace("&gt;", "_").toString();
      output = input.replace("&#39;", "_").toString();
      output = input.replace("&quot;", "_").toString();
      output = input.replace("&", "_").toString();
      output = output.replace("\\\"", "_").toString();
      output = output.replace("\\'", "_").toString();
      output = output.replace("'", "_").toString();
      output = output.replace("'", "_").toString();
      output = output.replace("<", "_").toString();
      output = output.replace(">", "_").toString();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return input;
    }
    return output;
    }


}
