package pro.lukasgorny.dto.auction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import pro.lukasgorny.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Łukasz on 26.10.2017.
 */
public class AuctionSaveDto {

    private String id;

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotNull
    private String categoryId;

    @NotNull
    private Boolean isNew;

    @NotBlank
    private String editorContent;

    private BigDecimal price;

    private BigDecimal bidStartingPrice;

    private BigDecimal bidMinimalPrice;

    private Integer amount;

    private Boolean isBuyout;

    private Boolean isBid;

    private User seller;

    private Long auctionDuration;

    private MultipartFile[] photos;

    private Boolean untilOutOfItems;

    private List<ShippingDto> shippingDtos;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public Long getAuctionDuration() {
        return auctionDuration;
    }

    public void setAuctionDuration(Long auctionDuration) {
        this.auctionDuration = auctionDuration;
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

    public BigDecimal getBidMinimalPrice() {
        return bidMinimalPrice;
    }

    public void setBidMinimalPrice(BigDecimal bidMinimalPrice) {
        this.bidMinimalPrice = bidMinimalPrice;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public MultipartFile[] getPhotos() {
        return photos;
    }

    public void setPhotos(MultipartFile[] photos) {
        this.photos = photos;
    }

    public Boolean getUntilOutOfItems() {
        return untilOutOfItems;
    }

    public void setUntilOutOfItems(Boolean untilOutOfItems) {
        this.untilOutOfItems = untilOutOfItems;
    }

    public List<ShippingDto> getShippingDtos() {
        return shippingDtos;
    }

    public void setShippingDtos(List<ShippingDto> shippingDtos) {
        this.shippingDtos = shippingDtos;
    }
}
