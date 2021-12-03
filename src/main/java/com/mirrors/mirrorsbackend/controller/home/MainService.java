package com.mirrors.mirrorsbackend.controller.home;

import com.mirrors.mirrorsbackend.model.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserInformation;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserService;
import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MainService {

    private final MarketplaceUserService marketplaceUserService;
    private final MarketplacePostService marketplacePostService;
    private final SessionRegistry sessionRegistry;

    public MarketplaceUser getCurrentUser() {
        return marketplaceUserService.getUserFromContext();
    }

    public MarketplaceUserInformation getUserInformation(String userId) {
        MarketplaceUser user = marketplaceUserService.getUserById(userId);
        return marketplaceUserService.getUserInformation(user);
    }

    public void banUser(String userId) {
        MarketplaceUser admin = marketplaceUserService.getUserFromContext();
        MarketplaceUser userToBeBanned = marketplaceUserService.getUserById(userId);

        if (admin.getMarketplaceUserRole() != MarketplaceUserRole.ADMIN)
            throw new MarketplaceException("No rights to ban user: " + userToBeBanned.getDisplayName());

        marketplacePostService.deletePostsOfUser(userToBeBanned);
        marketplaceUserService.lockUser(userToBeBanned);

        // Invalidating session of banned user

        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof MarketplaceUser sessionUser) {
                if (sessionUser.getEmail().equals(userToBeBanned.getEmail())) {
                    var sessions = sessionRegistry.getAllSessions(principal, true);
                    sessions.forEach((session) -> sessionRegistry.getSessionInformation(session.getSessionId()).expireNow());
                }
            }
        }
    }
}
