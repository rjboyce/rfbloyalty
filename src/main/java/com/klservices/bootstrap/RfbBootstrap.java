package com.klservices.bootstrap;

import com.klservices.domain.*;
import com.klservices.repository.*;
import com.klservices.service.KeycloakServices;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RfbBootstrap implements CommandLineRunner {

    private final RfbEventAttendanceRepository rfbEventAttendanceRepository;
    private final RfbEventRepository rfbEventRepository;
    private final RfbLocationRepository rfbLocationRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakServices keycloakServices;

    public RfbBootstrap(
        RfbEventAttendanceRepository rfbEventAttendanceRepository,
        RfbEventRepository rfbEventRepository,
        RfbLocationRepository rfbLocationRepository,
        ApplicationUserRepository applicationUserRepository,
        AuthorityRepository authorityRepository,
        PasswordEncoder passwordEncoder,
        KeycloakServices keycloakServices
    ) {
        this.rfbEventAttendanceRepository = rfbEventAttendanceRepository;
        this.rfbEventRepository = rfbEventRepository;
        this.rfbLocationRepository = rfbLocationRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.keycloakServices = keycloakServices;
    }

    @Transactional
    @Override
    public void run(String... Strings) throws Exception {
        //if(rfbLocationRepository.count() == 0){
        //only load data if no data loaded
        initData();
        //}
    }

    private void initData() {
        Authority runner = new Authority();
        runner.setName("ROLE_RUNNER");
        authorityRepository.save(runner);

        Authority organizer = new Authority();
        organizer.setName("ROLE_ORGANIZER");
        authorityRepository.save(organizer);

        ApplicationUser user = new ApplicationUser();
        user.setId(UUID.randomUUID().toString());
        user.setLogin("greywind");
        user.setFirstName("Akshay");
        user.setLastName("Bay");
        user.setEmail("greywind@thorns.com");
        user.setActivated(true);
        user.getAuthorities().add(authorityRepository.getOne("ROLE_RUNNER"));
        user.getAuthorities().add(authorityRepository.getOne("ROLE_ORGANIZER"));
        //applicationUserRepository.save(user);

        RfbLocation crapshot = getRfbLocation("Crapshot - Annex", DayOfWeek.MONDAY.getValue());
        user.setHomeLocation(crapshot);
        applicationUserRepository.save(user); //"funnybay" would be provided here as the password

        RfbEvent meetandgreet = getRfbEvent(crapshot);
        getRfbEventAttendance(user, meetandgreet);

        RfbLocation wileypete = getRfbLocation("Wiley Pete's - Etobicoke", DayOfWeek.TUESDAY.getValue());
        RfbEvent socialsinners = getRfbEvent(wileypete);
        getRfbEventAttendance(user, socialsinners);

        RfbLocation snarkyal = getRfbLocation("Snarky Al Steakhouse - Beaches/East York", DayOfWeek.WEDNESDAY.getValue());
        RfbEvent newcomer = getRfbEvent(snarkyal);
        getRfbEventAttendance(user, newcomer);

        RfbLocation roundhouse = getRfbLocation("Roundhouse Recreation - Downtown Toronto", DayOfWeek.MONDAY.getValue());
        RfbEvent socialmeet = getRfbEvent(roundhouse);
        getRfbEventAttendance(user, socialmeet);

        RfbLocation kasters = getRfbLocation("Kaster's Grill House", DayOfWeek.TUESDAY.getValue());
        RfbEvent socialdinner = getRfbEvent(kasters);
        getRfbEventAttendance(user, socialdinner);

        RfbLocation lockes = getRfbLocation("Locke's Fish and Chips", DayOfWeek.FRIDAY.getValue());
        RfbEvent easydining = getRfbEvent(lockes);
        getRfbEventAttendance(user, easydining);

        keycloakServices.saveToIdp(user, "funnybay");
    }

    private void getRfbEventAttendance(ApplicationUser user, RfbEvent event) {
        RfbEventAttendance rfbEventAttendance = new RfbEventAttendance();
        rfbEventAttendance.setApplicationUser(user);
        rfbEventAttendance.setRfbEvent(event);
        rfbEventAttendance.setAttendanceDate(LocalDate.now());

        System.out.println(rfbEventAttendance);

        rfbEventAttendanceRepository.save(rfbEventAttendance);
        rfbEventRepository.save(event);
    }

    private RfbEvent getRfbEvent(RfbLocation rfbLocation) {
        RfbEvent rfbEvent = new RfbEvent();
        rfbEvent.setEventCode(UUID.randomUUID().toString());
        rfbEvent.setEventDate(LocalDate.now());
        rfbLocation.addRfbEvent(rfbEvent);
        rfbLocationRepository.save(rfbLocation);
        rfbEventRepository.save(rfbEvent);
        return rfbEvent;
    }

    private RfbLocation getRfbLocation(String name, int value) {
        RfbLocation rfbLocation = new RfbLocation();
        rfbLocation.setLocationName(name);
        rfbLocation.setRunDayOfWeek(value);
        rfbLocationRepository.save(rfbLocation);
        return rfbLocation;
    }
}
