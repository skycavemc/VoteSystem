package de.hakuyamu.skybee.votesystem.listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.hakuyamu.skybee.votesystem.enums.TrustedServices;
import de.hakuyamu.skybee.votesystem.util.VoteUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotifierListener implements Listener {

    @EventHandler
    public void onVotifier(VotifierEvent event) {
        // check if the vote comes from a trusted service
        Vote vote = event.getVote();
        if (!TrustedServices.isListed(vote.getServiceName())) {
            return;
        }

        VoteUtil.processVote(vote.getUsername());
    }

}
