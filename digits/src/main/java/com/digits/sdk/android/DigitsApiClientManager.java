/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.digits.sdk.android;

import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.concurrent.ExecutorService;


public class DigitsApiClientManager {
    private final Digits digits;
    private final DigitsUserAgent userAgent;
    private final SessionManager<DigitsSession> sessionManager;
    private final TwitterCore twitterCore;
    private final ExecutorService executorService;
    private DigitsApiClient digitsApiClient;

    DigitsApiClientManager() {
        this(Digits.getInstance(), new DigitsUserAgent(), TwitterCore.getInstance(),
                Digits.getInstance().getExecutorService(), Digits.getSessionManager(), null);
    }

    DigitsApiClientManager(Digits digits, DigitsUserAgent userAgent, TwitterCore twitterCore,
                           ExecutorService executorService,
                           SessionManager<DigitsSession> sessionManager,
                           DigitsApiClient apiClient) {
        if (twitterCore == null) {
            throw new IllegalArgumentException("twitter must not be null");
        }
        if (digits == null) {
            throw new IllegalArgumentException("digits must not be null");
        }
        if (userAgent == null) {
            throw new IllegalArgumentException("userAgent must not be null");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("sessionManager must not be null");
        }
        if (executorService == null) {
            throw new IllegalArgumentException("executorService must not be null");
        }

        this.twitterCore = twitterCore;
        this.digits = digits;
        this.userAgent = userAgent;
        this.executorService = executorService;
        this.sessionManager = sessionManager;
        if (apiClient != null) {
            this.digitsApiClient = apiClient;
        } else {
            this.digitsApiClient = createNewClient();
        }
    }

    DigitsApiClient getApiClient() {
        if (!digitsApiClient.getSession().equals(sessionManager.getActiveSession())) {
            this.digitsApiClient = createNewClient();
        }
        return digitsApiClient;
    }

    DigitsApiClient.SdkService getService(){
       return getApiClient().getService();
    }

    protected DigitsApiClient createNewClient(){
        return new DigitsApiClient(sessionManager.getActiveSession(), twitterCore,
                twitterCore.getSSLSocketFactory(),
                executorService, userAgent);
    }

}