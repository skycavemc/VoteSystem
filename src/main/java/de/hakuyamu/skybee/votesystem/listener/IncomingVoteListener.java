package de.hakuyamu.skybee.votesystem.listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.hakuyamu.skybee.votesystem.enums.TrustedServices;
import de.hakuyamu.skybee.votesystem.util.VoteUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IncomingVoteListener implements Listener {

    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        if (!TrustedServices.isListed(vote.getServiceName())) {
            return;
        }

        VoteUtils.processVote(vote.getUsername());
    }

}
