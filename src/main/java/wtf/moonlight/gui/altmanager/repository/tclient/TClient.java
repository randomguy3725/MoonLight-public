/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.gui.altmanager.repository.tclient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfile;
import wtf.moonlight.gui.altmanager.repository.hypixel.HypixelProfileFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class TClient {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36 OPR/65.0.3467.62";
	private static final String CONTENT_TYPE = "application/json";
	private static final String LICENSE_KEY = "YWjDcNZfiryPSQBosg07bC4EVXkpI9GHT5MUnqwtmDdMTXgRENCkoSeatncvKiWq";
	private static final int TIMEOUT = 5_000;
	private final Logger logger = LogManager.getLogger();
	private final JsonParser parser = new JsonParser();
	public @Nullable HypixelProfile find(@NotNull UUID uuid) {
		HttpURLConnection connection;

		try {
            logger.info("Opening connection with TClient");
			connection = (HttpURLConnection) new URL(
					"https://tclient.club/Modules/api/hypixel?cmd=stats&lang=ENG&service=other&no_images=1&args=" + uuid).openConnection();
		} catch(IOException e) {
            logger.error("An I/O error occurred while opening connection", e);
			return null;
		}

		connection.setConnectTimeout(5_000);
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		connection.setRequestProperty("license-key", LICENSE_KEY);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		JsonElement jsonElement;
		byte[] byteArray = new byte[0];

		try {
			connection.connect();

			int status = connection.getResponseCode();

			if(status < 200 || status >= 300) {
                logger.error("Server returned code " + status);
				return null;
			}

			byteArray = IOUtils.toByteArray(connection.getInputStream());
			InputStream in = new ByteArrayInputStream(byteArray);

			jsonElement = parser.parse(new InputStreamReader(in));
		} catch(IOException e) {
			try {
                logger.error("An I/O error occurred while reading response: {}",
						IOUtils.readLines(new InputStreamReader(new ByteArrayInputStream(byteArray))), e);
			} catch(IOException ex) {
                logger.error("An I/O error occurred while reading response", e);
			}

			return null;
		} finally {
			connection.disconnect();
		}

		if(!jsonElement.isJsonObject()) {
			return null;
		}

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement okElement = jsonObject.get("ok");

		if(okElement == null || !okElement.isJsonPrimitive() || !okElement.getAsBoolean()) return null;

		JsonElement contentElement = jsonObject.get("response");
		if(contentElement == null || !contentElement.isJsonObject()) return null;
		JsonObject contentObject = contentElement.getAsJsonObject();

		JsonElement statsElement = contentObject.get("stats");
		if(statsElement == null || !statsElement.isJsonObject()) return null;
		JsonObject statsObject = statsElement.getAsJsonObject();

		JsonElement rankElement = statsObject.get("privilege");
		if(rankElement == null || !rankElement.isJsonPrimitive()) return null;
		String rank = rankElement.getAsString();

		JsonElement levelElement = statsObject.get("level");
		if(levelElement == null || !levelElement.isJsonPrimitive()) return null;
		int level = levelElement.getAsInt();

		JsonElement guildElement = statsObject.get("guild");
		if(guildElement == null || !guildElement.isJsonPrimitive()) return null;
		String guild = guildElement.getAsString();

		JsonElement achievementPointsElement = statsObject.get("achievementPoints");
		if(achievementPointsElement == null || !achievementPointsElement.isJsonPrimitive()) return null;
		int achievementPoints = achievementPointsElement.getAsInt();

		JsonElement questsElement = statsObject.get("quests");
		if(questsElement == null || !questsElement.isJsonPrimitive()) return null;
		int quests = questsElement.getAsInt();

		JsonElement friendsElement = statsObject.get("friends");
		if(friendsElement == null || !friendsElement.isJsonPrimitive()) return null;
		int friends = friendsElement.getAsInt();

		JsonElement firstLoginElement = statsObject.get("firstLogin_timestamp");
		if(firstLoginElement == null || !firstLoginElement.isJsonPrimitive()) return null;
		long firstLogin = firstLoginElement.getAsLong();

		JsonElement lastLoginElement = statsObject.get("lastLogin_timestamp");
		if(lastLoginElement == null || !lastLoginElement.isJsonPrimitive()) return null;
		long lastLogin = lastLoginElement.getAsLong();

		JsonElement cachedElement = statsObject.get("cached_timestamp");
		if(cachedElement == null || !cachedElement.isJsonPrimitive()) return null;
		long cached = cachedElement.getAsLong() * 1_000;

		return HypixelProfileFactory.hypixelProfile(rank, level, guild, achievementPoints, quests, friends, firstLogin, lastLogin, cached);
	}

}
