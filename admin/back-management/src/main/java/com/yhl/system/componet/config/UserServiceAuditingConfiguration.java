package com.yhl.system.componet.config;

import com.yhl.system.entity.OAthUserDetailes;
import com.yhl.system.service.OAthUserDetailesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

 @Slf4j
 @ConditionalOnBean(OAthUserDetailesService.class)
public class UserServiceAuditingConfiguration {
	@Bean
	@ConditionalOnMissingBean(AuditorAware.class)
	public AuditorAware<String> auditorAware(@Autowired OAthUserDetailesService userService) {
		 log.info("create default auditorAware");
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					Object principal = authentication.getPrincipal();
					if (principal instanceof OAthUserDetailes) {
						UserDetails ud = (UserDetails) principal;
						return  Optional.of(ud.getUsername()) ;
					}
				}
				return Optional.empty();
			}
		};
	}
}
