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
package wtf.moonlight.features.modules.impl.visual.finalkills;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FkCounter {
    private static final String RED_WITHER_DEATH = "The Red Wither has died!";
    private static final String GREEN_WITHER_DEATH = "The Green Wither has died!";
    private static final String YELLOW_WITHER_DEATH = "The Yellow Wither has died!";
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-F]");
    private static final String BLUE_WITHER_DEATH = "The Blue Wither has died!";
    private static final String OWN_WITHER_DEATH = "Your wither has died. You can no longer respawn!";
    private static final String DEATHMATCH = "All withers are dead! 10 seconds till deathmatch!";
    private static final String PREP_PHASE = "Prepare your defenses!";
    public static final String MW_GAME_START_MESSAGE = "   You have 6 minutes until the walls fall down!";
    private static final String[] KILL_MESSAGES = {/*Banana messages, put those messages at the top to not conflict with the other pattern (\w{1,16}) was killed by (\w{1,16})*/
            "(\\w{1,16}) got banana pistol'd by (\\w{1,16}).*",
            "(\\w{1,16}) was peeled by (\\w{1,16}).*",
            "(\\w{1,16}) was mushed by (\\w{1,16}).*",
            "(\\w{1,16}) was hit by a banana split from (\\w{1,16}).*",
            "(\\w{1,16}) was killed by an explosive banana from (\\w{1,16}).*",
            "(\\w{1,16}) was killed by a magic banana from (\\w{1,16}).*",
            "(\\w{1,16}) was turned into mush by (\\w{1,16}).*",/*Default messages*/
            "(\\w{1,16}) was shot and killed by (\\w{1,16}).*",
            "(\\w{1,16}) was snowballed to death by (\\w{1,16}).*",
            "(\\w{1,16}) was killed by (\\w{1,16}).*",
            "(\\w{1,16}) was killed with a potion by (\\w{1,16}).*",
            "(\\w{1,16}) was killed with an explosion by (\\w{1,16}).*",
            "(\\w{1,16}) was killed with magic by (\\w{1,16}).*",/*Western messages*/
            "(\\w{1,16}) was filled full of lead by (\\w{1,16}).*",
            "(\\w{1,16}) was iced by (\\w{1,16}).*",
            "(\\w{1,16}) met their end by (\\w{1,16}).*",
            "(\\w{1,16}) lost a drinking contest with (\\w{1,16}).*",
            "(\\w{1,16}) was killed with dynamite by (\\w{1,16}).*",
            "(\\w{1,16}) lost the draw to (\\w{1,16}).*",/*Fire messages*/
            "(\\w{1,16}) was struck down by (\\w{1,16}).*",
            "(\\w{1,16}) was turned to dust by (\\w{1,16}).*",
            "(\\w{1,16}) was turned to ash by (\\w{1,16}).*",
            "(\\w{1,16}) was melted by (\\w{1,16}).*",
            "(\\w{1,16}) was incinerated by (\\w{1,16}).*",
            "(\\w{1,16}) was vaporized by (\\w{1,16}).*",/*Love messages*/
            "(\\w{1,16}) was struck with Cupid's arrow by (\\w{1,16}).*",
            "(\\w{1,16}) was given the cold shoulder by (\\w{1,16}).*",
            "(\\w{1,16}) was hugged too hard by (\\w{1,16}).*",
            "(\\w{1,16}) drank a love potion from (\\w{1,16}).*",
            "(\\w{1,16}) was hit by a love bomb from (\\w{1,16}).*",
            "(\\w{1,16}) was no match for (\\w{1,16}).*",/*Paladin messages*/
            "(\\w{1,16}) was smote from afar by (\\w{1,16}).*",
            "(\\w{1,16}) was justly ended by (\\w{1,16}).*",
            "(\\w{1,16}) was purified by (\\w{1,16}).*",
            "(\\w{1,16}) was killed with holy water by (\\w{1,16}).*",
            "(\\w{1,16}) was dealt vengeful justice by (\\w{1,16}).*",
            "(\\w{1,16}) was returned to dust by (\\w{1,16}).*",/*Pirate messages*/
            "(\\w{1,16}) be shot and killed by (\\w{1,16}).*",
            "(\\w{1,16}) be snowballed to death by (\\w{1,16}).*",
            "(\\w{1,16}) be sent to Davy Jones' locker by (\\w{1,16}).*",
            "(\\w{1,16}) be killed with rum by (\\w{1,16}).*",
            "(\\w{1,16}) be shot with cannon by (\\w{1,16}).*",
            "(\\w{1,16}) be killed with magic by (\\w{1,16}).*",/*BBQ messages*/
            "(\\w{1,16}) was glazed in BBQ sauce by (\\w{1,16}).*",
            "(\\w{1,16}) was sprinkled with chilli powder by (\\w{1,16}).*",
            "(\\w{1,16}) was sliced up by (\\w{1,16}).*",
            "(\\w{1,16}) was overcooked by (\\w{1,16}).*",
            "(\\w{1,16}) was deep fried by (\\w{1,16}).*",
            "(\\w{1,16}) was boiled by (\\w{1,16}).*",/*Squeak messages*/
            "(\\w{1,16}) was squeaked from a distance by (\\w{1,16}).*",
            "(\\w{1,16}) was hit by frozen cheese from (\\w{1,16}).*",
            "(\\w{1,16}) was chewed up by (\\w{1,16}).*",
            "(\\w{1,16}) was chemically cheesed by (\\w{1,16}).*",
            "(\\w{1,16}) was turned into cheese whiz by (\\w{1,16}).*",
            "(\\w{1,16}) was magically squeaked by (\\w{1,16}).*",/*Bunny messages*/
            "(\\w{1,16}) was hit by a flying bunny by (\\w{1,16}).*",
            "(\\w{1,16}) was hit by a bunny thrown by (\\w{1,16}).*",
            "(\\w{1,16}) was turned into a carrot by (\\w{1,16}).*",
            "(\\w{1,16}) was hit by a carrot from (\\w{1,16}).*",
            "(\\w{1,16}) was bitten by a bunny from (\\w{1,16}).*",
            "(\\w{1,16}) was magically turned into a bunny by (\\w{1,16}).*",
            "(\\w{1,16}) was fed to a bunny by (\\w{1,16}).*",/*Natural deaths messages*/
            "(\\w{1,16}) starved to death\\.",
            "(\\w{1,16}) hit the ground too hard\\.",
            "(\\w{1,16}) blew up\\.",
            "(\\w{1,16}) exploded\\.",
            "(\\w{1,16}) tried to swim in lava\\.",
            "(\\w{1,16}) went up in flames\\.",
            "(\\w{1,16}) burned to death\\.",
            "(\\w{1,16}) suffocated in a wall\\.",
            "(\\w{1,16}) suffocated\\.",
            "(\\w{1,16}) fell out of the world\\.",
            "(\\w{1,16}) had a block fall on them\\.",
            "(\\w{1,16}) drowned\\.",
            "(\\w{1,16}) died from a cactus\\."
    };
    private static final int TEAMS = 4;
    public static int RED_TEAM = 0;
    public static int GREEN_TEAM = 1;
    public static int YELLOW_TEAM = 2;
    public static int BLUE_TEAM = 3;
    private static final String[] SCOREBOARD_PREFIXES = new String[]{"[R]", "[G]", "[Y]", "[B]"};
    private static final String[] DEFAULT_PREFIXES = new String[]{"c", "a", "e", "9"};
    private final boolean[] deadWithers;
    private final String[] prefixes;
    private final HashMap<String, Integer>[] teamKills;
    private final ArrayList<String> deadPlayers;
    private final Minecraft mc;
    private static final Logger logger = LogManager.getLogger();
    private static Pattern[] KILL_PATTERN;

    public FkCounter() {
        this.deadWithers = new boolean[4];
        this.prefixes = new String[4];
        this.teamKills = (HashMap<String, Integer>[]) new HashMap[4];
        this.deadPlayers = new ArrayList<String>();
        KILL_PATTERN = new Pattern[KILL_MESSAGES.length];
        for (int i = 0; i < KILL_MESSAGES.length; i++) {
            KILL_PATTERN[i] = Pattern.compile(KILL_MESSAGES[i]);
        }
        for (int team = 0; team < 4; ++team) {
            this.prefixes[team] = FkCounter.DEFAULT_PREFIXES[team];
            this.teamKills[team] = new HashMap<String, Integer>();
        }
        mc = Minecraft.getMinecraft();
    }

    public void onChatMessage(IChatComponent event) {

        String rawMessage = event.getUnformattedText();
        String colorMessage = event.getFormattedText();

//Team color detection
        if (rawMessage.equals(PREP_PHASE)) {
            setTeamPrefixes();
        }

//Wither death detection
        switch (rawMessage) {
            case RED_WITHER_DEATH:
                deadWithers[RED_TEAM] = true;
                return;
            case GREEN_WITHER_DEATH:
                deadWithers[GREEN_TEAM] = true;
                return;
            case YELLOW_WITHER_DEATH:
                deadWithers[YELLOW_TEAM] = true;
                return;
            case BLUE_WITHER_DEATH:
                deadWithers[BLUE_TEAM] = true;
                return;
            case OWN_WITHER_DEATH:
                String teamColor = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName().split("\u00a7")[1].substring(0, 1);
                setWitherDead(teamColor);
                return;
            case DEATHMATCH:
                for (int team = 0; team < TEAMS; team++) {
                    deadWithers[team] = true;
                }
                return;
        }
        for (final Pattern pattern : KILL_PATTERN) {

            final Matcher matcher = pattern.matcher(rawMessage);

            if (matcher.matches()) {

                if (matcher.groupCount() == 2) {
                    final String killedPlayer = matcher.group(1);
                    final String killer = matcher.group(2);
                    final String killedTeamColor = getLastColorCodeBefore(colorMessage, killedPlayer);
                    final String killerTeamColor = getLastColorCodeBefore(colorMessage.replaceFirst(killedPlayer, ""), killer);
                    if (!killedTeamColor.equals("") && !killerTeamColor.equals("")) {
                        removeKilledPlayer(killedPlayer, killedTeamColor);
                        if (getWitherDead(killedTeamColor)) {
                            addKill(killer, killerTeamColor);
                        }
                        break;
                    }
                }
            }
        }
    }

    public static String getLastColorCodeOf(String text) {
        final Matcher matcher = COLOR_CODE_PATTERN.matcher(text);
        String s = "";
        while (matcher.find()) {
            s = matcher.group();
        }
        return String.valueOf(s.charAt(1));
    }

    public static String getLastColorCodeBefore(String message, String target) {
        final String[] split = message.split(target, 2);
        if (split.length != 2) {
            return "";
        }
        return getLastColorCodeOf(split[0]);
    }

    public int getKills(int team) {
        if (!this.isValidTeam(team)) {
            return 0;
        }
        int kills = 0;
        for (int k : this.teamKills[team].values()) {
            kills += k;
        }
        return kills;
    }

    public HashMap<String, Integer> getPlayers(int team) {
        if (!this.isValidTeam(team)) {
            return new HashMap<String, Integer>();
        }
        return this.teamKills[team];
    }

    private void setWitherDead(String color) {
        int team = this.getTeamFromColor(color);
        if (this.isValidTeam(team)) {
            this.deadWithers[team] = true;
        }
    }

    private boolean getWitherDead(String color) {
        int team = this.getTeamFromColor(color);
        return this.isValidTeam(team) && this.deadWithers[team];
    }

    private void setTeamPrefixes() {
        for (String line : getScoreboardNames()) {
            for (int team = 0; team < 4; ++team) {
                if (line.contains(FkCounter.SCOREBOARD_PREFIXES[team])) {
                    this.prefixes[team] = line.split("��")[1].substring(0, 1);
                }
            }
        }
    }

    private void removeKilledPlayer(String player, String color) {
        int team = this.getTeamFromColor(color);
        if (!this.isValidTeam(team)) {
            return;
        }
        if (this.deadWithers[team]) {
            this.teamKills[team].remove(player);
            this.deadPlayers.add(player);
        }
    }

    private void addKill(String player, String color) {
        int team = this.getTeamFromColor(color);
        if (!this.isValidTeam(team)) {
            return;
        }
        if (this.deadPlayers.contains(player)) {
            return;
        }
        if (this.teamKills[team].containsKey(player)) {
            this.teamKills[team].put(player, this.teamKills[team].get(player) + 1);
        } else {
            this.teamKills[team].put(player, 1);
        }
        this.sortTeamKills(team);
    }


    private void sortTeamKills(int team) {
        if (!isValidTeam(team)) {
            return;
        }

        teamKills[team] = teamKills[team].entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    private int getTeamFromColor(String color) {
        for (int team = 0; team < 4; ++team) {
            if (this.prefixes[team].equalsIgnoreCase(color)) {
                return team;
            }
        }
        return -1;
    }

    private boolean isValidTeam(int team) {
        return team >= 0 && team < 4;
    }

    private ArrayList<String> getScoreboardNames() {
        ArrayList<String> scoreboardNames = new ArrayList<String>();
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
            Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
            for (Score score : scores) {
                ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                scoreboardNames.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
            }
        } catch (Exception ex) {
        }
        return scoreboardNames;
    }
}