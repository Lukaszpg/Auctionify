package pro.lukasgorny.service.auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.lukasgorny.dto.auction.AuctionSaveDto;
import pro.lukasgorny.model.Auction;
import pro.lukasgorny.repository.AuctionRepository;
import pro.lukasgorny.service.category.GetCategoryService;

import java.time.LocalDateTime;

/**
 * Created by Łukasz on 20.11.2017.
 */
@Service
public class CreateAuctionServiceImpl implements CreateAuctionService {

    private final AuctionRepository auctionRepository;
    private final GetCategoryService getCategoryService;

    @Autowired
    public CreateAuctionServiceImpl(AuctionRepository auctionRepository, GetCategoryService getCategoryService) {
        this.auctionRepository = auctionRepository;
        this.getCategoryService = getCategoryService;
    }

    @Override
    public Auction create(AuctionSaveDto auctionSaveDto) {
        Auction auction = createEntityFromDto(auctionSaveDto);
        return auctionRepository.save(auction);
    }

    private Auction createEntityFromDto(AuctionSaveDto auctionSaveDto) {
        Auction auction = new Auction();
        auction.setCategory(getCategoryService.getById(auctionSaveDto.getCategoryId()));
        auction.setTitle(auctionSaveDto.getTitle());
        auction.setNew(auctionSaveDto.getIsNew());
        auction.setEditorContent(auctionSaveDto.getEditorContent());
        auction.setBuyout(calculateBooleanFieldValue(auctionSaveDto.getIsBuyout()));
        auction.setBid(calculateBooleanFieldValue(auctionSaveDto.getIsBid()));
        auction.setPrice(auctionSaveDto.getPrice());
        auction.setAmount(auctionSaveDto.getAmount());
        auction.setBidMinimalPrice(auctionSaveDto.getBidMinimalPrice());
        auction.setBidStartingPrice(auctionSaveDto.getBidStartingPrice());
        auction.setEndDate(calculateEndDate(auctionSaveDto));
        auction.setDeleted(false);
        return auction;
    }

    private LocalDateTime calculateEndDate(AuctionSaveDto auctionSaveDto) {
        return LocalDateTime.now().plusDays(auctionSaveDto.getAuctionDuration());
    }

    private Boolean calculateBooleanFieldValue(Boolean field) {
        return field != null;
    }
}
