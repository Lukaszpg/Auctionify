package pro.lukasgorny.dto.auction;

import pro.lukasgorny.model.Category;

import java.math.BigDecimal;

/**
 * Created by Łukasz on 26.10.2017.
 */
public class AuctionResultDto {

    private String id;
    private String title;
    private Boolean isNew;
    private String editorContent;
    private BigDecimal price;
    private BigDecimal bidStartingPrice;
    private Integer amount;
    private Boolean isBuyout;
    private Boolean isBid;
    private Category category;
    private String endDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEditorContent() {
        return editorContent;
    }

    public void setEditorContent(String editorContent) {
        this.editorContent = editorContent;
    }

    public Boolean getIsBuyout() {
        return isBuyout;
    }

    public void setIsBuyout(Boolean buyout) {
        isBuyout = buyout;
    }

    public Boolean getIsBid() {
        return isBid;
    }

    public void setIsBid(Boolean bid) {
        isBid = bid;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean aNew) {
        isNew = aNew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getBidStartingPrice() {
        return bidStartingPrice;
    }

    public void setBidStartingPrice(BigDecimal bidStartingPrice) {
        this.bidStartingPrice = bidStartingPrice;
    }
}