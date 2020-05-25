package org.devocative.thallo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.net.ServerSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtil {
	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	public static void randomSleep(int factor) {
		try {
			Thread.sleep((long) (Math.random() * factor) + 10);
		} catch (InterruptedException e) {
			log.error("TestUtil.randomSleep", e);
		}
	}

	public static void setSecurity(String username) {
		setSecurity(username, Collections.emptyList());
	}

	public static void setSecurity(String username, Collection<String> authorities) {
		setSecurity(new User(username, username, Collections.emptyList()), authorities);
	}

	public static void setSecurity(User user, Collection<String> authorities) {
		final List<SimpleGrantedAuthority> authorityList = authorities.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, user.getPassword(), authorityList);
		SecurityContextHolder.setContext(new SecurityContextImpl(token));

		SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	}

	public static Integer findRandomOpenPortOnAllLocalInterfaces() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
