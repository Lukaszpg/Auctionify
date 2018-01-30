package pro.lukasgorny.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import pro.lukasgorny.dto.UserResultDto;
import pro.lukasgorny.model.User;
import pro.lukasgorny.model.VerificationToken;
import pro.lukasgorny.repository.VerificationTokenRepository;
import pro.lukasgorny.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

/**
 * Created by lukaszgo on 2017-05-25.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.messageSource = messageSource;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    @Override
    public UserResultDto createDtoFromEntity(User user) {
        UserResultDto userResultDto = new UserResultDto();
        userResultDto.setEmail(user.getEmail());
        userResultDto.setRegisteredSince(calculateAndFormatDateDifference(user));
        return userResultDto;
    }

    private String calculateAndFormatDateDifference(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tempDateTime = user.getCreateDate();

        Long years = tempDateTime.until(now, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);
        Long months = tempDateTime.until(now, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);
        Long days = tempDateTime.until(now, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        String yearsMessage = messageSource.getMessage("label.years", null, LocaleContextHolder.getLocale());
        String monthsMessage = messageSource.getMessage("label.months", null, LocaleContextHolder.getLocale());
        String daysMessage = messageSource.getMessage("label.days", null, LocaleContextHolder.getLocale());

        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(years.toString()).add(yearsMessage).add(months.toString()).add(monthsMessage).add(days.toString()).add(daysMessage);

        return stringJoiner.toString();
    }
}
