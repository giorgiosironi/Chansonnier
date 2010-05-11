package it.polimi.chansonnier.agent;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeGrabber// extends JApplet
{
  private String mv_decrypt(String input, int k1, int k2)
  {
    LinkedList req1= new LinkedList();
    int req3 = 0;
    while (req3 < input.length()) {
      char c = input.charAt(req3);
      switch (c) { case '0':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        break;
      case '1':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        break;
      case '2':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        break;
      case '3':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        break;
      case '4':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        break;
      case '5':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        break;
      case '6':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        break;
      case '7':
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        break;
      case '8':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        break;
      case '9':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        break;
      case 'a':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        break;
      case 'b':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        break;
      case 'c':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(0));
        break;
      case 'd':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        req1.add(Integer.valueOf(1));
        break;
      case 'e':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(0));
        break;
      case 'f':
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
        req1.add(Integer.valueOf(1));
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`': } ++req3;
    }

    LinkedList req6 = new LinkedList();
    req3 = 0;
    while (req3 < 384) {
      k1 = (k1 * 11 + 77213) % 81371;
      k2 = (k2 * 17 + 92717) % 192811;
      req6.add(Integer.valueOf((k1 + k2) % 128));
      ++req3;
    }
    req3 = 256;
    while (req3 >= 0) {
      int req5 = ((Integer)req6.get(req3)).intValue();
      int req4 = req3 % 128;
      int req8 = ((Integer)req1.get(req5)).intValue();
      req1.set(req5, req1.get(req4));
      req1.set(req4, Integer.valueOf(req8));
      --req3;
    }
    req3 = 0;
    while (req3 < 128) {
      req1.set(req3, Integer.valueOf(((Integer)req1.get(req3)).intValue() ^ ((Integer)req6.get(req3 + 256)).intValue() & 0x1));
      ++req3;
    }

    String out = "";
    req3 = 0;
    while (req3 < req1.size()) {
      int tmp = ((Integer)req1.get(req3)).intValue() * 8;
      tmp += ((Integer)req1.get(req3 + 1)).intValue() * 4;
      tmp += ((Integer)req1.get(req3 + 2)).intValue() * 2;
      tmp += ((Integer)req1.get(req3 + 3)).intValue();
      switch (tmp)
      {
      case 0:
        out = out + "0";
        break;
      case 1:
        out = out + "1";
        break;
      case 2:
        out = out + "2";
        break;
      case 3:
        out = out + "3";
        break;
      case 4:
        out = out + "4";
        break;
      case 5:
        out = out + "5";
        break;
      case 6:
        out = out + "6";
        break;
      case 7:
        out = out + "7";
        break;
      case 8:
        out = out + "8";
        break;
      case 9:
        out = out + "9";
        break;
      case 10:
        out = out + "a";
        break;
      case 11:
        out = out + "b";
        break;
      case 12:
        out = out + "c";
        break;
      case 13:
        out = out + "d";
        break;
      case 14:
        out = out + "e";
        break;
      case 15:
        out = out + "f";
      }

      req3 += 4;
    }
    return out;
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
  
  private Map<String, String> _parameters = new HashMap<String, String>();
  
  public String getParameter(String name) {
	 return _parameters.get(name);
  }
  
  
  /**
   * 
   * @param pageUrl
   * @return url of the FLV file
   * @throws Exception
   */
  private String getFlvUrl(String pageUrl) throws Exception
  {
	  URL page = new URL(pageUrl);
	  String query = page.getQuery();
	  String[] tokens = query.split("&");
	  HashMap<String, String> params = new HashMap<String, String>();
	  for (int i = 0; i < tokens.length; i++) {
		  String[] parts = tokens[i].split("=");
		  params.put(parts[0], parts[1]);
	  }
	  Pattern p = Pattern.compile("watch\\?v=([.]*)");
	  Matcher m = p.matcher(pageUrl);
	  _parameters.put("v", params.get("v"));
	  _parameters.put("u", pageUrl);
	  _parameters.put("site", "youtube.com");
	  _parameters.put("ua", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.8) Gecko/20100215 Ubuntu/9.04 (jaunty) Shiretoko/3.5.8");
    String error = null;
      String vParam = getParameter("v");
      String uParam = getParameter("u");
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

          if (dl_flvmed == null) dl_flvmed = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=34");
          if (dl_flvmed != null) return dl_flvmed;
          if (dl_flvmed2 == null) dl_flvmed2 = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=6");
          if (dl_flvmed2 != null) return dl_flvmed2;
          if (dl_flvlow == null) dl_flvlow = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=5");
          if (dl_flvlow != null) return dl_flvlow;
          if (dl_flvhigh == null) dl_flvhigh = getRedirUrl("http://www.youtube.com/get_video?video_id=" + video_id + "&t=" + token + "&fmt=35");
          if (dl_flvhigh != null) return dl_flvhigh;
          if (1 == 1) return "";
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
      throw new Exception("No suitable FLV found.");

  }

  private String getRedirUrl(String url)
  {
    String hdr = "";
    try
    {
      HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
      conn.setInstanceFollowRedirects(false);
      conn.addRequestProperty("User-Agent", getParameter("ua"));
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

  	public InputStream getVideo(String pageUrl) {
  		try {
			URL apiEndPoint = new URL(getFlvUrl(pageUrl)); 
                  
			URLConnection connection = apiEndPoint.openConnection();
			return connection.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
