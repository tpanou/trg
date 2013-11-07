package org.teiath.web.facebook.trg;

import com.visural.common.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Konstantinos Efthymiou
 * Date: 2/5/2013
 * Time: 10:28 πμ
 * To change this template use File | Settings | File Templates.
 */
public class FacebookToolKitListings {

	private static final String api_key = "199970486793280";
	private static final String secret = "0662fb220ab3450507cb17563bfca6fe";

	// set this to your servlet URL for the authentication servlet/filter
	private static final String redirect_uri = "https://swaps.teiath.gr/fbauthlistings";
	/// set this to the list of extended permissions you want
	private static final String[] perms = new String[] {"publish_stream, offline_access"};

	public static String getAPIKey() {
		return api_key;
	}

	public static String getSecret() {
		return secret;
	}

	public static String getLoginRedirectURL() {
		return "https://www.facebook.com/dialog/oauth?client_id=" +
				api_key + "&redirect_uri=" +
				redirect_uri + "&scope=" + StringUtil.delimitObjectsToString(",", perms);
	}

	public static String getAuthURL(String authCode) {
		return "https://graph.facebook.com/oauth/access_token?client_id=" +
				api_key + "&redirect_uri=" +
				redirect_uri + "&client_secret=" + secret + "&code=" + authCode;
	}
}
