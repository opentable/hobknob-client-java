package com.opentable.hobknob.client;

import java.util.ArrayList;
import java.util.List;

public class HobknobClient implements CacheUpdatedEvent, CacheUpdateFailedEvent {

    FeatureToggleCache _featureToggleCache;
    String _applicationName;

    public HobknobClient(FeatureToggleCache featureToggleCache, String applicationName) {

        _featureToggleCache = featureToggleCache;
        _applicationName = applicationName;

        _featureToggleCache.addCacheUpdatedLstener(this);
    }

    public boolean get(String toggleName) throws Exception {

        Boolean value = _featureToggleCache.get(toggleName);
        if (value == null)
        {
            throw new Exception(String.format("Key not found for toggle %s/%s", _applicationName, toggleName));
        }
        return value;
    }

    public boolean getOrDefault(String featureToggleName, boolean defaultValue) {

        Boolean value =_featureToggleCache.get(featureToggleName);
        return value != null ? value : defaultValue;
    }

    private List<CacheUpdatedEvent> _cacheUpdatedListeners = new ArrayList<CacheUpdatedEvent>();
    private List<CacheUpdateFailedEvent> _cacheUpdateFailedListeners = new ArrayList<CacheUpdateFailedEvent>();

    public void addCacheUpdatedLstener(CacheUpdatedEvent listener) {
        _cacheUpdatedListeners.add(listener);
    }

    public void addCacheUpdateFailedLstener(CacheUpdateFailedEvent listener) {
        _cacheUpdateFailedListeners.add(listener);
    }

    private void raiseCacheUpdatedEvent(List<CacheUpdate> updates) {
        for(CacheUpdatedEvent listener : _cacheUpdatedListeners) {
            listener.cacheUpdated(updates);
        }
    }

    private void raiseCacheUpdateFailedEvent(Exception exception) {
        for(CacheUpdateFailedEvent listener : _cacheUpdateFailedListeners) {
            listener.cacheUpdateFailed(exception);
        }
    }

    @Override
    public void cacheUpdated(List<CacheUpdate> cacheUpdates) {
        raiseCacheUpdatedEvent(cacheUpdates);
    }

    @Override
    public void cacheUpdateFailed(Exception exception) {
        raiseCacheUpdateFailedEvent(exception);
    }
}