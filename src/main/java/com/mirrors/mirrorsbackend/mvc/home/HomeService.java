package com.mirrors.mirrorsbackend.mvc.home;

import com.mirrors.mirrorsbackend.entities.marketplace_post.MarketplacePostService;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUser;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserInformation;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserRole;
import com.mirrors.mirrorsbackend.entities.marketplace_user.MarketplaceUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HomeService {

    private final MarketplaceUserService marketplaceUserService;
    private final MarketplacePostService marketplacePostService;

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
            throw new IllegalStateException("No rights to ban user: " + userToBeBanned.getDisplayName());

        marketplacePostService.deleteMarketplacePostsOfUser(userToBeBanned);
        marketplaceUserService.disableUser(userToBeBanned);
    }

}
