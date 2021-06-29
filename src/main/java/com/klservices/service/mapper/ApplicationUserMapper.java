package com.klservices.service.mapper;

import com.klservices.domain.*;
import com.klservices.repository.ApplicationUserRepository;
import com.klservices.service.dto.ApplicationUserDTO;
import java.time.Instant;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link ApplicationUser} and its DTO {@link ApplicationUserDTO}.
 */

//@Service
@Mapper(componentModel = "spring", uses = { RfbLocationMapper.class })
public interface ApplicationUserMapper extends EntityMapper<ApplicationUserDTO, ApplicationUser> {
    @Mapping(target = "homeLocation", source = "homeLocation", qualifiedByName = "id")
    ApplicationUserDTO toDto(ApplicationUser s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ApplicationUserDTO toDtoId(ApplicationUser applicationUser);

    @Mapping(target = "homeLocation", ignore = true)
    @Mapping(target = "rfbEventAttendances", ignore = true)
    ApplicationUser fromUser(User user);
    /*private final RfbLocationMapper rfbLocationMapper;

    public ApplicationUserMapper(RfbLocationMapper rfbLocationMapper) {
        this.rfbLocationMapper = rfbLocationMapper;
    }

    public ApplicationUserDTO toDto(ApplicationUser applicationUser) {

        ApplicationUserDTO userDTO = new ApplicationUserDTO();
        userDTO.setId(applicationUser.getId());
        userDTO.setLogin(applicationUser.getLogin());
        userDTO.setFirstName(applicationUser.getFirstName());
        userDTO.setLastName(applicationUser.getLastName());
        userDTO.setEmail(applicationUser.getEmail());
        userDTO.setLangKey(applicationUser.getLangKey());
        userDTO.setImageUrl(applicationUser.getImageUrl());
        userDTO.setHomeLocation(rfbLocationMapper.toDto(applicationUser.getHomeLocation()));
        return userDTO;
    }

    public ApplicationUser toEntity(ApplicationUserDTO applicationUserDTO, ApplicationUser user){

        //ensure references from EventAttendance are not null - new Event Attendances will have a null ApplicationUser
        if(user == null) user = new ApplicationUser();

        //differentiate between a new user and an existing user - DTO id is null with a new user!
        //new users will have their ids set in the service, already set to user object
        if(applicationUserDTO.getId() != null) user.setId(applicationUserDTO.getId());
        user.setLogin(applicationUserDTO.getLogin());
        user.setFirstName(applicationUserDTO.getFirstName());
        user.setLastName(applicationUserDTO.getLastName());
        user.setEmail(applicationUserDTO.getEmail());
        user.setLangKey(applicationUserDTO.getLangKey());
        user.setImageUrl(applicationUserDTO.getImageUrl());
        user.setHomeLocation(rfbLocationMapper.toEntity(applicationUserDTO.getHomeLocation()));
        //missing created/last modified by names and dates as well as authorities...  add on service side with
        //pricipal user passed from resource?
        return user;

    }

    public void partialUpdate(ApplicationUser existingApplicationUser, ApplicationUserDTO applicationUserDTO) {
        if (applicationUserDTO.getId() != null){
            existingApplicationUser.setFirstName(applicationUserDTO.getFirstName());
            existingApplicationUser.setLastName(applicationUserDTO.getLastName());
            existingApplicationUser.setEmail(applicationUserDTO.getEmail());
            existingApplicationUser.setImageUrl(applicationUserDTO.getImageUrl());
            existingApplicationUser.setLangKey(applicationUserDTO.getLangKey());
            existingApplicationUser.setHomeLocation(rfbLocationMapper.toEntity(applicationUserDTO.getHomeLocation()));
            existingApplicationUser.setLastModifiedDate(Instant.now());
            existingApplicationUser.setLastModifiedBy(applicationUserDTO.getLogin());
        }
    }

    public ApplicationUser fromUser(User user, ApplicationUser appUser){
        //used to save synchronized user as application user which can be accessed by application
        *//*appUser.setId(user.getId());
        appUser.setLogin(user.getLogin());
        appUser.setFirstName(user.getFirstName());
        appUser.setLastName(user.getFirstName());
        appUser.setEmail(user.getEmail());
        appUser.setLangKey(user.getLangKey());
        appUser.setImageUrl(user.getImageUrl());
        appUser.setLastModifiedBy(user.getLastModifiedBy());
        appUser.setLastModifiedDate(user.getLastModifiedDate());
        appUser.setCreatedBy(user.getCreatedBy());
        appUser.setCreatedDate(user.getCreatedDate());
        appUser.setActivated(user.isActivated());
        appUser.setAuthorities(user.getAuthorities());*//*
        return appUser;
    }*/
}
