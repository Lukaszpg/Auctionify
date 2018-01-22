package pro.lukasgorny.service.auction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.lukasgorny.dto.UserResultDto;
import pro.lukasgorny.dto.auction.AuctionResultDto;
import pro.lukasgorny.dto.auction.TransactionResultDto;
import pro.lukasgorny.model.Auction;
import pro.lukasgorny.model.User;
import pro.lukasgorny.repository.AuctionRepository;
import pro.lukasgorny.service.hash.HashService;
import pro.lukasgorny.util.DateFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Łukasz on 21.11.2017.
 */
@Service
public class GetAuctionServiceImpl implements GetAuctionService {

    private final HashService hashService;
    private final AuctionRepository auctionRepository;
    private final GetTransactionService getTransactionService;

    @Autowired
    public GetAuctionServiceImpl(AuctionRepository auctionRepository, HashService hashService,
                                 GetTransactionService getTransactionService) {
        this.hashService = hashService;
        this.auctionRepository = auctionRepository;
        this.getTransactionService = getTransactionService;
    }

    @Override
    public List<AuctionResultDto> getTopAuctions() {
        List<Auction> auctions = auctionRepository.findTop10ByHasEndedIsFalse();
        return createDtoListFromEntityList(auctions);
    }

    @Override
    public AuctionResultDto getOne(String id) {
        Auction auction = auctionRepository.findOne(hashService.decode(id));

        if (auction != null) {
            return createDtoFromEntity(auction);
        }

        return null;
    }

    @Override
    public AuctionResultDto getOne(Long id) {
        Auction auction = auctionRepository.findOne(id);

        if (auction != null) {
            return createDtoFromEntity(auction);
        }

        return null;
    }

    @Override
    public Auction getOneEntity(String id) {
        return auctionRepository.findOne(hashService.decode(id));
    }

    @Override
    public Boolean auctionExists(String id) {
        return getOne(id) != null;
    }

    @Override
    public List<AuctionResultDto> getAllObservedByUserId(Long id) {
        List<Auction> auctions = auctionRepository.findAllByUsersObserving_Id(id);
        return createDtoListFromEntityList(auctions);
    }

    @Override
    public List<Auction> getAllAuctionsToEnd() {
        return auctionRepository.findAllByHasEndedIsFalseAndEndDateBefore(LocalDateTime.now());
    }

    @Override
    public Integer getAuctionCurrentItemAmount(String id) {
        return auctionRepository.findCurrentItemsAmountByAuctionId(hashService.decode(id));
    }

    private List<AuctionResultDto> createDtoListFromEntityList(List<Auction> auctions) {
        return auctions != null ? auctions.stream().map(this::createDtoFromEntity).collect(Collectors.toList()) : new ArrayList<>();
    }

    private AuctionResultDto createDtoFromEntity(Auction auction) {
        AuctionResultDto auctionResultDto = new AuctionResultDto();
        auctionResultDto.setId(hashService.encode(auction.getId()));
        auctionResultDto.setTitle(auction.getTitle());
        auctionResultDto.setEditorContent(auction.getEditorContent());
        auctionResultDto.setIsBuyout(auction.getBuyout());
        auctionResultDto.setIsBid(auction.getBid());
        auctionResultDto.setPrice(auction.getPrice());
        auctionResultDto.setAmount(auction.getAmount());
        auctionResultDto.setIsNew(auction.getNew());
        auctionResultDto.setCategory(auction.getCategory());
        auctionResultDto.setBidStartingPrice(auction.getBidStartingPrice());
        auctionResultDto.setBidMinimalPrice(auction.getBidMinimalPrice());
        auctionResultDto.setEndDate(DateFormatter.formatDateToCountdownFormat(auction.getEndDate()));
        auctionResultDto.setSellerDto(createUserDtoFromEntity(auction.getSeller()));
        auctionResultDto.setWinningBid(getWinningBid(auctionResultDto.getId()));
        auctionResultDto.setHasEnded(auction.getHasEnded());
        auctionResultDto.setCurrentAmount(auction.getCurrentAmount());

        return auctionResultDto;
    }

    private UserResultDto createUserDtoFromEntity(User user) {
        UserResultDto userResultDto = new UserResultDto();
        userResultDto.setEmail(user.getEmail());
        return userResultDto;
    }

    private TransactionResultDto getWinningBid(String id) {
        return getTransactionService.getWinningBidForAuction(id);
    }
}
