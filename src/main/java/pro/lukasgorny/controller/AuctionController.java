package pro.lukasgorny.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import pro.lukasgorny.dto.auction.AuctionSaveDto;
import pro.lukasgorny.dto.auction.BidDto;
import pro.lukasgorny.service.auction.BidService;
import pro.lukasgorny.service.user.UserService;
import pro.lukasgorny.util.Templates;
import pro.lukasgorny.model.Auction;
import pro.lukasgorny.service.auction.GetAuctionService;
import pro.lukasgorny.service.auction.CreateAuctionService;
import pro.lukasgorny.service.hash.HashService;
import pro.lukasgorny.util.Urls;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by Łukasz on 25.10.2017.
 */

@Controller
@RequestMapping(Urls.Auction.MAIN)
public class AuctionController {

    private final UserService userService;
    private final GetAuctionService getAuctionService;
    private final CreateAuctionService createAuctionService;
    private final HashService hashService;
    private final BidService bidService;

    @Autowired
    public AuctionController(UserService userService, GetAuctionService getAuctionService,
                             CreateAuctionService createAuctionService,
                             HashService hashService, BidService bidService) {
        this.userService = userService;
        this.getAuctionService = getAuctionService;
        this.createAuctionService = createAuctionService;
        this.hashService = hashService;
        this.bidService = bidService;
    }

    @GetMapping(Urls.Auction.SELL)
    public ModelAndView sell() {
        ModelAndView modelAndView = new ModelAndView(Templates.AuctionTemplates.SELL);
        AuctionSaveDto auctionSaveDto = new AuctionSaveDto();
        auctionSaveDto.setIsBid(false);
        auctionSaveDto.setIsBuyout(true);
        modelAndView.addObject("auctionSaveDto", auctionSaveDto);
        return modelAndView;
    }

    @PostMapping(Urls.Auction.SELL)
    public ModelAndView createAuction(@Valid AuctionSaveDto auctionSaveDto, BindingResult bindingResult, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();

        if(auctionSaveDto.getIsBid() == null && auctionSaveDto.getIsBuyout() == null) {
            bindingResult.rejectValue("isBuyout", "error.pick.auction.type");
        }

        if(auctionSaveDto.getIsBid() != null && auctionSaveDto.getBidStartingPrice() == null) {
            bindingResult.rejectValue("bidStartingPrice", "error.bid.price");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName(Templates.AuctionTemplates.SELL);
        } else {
            auctionSaveDto.setUser(userService.getByEmail(principal.getName()));
            createAuctionService.setAuctionSaveDto(auctionSaveDto);

            Auction auction = createAuctionService.create();
            ModelMap modelMap = new ModelMap();
            modelMap.addAttribute("auctionId", hashService.encode(auction.getId()));
            return new ModelAndView(Urls.Auction.CREATE_SUCCESS_REDIRECT, modelMap);
        }

        return modelAndView;
    }

    @GetMapping(Urls.Auction.CREATE_SUCCESS)
    public ModelAndView createSuccess(@ModelAttribute("auctionId") String id) {
        ModelAndView modelAndView = new ModelAndView(Templates.AuctionTemplates.CREATE_SUCCESS);
        modelAndView.addObject("auctionId", id);
        return modelAndView;
    }

    @GetMapping(Urls.Auction.GET)
    public ModelAndView getAuction(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView(Templates.AuctionTemplates.ITEM);
        modelAndView.addObject("auctionDto", getAuctionService.getOne(id));
        modelAndView.addObject("bidDto", new BidDto());

        return modelAndView;
    }

    @PostMapping(Urls.Auction.BID)
    public ModelAndView bid(@PathVariable String id, @Valid BidDto bidDto, BindingResult bindingResult, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();

        bidDto.setUsername(principal.getName());
        bidDto.setAuctionId(id);
        bidService.setBidDto(bidDto);

        if(!getAuctionService.auctionExists(id)) {
            modelAndView.setViewName(Templates.AuctionTemplates.ITEM);
            return modelAndView;
        }

        if(bidService.checkOfferLowerThanCurrentPrice()) {
            bindingResult.rejectValue("amount", "error.bid.price.lower");
        }

        if(bidService.checkIsBiddingUserAuctionCreator()) {
            bindingResult.rejectValue("amount","error.bid.same.user");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("auctionDto", getAuctionService.getOne(id));
            modelAndView.setViewName(Templates.AuctionTemplates.ITEM);
            return modelAndView;
        }

        bidService.createBid();

        modelAndView.setViewName(String.format(Urls.Auction.BID_SUCCESS_REDIRECT, id));
        return modelAndView;
    }

    @GetMapping(Urls.Auction.BID_SUCCESS)
    public ModelAndView bidSuccess(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView(Templates.AuctionTemplates.BID_SUCCESS);
        modelAndView.addObject("auctionId", id);
        return modelAndView;
    }
}
