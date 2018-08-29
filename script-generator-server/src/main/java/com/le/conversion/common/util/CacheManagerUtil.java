package com.le.conversion.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CacheManagerUtil {

	@Autowired
	private CacheManager cacheManager;

	public void addToCache(String cacheName, Object cacheKey, Object cacheData) {

		getCache(cacheName).put(cacheKey, cacheData);

	}
	
	public Object getFromCache(String cacheName, Object cacheKey) {

		return getCache(cacheName).get(cacheKey).get();

	}

	private Cache getCache(String cacheName) {

		return cacheManager.getCache(cacheName);

	}


}
