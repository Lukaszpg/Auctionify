package pro.lukasgorny.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by Łukasz on 22.11.2017.
 */
@Entity
@Table(name = "bids")
public class Bid extends Model {

    private BigDecimal offer;
    private Boolean isWinning;
    protected Auction auction;
    protected User user;

    @ManyToOne
    @JoinColumn(name="auction_id")
    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Boolean getIsWinning() {
        return isWinning;
    }

    public void setIsWinning(Boolean winning) {
        isWinning = winning;
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getOffer() {
        return offer;
    }

    public void setOffer(BigDecimal offer) {
        this.offer = offer;
    }
}