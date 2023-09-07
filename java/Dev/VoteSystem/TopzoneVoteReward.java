package Dev.VoteSystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSite.TOPZONE, apiKey = "TopzoneApiKey")
public class TopzoneVoteReward extends VoteRewardSite {

    @Override
    protected String getEndpoint(Player player) {
        return String.format(Config.VOTE_TOPZONE, getApiKey(), player.getIpAddress());
    }

    @Override
    protected boolean hasVoted(Player player) {
        String serverResponse = VoteApiService.getApiResponse(getEndpoint(player));
        if(serverResponse.length() == 0){
            player.sendMessage("Something went wrong with this request. Report it to the administrator.");
            return false;
        }
        JsonElement jelement = new JsonParser().parse(serverResponse);
        JsonObject  topObject = jelement.getAsJsonObject();
        JsonPrimitive isOkPrimitive = topObject.getAsJsonPrimitive("ok");
        if(!isOkPrimitive.getAsBoolean()){
            return false;
        }

        JsonObject votedObject = topObject.getAsJsonObject("result");
        JsonPrimitive isVotedObject = votedObject.getAsJsonPrimitive("isVoted");
        return isVotedObject.getAsBoolean();
    }
}